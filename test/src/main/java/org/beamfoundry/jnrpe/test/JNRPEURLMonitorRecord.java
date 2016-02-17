package org.beamfoundry.jnrpe.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JNRPEURLMonitorRecord {

	private Map<String,AtomicInteger> methodCallCounter = new ConcurrentHashMap<String,AtomicInteger>();
	
	public synchronized void incrementMethodCounter(String method) {
		if (methodCallCounter.containsKey(method)) {
			methodCallCounter.get(method).incrementAndGet();
		}else{
			methodCallCounter.put(method, new AtomicInteger(1));
		}
	}
	
	public Integer getMethodCount(String method) {
		if (methodCallCounter.containsKey(method)) {
			return methodCallCounter.get(method).get();
		}else{
			return 0;
		}
	}
	
	public Integer getCount() {
		int count = 0;
		for (Map.Entry <String,AtomicInteger> entry : methodCallCounter.entrySet() ) {
			count += entry.getValue().get();
		}
		return count;
	}
}
