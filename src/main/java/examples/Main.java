package examples;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.queryparser.classic.ParseException;

import examples.IntegerRangeQueryProvider.IntRangeQueryProvider2;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;
import org.apache.geode.cache.lucene.internal.LuceneIndexForPartitionedRegion;
import org.apache.geode.cache.lucene.internal.LuceneIndexImpl;
import org.apache.geode.cache.lucene.internal.LuceneServiceImpl;
import org.apache.geode.distributed.ServerLauncher;
import org.apache.geode.distributed.ServerLauncher.Builder;
import org.apache.geode.internal.DSFIDFactory;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;

public class Main {
  // private final Version version = Version.LUCENE_5_3_0;
  ServerLauncher serverLauncher;
  GemFireCache cache;
  Region PersonRegion;
  Region CustomerRegion;
  Region PageRegion;
  LuceneServiceImpl service;
  static int serverPort = 50505;
  static boolean useLocator = false;

  final static int ENTRY_COUNT = 1000;
  final static Logger logger = LogService.getLogger();

  final static int SERVER_WITH_FEEDER = 1;
  final static int SERVER_ONLY = 2;
  final static int CLIENT = 3;
  static int instanceType = SERVER_WITH_FEEDER;
  
  /* Usage: ~ [1|2|3 [serverPort [isUsingLocator]]]
   * 1: server with feeder
   * 2: server only 
   * 3: client
   */
  public static void main(final String[] args) throws LuceneQueryException, IOException, InterruptedException {
    System.out.println("There are "+args.length+" arguments.");
    for (int i=0; i<args.length; i++) {
      System.out.println("arg"+i+":"+args[i]);
    }

    Main prog = new Main();
    try {
      //    System.setProperty("gemfire.NotUsing.RegionDirectory", "true");
      if (args.length > 0) {
        instanceType = Integer.valueOf(args[0]);
      }
      if (args.length > 1) {
        serverPort = Integer.valueOf(args[1]);
      }
      if (args.length > 2) {
        useLocator = Boolean.valueOf(args[2]);
      }
      
      if (instanceType == CLIENT)
      {
        prog.createClientCache(serverPort);
      } else {
        registerDataSerializables();
        prog.createCache(serverPort);

        // note: we have to create lucene index before the region
        prog.createIndexAndRegions(RegionShortcut.PARTITION_PERSISTENT);        
      }
      
      if (instanceType == SERVER_WITH_FEEDER) {
        prog.feed(ENTRY_COUNT);
        prog.waitUntilFlushed("personIndex", "Person");
        prog.waitUntilFlushed("customerIndex", "Customer");
      }

      if (instanceType != SERVER_ONLY) {
        // now let's do search on lucene index
        prog.doQuery();
      }
      
      if (instanceType == SERVER_WITH_FEEDER) {
        prog.doDump("personIndex", "Person");
        prog.doDump("customerIndex", "Customer");
        prog.doDump("analyzerIndex", "Person");
        System.out.println("Dumpped index");
      }

      System.out.println("Press any key to exit");
      int c = System.in.read();

    } finally {
      prog.stopServer();
    }
  }

  private void createClientCache(int port) {
    ClientCacheFactory cf = new ClientCacheFactory().addPoolLocator("localhost", 12345);
    cache = cf.setPdxReadSerialized(true).create();
    ClientRegionFactory crf = ((ClientCache)cache).createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
    PersonRegion = crf.create("Person");
    CustomerRegion = crf.create("Customer");
    PageRegion = crf.create("Page");
    
    service = (LuceneServiceImpl) LuceneServiceProvider.get(cache);
  }
  
