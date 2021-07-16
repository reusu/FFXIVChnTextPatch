package name.yumao.ffxiv.chn.util;

public class HexUtils {
	public static String bytesToHexString(byte[] b) {
		byte[] buff = new byte[3 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[3 * i] = hex[b[i] >> 4 & 0xF];
			buff[3 * i + 1] = hex[b[i] & 0xF];
			buff[3 * i + 2] = 45;
		} 
		String re = new String(buff);
		return re.replace("-", " ").trim();
	}
	
	public static String bytesToHexStringWithOutSpace(byte[] b) {
		byte[] buff = new byte[3 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[3 * i] = hex[b[i] >> 4 & 0xF];
			buff[3 * i + 1] = hex[b[i] & 0xF];
			buff[3 * i + 2] = 45;
		} 
		String re = new String(buff);
		return re.replace("-", "");
	}
	
	public static String bytesToHexStringLower(byte[] b) {
		byte[] buff = new byte[3 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[3 * i] = hex[b[i] >> 4 & 0xF];
			buff[3 * i + 1] = hex[b[i] & 0xF];
			buff[3 * i + 2] = 45;
		} 
		String re = new String(buff);
		return re.replace("-", "").toLowerCase();
	}
	
	public static String stringToHexString(String str) {
		byte[] b = str.getBytes();
		byte[] buff = new byte[3 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[3 * i] = hex[b[i] >> 4 & 0xF];
			buff[3 * i + 1] = hex[b[i] & 0xF];
			buff[3 * i + 2] = 45;
		} 
		String re = new String(buff);
		return re.replace("-", " ");
	}
	
	public static byte[] hexStringToBytes(String hexstr) {
		hexstr = hexstr.replace(" ", "");
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte)(parse(c0) << 4 | parse(c1));
		} 
		return b;
	}
	
	private static final byte[] hex = "0123456789ABCDEF".getBytes();
	
	private static int parse(char c) {
		if (c >= 'a')
			return c - 97 + 10 & 0xF; 
		if (c >= 'A')
			return c - 65 + 10 & 0xF; 
		return c - 48 & 0xF;
	}
	
	public static int getHexStringLength(String hexStr) {
		hexStr = hexStr.replace(" ", "");
		if (hexStr.length() < 8)
			return hexStr.length() + 1; 
		String headerStr = hexStr.substring(0, 8);
		return bytesToLength(hexStringToBytes(headerStr), "left");
	}
	
	public static int bytesToLength(byte[] bytes, String level) {
		if (level.equals("left")) {
			int leng = bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF) << 16;
			return leng;
		} 
		if (level.equals("right")) {
			int leng = bytes[3] & 0xFF | (bytes[2] & 0xFF) << 8 | (bytes[1] & 0xFF) << 16;
			return leng;
		} 
		return 0;
	}
}
