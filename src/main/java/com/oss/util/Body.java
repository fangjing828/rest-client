package com.oss.util;

public class Body {
	private String type;
	private String content;
	public Body(){}
	public Body(final String type, final String content) {
		this.type = type;
		this.content = content;
	}
	public String getType() {
		return this.type;
	}
	public void setType(final String type) {
		this.type = type;
	}
	public String getContent() {
		return this.content;
	}
	public void setContent(final String content) {
		this.content = content;
	}
}
