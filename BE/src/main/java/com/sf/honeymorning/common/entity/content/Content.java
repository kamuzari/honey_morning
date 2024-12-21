package com.sf.honeymorning.common.entity.content;

import com.sf.honeymorning.common.entity.basic.BaseEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
@Embeddable
@AttributeOverrides({
	@AttributeOverride(name = "createdAt", column = @Column(name = "upload_at", updatable = false)),
	@AttributeOverride(name = "modifiedAt", column = @Column(name = "content_modified_at"))
})
public class Content extends BaseEntity {
	private String fileName;

	private Long fileSize;

	@Enumerated(EnumType.STRING)
	private FileType fileType;

	private String fileUrl;

	@Enumerated(EnumType.STRING)
	private AccessAuthority accessAuthority;

	protected Content() {
	}

	public Content(String fileName,
		Long fileSize,
		FileType fileType,
		String fileUrl,
		AccessAuthority accessAuthority) {
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileType = fileType;
		this.fileUrl = fileUrl;
		this.accessAuthority = accessAuthority;
	}
}
