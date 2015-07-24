package com.oss;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;
public class RestClientTest {
	private HttpServer server;
	private final ObjectMapper mapper = new ObjectMapper();
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
		for (int i = 0; i < 20; i++) {
			final RestClient client = new RestClient(Server.baseUrl() + Server.REST);
			for (int j = 0, len = (int) Random.number(0, 10); j < len; j++) {
				client.addParam(Random.letter(1,10), Random.letter(5,15));
			}
			final Body body = new Body(Random.letter(10, 15), Random.letter(1,100));
			client.setBody(body);
			System.out.println(String.format("get:%s%s", client.getUrl(),client.queryString()));
			for (final RequestMethod method : RequestMethod.values()) {
				client.execute(method);
				Assert.assertEquals(true, client.executeSuccess());
				final RequestInfo requestInfo = this.mapper.readValue(client.getResponse(), RequestInfo.class);
				switch(method) {
				case DELETE:
					Assert.assertEquals("delete", requestInfo.getMethod());
					Assert.assertEquals(true, client.queryString().isEmpty() || client.queryString().equals("?" + requestInfo.getQuery()));
					Assert.assertEquals("", requestInfo.getBody());
					Assert.assertNull(requestInfo.getType());
					break;
				case GET:
					Assert.assertEquals("get", requestInfo.getMethod());
					Assert.assertEquals(true, client.queryString().isEmpty() || client.queryString().equals("?" + requestInfo.getQuery()));
					Assert.assertEquals("", requestInfo.getBody());
					Assert.assertNull(requestInfo.getType());
					break;
				case POST:
					Assert.assertEquals("post", requestInfo.getMethod());
					Assert.assertEquals(null, requestInfo.getQuery());
					Assert.assertEquals(body.getContent(), requestInfo.getBody());
					Assert.assertEquals("[" + body.getType() + "]", requestInfo.getType());
					break;
				case PUT:
					Assert.assertEquals("put", requestInfo.getMethod());
					Assert.assertEquals(true, client.queryString().isEmpty() || client.queryString().equals("?" + requestInfo.getQuery()));
					Assert.assertEquals(body.getContent(), requestInfo.getBody());
					Assert.assertEquals("[" + body.getType() + "]", requestInfo.getType());
					break;
				default:
					break;

				}

			}

		}

	}
}