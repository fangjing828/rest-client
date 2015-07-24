package com.oss.util;


import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;


public class XmlSerializer {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(XmlSerializer.class);
	private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();

	public <T> String serialize(final T request, final OutputStream os) {
		String res = null;
		try {
			final JAXBContext jctxt = JAXBContext.newInstance(request.getClass());
			final Marshaller marshaller = jctxt.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			final XMLStreamWriter xmlStreamWriter = XmlSerializer.XML_OUTPUT_FACTORY.createXMLStreamWriter(os, "UTF-8");
			xmlStreamWriter.writeStartDocument((String) marshaller.getProperty(Marshaller.JAXB_ENCODING), "1.0");


			marshaller.marshal(request, xmlStreamWriter);
			xmlStreamWriter.writeEndDocument();
			xmlStreamWriter.close();

			res = xmlStreamWriter.toString();
		} catch (final Exception e) {
			XmlSerializer.LOG.warn("serialize exception:", e);
		}
		return res;
	}

	public <W> W deserialize(final InputStream stream, final Class<W> outputType) {
		W entity = null;
		try {
			final JAXBContext jctxt = JAXBContext.newInstance(outputType);
			final Unmarshaller unmarshaller = jctxt.createUnmarshaller();
			JAXBElement<W> entityElement;
			entityElement = unmarshaller.unmarshal(new StreamSource(stream), outputType);
			entity = entityElement.getValue();
		} catch (final Exception e) {
			XmlSerializer.LOG.warn("deserialize exception", e);
		}

		return entity;
	}
}
