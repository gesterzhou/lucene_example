package examples;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.util.CharTokenizer;

public class MyCharacterAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer tokenizer = new MyCharacterTokenizer();
    TokenStream filter = new LowerCaseFilter(tokenizer);
    return new TokenStreamComponents(tokenizer, filter);
  }

  private static class MyCharacterTokenizer extends CharTokenizer {
    @Override
    protected boolean isTokenChar(final int character) {
      return '_' != character;
    }
  }
}
