/* Copyright 2006-2007 Dave Cheney
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cheney.webdav.resource.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.cheney.motown.common.api.Depth;
import net.cheney.motown.common.api.MimeType;
import net.cheney.motown.common.api.Status;
import net.cheney.motown.common.api.Version;
import net.cheney.snax.model.Element;
import net.cheney.snax.model.Namespace;
import net.cheney.snax.model.Node;
import net.cheney.snax.model.QName;
import net.cheney.snax.model.Text;
import net.cheney.webdav.resource.api.Lock.Scope;
import net.cheney.webdav.resource.api.Lock.Type;

import org.apache.commons.lang.time.FastDateFormat;

public final class Elements {
	
	private static final FastDateFormat RFC1123_DATE_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss zzz", TimeZone.getTimeZone("GMT"), Locale.US);
	
	public static final Namespace DAV_NAMESPACE = Namespace.valueOf("", "DAV:");
	
	public static final QName DISPLAY_NAME = QName.valueOf(DAV_NAMESPACE, "displayname"), 
		GET_CONTENT_LENGTH = QName.valueOf(DAV_NAMESPACE, "getcontentlength"),
		GET_CONTENT_TYPE = QName.valueOf(DAV_NAMESPACE, "getcontenttype"),
		GET_LAST_MODIFIED = QName.valueOf(DAV_NAMESPACE, "getlastmodified"),
		LOCK_DISCOVERY = QName.valueOf(DAV_NAMESPACE, "lockdiscovery"),
		ACTIVE_LOCK = QName.valueOf(DAV_NAMESPACE, "activelock"),
		COLLECTION = QName.valueOf(DAV_NAMESPACE, "collection"),
		RESOURCE_TYPE = QName.valueOf(DAV_NAMESPACE, "resourcetype"),
		RESPONSE = QName.valueOf(DAV_NAMESPACE, "response"),
		DEPTH = QName.valueOf(DAV_NAMESPACE, "depth"),
		LOCK_TYPE = QName.valueOf(DAV_NAMESPACE, "locktype"),
		LOCK_SCOPE = QName.valueOf(DAV_NAMESPACE, "lockscope"),
		LOCK_ENTRY = QName.valueOf(DAV_NAMESPACE, "lockentry"),
		STATUS = QName.valueOf(DAV_NAMESPACE, "status");
	
	public static final QName MULTISTATUS = QName.valueOf(DAV_NAMESPACE, "multistatus"),
		PROP = QName.valueOf(DAV_NAMESPACE, "prop"),
		PROPSTAT = QName.valueOf(DAV_NAMESPACE, "propstat"),
		LOCK_TOKEN = QName.valueOf(DAV_NAMESPACE, "locktoken"),
		HREF = QName.valueOf(DAV_NAMESPACE, "href");
		
	public static final QName EXCLUSIVE = QName.valueOf(DAV_NAMESPACE, "exclusive"), 
		SHARED = QName.valueOf(DAV_NAMESPACE, "shared"), 
		NONE = QName.valueOf(DAV_NAMESPACE, "none");

	static final DEPTH DEPTH_ZERO = new DEPTH(Depth.ZERO);
	static final DEPTH DEPTH_ONE = new DEPTH(Depth.ONE);
	static final DEPTH DEPTH_INFINITY = new DEPTH(Depth.INFINITY);
	
	static final RESOURCE_TYPE RESOURCE_TYPE_RESOURCE = new RESOURCE_TYPE();
	static final RESOURCE_TYPE RESOURCE_TYPE_COLLECTION = new RESOURCE_TYPE(new Element(COLLECTION));
	
	
	private Elements() { } // yagni
	
	/**
	 * Provides a name for the resource that is suitable for presentation to a user. 
	 * (RFC 2518, section 13.2)
	 */
	public static class DISPLAY_NAME extends Element {

		public DISPLAY_NAME(String displayName) {
			super(DISPLAY_NAME, new Text(displayName));
		}
		
	}
	
	public static DISPLAY_NAME displayName(String displayName) {
		return new DISPLAY_NAME(displayName);
	}

	public static class MULTISTATUS extends Element {

		public MULTISTATUS(List<RESPONSE> content) {
			super(MULTISTATUS, content);
		}
		
	}
	
	public static MULTISTATUS multistatus(List<RESPONSE> children) {
		return new MULTISTATUS(children);
	}
	
	public static class STATUS extends Element {

		public STATUS(Status status) {
			super(STATUS, new Text(String.format("%s %s %s", Version.HTTP_1_1, status.code(), status.reason())));
		}
		
	}
	
	public static STATUS status(Status status) {
		return new STATUS(status);
	}

	public static Element getContentLength(long l) {
		return new Element(GET_CONTENT_LENGTH, new Text(l));
	}
	
	/**
	 *     Contains the Content-Type header returned by a GET without accept headers. (RFC 2518, section 13.5)
	 */
	public static Element getContentType(MimeType mimeType) {
		return new Element(GET_CONTENT_TYPE, new Text(mimeType.toString()));
	}
	
	/**
	 * Contains the Last-Modified header returned by a GET without accept headers. (RFC 2518, section 13.7)
	 */
	public static Element getLastModified(Date date) {
		return new Element(GET_LAST_MODIFIED, new Text(RFC1123_DATE_FORMAT.format(date)));
	}
	
	public static class HREF extends Element {

		public HREF(URI uri) {
			super(HREF, new Text(String.format("/%s", uri.toASCIIString())));
		}
		
	}
	
	public static HREF href(URI uri) {
		return new HREF(uri);
	}

	public static PROP prop(List<Element> elements) {
		return new PROP(elements.toArray(new Element[0]));
	}
	
	public static class PROP extends Element {

		public PROP(Node[] content) {
			super(PROP, content);
		}
		
	}
	
	public static PROP prop(Element... elements) {
		return new PROP(elements);	
	}
	
	public static class PROPSTAT extends Element {

		public PROPSTAT(PROP prop, STATUS status) {
			super(PROPSTAT, prop, status);
		}
		
	}
	
	public static PROPSTAT propertyStatus(PROP prop, Status s) {
		return new PROPSTAT(prop, status(s));
	}
	
	public static PROPSTAT propertyStatus(PROP prop, STATUS status) {
		return new PROPSTAT(prop, status);
	}

	/**
	 *     Holds a single response describing the effect of a method on a resource and/or its properties. (RFC 2518, section 12.9.1)
	 */
	public static class RESPONSE extends Element {

		public RESPONSE(HREF href, List<PROPSTAT> propstats) {
			super(RESPONSE, nodes(href, propstats));
		}

		private static List<Node> nodes(HREF href, List<PROPSTAT> propstats) {
			ArrayList<Node> nodes = new ArrayList<Node>();
			nodes.add(href);
			nodes.addAll(propstats);
			return nodes;
		}
		
	}
	
	public static RESPONSE response(HREF href, List<PROPSTAT> propstats) {
		return new RESPONSE(href, propstats);
	}

	/**
	 *     Describes the active locks on a resource. (RFC 2518, section 13.8)
	 */
	public static final class LOCK_DISCOVERY extends Element {

		public LOCK_DISCOVERY(ACTIVE_LOCK... content) {
			super(ACTIVE_LOCK, content);
		}
		
	}
	
	public static Element lockDiscovery(ACTIVE_LOCK... activelocks) {
		return new LOCK_DISCOVERY(activelocks);
	}

	public static class ACTIVE_LOCK extends Element {

		public ACTIVE_LOCK(LOCK_TYPE type, LOCK_SCOPE scope, DEPTH depth, LOCK_TOKEN token) {
			super(ACTIVE_LOCK, type, scope, depth, token);
		}
		
	}
	
	public static ACTIVE_LOCK activeLock(Lock lock, Depth depth, URI uri) {
		return new ACTIVE_LOCK(
			locktype(lock.type()),
			lockscope(lock.scope()),
			depth(depth), 
			lockToken(lock.token()));
	}
	
	public static class LOCK_SCOPE extends Element {

		public LOCK_SCOPE(Scope scope) {
			super(LOCK_SCOPE, new Text(scope.toString()));
		}
		
	}
	
	public static LOCK_SCOPE lockscope(Scope scope) {
		return new LOCK_SCOPE(scope);
	}

	public static class LOCK_TYPE extends Element {
		public LOCK_TYPE(Lock.Type type) {
			super(LOCK_TYPE, new Text(type.toString()));
		}
	}
	
	public static LOCK_TYPE locktype(Type type) {
		return new LOCK_TYPE(type);
	}

	public static class LOCK_TOKEN extends Element {

		public LOCK_TOKEN(String token) {
			super(LOCK_TOKEN, new Text(token));
		}
		
	}
	
	private static LOCK_TOKEN lockToken(String token) {
		return new LOCK_TOKEN(token);
	}

	public static class DEPTH extends Element {
		
		DEPTH(Depth depth) {
			super(DEPTH, new Text(depth.toString()));
		}
		
	}
	
	private static DEPTH depth(Depth depth) {
		switch (depth) {
		case ZERO: return DEPTH_ZERO;
		case ONE: return DEPTH_ONE;
		default: return DEPTH_INFINITY;
		}
	}
	
	public static class RESOURCE_TYPE extends Element {

		public RESOURCE_TYPE(Element... content) {
			super(RESOURCE_TYPE, content);
		}
		
	}

	public static RESOURCE_TYPE resourceType(boolean collection) {
		return collection ? RESOURCE_TYPE_COLLECTION : RESOURCE_TYPE_RESOURCE;
	}

	public static Element collection() {
		return new Element(COLLECTION);
	}
	
	
//	
//	LockEntry(LockScope scope, LockType type) {
//		super(LOCK_ENTRY, toXML(scope), toXML(type));
//	}
//
//	private static Element toXML(LockScope scope) {
//		switch (scope) {
//		case EXCLUSIVE:
//			return lockscope(scope).addContent(new Element(EXCLUSIVE));
//
//		case SHARED:
//			return lockscope(scope).addContent(new Element(SHARED));
//			
//		case NONE:
//		default:
//			return lockscope(scope).addContent(new Element(NONE));
//		}
//	}
//
//	private static Element toXML(LockType type) {
//		switch (type) {
//		case READ:
//			return locktype(type).addContent(new Element(new QName("read", DAV_NAMESPACE)));
//
//		case WRITE:
//			return locktype(type).addContent(new Element(new QName("write", DAV_NAMESPACE)));
//
//		case NONE:
//		default:
//			return locktype(type).addContent(new Element(new QName("none", DAV_NAMESPACE)));
//		}
//	}
}
