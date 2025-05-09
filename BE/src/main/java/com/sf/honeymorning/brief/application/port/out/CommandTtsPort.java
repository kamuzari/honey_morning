package com.sf.honeymorning.brief.application.port.out;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface CommandTtsPort {
	ResponseEntity<Resource> create(String text);
}
