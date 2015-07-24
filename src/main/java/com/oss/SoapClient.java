package com.oss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.oss.util.XmlSerializer;

public class SoapClient extends BaseClient{
	public SoapClient(final String url) {
		super(url);
	}

	public <RES> RES  exec(final Object request, final Class<RES> outputType) throws Exception {
		final HttpPost req = new HttpPost(this.url);
		req.setHeader("Content-Type", "text/xml");

		final ByteArrayOutputStream  out = SoapClient.wrapSOAP(request);
		final StringEntity se = new StringEntity(out.toString(), "UTF-8");
		out.close();
		se.setContentType("text/xml");

		req.setEntity(se);

		return this.execRequest(req, outputType);
	}

	private <RES> RES execRequest(final HttpUriRequest request, final Class<RES> outputType) throws IOException, SOAPException, IllegalStateException, TransformerException {
		RES response = null;
		final RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).build();
		try(final CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build())
		{
			HttpResponse httpResponse;
			httpResponse = client.execute(request);
			this.responseCode = httpResponse.getStatusLine().getStatusCode();
			this.msg = httpResponse.getStatusLine().getReasonPhrase();

			final HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				response = SoapClient.unwarpSOAP(entity.getContent(), outputType);
			}
		}

		return response;
	}

	public static <T> T unwarpSOAP(final InputStream is, final Class<T> outputType) throws IOException, SOAPException, TransformerException {
		T response = null;
		final SOAPMessage sm = MessageFactory.newInstance().createMessage(null, is);
		final SOAPBody sb = sm.getSOAPBody();
		final String body = SoapClient.body2xml(sb);
		if (body != null) {
			response  = new XmlSerializer().deserialize( new ByteArrayInputStream(body.getBytes("UTF-8")), outputType);
		}
		return response;
	}

	public static ByteArrayOutputStream wrapSOAP(final Object request) throws UnsupportedOperationException, SOAPException, IOException, ParserConfigurationException, SAXException {
		final MessageFactory mf = MessageFactory.newInstance();
		final SOAPMessage sm = mf.createMessage();
		final SOAPEnvelope envelope = sm.getSOAPPart().getEnvelope();
		envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
		final SOAPHeader sh = sm.getSOAPHeader();
		final SOAPBody sb = sm.getSOAPBody();
		sh.detachNode();

		try(ByteArrayOutputStream  os = new ByteArrayOutputStream ()){
			new XmlSerializer().serialize(request, os);
			sb.addDocument(SoapClient.xml2doc(os.toString()));
		}

		sm.saveChanges();

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		sm.writeTo(out);
		return out;
	}

	public static String body2xml(final SOAPBody body) throws TransformerException, IOException {
		String xml = null;
		final NodeList resList = body.getChildNodes();
		if (resList.getLength() > 0) {
			try (final StringWriter writer = new StringWriter()) {
				final DOMSource domSource = new DOMSource(resList.item(0));
				final StreamResult sr = new StreamResult(writer);
				final TransformerFactory tf = TransformerFactory.newInstance();
				final Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, sr);
				xml = writer.toString();
			}
		}
		return xml;
	}

	public static Document xml2doc(final String xml) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		final DocumentBuilder builder=factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xml)));
	}
}
