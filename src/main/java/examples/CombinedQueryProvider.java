package examples;

import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class CombinedQueryProvider implements LuceneQueryProvider {
  String fieldName;
  int lowerValue;
  int upperValue;

  private transient Query luceneQuery;

  public CombinedQueryProvider(String fieldName, int lowerValue, int upperValue) {
    this.fieldName = fieldName;
    this.lowerValue = lowerValue;
    this.upperValue = upperValue;
  }

  @Override
  public Query getQuery(LuceneIndex index) throws LuceneQueryException {
    Query standardStringQuery = null;
    if (luceneQuery != null) {
      return luceneQuery;
    }

    try {
      StandardQueryParser parser = new StandardQueryParser();
//                                  parser.setAllowLeadingWildcard(true);
      standardStringQuery = parser.parse("PivotalPageManager99*", "title");
    } catch (QueryNodeException e) {
    }
    luceneQuery = new BooleanQuery.Builder()
            .add(standardStringQuery, BooleanClause.Occur.MUST)
            .add(IntPoint.newRangeQuery(fieldName, lowerValue, upperValue), BooleanClause.Occur.MUST)
            .build();
    System.out.println("CombinedQueryProvider, using java serializable");
    return luceneQuery;
  }
}