  private void createCache(int port) {
    if (cache != null) {
      return;
    }

    System.out.println("GEMFIRE="+System.getenv("GEMFIRE"));
    Builder builder = new ServerLauncher.Builder()
        .setMemberName("server"+port)
        .setServerPort(port)
        .setPdxPersistent(true)
        .set("mcast-port", "0")
        .set("enable-time-statistics","true")
        .set("statistic-sample-rate","1000")
        .set("statistic-sampling-enabled", "true")
        .set("statistic-archive-file", "server1.gfs");
    //        .set("log-level", "debug")
    ;

    if (instanceType != CLIENT) {
      builder.set("start-dev-rest-api", "true")
      .set("http-service-port","808"+instanceType)
      .set("http-service-bind-address", "localhost");
    }
        
    if (useLocator && instanceType != CLIENT) {
      builder.set("locators", "localhost[12345]");
    }
    serverLauncher  = builder.build();

    serverLauncher.start();
    System.out.println("Server started!");

    cache = CacheFactory.getAnyInstance();
    service = (LuceneServiceImpl) LuceneServiceProvider.get(cache);
  }

  public static void registerDataSerializables() {
    DSFIDFactory.registerDSFID(IntegerRangeQueryProvider.LUCENE_INTEGER_RANGE_QUERY_PROVIDER, IntegerRangeQueryProvider.class);
    IntRangeQueryProvider2 c = new IntRangeQueryProvider2();
  }
  
  private void stopServer() {
    if (serverLauncher != null) {
      serverLauncher.stop();
      serverLauncher = null;
      System.out.println("server is stopped");
    }
  }

  private void createIndexAndRegions(RegionShortcut shortcut) {
    Map<String, Analyzer> fields = new HashMap<String, Analyzer>();
    fields.put("name",  null);
    fields.put("email", new KeywordAnalyzer());
    fields.put("address", new MyCharacterAnalyzer());
    service.createIndex("analyzerIndex", "Person", fields);

    // LuceneServiceImpl.luceneIndexFactory = new LuceneRawIndexFactory();
    //    LuceneIndexForPartitionedRegion.partitionedRepositoryManagerFactory = new RawLuceneRepositoryManagerFactory();
    service.createIndex("personIndex", "Person", "name", "email", "address", "streetAddress", "revenue");
    // PersonRegion = cache.createRegionFactory(shortcut).create("Person");
    cache.createDiskStoreFactory().create("data");
    PersonRegion = ((Cache)cache).createRegionFactory()
        .setDiskStoreName("data")
        //.setDataPolicy(DataPolicy.PARTITION).create("Person");
        .setDataPolicy(DataPolicy.PERSISTENT_PARTITION).create("Person");

    service.createIndex("customerIndex", "Customer", "symbol", "revenue", "SSN", "name", "email", "address", LuceneService.REGION_VALUE_FIELD);
    CustomerRegion = ((Cache)cache).createRegionFactory(shortcut).create("Customer");

    service.createIndex("pageIndex", "Page", "symbol", /*"revenue",*/ "name", "email", "address");
    PageRegion = ((Cache)cache).createRegionFactory(shortcut).create("Page");
  }

  public void doQuery() throws LuceneQueryException {
    queryByStringQueryParser("personIndex", "Person", "name:Tom9*");
    queryByStringQueryParser("personIndex", "Person", "streetAddress:21*");
    queryByStringQueryParser("customerIndex", "Customer", "name:Tom123");

    queryByStringQueryParser("customerIndex", "Customer", "symbol:456");
    queryByStringQueryParser("customerIndex", "Customer", LuceneService.REGION_VALUE_FIELD+":[123 TO *]");
    queryByStringQueryParser("customerIndex", "Customer", LuceneService.REGION_VALUE_FIELD+":[123 TO 223]");

    System.out.println();
    queryByIntRange("customerIndex", "Customer", "SSN", 456, Integer.MAX_VALUE);
    queryByInRange1("customerIndex", "Customer", LuceneService.REGION_VALUE_FIELD, 123, 123);
    queryByInRange2("personIndex", "Person", "revenue", 3000, 5000);

    //  prog.queryByInRange("customerIndex", "Customer", LuceneService.REGION_VALUE_FIELD+":[123000.0 TO 123000.0]");
    //  prog.queryByInRange("customerIndex", "Customer", LuceneService.REGION_VALUE_FIELD+":1230*");
    //  prog.doSearch("pageIndex", "Page", "id:10");

    //  prog.feedAndDoSpecialSearch("analyzerIndex", "Person");    
  }
  
