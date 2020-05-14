package examples;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.phonetic.DoubleMetaphoneFilter;

public class DoubleMetaphoneAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer tokenizer = new StandardTokenizer();
    TokenStream stream = new DoubleMetaphoneFilter(tokenizer, 6, false);
    return new TokenStreamComponents(tokenizer, stream);
  }

}
