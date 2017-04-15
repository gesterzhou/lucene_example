package objectsize;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.DiskStore;

import org.apache.geode.cache.control.ResourceManager;

import org.apache.geode.distributed.DistributedLockService;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;

import org.apache.geode.internal.cache.CachePerfStats;
import org.apache.geode.internal.cache.DiskRegionStats;
import org.apache.geode.internal.cache.PartitionedRegionStats;

import org.apache.geode.internal.cache.persistence.PersistentMemberManager;

import org.apache.geode.internal.cache.lru.HeapLRUStatistics;

import org.apache.geode.internal.size.ObjectGraphSizer.ObjectFilter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class RegionObjectFilter implements ObjectFilter {

  private boolean logAllClasses = false;

  private boolean logRejectedClasses = false;

  private boolean logAcceptedClasses = false;

  public boolean accept(Object parent, Object object) {
    boolean accept = true;
    String parentClassName = null;
    if (this.logAllClasses || this.logRejectedClasses || this.logAcceptedClasses) {
      if (parent != null) {
        parentClassName = parent.getClass().getName();
      }
    }
    if (object instanceof Cache
        || object instanceof Class
        || object instanceof CachePerfStats
        || object instanceof HeapLRUStatistics
        || object instanceof DiskRegionStats
        || object instanceof DistributedLockService
        || object instanceof PersistentMemberManager
        || object instanceof DistributedMember
        || object instanceof DistributedSystem
        || object instanceof PartitionedRegionStats
        || object instanceof ResourceManager
        || object instanceof ScheduledThreadPoolExecutor
        || object instanceof DiskStore) {
      if (this.logAllClasses || this.logRejectedClasses) {
        System.out.println("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
      }
      accept = false;
    } else {
      if (this.logAllClasses || this.logAcceptedClasses) {
        //System.out.println("Accepting parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + "); object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
        //System.out.println("Accepting object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
        System.out.println("Accepting parent=" + parentClassName + "; object=" + object.getClass().getName());
      }
    }
    return accept;
  }
}
