package examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.logging.internal.log4j.api.LogService;
import org.apache.logging.log4j.Logger;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolManager;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneServiceProvider;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;
import org.apache.geode.cache.lucene.internal.LuceneIndexForPartitionedRegion;
import org.apache.geode.cache.lucene.internal.LuceneIndexImpl;
import org.apache.geode.cache.lucene.internal.LuceneServiceImpl;
import org.apache.geode.distributed.ServerLauncher;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class ClientMain {
  ServerLauncher serverLauncher;
  GemFireCache cache;
  Region PageRegion;
  LuceneServiceImpl service;
  static int serverPort = 50505;
  static boolean useLocator = false;

  final static int ENTRY_COUNT = 10000;
  final static Logger logger = LogService.getLogger();
  final static int SERVER_WITH_FEEDER = 1;
  final static int CLIENT = 3;
  final static int SERVER_WITH_CLUSTER_CONFIG = 4;
  static int instanceType = SERVER_WITH_FEEDER;

  // test different numeric type
  final String[] pageIndexFields = {"id", "title", "content"};

  public static void main(final String[] args) throws LuceneQueryException, IOException, InterruptedException, java.text.ParseException, QueryNodeException {
    ClientMain prog = new ClientMain();

    prog.createClientCache();
    prog.feed(ENTRY_COUNT);
    prog.doQueryOnPage();
//    prog.doClientFunction();

    System.out.println("Press any key to exit");
    int c = System.in.read();
  }

  private void createClientCache() {
    ClientCacheFactory cf = new ClientCacheFactory().addPoolLocator("localhost", 12345);
    cache = cf.setPdxReadSerialized(true).create();
    ClientRegionFactory crf = ((ClientCache) cache).createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
    PageRegion = crf.create("Page");

    service = (LuceneServiceImpl) LuceneServiceProvider.get(cache);
//    service.LUCENE_REINDEX = true;
  }

  private void waitUntilFlushed(String indexName, String regionName) throws InterruptedException {
    LuceneIndexImpl index = (LuceneIndexImpl)service.getIndex(indexName, regionName);
    if (index == null) {
      // it's a client
      return;
    }
    boolean status = false;
    long then = System.currentTimeMillis();
    do {
      status = service.waitUntilFlushed(indexName, regionName, 60000, TimeUnit.MILLISECONDS);
    } while (status == false);
    System.out.println("wait time after feed is:"+(System.currentTimeMillis() - then));
  }

  private Region createRegion(RegionShortcut shortcut, String regionName) {
    Region region = ((Cache) cache).createRegionFactory(shortcut).create(regionName);
    return region;
  }

  public void doASimpleQuery() throws LuceneQueryException {
    LuceneIndexImpl index = (LuceneIndexImpl) service.getIndex("personIndex", "Person");
    if (index == null) {
      // it's a client
      return;
    }

    System.out.println("Regular query on standard analyzer:");
    queryByStringQueryParser("personIndex", "Person", "name:Tom999*", 5, "name");
  }

  private void feed(int count) {
    for (int i = 0; i < count; i++) {
      PageRegion.put("key" + i, new Page(i));
    }
  }

  public void doQueryOnPage() {
    try {
      LuceneQuery<String, Page> luceneQuery = service.createLuceneQueryFactory().setLimit(50)
              .create("pageIndex", "Page", "PivotalPageManager99*", "title");

      Collection<Page> allManagersPages = luceneQuery.findValues();
      System.out.println("All managers' pages =>\n" + allManagersPages);
    } catch (Exception e) {
      System.out.println("Case 1 Exception:");
      System.out.println(e.getMessage());
    }

    try {
      IntRangeQueryProvider provider = new IntRangeQueryProvider("id", 9960, 9980);
      LuceneQuery luceneQuery = service.createLuceneQueryFactory().create("pageIndex", "Page", provider);
      Collection<Page> allPages = luceneQuery.findValues();
      System.out.println("\nAll pages for id 9960-9980 using predefined QueryProvider =>\n" + allPages);
    } catch (Exception e) {
      System.out.println("Case 2 Exception:");
      System.out.println(e.getMessage());
    }

    try {
      LuceneQuery<String, Page> luceneQuery = service.createLuceneQueryFactory().setLimit(10)
              .create("pageIndex", "Page", (LuceneIndex index) -> {

                LuceneIndexImpl indexImpl = (LuceneIndexImpl) index;
                StandardQueryParser parser = new StandardQueryParser(((LuceneIndexImpl) index).getAnalyzer());
                parser.setAllowLeadingWildcard(true);
                try {
                  return parser.parse("PivotalPageManager99*", "title");
                } catch (QueryNodeException e) {
                }
                return null;
              });

      Collection<Page> allManagersPages = luceneQuery.findValues();
      System.out.println("\nAll managers' pages using lambda provider on title =>\n" + allManagersPages);
    } catch (Exception e) {
      System.out.println("Case 3 Exception:");
      e.printStackTrace();
    }

    try {
      LuceneQuery<String, Page> luceneQuery = service.createLuceneQueryFactory().setLimit(10)
              .create("pageIndex", "Page", index -> {
                return IntPoint.newRangeQuery("id", 9960, 9980);
              });

      Collection<Page> allManagersPages = luceneQuery.findValues();
      System.out.println("\nAll managers' pages using lambda provider on id =>\n" + allManagersPages);
    } catch (Exception e) {
      System.out.println("Case 4 Exception:");
      System.out.println(e.getMessage());
    }

    try {
      LuceneQuery<String, Customer> luceneQuery =
              service.createLuceneQueryFactory().setLimit(10)
                      .create("pageIndex", "Page",
                              index -> {
                                Query standardStringQuery = null;
                                try {
                                  StandardQueryParser parser = new StandardQueryParser();
//                                  parser.setAllowLeadingWildcard(true);
                                  standardStringQuery = parser.parse("PivotalPageManager99*", "title");
                                } catch (QueryNodeException e) {
                                }
                                return new BooleanQuery.Builder()
                                        .add(standardStringQuery, BooleanClause.Occur.MUST)
                                        .add(IntPoint.newRangeQuery("id", 9960, 9980), BooleanClause.Occur.MUST)
                                        .build();
                              });
      Collection<Customer> allManagersPages = luceneQuery.findValues();
      System.out.println("\nAll managers' pages using combined provider =>\n" + allManagersPages);
    } catch (Exception e) {
      System.out.println("Case 5 Exception:");
      System.out.println(e.getMessage());
    }

    try {
      StandardQueryParser parser = new StandardQueryParser();
      Query standardStringQuery = parser.parse("PivotalPageManager99*", "title");

      LuceneQuery<String, Customer> luceneQuery =
              service.createLuceneQueryFactory().setLimit(10)
                      .create("pageIndex", "Page",
                              index -> {
                                return new BooleanQuery.Builder()
                                        .add(standardStringQuery, BooleanClause.Occur.MUST)
                                        .add(IntPoint.newRangeQuery("id", 9970, 9990), BooleanClause.Occur.MUST)
                                        .build();
                              });
      Collection<Customer> allManagersPages = luceneQuery.findValues();
      System.out.println("\nAll managers' pages when parser in moved out of lambda =>\n" + allManagersPages);
    } catch (Exception e) {
      System.out.println("Case 6 Exception:");
      System.out.println(e.getMessage());
    }

    try {
      CombinedQueryProvider provider = new CombinedQueryProvider("id", 9960, 9980);
      LuceneQuery luceneQuery = service.createLuceneQueryFactory().create("pageIndex", "Page", provider);
      Collection<Page> allPages = luceneQuery.findValues();
      System.out.println("\nAll pages for id 9960-9980 using predefined CombinedQueryProvider =>\n" + allPages);
    } catch (Exception e) {
      System.out.println("Case 7 Exception:");
      System.out.println(e.getMessage());
    }
  }

  public void doClientFunction() {
    System.out.println("\nCalling function from a client");
    Pool pool = PoolManager.createFactory().addLocator("localhost", 12345).create("clientPool");

    LuceneQueryInfo queryInfo = new LuceneQueryInfo("personIndex", "Person", "name:Tom99*", "name", -1, false);
    Execution execution = FunctionService.onServer(pool).withArgs(queryInfo);
    // Client code can call function via its name, it does not need to know function class object
    ResultCollector<?, ?> rc = execution.execute("LuceneSearchIndexFunction");
    displayResults(rc);

    {
      String parametersForREST = "personIndex,Person,name:Tom99*,name,-1,false";
      System.out.println("Paramter is: " + parametersForREST);
      execution = FunctionService.onServer(pool).withArgs(parametersForREST);
      rc = execution.execute("LuceneSearchIndexFunction");
      displayResults(rc);
    }

    {
      String parametersForREST = "personIndex,Person,name:Tom99*,name,-1,true";
      System.out.println(parametersForREST);
      execution = FunctionService.onServer(pool).withArgs(parametersForREST);
      rc = execution.execute("LuceneSearchIndexFunction");
      displayResults(rc);
    }

    queryInfo = new LuceneQueryInfo("analyzerIndex", "/Person", "address:97763", "name", -1, false);
    execution = FunctionService.onServer(pool).withArgs(queryInfo);
    rc = execution.execute("LuceneSearchIndexFunction");
    displayResults(rc);
  }

  private void displayResults(ResultCollector<?, ?> rc) {
//    List<Set<LuceneSearchResults>> functionResults = (List<Set<LuceneSearchResults>>) rc.getResult();
//    List<LuceneSearchResults> results = functionResults.stream().flatMap(set -> set.stream()).sorted()
//        .collect(Collectors.toList());
    ArrayList functionResults = (ArrayList) ((ArrayList) rc.getResult()).get(0);

    System.out.println("\nClient Function found " + functionResults.size() + " results");
    functionResults.stream().forEach(result -> {
      System.out.println(result);
    });
  }

  private void doDump(String indexName, String regionName) {
    LuceneIndexForPartitionedRegion index = (LuceneIndexForPartitionedRegion) service.getIndex(indexName, regionName);
    if (index == null) {
      return;
    }
    index.dumpFiles("dump" + indexName);
  }

  private HashSet getResults(LuceneQuery query, String regionName) throws LuceneQueryException {
    if (query == null) {
      return null;
    }

    PageableLuceneQueryResults<Object, Object> results = query.findPages();
    if (results.size() > 0) {
      System.out.println("Search found " + results.size() + " results in " + regionName + ", page size is " + query.getPageSize());
    }

    HashSet values = new HashSet<>();
    int pageno = 0;
    if (results.size() < 20) {
      final AtomicInteger cnt = new AtomicInteger(0);
      while (results.hasNext()) {
        if (query.getPageSize() != 0) {
          System.out.println("Page:" + pageno + " starts here ------------");
        }
        results.next().stream()
                .forEach(struct -> {
                  Object value = struct.getValue();
                  if (value instanceof PdxInstance) {
                    PdxInstance pdx = (PdxInstance) value;
                    Object revenueObj = pdx.getField("revenue");
                    String jsonString = JSONFormatter.toJSON(pdx);
                    System.out.println("Found a json object:" + jsonString);
                    values.add(pdx);
                  } else {
                    System.out.println("No: " + cnt.get() + ":key=" + struct.getKey() + ",value=" + value + ",score=" + struct.getScore());
                    values.add(value);
                  }
                  cnt.incrementAndGet();
                });
        if (query.getPageSize() != 0) {
          System.out.println("Page:" + pageno + " ends here, press any key to show next page ------------");
          try {
            int c = System.in.read();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        pageno++;
      }
    }
    System.out.println("Search found " + values.size() + " results in " + regionName);
    return values;
  }

  private HashSet queryByStringQueryParser(String indexName, String regionName, String queryString, int pageSize, String defaultField) throws LuceneQueryException {
    System.out.println("\nQuery string is " + queryString + ", default field is " + defaultField);
    HashSet results = null;
    long then = System.currentTimeMillis();
//  for (int i=0; i<100; i++) {

    LuceneQuery query = service.createLuceneQueryFactory().setPageSize(pageSize).create(indexName, regionName, queryString, defaultField);

    results = getResults(query, regionName);
//  }
    System.out.println("Query took " + (System.currentTimeMillis() - then));
    return results;
  }

  private void queryByIntRange(String indexName, String regionName, String fieldName, int lowerValue, int upperValue) throws LuceneQueryException {
    // Note: IntRangeClientQueryProvider does not exist at server
    System.out.println("\nQuery range is:" + fieldName + ":[" + lowerValue + " TO " + upperValue + "]");
    long then = System.currentTimeMillis();
    IntRangeQueryProvider provider = new IntRangeQueryProvider(fieldName, lowerValue, upperValue);
    LuceneQuery query = service.createLuceneQueryFactory().create(indexName, regionName, provider);

    HashSet results = getResults(query, regionName);
    System.out.println("Query took " + (System.currentTimeMillis() - then));

    // Note: the anonymous provider does not exist at server
    LuceneQuery query2 = service.createLuceneQueryFactory().create(indexName, regionName, index -> {
      System.out.println("Where am I?");
      return IntPoint.newRangeQuery(fieldName, lowerValue, upperValue);
    });
    HashSet results2 = getResults(query2, regionName);
    System.out.println("Query2 took " + (System.currentTimeMillis() - then));
    // Note: to workaround the query, need to call a function at server, define the provider and query in function
  }

}
