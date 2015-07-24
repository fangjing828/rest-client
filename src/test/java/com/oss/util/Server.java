package com.oss.util;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.oss.RestClient;
import com.sun.net.httpserver.HttpServer;

public class Server {
	private static final int PORT = 8888;
	public static final String CONSTANT = "/test/constant";
	public static final String REST = "/test/rest";
	public static final String SOAP = "/test/soap";
	private HttpServer server;
	public HttpServer init() throws IOException {
		final HttpServer server = HttpServer.create(new InetSocketAddress(Server.PORT), 0);
		server.createContext(Server.CONSTANT, new ConstantHandler());
		server.createContext(Server.REST, new RestHandler());
		server.createContext(Server.SOAP, new SoapHandler());
		server.setExecutor(null);
		return server;
	}

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
	public void validServer() throws Exception {
		final RestClient client = new RestClient(Server.baseUrl() + Server.CONSTANT);
		client.execute(RequestMethod.GET);
		Assert.assertEquals(ConstantHandler.CONTENT, client.getResponse().replace("\n", ""));
	}

	public static void main(final String[] args) throws IOException {
		new Server().init().start();
	}

	public static String baseUrl() {
		return String.format("http://localhost:%d", Server.PORT);
	}
}
