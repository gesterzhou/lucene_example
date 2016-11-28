package examples;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.geode.DataSerializer;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.internal.DataSerializableFixedID;
import org.apache.geode.internal.Version;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.Query;

public class IntegerRangeQueryProvider
implements LuceneQueryProvider, DataSerializableFixedID {
  public static final short LUCENE_INTEGER_RANGE_QUERY_PROVIDER = 3001;
  String fieldName;
  int lowerValue;
  int upperValue;

  private transient Query luceneQuery;

  public IntegerRangeQueryProvider(String fieldName, int lowerValue, int upperValue) {
    this.fieldName = fieldName;
    this.lowerValue = lowerValue;
    this.upperValue = upperValue;
  }

  public IntegerRangeQueryProvider() {}

  @Override
  public int getDSFID() {
    System.out.println("GGG:"+3001);
    return LUCENE_INTEGER_RANGE_QUERY_PROVIDER;
  }

  @Override
  public Version[] getSerializationVersions() {
    return null;
  }

  @Override
  public void toData(DataOutput out) throws IOException {
    DataSerializer.writeString(fieldName, out);
    out.writeInt(lowerValue);
    out.writeInt(upperValue);
  }

  @Override
  public void fromData(DataInput in) throws IOException, ClassNotFoundException {
    fieldName = DataSerializer.readString(in);
    lowerValue = in.readInt();
    upperValue = in.readInt();
  }

  @Override
  public Query getQuery(LuceneIndex index) throws LuceneQueryException {
    if (luceneQuery == null) {
      luceneQuery = IntPoint.newRangeQuery(fieldName, lowerValue, upperValue);
    }
    System.out.println("GGG:myquery");
    return luceneQuery;
  }
}

