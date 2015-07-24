package com.oss;

import java.io.UnsupportedEncodingException;

public class BaseClient {
	public static final String UTF_8 = "UTF-8";
	protected final String url;
	protected int responseCode;
	protected String msg;
	protected String response;

	public BaseClient(final String url) {
		this.url = url;
	}
	public String getMsg() {
		return this.msg;
	}

	public String getResponse () throws UnsupportedEncodingException {
		return new String(this.response.getBytes(BaseClient.UTF_8), BaseClient.UTF_8);
	}

	public int getResponseCode() {
		return this.responseCode;
	}

	public boolean executeSuccess() {
		return this.responseCode == 200;
	}

}
