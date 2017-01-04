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
  final static int SERVER_WITH_CLUSTER_CONFIG = 4;
  static int instanceType = SERVER_WITH_FEEDER;
  
  /* Usage: ~ [1|2|3 [serverPort [isUsingLocator]]]
   * 1: server with feeder
   * 2: server only 
   * 3: client
   */
  public static void main(final String[] args) throws LuceneQueryException, IOException, InterruptedException {
//    System.out.println("There are "+args.length+" arguments.");
//    for (int i=0; i<args.length; i++) {
//      System.out.println("arg"+i+":"+args[i]);
//    }

    Main prog = new Main();
    try {
      //    System.setProperty("gemfire.NotUsing.RegionDirectory", "true");
      if (args.length > 0) {
        instanceType = Integer.valueOf(args[0]);
      }
      if (args.length > 1) {
        useLocator = Boolean.valueOf(args[1]);
      }
      serverPort += instanceType;
      
      switch (instanceType) {
        case CLIENT:
          // create client cache and proxy regions
          // do query
          prog.createClientCache();
          //        prog.feed(ENTRY_COUNT);
          prog.doQuery();
          break;

        case SERVER_WITH_CLUSTER_CONFIG:
          // create cache without region
          // do feed
          // do query
          prog.createCache(serverPort);
          prog.getRegions();

          prog.feed(ENTRY_COUNT);
          prog.waitUntilFlushed("personIndex", "Person");
          prog.waitUntilFlushed("analyzerIndex", "Person");
          prog.waitUntilFlushed("customerIndex", "Customer");
          prog.waitUntilFlushed("pageIndex", "Page");

          prog.doQuery();
          break;

        case SERVER_WITH_FEEDER:
          // create cache, create index, create region
          // do feed
          // do query
          prog.createCache(serverPort);
          prog.createIndexAndRegions(RegionShortcut.PARTITION_PERSISTENT);        

          prog.feed(ENTRY_COUNT);
          prog.waitUntilFlushed("personIndex", "Person");
          prog.waitUntilFlushed("analyzerIndex", "Person");
          prog.waitUntilFlushed("customerIndex", "Customer");
          prog.waitUntilFlushed("pageIndex", "Page");
          
          prog.doQuery();
          break;
      
        case SERVER_ONLY:
          // create cache, create index, create region
          prog.createCache(serverPort);
          prog.createIndexAndRegions(RegionShortcut.PARTITION_PERSISTENT);
          break;
      }
      
      System.out.println("Press any key to exit");
      int c = System.in.read();

    } finally {
      prog.stopServer();
    }
  }

  private void createClientCache() {
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

  private void getRegions() {
    PersonRegion = cache.getRegion("/Person");
    CustomerRegion = cache.getRegion("/Customer");
    PageRegion = cache.getRegion("/Page");
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

    service.createIndex("personIndex", "Person", "name", "email", "address", "revenue");
    // PersonRegion = cache.createRegionFactory(shortcut).create("Person");
    cache.createDiskStoreFactory().create("data");
    PersonRegion = ((Cache)cache).createRegionFactory()
        .setDiskStoreName("data")
        //.setDataPolicy(DataPolicy.PARTITION).create("Person");
        .setDataPolicy(DataPolicy.PERSISTENT_PARTITION).create("Person");

    service.createIndex("customerIndex", "Customer", "symbol", "revenue", "SSN", "name", "email", "address", LuceneService.REGION_VALUE_FIELD);
    CustomerRegion = ((Cache)cache).createRegionFactory(shortcut).create("Customer");

    service.createIndex("pageIndex", "Page", "id", "title", "content");
    PageRegion = ((Cache)cache).createRegionFactory(shortcut).create("Page");
  }

  public void doQuery() throws LuceneQueryException {
    System.out.println("Regular query on standard analyzer:");
    queryByStringQueryParser("personIndex", "Person", "name:Tom99*");
    
    System.out.println("\nUse customized analyzer to tokenize by '_'");
    queryByStringQueryParser("analyzerIndex", "Person", "address:97763");
    System.out.println("\nCompare with standard analyzer");
    queryByStringQueryParser("personIndex", "Person", "address:97763");

    System.out.println("\nsearch region customer for symbol 123 and 456");
    queryByStringQueryParser("customerIndex", "Customer", "symbol:123");
    queryByStringQueryParser("customerIndex", "Customer", "symbol:456");
    
    queryByStringQueryParser("customerIndex", "Customer", "name:Tom99*");
    queryByIntRange("pageIndex", "Page", "id", 100, 102);
    
    System.out.println("\nExamples of QueryProvider");
    queryByIntRange("customerIndex", "Customer", "SSN", 995, Integer.MAX_VALUE);
  }
  
  private void waitUntilFlushed(String indexName, String regionName) {
    LuceneIndexImpl index = (LuceneIndexImpl)service.getIndex(indexName, regionName);
    if (index == null) {
      // it's a client
      return;
    }
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
      PersonRegion.put("key"+i, new Person(i));
    }
    for (int i=0; i<count; i++) {
      CustomerRegion.put("key"+i, new Customer(i));
    }
    for (int i=0; i<count; i++) {
      PageRegion.put("key"+i, new Page(i));
    }

    insertAJson(PersonRegion);
    insertNestObjects(CustomerRegion);
  }

  private void insertPrimitiveTypeValue(Region region) {
    region.put("primitiveInt1", 123);
    region.put("primitiveInt2", "223");
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

    if (results.size() < 20) {
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

  private void queryByStringQueryParser(String indexName, String regionName, String queryString) throws LuceneQueryException {
    System.out.println("\nQuery string is:"+queryString);
    LuceneQuery query = service.createLuceneQueryFactory().create(indexName, regionName, queryString, "name");

    getResults(query, regionName);
  }

  private void queryByIntRange(String indexName, String regionName, String fieldName, int lowerValue, int upperValue) throws LuceneQueryException {
    System.out.println("\nQuery range is:"+fieldName+":["+lowerValue+" TO "+upperValue+"]");
    IntRangeQueryProvider provider = new IntRangeQueryProvider(fieldName, lowerValue, upperValue);
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

//  private static class MyCharacterTokenizer extends CharTokenizer {
//    @Override
//    protected boolean isTokenChar(final int character) {
//      return '_' != character;
//    }
//  }
//
//  private static class MyCharacterAnalyzer extends Analyzer {
//    @Override
//    protected TokenStreamComponents createComponents(final String field) {
//      Tokenizer tokenizer = new MyCharacterTokenizer();
//      TokenStream filter = new LowerCaseFilter(tokenizer);
//      return new TokenStreamComponents(tokenizer, filter);
//    }
//  }
}
