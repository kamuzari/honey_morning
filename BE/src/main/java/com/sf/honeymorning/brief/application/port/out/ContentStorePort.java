package com.sf.honeymorning.brief.application.port.out;

import org.springframework.core.io.Resource;

public interface ContentStorePort {
	void upload(String fullPath, Resource resource, Long contentLength, String contentType);
}
