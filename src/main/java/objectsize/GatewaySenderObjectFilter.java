package objectsize;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.Region;

import org.apache.geode.cache.asyncqueue.AsyncEventListener;

import org.apache.geode.cache.asyncqueue.internal.AsyncEventQueueStats;

import org.apache.geode.cache.control.ResourceManager;

import org.apache.geode.cache.partition.PartitionListener;

import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewayEventSubstitutionFilter;
import org.apache.geode.cache.wan.GatewaySender;
import org.apache.geode.cache.wan.GatewayTransportFilter;

import org.apache.geode.internal.cache.wan.parallel.ParallelGatewaySenderQueue;

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

import org.apache.geode.internal.cache.partitioned.PersistentBucketRecoverer;
import org.apache.geode.internal.cache.partitioned.RegionAdvisor;

import org.apache.geode.internal.cache.persistence.PersistentMemberManager;

import org.apache.geode.internal.cache.eviction.EvictionStats;
import org.apache.geode.internal.cache.wan.AbstractGatewaySender;
import org.apache.geode.internal.cache.wan.GatewaySenderStats;

import org.apache.geode.internal.size.ObjectGraphSizer.ObjectFilter;

import org.apache.logging.log4j.Logger;

import java.security.AccessControlContext;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class GatewaySenderObjectFilter implements ObjectFilter {
  
	private boolean logAllClasses = true;
	
	private boolean logRejectedClasses = false;
	
	private boolean logAcceptedClasses = false;
	
	private AbstractGatewaySender sender;
	
	private Cache cache;
	
	public GatewaySenderObjectFilter(GatewaySender sender) {
	  this.sender = (AbstractGatewaySender) sender;
	  this.cache = this.sender.getCache();
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
				|| object instanceof EvictionStats
				|| object instanceof DiskRegionStats
				|| object instanceof DistributedLockService
				|| object instanceof PersistentMemberManager
				|| object instanceof DistributedMember
				|| object instanceof DistributionManager
				|| object instanceof DistributedSystem
				|| object instanceof PartitionedRegionStats
				|| object instanceof ResourceManager
				|| object instanceof ScheduledThreadPoolExecutor
				|| object instanceof DiskStore
				|| object instanceof Logger
				|| object instanceof AsyncEventQueueStats
				|| object instanceof GatewaySenderStats
				|| object instanceof GatewayEventFilter
        || object instanceof GatewayTransportFilter
        || object instanceof AsyncEventListener
        || object instanceof GatewayEventSubstitutionFilter
        || object instanceof ClassLoader 
        || object instanceof AccessControlContext
        || object instanceof ThreadGroup
        || object instanceof PartitionListener
        || object instanceof PersistentBucketRecoverer
        //|| object instanceof RegionAdvisor
        //|| object instanceof BucketAdvisor
				) {
			if (this.logAllClasses || this.logRejectedClasses) {
			  log("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
			}
			accept = false;
		} else if (parent instanceof PartitionedRegion 
			&& (object instanceof CopyOnWriteArrayList // the colocatedByList field
				|| object instanceof DistributedRegion // the prRoot field (PartitionedRegion called _PR)
				|| object instanceof PartitionedRegion // is the colocatedWithRegion field
				)) {
			if (this.logAllClasses || this.logRejectedClasses) {
			  log("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
			}
			accept = false;
		} else if (parent instanceof BucketAdvisor 
			&& (object instanceof BucketAdvisor // the parentAdvisor field
				)) {
			if (this.logAllClasses || this.logRejectedClasses) {
			  log("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
			}
			accept = false;
		} else if (parent instanceof AbstractGatewaySender 
			&& (object instanceof DistributedRegion // the eventIdIndexMetaDataRegion field
				)) {
			if (this.logAllClasses || this.logRejectedClasses) {
			  log("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
			}
			accept = false;
		} else if (object instanceof PartitionedRegion) { // Checks PartitionedRegions
		  Region region = (Region) object;
		  if (region.getName().contains(ParallelGatewaySenderQueue.QSTRING)) {
				if (this.logAllClasses || this.logAcceptedClasses) {
					//log("Accepting parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + "); object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
			    log("Accepting parent=" + parentClassName + "; object=" + object.getClass().getName());
					//log("Accepting parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + "); objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
				}
		  } else {
				if (this.logAllClasses || this.logRejectedClasses) {
					log("Rejecting parent=" + parentClassName + "; object=" + object.getClass().getName());
				}
			  accept = false;
		  }
		} else {
			if (this.logAllClasses || this.logAcceptedClasses) {
				log("Accepting parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + "); object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
				//log("Accepting parent=" + parentClassName + "; object=" + object.getClass().getName());
				//log("Accepting parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + "); objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
				//System.out.println("Accepting object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + ")");
			}
		}
		return accept;
	}
	
	private void log(String message) {
//		System.out.println(message);
		this.cache.getLogger().fine(message);
	}
}