package com.oss;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.oss.util.Random;
import com.oss.util.RequestInfo;
import com.oss.util.ResponseInfo;
import com.oss.util.Server;
import com.sun.net.httpserver.HttpServer;

public class SoapClientTest {
	private HttpServer server;
	@Before
	public void setup() throws IOException{
		this.server = new Server().init();
		this.server.start();
	}

	@After
	public void tearDown() {
		this.server.stop(0);
	}

	@Test
	public void test() throws Exception {
		final SoapClient client = new SoapClient(Server.baseUrl() + Server.SOAP);
		for (int i = 0; i < 10; i++) {
			final RequestInfo request = new RequestInfo();
			request.setBody(Random.letter(1,10));
			request.setMethod(Random.letter(1,10));
			request.setQuery(Random.letter(1,10));
			request.setType(Random.letter(0,15));
			final ResponseInfo response = client.exec(request, ResponseInfo.class);
			Assert.assertEquals(request.getBody(), response.getBody());
			Assert.assertEquals(request.getMethod(), response.getMethod());
			Assert.assertEquals(request.getQuery(), response.getQuery());
			Assert.assertEquals(request.getType(), response.getType());
		}
	}
}