  // for test purpose
  private void waitUntilFlushed(String indexName, String regionName) {
    LuceneIndexImpl index = (LuceneIndexImpl)service.getIndex(indexName, regionName);
    boolean status = false;
    long then = System.currentTimeMillis();
    do {
      status = index.waitUntilFlushed(60000);
    } while (status == false);
    System.out.println("Total wait time is:"+(System.currentTimeMillis() - then));
  }

  public void doDump(String indexName, String regionName) {
    LuceneIndexImpl index = (LuceneIndexImpl)service.getIndex(indexName, regionName);
    index.dumpFiles("dump"+indexName);
  }

  private void defineIndexAndRegion(String indexName, String regionName, RegionShortcut regionType, String...fields) {
    service.createIndex(indexName, regionName, fields);
  }

  private Region createRegion(String regionName, RegionShortcut regionType) {
    RegionFactory<Object, Object> regionFactory = ((Cache)cache).createRegionFactory(regionType);
    return regionFactory.create(regionName);
  }

  private void feed(int count) {
    for (int i=0; i<count; i++) {
      PersonRegion.put("key"+i, new Person(i, ""));
    }
    //    for (int i=0; i<count; i++) {
    //      CustomerRegion.put("key"+i, new Customer(i));
    //    }
    //    for (int i=0; i<count; i++) {
    //      PageRegion.put("key"+i, new Page(i));
    //    }

    insertAJson(PersonRegion);
    insertNestObjects(CustomerRegion);
    insertPrimitiveTypeValue(CustomerRegion);
  }

  private void insertPrimitiveTypeValue(Region region) {
    region.put("primitiveInt1", 123);
    region.put("primitiveInt2", 223);
    //    region.put("primitiveDouble1", 123000.0);
    //    region.put("primitiveString1", "123");
    //    region.put("primitiveString2", "22");
  }

  private void insertNestObjects(Region region) {
    Customer customer123 = new Customer(123);
    Customer customer456 = new Customer(456);
    region.put("customer123", customer123);
    region.put("customer456", customer456);
  }

  private void insertAJson(Region region) {
    String jsonCustomer = "{"
        + "\"name\": \"Tom9_JSON\","
        + "\"lastName\": \"Smith\","
        + " \"age\": 25,"
        + " \"revenue\": 4000,"
        + "\"address\":"
        + "{"
        + "\"streetAddress\": \"21 2nd Street\","
        + "\"city\": \"New York\","
        + "\"state\": \"NY\","
        + "\"postalCode\": \"10021\""
        + "},"
        + "\"phoneNumber\":"
        + "["
        + "{"
        + " \"type\": \"home\","
        + "\"number\": \"212 555-1234\""
        + "},"
        + "{"
        + " \"type\": \"fax\","
        + "\"number\": \"646 555-4567\""
        + "}"
        + "]"
        + "}";

    region.put("jsondoc1", JSONFormatter.fromJSON(jsonCustomer));
    System.out.println("JSON documents added into Cache: " + jsonCustomer);
    System.out.println(region.get("jsondoc1"));
    System.out.println();

    String jsonCustomer2 = "{"
        + "\"name\": \"Tom99_JSON\","
        + "\"lastName\": \"Smith\","
        + " \"age\": 25,"
        + " \"revenue\": 4001,"
        + "\"address\":"
        + "{"
        + "\"streetAddress\": \"21 2nd Street\","
        + "\"city\": \"New York\","
        + "\"state\": \"NY\","
        + "\"postalCode\": \"10021\""
        + "},"
        + "\"phoneNumber\":"
        + "["
        + "{"
        + " \"type\": \"home\","
        + "\"number\": \"212 555-1234\""
        + "},"
        + "{"
        + " \"type\": \"fax\","
        + "\"number\": \"646 555-4567\""
        + "}"
        + "]"
        + "}";
    region.put("jsondoc2", JSONFormatter.fromJSON(jsonCustomer2));
    System.out.println("JSON documents added into Cache: " + jsonCustomer2);
    System.out.println(region.get("jsondoc2"));
    System.out.println();
  }

