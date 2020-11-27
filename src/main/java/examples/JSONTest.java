package examples;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneServiceProvider;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;
import org.apache.geode.cache.lucene.internal.LuceneIndexImpl;
import org.apache.geode.cache.lucene.internal.LuceneServiceImpl;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.TypeMismatchException;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class JSONTest {
  //NOTE: Below is truncated json, single json document can max contain an array of col1...col30 (30 diff attributes)
  // within data.
  public final static String jsonDoc_2 = "{" +
          "\"data\":[{" +
          "\"col1\": {" +
          "\"k11\": \"aaa\"," +
          "\"k12\":true," +
          "\"k13\": 1111," +
          "\"k14\": \"2020-12-31:00:00:00\"" +
          "}," +
          "\"col2\":[{" +
          "\"k21\": \"222222\"," +
          "\"k22\": true" +
          "}]" +
          "}]" +
          "}";
  public final static String jsonDoc_3 = "{" +
          "\"data\":[{" +
          "\"col1\": {" +
          "\"k11\": \"bbb\"," +
          "\"k12\":true," +
          "\"k13\": 1111," +
          "\"k14\": \"2020-12-31:00:00:00\"" +
          "}," +
          "\"col2\":[{" +
          "\"k21\": \"333333\"," +
          "\"k22\": true" +
          "}]" +
          "}]" +
          "}";

  //NOTE: Col1....col30 are mix of JSONObject ({}) and JSONArray  ([]) as shown above in jsonDoc_2;

  public final static String REGION_NAME = "JSONRegion";

  static ClientCache cache = null;

  public static void main(String[] args) throws InterruptedException, LuceneQueryException, NameResolutionException, TypeMismatchException, QueryInvocationTargetException, FunctionDomainException {

    //create client-cache
    cache = new
            ClientCacheFactory().addPoolLocator("localhost", 10334).setPdxReadSerialized(true).create();
    Region<String, PdxInstance> region = cache.<String,
            PdxInstance>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
            .create(REGION_NAME);

    //store json document
    region.put("key", JSONFormatter.fromJSON(jsonDoc_2));
    region.put("key3", JSONFormatter.fromJSON(jsonDoc_3));

    LuceneServiceImpl service = (LuceneServiceImpl) LuceneServiceProvider.get(cache);
    LuceneIndexImpl index = (LuceneIndexImpl) service.getIndex("jsonIndex", "JSONRegion");
    if (index != null) {
      service.waitUntilFlushed("jsonIndex", "JSONRegion", 60000, TimeUnit.MILLISECONDS);
    }

    LuceneQuery query = service.createLuceneQueryFactory().create("jsonIndex", "JSONRegion",
            "222222 OR 333333", "data.col2.k21");
    System.out.println("Query 222222 OR 333333");
    HashSet results = getResults(query, "JSONRegion");

    LuceneQuery query2 = service.createLuceneQueryFactory().create("jsonIndex", "JSONRegion",
            "aaa OR xxx OR yyy", "data.col1.k11");
    System.out.println("Query aaa OR xxx OR yyy");
    results = getResults(query2, "JSONRegion");

    // server side:
    // gfsh> start locator
    // gfsh> start server --name=server50505 --server-port=50505
    // gfsh> create lucene index --name=jsonIndex --region=/JSONRegion --field=data.col2.k21,data.col1.k11
    // --serializer=org.apache.geode.cache.lucene.FlatFormatSerializer
    // gfsh> create region --name=JSONRegion --type=PARTITION --redundant-copies=1 --total-num-buckets=61

    // How to query json document like,

    // 1. select col2.k21, col1, col20 from /JSONRegion where
    //data.col2.k21 = '222222' OR data.col2.k21 = '333333'

    // 2. select col2.k21, col1.k11, col1 from /JSONRegion where
    // data.col1.k11 in ('aaa', 'xxx', 'yyy')

    // OQL examples
    doOQLQuery("SELECT d.col1 FROM /JSONRegion v, v.data d where d.col1.k11 = 'aaa'");
    doOQLQuery("SELECT d.col2 FROM /JSONRegion v, v.data d, d.col2 c where c.k21 in SET('222222','333333')");
  }

  static void doOQLQuery(String queryStr) throws NameResolutionException, TypeMismatchException, QueryInvocationTargetException, FunctionDomainException {
    System.out.println("Query String is: " + queryStr);
    QueryService queryService = cache.getQueryService();
    Query oqlQuery = queryService.newQuery(queryStr);
    SelectResults selectResults = (SelectResults) oqlQuery.execute();
    Iterator itor = selectResults.iterator();
    while (itor.hasNext()) {
      System.out.println("OQL found:" + itor.next());
    }
  }

  private static HashSet getResults(LuceneQuery query, String regionName) throws LuceneQueryException {
    if (query == null) {
      return null;
    }

    PageableLuceneQueryResults<Object, Object> results = query.findPages();
    if (results.size() > 0) {
      System.out.println("Search found " + results.size() + " results in " + regionName + ", page size is " + query.getPageSize());
    }

    HashSet values = new HashSet<>();
    while (results.hasNext()) {
      results.next().stream()
              .forEach(struct -> {
                Object value = struct.getValue();
                if (value instanceof PdxInstance) {
                  PdxInstance pdx = (PdxInstance) value;
                  String jsonString = JSONFormatter.toJSON(pdx);
                  List<PdxInstance> dataList = (LinkedList<PdxInstance>) pdx.getField("data");
                  for (PdxInstance data : dataList) {
                    Object colObject = ((PdxInstance) data).getField("col1");
                    System.out.println("col=" + colObject);
                  }
                  System.out.println("Found a json object:" + jsonString + ":dataList=" + dataList);
                  values.add(pdx);
                } else {
                  System.out.println("key=" + struct.getKey() + ",data=" + value);
                  values.add(value);
                }
              });
    }
    System.out.println("Search found " + values.size() + " results in " + regionName);
    return values;
  }
}
