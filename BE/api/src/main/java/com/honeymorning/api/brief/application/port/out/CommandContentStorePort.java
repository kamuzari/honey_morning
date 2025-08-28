package com.honeymorning.api.brief.application.port.out;

import org.springframework.core.io.Resource;

public interface CommandContentStorePort {
	void upload(String fullPath, Resource resource, Long contentLength, String contentType);
}