  private void getResults(LuceneQuery query, String regionName) throws LuceneQueryException {
    if (query == null) {
      return;
    }

    PageableLuceneQueryResults<String, Object> results = query.findPages();
    if (results.size() >0 ) {
      System.out.println("Search found "+results.size()+" rows in "+regionName);
    }

    if (results.size() < 10) {
    final AtomicInteger cnt = new AtomicInteger(0);
    while(results.hasNext()) {
      results.next().stream()
      .forEach(struct -> {
        Object value = struct.getValue();
        if (value instanceof PdxInstance) {
          PdxInstance pdx = (PdxInstance)value;
          String jsonString = JSONFormatter.toJSON(pdx);
          System.out.println("Found a json object:"+jsonString);
        } else {
          System.out.println("No: "+cnt.get()+":key="+struct.getKey()+",value="+value+",score="+struct.getScore());
        }
        cnt.incrementAndGet();
      });
    }
    }
  }

  private void feedAndDoSpecialSearch(String indexName, String regionName) throws LuceneQueryException {
    String value1 = "one three";
    String value2 = "one two three";
    String value3 = "one@three";
    String value4 = "two_four";

    Region region = cache.getRegion(regionName);
    if (!regionName.equals("Person")) {
      return;
    }

    region.put("A", new Person(value1, value1, ""));
    region.put("B", new Person(value2, value2, ""));
    region.put("C", new Person(value3, value3, ""));
    waitUntilFlushed(indexName, regionName);

    // standard analyzer with double quote
    // this query string will be parsed as "one three"
    // but standard analyzer will parse value "one@three" to be "one three"
    // query will be--name:"one three"
    // so C will be hit by query
    verifyQuery(indexName, regionName, "name:\"one three\"", "A", "C");

    // standard analyzer without double quote
    // this query string will be parsed as "one" "three"
    // query will be--name:one (name:three email:three)
    verifyQuery(indexName, regionName, "name:one three", "A", "B", "C");

    // standard analyzer will not tokenize by '_'
    // this query string will be parsed as "one_three"
    // query will be--name:one_three
    verifyQuery(indexName, regionName, "name:one_three");

    // standard analyzer will tokenize by '@'
    // this query string will be parsed as "one" "three"
    // query will be--name:one field1:three
    verifyQuery(indexName, regionName, "name:one@three", "A", "B", "C");

    // keyword analyzer, this query will only match the entry that exactly matches
    // this query string will be parsed as "one three"
    // but keyword analyzer will parse one@three to be "one three"
    // query will be--email:one three
    verifyQuery(indexName, regionName, "email:\"one three\"", "A");

    // keyword analyzer without double quote. It will be parsed as "one" "three"
    // query will be--email:one (name:three email:three)
    verifyQuery(indexName, regionName, "email:one three", "A", "B", "C");

    // standard analyzer without double quote. It will be parsed as "one" "three"
    // query will be--(name:one email:one) (name:three email:three)
    verifyQuery(indexName, regionName, "one three", "A", "B", "C");

    // standard analyzer without double quote. It will be parsed as "one" "three"
    // query will be--(name:one email:one) (name:three email:three)
    verifyQuery(indexName, regionName, "one OR two", "A", "B", "C");

    // standard analyzer without double quote. It will be parsed as "one" "three"
    // query will be-- +(name:one email:one) +(name:two email:two)
    verifyQuery(indexName, regionName, "one AND two", "B");

    // keyword analyzer without double quote. It will be parsed as "one"
    // query will be--email:one)
    verifyQuery(indexName, regionName, "email:one");

    // keyword analyzer without double quote. It will be parsed as "one" and "three"
    // query will be--email:one*)
    verifyQuery(indexName, regionName, "email:one*", "A", "B", "C");

    // keyword analyzer without double quote. It should be the same as 
    // with double quote
    // query will be--email:one@three
    verifyQuery(indexName, regionName, "email:one@three", "C");

    // now test my analyzer
    region.put("A", new Person(value1, "", value4));
    region.put("B", new Person(value1, "", value3));
    region.destroy("C");
    waitUntilFlushed(indexName, regionName);
    verifyQuery(indexName, regionName, "name:one AND address:two_four", "A");
    verifyQuery(indexName, regionName, "name:one AND address:two", "A");
    verifyQuery(indexName, regionName, "name:three AND address:four", "A");
  }

