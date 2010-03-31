package net.cheney.webdav.resource.api;

import net.cheney.webdav.resource.api.Lock.Scope;
import net.cheney.webdav.resource.api.Lock.Type;

public interface LockManager {

	Lock lock(Resource resource, Type type, Scope scope);
	
	Lock unlock(Resource resource);

	boolean isLocked(Resource resource);
}