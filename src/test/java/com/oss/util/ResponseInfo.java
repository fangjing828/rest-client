package com.oss.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResponseInfo {
	private String method;
	private String body;
	private String type;
	private String query;

	public String getMethod() {
		return this.method;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getQuery() {
		return this.query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}
}
