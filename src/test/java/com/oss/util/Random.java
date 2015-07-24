package com.oss.util;

public class Random {
	public static char letter() {
		final long letterBeginPos = 'a';
		final long tmp = letterBeginPos + Random.number(25);
		return (char) tmp;
	}

	public static String letter(final int len) {
		final char[] chars = new char[len];
		for (int i = 0; i < len; i++) {
			chars[i] = Random.letter();
		}
		return new String(chars);
	}

	public static String letter(final int minLen, final int maxLen) {
		final int len = (int) Random.number(minLen, maxLen);
		return Random.letter(len);
	}

	public static long number(final int interval) {
		return  Math.round(Math.random() * interval);
	}

	public static long number(final int min, final int max) {
		final int interval = max - min;
		return min + Random.number(interval);
	}

	public static <T> T arr(final T[] var) {
		final int len = (int) Random.number(1, var.length)  - 1;
		return var[len];
	}
}
