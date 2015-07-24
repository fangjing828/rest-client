package com.oss;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ConstantHandler implements HttpHandler{
	public static final String CONTENT = "hello world!";
	@Override
	public void handle(final HttpExchange t) throws IOException {
		final String response = ConstantHandler.CONTENT;
		t.sendResponseHeaders(200, response.length());
		final OutputStream os = t.getResponseBody();
		os.write(response.getBytes("utf-8"));
		os.close();
	}
}
