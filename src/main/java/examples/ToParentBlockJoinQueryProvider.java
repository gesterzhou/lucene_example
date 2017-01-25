package examples;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
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
public class ToParentBlockJoinQueryProvider implements LuceneQueryProvider {
  String parentFilterField;
  String parentFilterString;
  String queryOnChild;
  String defaultFieldOnChild;
  
  private transient Query luceneQuery;

  public ToParentBlockJoinQueryProvider(String parentFilterField, String parentFilterString, String queryOnChild, 
      String defaultFieldOnChild) {
    this.parentFilterField = parentFilterField;
    this.parentFilterString = parentFilterString;
    this.queryOnChild = queryOnChild;
    this.defaultFieldOnChild = defaultFieldOnChild;
  }
  
  @Override
  public Query getQuery(LuceneIndex index) throws LuceneQueryException {
    if (luceneQuery == null) {
      final StandardQueryParser queryParser = new StandardQueryParser(new KeywordAnalyzer());
      Query childQuery = null;
      try {
        childQuery = queryParser.parse(queryOnChild, defaultFieldOnChild);
        System.out.println("childQuery is:"+childQuery);
      }
      catch (QueryNodeException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      BitSetProducer parentFilter = new QueryBitSetProducer(new WildcardQuery(new Term(parentFilterField, parentFilterString)));
      luceneQuery = new ToParentBlockJoinQuery(childQuery, parentFilter, ScoreMode.Total);
    }
    return luceneQuery;
  }

}
