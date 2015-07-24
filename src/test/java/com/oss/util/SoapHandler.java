package com.oss.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.oss.SoapClient;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SoapHandler implements HttpHandler{
	@Override
	public void handle(final HttpExchange t) throws IOException{
		final InputStream is = t.getRequestBody();
		try {
			final RequestInfo info = SoapClient.unwarpSOAP(is, RequestInfo.class);
			final ResponseInfo response = new ResponseInfo();
			response.setBody(info.getBody());
			response.setMethod(info.getMethod());
			response.setQuery(info.getQuery());
			response.setType(info.getType());
			final String reponseContent = SoapClient.wrapSOAP(response).toString();
			t.sendResponseHeaders(200, reponseContent.length());
			final OutputStream os = t.getResponseBody();
			os.write(reponseContent.getBytes("utf-8"));
			os.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
