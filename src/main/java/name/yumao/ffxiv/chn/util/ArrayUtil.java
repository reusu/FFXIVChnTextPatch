package name.yumao.ffxiv.chn.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class ArrayUtil {
	public static byte[] arrayReplace(byte[] org, byte[] search, byte[] replace, int startIndex) throws UnsupportedEncodingException {
		int index = indexOf(org, search, startIndex);
		if (index != -1) {
			int newLength = org.length + replace.length - search.length;
			byte[] newByte = new byte[newLength];
			System.arraycopy(org, 0, newByte, 0, index);
			System.arraycopy(replace, 0, newByte, index, replace.length);
			System.arraycopy(org, index + search.length, newByte, index + replace.length, org.length - index - search.length);
			int newStart = index + replace.length;
			if (newByte.length - newStart > replace.length)
				return arrayReplace(newByte, search, replace, newStart); 
			return newByte;
		} 
		return org;
	}
	
	public static byte[] append(byte[] org, byte[] to) {
		byte[] newByte = new byte[org.length + to.length];
		System.arraycopy(org, 0, newByte, 0, org.length);
		System.arraycopy(to, 0, newByte, org.length, to.length);
		return newByte;
	}
	
	public static byte[] append(byte[] org, byte to) {
		byte[] newByte = new byte[org.length + 1];
		System.arraycopy(org, 0, newByte, 0, org.length);
		newByte[org.length] = to;
		return newByte;
	}
	
	public static void append(byte[] org, int from, byte[] append) {
		System.arraycopy(append, 0, org, from, append.length);
	}
	
	public static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to); 
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
		return copy;
	}
	
	public static byte[] copyAarry(byte[] original, int from, int len) {
		int to = from + len;
		return copyOfRange(original, from, to);
	}
	
	public static byte[] copyAarry(byte[] original, int len) {
		return copyOfRange(original, 0, len);
	}
	
	public static byte[] char2byte(String encode, char... chars) {
		Charset cs = Charset.forName(encode);
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);
		return bb.array();
	}
	
	public static int searchArray(byte[] org, byte[] search, int startIndex) {
		int N = org.length, M = search.length;
		for (int i = startIndex; i <= N - M; i++) {
			int j;
			for (j = 0; j < M && 
				org[i + j] == search[j]; j++);
			if (M == j)
				return i; 
		} 
		return -1;
	}
	
	public static int indexOf(byte[] org, byte[] search) {
		return indexOf(org, search, 0);
	}
	
	public static int indexOf(byte[] org, byte[] search, int startIndex) {
		KMPMatcher kmpMatcher = new KMPMatcher();
		kmpMatcher.computeFailure4Byte(search);
		return kmpMatcher.indexOf(org, startIndex);
	}
	
	public static int lastIndexOf(byte[] org, byte[] search) {
		return lastIndexOf(org, search, 0);
	}
	
	public static int lastIndexOf(byte[] org, byte[] search, int fromIndex) {
		KMPMatcher kmpMatcher = new KMPMatcher();
		kmpMatcher.computeFailure4Byte(search);
		return kmpMatcher.lastIndexOf(org, fromIndex);
	}
	
	static class KMPMatcher {
		private int[] failure;
		
		private int matchPoint;
		
		private byte[] bytePattern;
		
		public int indexOf(byte[] text, int startIndex) {
			int j = 0;
			if (text.length == 0 || startIndex > text.length)
				return -1; 
			for (int i = startIndex; i < text.length; i++) {
				while (j > 0 && this.bytePattern[j] != text[i])
					j = this.failure[j - 1]; 
				if (this.bytePattern[j] == text[i])
					j++; 
				if (j == this.bytePattern.length) {
					this.matchPoint = i - this.bytePattern.length + 1;
					return this.matchPoint;
				} 
			} 
			return -1;
		}
		
		public int lastIndexOf(byte[] text, int startIndex) {
			this.matchPoint = -1;
			int j = 0;
			if (text.length == 0 || startIndex > text.length)
				return -1; 
			int end = text.length;
			for (int i = startIndex; i < end; i++) {
				while (j > 0 && this.bytePattern[j] != text[i])
					j = this.failure[j - 1]; 
				if (this.bytePattern[j] == text[i])
					j++; 
				if (j == this.bytePattern.length) {
					this.matchPoint = i - this.bytePattern.length + 1;
					if (text.length - i > this.bytePattern.length) {
						j = 0;
					} else {
						return this.matchPoint;
					} 
				} else if (startIndex != 0 && i + 1 == end) {
					end = startIndex;
					i = -1;
					startIndex = 0;
				} 
			} 
			return this.matchPoint;
		}
		
		public int lastIndexOfWithNoLoop(byte[] text, int startIndex) {
			this.matchPoint = -1;
			int j = 0;
			if (text.length == 0 || startIndex > text.length)
				return -1; 
			for (int i = startIndex; i < text.length; i++) {
				while (j > 0 && this.bytePattern[j] != text[i])
					j = this.failure[j - 1]; 
				if (this.bytePattern[j] == text[i])
					j++; 
				if (j == this.bytePattern.length) {
					this.matchPoint = i - this.bytePattern.length + 1;
					if (text.length - i > this.bytePattern.length) {
						j = 0;
					} else {
						return this.matchPoint;
					} 
				} 
			} 
			return this.matchPoint;
		}
		
		public void computeFailure4Byte(byte[] patternStr) {
			this.bytePattern = patternStr;
			int j = 0;
			int len = this.bytePattern.length;
			this.failure = new int[len];
			for (int i = 1; i < len; i++) {
				while (j > 0 && this.bytePattern[j] != this.bytePattern[i])
					j = this.failure[j - 1]; 
				if (this.bytePattern[j] == this.bytePattern[i])
					j++; 
				this.failure[i] = j;
			} 
		}
	}
}
