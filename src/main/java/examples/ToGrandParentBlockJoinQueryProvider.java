package examples;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.join.BitSetProducer;
import org.apache.lucene.search.join.QueryBitSetProducer;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;
import org.apache.geode.DataSerializer;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.internal.DataSerializableFixedID;
import org.apache.geode.internal.Version;

/* Example: 
 * 
 */
public class ToGrandParentBlockJoinQueryProvider implements LuceneQueryProvider {
  String parentDefaultField;
  String childDefaultField;
  String grandChildDefaultField;
  String queryOnChild;
  String queryOnGrandChild;
  
  private transient Query luceneQuery;

  public ToGrandParentBlockJoinQueryProvider(String parentDefaultField, String childDefaultField, 
      String grandChildDefaultField, String queryOnChild, String queryOnGrandChild) {
    this.parentDefaultField = parentDefaultField;
    this.childDefaultField = childDefaultField;
    this.grandChildDefaultField = grandChildDefaultField;
    this.queryOnChild = queryOnChild;
    this.queryOnGrandChild = queryOnGrandChild;
  }
  
  @Override
  public Query getQuery(LuceneIndex index) throws LuceneQueryException {
    if (luceneQuery == null) {
      StandardQueryParser queryParser = new StandardQueryParser();
      Query grandChildQuery = null;
      Query childQuery = null;
      try {
        grandChildQuery = queryParser.parse(queryOnGrandChild, grandChildDefaultField);
        childQuery = queryParser.parse(queryOnChild, childDefaultField);
        System.out.println("childQuery is:"+childQuery+":grandChildQuery is:"+grandChildQuery);
      }
      catch (QueryNodeException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      BitSetProducer childFilter = new QueryBitSetProducer(new WildcardQuery(new Term(childDefaultField, "*")));     
      BitSetProducer parentFilter = new QueryBitSetProducer(new WildcardQuery(new Term(parentDefaultField, "*")));
      ToParentBlockJoinQuery grandToChildJoinQuery = new ToParentBlockJoinQuery(grandChildQuery, childFilter, ScoreMode.Max);

      BooleanQuery.Builder builder = new Builder();
      builder.add(grandToChildJoinQuery, Occur.MUST);
      builder.add(childQuery, Occur.MUST);
      luceneQuery = new ToParentBlockJoinQuery(builder.build(), parentFilter, ScoreMode.Max);
    }
    return luceneQuery;
  }

}
