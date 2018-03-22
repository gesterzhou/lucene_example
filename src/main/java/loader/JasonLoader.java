package loader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.geode.cache.Region;

import org.apache.geode.pdx.JSONFormatter;

import java.io.File;

public class JasonLoader {
  public static void load(String fileName, Region region) throws Exception {
    int numEntries = 0;
    long start=0, end=0, startTotal=0, endTotal=0;
    startTotal = System.currentTimeMillis();
    start = System.currentTimeMillis();

    ObjectMapper mapper = new ObjectMapper();
    JsonParser parser = mapper.getFactory().createParser(new File(fileName));

    if (parser.nextToken() != JsonToken.START_ARRAY) {
      throw new IllegalStateException("The first token should be the start of an array");
    }

    while(parser.nextToken() == JsonToken.START_OBJECT) {
      ObjectNode node = mapper.readTree(parser);
      //String nodeStr = mapper.writeValueAsString(node);
      Object key = node.findValue("Unique_Key").asLong();
      Object value = JSONFormatter.fromJSON(node.toString());
      System.out.println("GGG:"+key);
      System.out.println(""+value);
//      region.put(key, value);
      numEntries++;
    }

    endTotal = System.currentTimeMillis();
    System.out.println("Loaded " + numEntries + " json entries in " + (endTotal-startTotal) + " ms");
  }
}
