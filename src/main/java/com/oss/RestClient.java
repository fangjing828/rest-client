package com.oss;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClient {
	private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);
	private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(10000).build();
	private static final String UTF_8 = "UTF-8";

	private final ArrayList<NameValuePair> headers;
	private final ArrayList<NameValuePair> params;
	private final String url;

	private Body body;
	private int responseCode;
	private String msg;
	private String response;

	public RestClient(final String url) {
		this.url = url;
		this.headers = new ArrayList<NameValuePair>();
		this.params = new ArrayList<NameValuePair>();
	}

	public void addHeader(final String name, final String value) {
		this.headers.add(new BasicNameValuePair(name, value));
	}

	public void addParam(final String name, final String value) {
		this.params.add(new BasicNameValuePair(name, value));
	}
	public void execute(final RequestMethod method) throws Exception {
		switch (method) {
		case GET: {
			final HttpGet request = new HttpGet(this.getUrl(method));
			this.execute(request);
			break;
		}
		case POST: {
			final HttpPost request = new HttpPost(this.getUrl(method));
			this.setBody(request);
			this.execute(request);
			break;
		}
		case PUT:{
			final HttpPut request = new HttpPut(this.getUrl(method));
			this.setBody(request);
			this.execute(request);
			break;
		}
		case DELETE:
		{
			final HttpDelete request = new HttpDelete(this.getUrl(method));
			this.execute(request);
		}
		break;
		default:
			break;
		}

	}

	public String getUrl() {
		return this.url;
	}

	public String getUrl(final RequestMethod method) throws UnsupportedEncodingException {
		String url = this.url;
		if (RequestMethod.GET.equals(method) || RequestMethod.PUT.equals(method) || RequestMethod.DELETE.equals(method)) {
			url += this.queryString();
		}

		return url;
	}

	public String queryString() throws UnsupportedEncodingException {
		final StringBuilder paramsBuilder = new StringBuilder();
		for (final NameValuePair p : this.params) {
			paramsBuilder.append(String.format("&%s=%s", p.getName(), URLEncoder.encode(p.getValue(), RestClient.UTF_8)));
		}
		if (paramsBuilder.length() > 0) {
			paramsBuilder.replace(0, 1, "?");
		}
		return paramsBuilder.toString();
	}

	public String getMsg() {
		return this.msg;
	}

	public String getResponse () throws UnsupportedEncodingException {
		return new String(this.response.getBytes(RestClient.UTF_8), RestClient.UTF_8);
	}

	public int getResponseCode() {
		return this.responseCode;
	}

	public boolean executeSuccess() {
		return this.responseCode == 200;
	}

	public void setBody(final Body body) {
		this.body = body;
	}

	private void setBody(final HttpEntityEnclosingRequestBase request) throws UnsupportedEncodingException {
		if (this.body != null) {
			final StringEntity se = new StringEntity(this.body.getContent(), RestClient.UTF_8);
			se.setContentType(this.body.getType());
			request.setHeader("Content-Type", this.body.getType());
			request.setEntity(se);
		} else if (!this.params.isEmpty()) {
			request.setEntity(new UrlEncodedFormEntity(this.params, RestClient.UTF_8));
		}
	}

	private void execute(final HttpUriRequest request) throws IOException {
		try (final CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RestClient.REQUEST_CONFIG).build()){
			// Initialize headers
			for (final NameValuePair h : this.headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			final CloseableHttpResponse httpResponse = client.execute(request);
			this.responseCode = httpResponse.getStatusLine().getStatusCode();
			this.msg = httpResponse.getStatusLine().getReasonPhrase();

			final HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				try(InputStream is = entity.getContent()) {
					this.response = Util.stream2String(is);
					/* Closing the input stream will trigger connection release */
					is.close();
				}
			}
			client.close();
			httpResponse.close();
		} catch (final ClientProtocolException e) {
			RestClient.LOG.info("rest client ClientProtocolException", e);
		} catch (final IOException e) {
			RestClient.LOG.info("rest client IOException", e);
		}
	}
}