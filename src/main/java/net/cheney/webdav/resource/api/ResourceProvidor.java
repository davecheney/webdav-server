package net.cheney.webdav.resource.api;

import java.net.URI;

import net.cheney.motown.server.api.Path;

public interface ResourceProvidor extends LockManagerProvidor {

	Resource resolveResource(Path path);

	URI relativizeResource(Resource resource);
	
}
