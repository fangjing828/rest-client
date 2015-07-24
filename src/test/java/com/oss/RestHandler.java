package com.oss;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.map.ObjectMapper;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RestHandler implements HttpHandler {
	@Override
	public void handle(final HttpExchange t) throws IOException {
		final RequestInfo info = new RequestInfo();
		info.setMethod(t.getRequestMethod().toLowerCase());
		info.setQuery(t.getRequestURI().getQuery());
		try (InputStream is = t.getRequestBody()){
			try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				for(int i=is.read(); i != -1; i = is.read()) {
					os.write(i);
				}
				info.setBody(os.toString());
				os.close();
			}
			is.close();
		}

		if ((info.getBody() != null) && (t.getRequestHeaders().getFirst("Content-Type") != null)) {
			info.setType(t.getRequestHeaders().get("Content-Type").toString());
		}

		final ObjectMapper mapper = new ObjectMapper();

		final String reponseContent = mapper.writeValueAsString(info);
		t.sendResponseHeaders(200, reponseContent.length());
		final OutputStream response = t.getResponseBody();
		response.write(reponseContent.getBytes("utf-8"));
		response.close();

	}
}
