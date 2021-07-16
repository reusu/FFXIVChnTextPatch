package name.yumao.ffxiv.chn.util;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.Inflater;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class JLibzSqpacklTools {
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals(""))
			return null; 
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		} 
		return d;
	}
	
	private static byte charToByte(char c) {
		return (byte)"0123456789ABCDEF".indexOf(c);
	}
	
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0)
			return null; 
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2)
				stringBuilder.append(0); 
			stringBuilder.append(hv);
		} 
		return stringBuilder.toString();
	}
	
	public static byte[] compressBlock(byte[] block) throws IOException {
		int comprLen = block.length + 255;
		byte[] compr = new byte[comprLen];
		try {
			Deflater deflater = new Deflater();
			deflater.setInput(block);
			deflater.setOutput(compr);
			int err = deflater.init(9, true);
			CHECK_DEFALTER_ERR(deflater, err, "deflateInit");
			while (deflater.total_in != block.length && deflater.total_out < comprLen) {
				deflater.avail_in = deflater.avail_out = 1;
				err = deflater.deflate(0);
				if (err == 1)
					break; 
				CHECK_DEFALTER_ERR(deflater, err, "deflate");
			} 
			while (true) {
				deflater.avail_out = 1;
				err = deflater.deflate(4);
				if (err == 1)
					break; 
				CHECK_DEFALTER_ERR(deflater, err, "deflate");
			} 
			err = deflater.end();
			CHECK_DEFALTER_ERR(deflater, err, "deflateEnd");
			deflater.finished();
			byte[] tmpr = new byte[(int)deflater.total_out];
			System.arraycopy(compr, 0, tmpr, 0, tmpr.length);
			return tmpr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public static byte[] decompressBlock(byte[] compr, int decompressedSize) throws IOException {
		int compressedSize = compr.length;
		byte[] decompressedData = new byte[decompressedSize];
		byte[] gzipedData = new byte[compressedSize + 6];
		gzipedData[0] = 120;
		gzipedData[1] = -100;
		System.arraycopy(compr, 0, gzipedData, 2, compr.length);
		int checksum = adler32(gzipedData, 2, compressedSize);
		byte[] checksumByte = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(checksum).array();
		System.arraycopy(checksumByte, 0, gzipedData, 2 + compressedSize, 4);
		try {
			Inflater inflater = new Inflater();
			inflater.setInput(gzipedData);
			inflater.setOutput(decompressedData);
			int err = inflater.init();
			CHECK_INFLATER_ERR(inflater, err, "inflateInit");
			while (inflater.total_out < decompressedSize && inflater.total_in < gzipedData.length) {
				inflater.avail_in = inflater.avail_out = 1;
				err = inflater.inflate(0);
				if (err == 1)
					break; 
				CHECK_INFLATER_ERR(inflater, err, "inflate");
			} 
			err = inflater.end();
			CHECK_INFLATER_ERR(inflater, err, "inflateEnd");
			inflater.finished();
			return decompressedData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	private static void CHECK_DEFALTER_ERR(Deflater deflater, int err, String msg) {
		if (err != 0) {
			if (deflater.msg != null)
				System.out.print(deflater.msg + " "); 
			System.out.println(msg + " error: " + err);
			System.exit(1);
		} 
	}
	
	public static int actualLength(byte[] data) {
		int i = data.length - 1;
		for (; i >= 0 && 
			data[i] == 0; i--);
		return i + 1;
	}
	
	private static void CHECK_INFLATER_ERR(Inflater z, int err, String msg) {
		if (err != 0) {
			if (z.msg != null)
				System.out.print(z.msg + " "); 
			System.out.println(msg + " error: " + err);
			System.exit(1);
		} 
	}
	
	private static int adler32(byte[] bytes, int offset, int size) {
		int a32mod = 65521;
		int s1 = 1;
		int s2 = 0;
		for (int i = offset; i < size; i++) {
			int b = bytes[i];
			s1 = (s1 + b) % 65521;
			s2 = (s2 + s1) % 65521;
		} 
		return (s2 << 16) + s1;
	}
}
