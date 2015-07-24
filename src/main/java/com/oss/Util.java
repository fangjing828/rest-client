package com.oss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Util {
	public static String stream2String(final InputStream is) throws UnsupportedEncodingException {

		final BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		final StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
