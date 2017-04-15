package objectsize;

import org.apache.geode.cache.Region;

import org.apache.geode.internal.size.ObjectGraphSizer;
import org.apache.geode.internal.size.ObjectGraphSizer.ObjectFilter;

public class ObjectSizer {

  private static final boolean LOG_SIZE = false;

  public static long calculateSize(Region region, boolean dumpHistogram) {
    long size = 0l;
    ObjectFilter filter = new RegionObjectFilter();
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
    }
    return size;
  }

  private static void dumpHistogram(Object obj, ObjectFilter filter) throws IllegalAccessException {
    System.out.println("Histogram for " + obj + " (an instance of " + obj.getClass().getName() + ")");
    System.out.println(ObjectGraphSizer.histogram(obj, filter, false));
  }
}
