package examples;

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

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.DataPolicy;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionFactory;
import com.gemstone.gemfire.cache.RegionShortcut;
import com.gemstone.gemfire.cache.lucene.LuceneIndex;
import com.gemstone.gemfire.cache.lucene.LuceneQuery;
import com.gemstone.gemfire.cache.lucene.LuceneQueryException;
import com.gemstone.gemfire.cache.lucene.LuceneResultStruct;
import com.gemstone.gemfire.cache.lucene.LuceneServiceProvider;
import com.gemstone.gemfire.cache.lucene.PageableLuceneQueryResults;
//import com.gemstone.gemfire.cache.lucene.internal.FSRepositoryManagerFactory;
import com.gemstone.gemfire.cache.lucene.internal.LuceneIndexForPartitionedRegion;
import com.gemstone.gemfire.cache.lucene.internal.LuceneIndexImpl;
import com.gemstone.gemfire.cache.lucene.internal.LuceneRawIndexFactory;
import com.gemstone.gemfire.cache.lucene.internal.LuceneServiceImpl;
import com.gemstone.gemfire.distributed.ServerLauncher;
import com.gemstone.gemfire.internal.logging.LogService;
import com.gemstone.gemfire.pdx.JSONFormatter;
import com.gemstone.gemfire.pdx.PdxInstance;

public class Main {
  // private final Version version = Version.LUCENE_5_3_0;
  ServerLauncher serverLauncher;
  Cache cache;
  Region PersonRegion;
  Region CustomerRegion;
  Region PageRegion;
  LuceneServiceImpl service;
  
  final static int ENTRY_COUNT = 1000;
  final static Logger logger = LogService.getLogger();

  public static void main(final String[] args) throws LuceneQueryException {
    System.out.println("There are "+args.length+" arguments.");
    for (int i=0; i<args.length; i++) {
      System.out.println("arg"+i+":"+args[i]);
    }

    Main prog = new Main();
    try {
//    System.setProperty("gemfire.NotUsing.RegionDirectory", "true");
    prog.createCache(40405);
    
    // note: we have to create lucene index before the region
    prog.createIndexAndRegions(RegionShortcut.PARTITION_PERSISTENT);
    prog.feed(ENTRY_COUNT);
    prog.waitUntilFlushed("personIndex", "Person");
    prog.waitUntilFlushed("customerIndex", "Customer");
    
    // now let's do search on lucene index
    prog.doSearch("personIndex", "Person", "name:Tom9*");
    prog.doSearch("customerIndex", "Customer", "name:Tom123");
    prog.doSearch("customerIndex", "Customer", "symbol:456");
    prog.doSearch("customerIndex", "Customer", "SSN:123");
    prog.doSearch("personIndex", "Person", "name:Tom999*");
//      prog.doSearch("customerIndex", "Customer", "name:Tom*");
//      prog.doSearch("pageIndex", "Page", "id:10");
    
    prog.feedAndDoSpecialSearch("analyzerIndex", "Person");
    
    } finally {
      prog.stopServer();
    }
  }
  
  private void createCache(int port) {
    if (cache != null) {
      return;
    }
    
    serverLauncher  = new ServerLauncher.Builder()
    .setMemberName("server1")
    .setServerPort(port)
    .setPdxPersistent(true)
    .set("mcast-port", "0")
    .set("enable-time-statistics","true")
    .set("statistic-sample-rate","1000")
    .set("statistic-sampling-enabled", "true")
    .set("statistic-archive-file", "server1.gfs")
//    .set("log-level", "debug")
    .build();

    serverLauncher.start();
    System.out.println("Server started!");
    
    cache = CacheFactory.getAnyInstance();
    service = (LuceneServiceImpl) LuceneServiceProvider.get(cache);
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

    LuceneServiceImpl.luceneIndexFactory = new LuceneRawIndexFactory();
//    LuceneIndexForPartitionedRegion.partitionedRepositoryManagerFactory = new FSRepositoryManagerFactory();
    service.createIndex("personIndex", "Person", "name", "email", "address");
    // PersonRegion = cache.createRegionFactory(shortcut).create("Person");
    cache.createDiskStoreFactory().create("data");
    PersonRegion = cache.createRegionFactory()
        .setDiskStoreName("data")
        //.setDataPolicy(DataPolicy.PARTITION).create("Person");
    .setDataPolicy(DataPolicy.PERSISTENT_PARTITION).create("Person");
    
    service.createIndex("customerIndex", "Customer", "symbol", "revenue", "SSN", "name", "email", "address");
    CustomerRegion = cache.createRegionFactory(shortcut).create("Customer");
    
    service.createIndex("pageIndex", "Page", "symbol", /*"revenue",*/ "name", "email", "address");
    PageRegion = cache.createRegionFactory(shortcut).create("Page");
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
	
	index.dumpFiles("dump");
//    String aeqId = LuceneServiceImpl.getUniqueIndexName(indexName, regionName);
//    AsyncEventQueueImpl queue = (AsyncEventQueueImpl)cache.getAsyncEventQueue(aeqId);
//    GatewaySender sender = queue.getSender();
//    sender.resume();
//    Awaitility.waitAtMost(30, TimeUnit.SECONDS).until(() -> { return (0==queue.size()); });
  }
  
  private void defineIndexAndRegion(String indexName, String regionName, RegionShortcut regionType, String...fields) {
    service.createIndex(indexName, regionName, fields);
  }
  
  private Region createRegion(String regionName, RegionShortcut regionType) {
    RegionFactory<Object, Object> regionFactory = cache.createRegionFactory(regionType);
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
  }

  private void doSearch(String indexName, String regionName, String queryString) throws LuceneQueryException {
    LuceneQuery query = getLuceneQuery(indexName, regionName, queryString);
    if (query == null) {
      return;
    }
    
    PageableLuceneQueryResults<String, Object> results = query.findPages();
    if (results.size() >0 ) {
      System.out.println("Search found "+results.size()+" rows in "+regionName);
    }
    
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
        });
      cnt.incrementAndGet();
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

  private LuceneQuery getLuceneQuery(String indexName, String regionName, String queryString) {
    LuceneQuery query = service.createLuceneQueryFactory().create(indexName, regionName, queryString, "name");
    return query;
  }
  
  private void verifyQuery(String indexName, String regionName, String queryString, String... expectedKeys) throws LuceneQueryException {
    LuceneQuery query = getLuceneQuery(indexName, regionName, queryString);
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