  private void queryByStringQueryParser(String indexName, String regionName, String queryString) throws LuceneQueryException {
    System.out.println("Query string is:"+queryString);
    LuceneQuery query = service.createLuceneQueryFactory().create(indexName, regionName, queryString, "name");

    getResults(query, regionName);
  }

  private void queryByIntRange(String indexName, String regionName, String fieldName, int lowerValue, int upperValue) throws LuceneQueryException {
    System.out.println("Query range is:"+fieldName+":["+lowerValue+" TO "+upperValue+"]");
    IntRangeQueryProvider provider = new IntRangeQueryProvider(fieldName, lowerValue, upperValue);
    LuceneQuery query = service.createLuceneQueryFactory().create(indexName, regionName, provider);

    getResults(query, regionName);
  }
  
  private void queryByInRange1(String indexName, String regionName, String fieldName, int lowerValue, int upperValue) throws LuceneQueryException {
    System.out.println("Query range is:"+fieldName+":["+lowerValue+" TO "+upperValue+"]");
    IntegerRangeQueryProvider provider = new IntegerRangeQueryProvider(fieldName, lowerValue, upperValue);
    LuceneQuery query = service.createLuceneQueryFactory().create(indexName, regionName, provider);

    getResults(query, regionName);
  }
  
  private void queryByInRange2(String indexName, String regionName, String fieldName, int lowerValue, int upperValue) throws LuceneQueryException {
    System.out.println("Query range is:"+fieldName+":["+lowerValue+" TO "+upperValue+"]");
    IntRangeQueryProvider2 provider = new IntRangeQueryProvider2(fieldName, lowerValue, upperValue);
    LuceneQuery query = service.createLuceneQueryFactory().create(indexName, regionName, provider);

    getResults(query, regionName);
  }
  
  private void verifyQuery(String indexName, String regionName, String queryString, String... expectedKeys) throws LuceneQueryException {
    LuceneQuery query = service.createLuceneQueryFactory().create(indexName, regionName, queryString, "name");
    if (query == null) {
      return;
    }

    Set expectedKeySet = new HashSet<>(Arrays.asList(expectedKeys));
    Set actualKeySet = new HashSet<>();
    final PageableLuceneQueryResults<String, Object> results = query.findPages();
    while(results.hasNext()) {
      results.next().stream()
      .forEach(struct -> { 
        String key = (String)((LuceneResultStruct)struct).getKey(); 
        //                  System.out.println("key="+key); 
        actualKeySet.add(key); 
      });
    }

    if (expectedKeySet.containsAll(actualKeySet) && actualKeySet.containsAll(expectedKeySet)) {
      System.out.println(queryString + " expects "+expectedKeySet+ " -- OK");
    } else {
      System.out.println(queryString + " expects "+expectedKeySet+ " -- failed, actual is "+actualKeySet);
    }
  }

  private static class MyCharacterTokenizer extends CharTokenizer {
    @Override
    protected boolean isTokenChar(final int character) {
      return '_' != character;
    }
  }

  private static class MyCharacterAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(final String field) {
      Tokenizer tokenizer = new MyCharacterTokenizer();
      TokenStream filter = new LowerCaseFilter(tokenizer);
      return new TokenStreamComponents(tokenizer, filter);
    }
  }
}
