package objectsize;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.Region;

import org.apache.geode.cache.control.ResourceManager;

import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.internal.LuceneIndexImpl;
import org.apache.geode.cache.lucene.internal.LuceneIndexStats;

import org.apache.geode.cache.lucene.internal.filesystem.FileSystemStats;

import org.apache.geode.distributed.DistributedLockService;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;

import org.apache.geode.distributed.internal.DistributionManager;

import org.apache.geode.internal.cache.BucketAdvisor;
import org.apache.geode.internal.cache.CachePerfStats;
import org.apache.geode.internal.cache.DiskRegionStats;
import org.apache.geode.internal.cache.DistributedRegion;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.PartitionedRegionStats;
import org.apache.geode.internal.cache.PRHARedundancyProvider;

import org.apache.geode.internal.cache.partitioned.RegionAdvisor;

import org.apache.geode.internal.cache.persistence.PersistentMemberManager;

import org.apache.geode.internal.cache.eviction.EvictionStatistics;

import org.apache.geode.internal.size.ObjectGraphSizer.ObjectFilter;

import org.apache.logging.log4j.Logger;

import java.security.AccessControlContext;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class LuceneIndexObjectFilter implements ObjectFilter {
  
	private boolean logAllClasses = false;
	
	private boolean logRejectedClasses = false;
	
	private boolean logAcceptedClasses = false;
	
	private LuceneIndexImpl index;
	
	private Cache cache;
	
	public LuceneIndexObjectFilter(LuceneIndex index) {
	  this.index = (LuceneIndexImpl) index;
	  this.cache = this.index.getCache();
	}
	
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
				|| object instanceof EvictionStatistics
				|| object instanceof DiskRegionStats
				|| object instanceof DistributedLockService
				|| object instanceof PersistentMemberManager
				|| object instanceof DistributedMember
				|| object instanceof DistributionManager
				|| object instanceof DistributedSystem
				|| object instanceof PartitionedRegionStats
				|| object instanceof ResourceManager
				|| object instanceof ScheduledThreadPoolExecutor
				//|| object instanceof DiskStore
				|| object instanceof Logger
				|| object instanceof FileSystemStats
				|| object instanceof LuceneIndexStats
        || object instanceof ClassLoader 
        || object instanceof AccessControlContext
        || object instanceof ThreadGroup
        || object instanceof RegionAdvisor
        || object instanceof BucketAdvisor
				) {
			if (this.logAllClasses || this.logRejectedClasses) {
			  log("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
			}
			accept = false;
		} else if (parent instanceof PartitionedRegion 
			&& (object instanceof CopyOnWriteArrayList // the colocatedByList field
				|| object instanceof DistributedRegion // the prRoot field (PartitionedRegion called _PR)
				|| object instanceof PartitionedRegion // is the colocatedWithRegion field
				|| object instanceof PRHARedundancyProvider // is the redundancyProvider field
				)) {
			if (this.logAllClasses || this.logRejectedClasses) {
			  //log("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
			  log("Rejecting parent=" + parentClassName + "; object=" + object);
			}
			accept = false;
		/*} else if (parent instanceof BucketAdvisor && object instanceof BucketAdvisor) {
		  // the parentAdvisor field
			if (this.logAllClasses || this.logRejectedClasses) {
			  log("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
			}
			accept = false;*/
		} else if (parent instanceof LuceneIndex && object instanceof Region) {
		  Region potentialDataRegion = (Region) object;
		  if (potentialDataRegion.getFullPath().equals(this.index.getRegionPath())) {
				if (this.logAllClasses || this.logRejectedClasses) {
					//log("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
					log("Rejecting parent=" + parentClassName + "; object=" + object);
				}
			  accept = false;
			} else {
				if (this.logAllClasses || this.logAcceptedClasses) {
					//log("Accepting parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + "); object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
			    log("Accepting parent=" + parentClassName + "; object=" + object.getClass().getName());
					//log("Accepting parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + "); objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
				}
			}
		} else {
			if (this.logAllClasses || this.logAcceptedClasses) {
				//log("Accepting parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + "); object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
				log("Accepting parent=" + parentClassName + "; object=" + object.getClass().getName());
				//log("Accepting parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + "); objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
				//System.out.println("Accepting object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
			}
		}
		return accept;
	}
	
	private void log(String message) {
		System.out.println(message);
		this.cache.getLogger().info(message);
	}
}