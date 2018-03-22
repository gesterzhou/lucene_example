package objectsize;

import org.apache.geode.cache.Region;

import org.apache.geode.cache.lucene.LuceneIndex;

import org.apache.geode.cache.wan.GatewaySender;

import org.apache.geode.internal.size.ObjectGraphSizer;
import org.apache.geode.internal.size.ObjectGraphSizer.ObjectFilter;

public class ObjectSizer {

  private static final boolean LOG_SIZE = false;

  public static long calculateSize(Region region, boolean dumpHistogram) {
    long size = 0l;
    ObjectFilter filter = new RegionObjectFilter(region);
    try {
      size = ObjectGraphSizer.size(region, filter, false);
      if (dumpHistogram) {
        dumpHistogram(region, filter);
      }
      if (LOG_SIZE) {
        System.out.println("Size of " + region.getFullPath() + " (an instance of " + region.getClass().getName() + "): " + size);
      }
    } catch (Exception e) {
      System.out.println("Caught the following exception attempting to dump the size of region " + region.getFullPath() + ":" + e);
      e.printStackTrace();
    }
    return size;
  }

  public static long calculateSize(LuceneIndex index, boolean dumpHistogram) {
    long size = 0l;
    ObjectFilter filter = new LuceneIndexObjectFilter(index);
    try {
      size = ObjectGraphSizer.size(index, filter, false);
      if (dumpHistogram) {
        dumpHistogram(index, filter);
      }
      if (LOG_SIZE) {
        System.out.println("Size of " + index.getName() + " (an instance of " + index.getClass().getName() + "): " + size);
      }
    } catch (Exception e) {
      System.out.println("Caught the following exception attempting to dump the size of index " + index.getName() + ":" + e);
      e.printStackTrace();
    }
    return size;
  }

  public static long calculateSize(GatewaySender sender, boolean dumpHistogram) {
    long size = 0l;
    ObjectFilter filter = new GatewaySenderObjectFilter(sender);
    try {
      size = ObjectGraphSizer.size(sender, filter, false);
      if (dumpHistogram) {
        dumpHistogram(sender, filter);
      }
      if (LOG_SIZE) {
        System.out.println("Size of " + sender.getId() + " (an instance of " + sender.getClass().getName() + "): " + size);
      }
    } catch (Exception e) {
      System.out.println("Caught the following exception attempting to dump the size of index " + sender.getId() + ":" + e);
      e.printStackTrace();
    }
    return size;
  }

  private static void dumpHistogram(Object obj, ObjectFilter filter) throws IllegalAccessException {
    System.out.println("Histogram for " + obj + " (an instance of " + obj.getClass().getName() + ")");
    System.out.println(ObjectGraphSizer.histogram(obj, filter, false));
  }
}
