//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package name.yumao.ffxiv.chn.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Vector;

public class FFXIVString {

	static final int START_BYTE = 2;
	static final int END_BYTE = 3;
	static final int TYPE_NONE = 0;
	static final int TYPE_TIME = 7;
	static final int TYPE_IF = 8;
	static final int TYPE_SWITCH = 9;
	static final int TYPE_NEWLINE = 16;
	static final int TYPE_ICON = 18;
	static final int TYPE_COLOR = 19;
	static final int TYPE_ITALICS = 26;
	static final int TYPE_INDENT = 29;
	static final int TYPE_ICON2 = 30;
	static final int TYPE_DASH = 31;
	static final int TYPE_SERVER_VALUE0 = 32;
	static final int TYPE_SERVER_VALUE1 = 33;
	static final int TYPE_SERVER_VALUE2 = 34;
	static final int TYPE_SERVER_VALUE3 = 35;
	static final int TYPE_SERVER_VALUE4 = 36;
	static final int TYPE_SERVER_VALUE5 = 37;
	static final int TYPE_SERVER_VALUE6 = 38;
	static final int TYPE_PLAYERLINK = 39;
	static final int TYPE_REFERENCE = 40;
	static final int TYPE_INFO = 41;
	static final int TYPE_LINK = 43;
	static final int TYPE_SPLIT = 44;
	static final int TYPE_REFERENCE_JA = 48;
	static final int TYPE_REFERENCE_EN = 49;
	static final int TYPE_REFERENCE_DE = 50;
	static final int TYPE_REFERENCE_FR = 51;
	static final int TYPE_REFERENCE2 = 64;
	static final int TYPE_ITEM_LOOKUP = 49;
	static final int TYPE_STYLE = 72;
	static final int TYPE_STYLE2 = 73;
	static final int INFO_NAME = 235;
	static final int INFO_GENDER = 233;
	static final int SIZE_DATATYPE_BYTE = 240;
	static final int SIZE_DATATYPE_BYTE256 = 241;
	static final int SIZE_DATATYPE_INT16 = 242;
	static final int SIZE_DATATYPE_INT24 = 250;
	static final int SIZE_DATATYPE_INT32 = 254;
	static final int DECODE_BYTE = 240;
	static final int DECODE_INT16_MINUS1 = 241;
	static final int DECODE_INT16_1 = 242;
	static final int DECODE_INT16_2 = 244;
	static final int DECODE_INT24_MINUS1 = 245;
	static final int DECODE_INT24 = 246;
	static final int DECODE_INT24_1 = 250;
	static final int DECODE_INT24_2 = 253;
	static final int DECODE_INT32 = 254;
	static final int DECODE_VARIABLE = 255;
	static final int COMPARISON_GE = 224;
	static final int COMPARISON_UN = 225;
	static final int COMPARISON_LE = 226;
	static final int COMPARISON_NEQ = 227;
	static final int COMPARISON_EQ = 228;
	static final int INFO_INTEGER = 232;
	static final int INFO_PLAYER = 233;
	static final int INFO_STRING = 234;
	static final int INFO_OBJECT = 235;
	
	// Hard Coded Payloads that are known
	static final byte[] forenamePayload = new byte[] {-1, 7, 2, 41, 3, -21, 2, 3, -1, 2, 32, 2, 3 };
	static final byte[] surnamePayload = new byte[] {-1, 7, 2, 41, 3, -21, 2, 3, -1, 2, 32, 3, 3 };
	
	public static String bytes2fstr(byte[] bytes) {
		return parseFFXIVString(bytes);
	}
	
	public static String parseFFXIVString(byte[] bytes) {
		try {
			LERandomBytes inBytes = new LERandomBytes(bytes, true, false);
			LERandomBytes outBytes = new LERandomBytes();
			while (inBytes.hasRemaining()) {
				byte b = inBytes.readByte();
				if (b == 2) {
					parsePayload(inBytes, outBytes);
					continue;
				} 
				outBytes.writeByte(b);
			} 
			return new String(outBytes.getWork(), "UTF-8");
		} catch (Exception e) {
			return "<unk:" + HexUtils.bytesToHexStringWithOutSpace(bytes) + ">";
		} 
	}
	
	private static void parsePayload(LERandomBytes inBytes, LERandomBytes outBytes) {
		int possition = inBytes.position();
		int type = inBytes.readByte() & 0xFF;
		int size = getBodySize(inBytes.readByte() & 0xFF, inBytes);
		byte[] body = new byte[size - 1];
		inBytes.readFully(body);
		inBytes.skip();
		long fullLength = (inBytes.position() - possition + 1);
		byte[] full = new byte[(int)fullLength];
		inBytes.seek(possition - 1);
		inBytes.readFully(full);
		String outString = null;
		switch (type) {
		
		} 
		outString = "<hex:" + HexUtils.bytesToHexStringWithOutSpace(full) + ">";
		outBytes.write(outString);
	}
	
	private static int getBodySize(int payloadSize, LERandomBytes inBytes) {
		if (payloadSize < 240)
			return payloadSize; 
		switch (payloadSize) {
			case 240:
				return inBytes.readInt8();
			case 241:
			case 242:
				return inBytes.readInt16();
			case 250:
				return inBytes.readInt24();
			case 254:
				return inBytes.readInt32();
		} 
		return payloadSize;
	}
	
	public static byte[] fstr2bytes(String fstr) {
		try {
			if (fstr.contains("<hex:") || fstr.contains("<unk:")) {
				LERandomBytes inBytes = new LERandomBytes(fstr.getBytes("UTF-8"), true, false);
				LERandomBytes outBytes = new LERandomBytes();
				Vector<byte[]> outBytesVector = (Vector)new Vector<>();
				byte[] newFFXIVStringBytes = new byte[0];
				while (inBytes.hasRemaining()) {
					byte b = inBytes.readByte();
					if (b == 60) {
						outBytesVector.add(outBytes.getWork());
						outBytes.clear();
						parsePayload2(inBytes, outBytes);
						outBytesVector.add(outBytes.getWork());
						outBytes.clear();
						continue;
					} 
					outBytes.writeByte(b);
				} 
				outBytesVector.add(outBytes.getWork());
				outBytes.clear();
				for (byte[] bytes : outBytesVector) {
					if (bytes.length > 0)
						newFFXIVStringBytes = ArrayUtil.append(newFFXIVStringBytes, bytes); 
				} 
				return newFFXIVStringBytes;
			} 
			return fstr.getBytes("UTF-8");
		} catch (Exception e) {
			return fstr.getBytes();
		} 
	}
	
	private static void parsePayload2(LERandomBytes inBytes, LERandomBytes outBytes) throws Exception {
		int start = inBytes.position() - 1;
		int end = 0;
		while (inBytes.hasRemaining()) {
			byte b = inBytes.readByte();
			if (b == 62)
				end = inBytes.position(); 
		} 
		inBytes.seek(start);
		byte[] hexStrBytes = new byte[end - start];
		inBytes.readFully(hexStrBytes);
		String hexStr = new String(hexStrBytes, "UTF-8");
		String hexBytesStr = hexStr.substring(hexStr.indexOf(":") + 1, hexStr.length() - 1);
		outBytes.write(HexUtils.hexStringToBytes(hexBytesStr));
	}
	
	public static String parseFFXIVString2(byte[] stringBytes) {
		try {
			byte[] newStringBytes = new byte[stringBytes.length * 4];
			ByteBuffer buffIn = ByteBuffer.wrap(stringBytes);
			buffIn.order(ByteOrder.LITTLE_ENDIAN);
			ByteBuffer buffOut = ByteBuffer.wrap(newStringBytes);
			buffIn.order(ByteOrder.LITTLE_ENDIAN);
			while (buffIn.hasRemaining()) {
				byte b = buffIn.get();
				if (b == 2) {
					try {
						processPacket(buffIn, buffOut);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} 
					continue;
				} 
				buffOut.put(b);
			} 
			try {
				return new String(newStringBytes, 0, buffOut.position(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
		} catch (Exception e) {
			try {
				return "<ERROR Parsing: " + new String(stringBytes, "UTF-8") + ">";
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} 
		} 
		return "ERROR";
	}
	
	private static void processPacket(ByteBuffer buffIn, ByteBuffer buffOut) throws UnsupportedEncodingException {
		int i;
		byte[] opt1, opt2, exdName;
		ByteBuffer payloadBB;
		StringBuilder builder;
		int pos2;
		String switchString2;
		int type = buffIn.get() & 0xFF;
		int payloadSize = buffIn.get() & 0xFF;
		if (payloadSize == 0)
			return; 
		payloadSize = getPayloadSize(payloadSize, buffIn);
		byte[] payload = new byte[payloadSize];
		buffIn.get(payload);
		switch (type) {
			case -33:
				for (i = 0; i < payload.length; i++) {
					System.out.print(String.format("0x%x ", new Object[] { Byte.valueOf(payload[i]) }));
				} 
				System.out.print("\n");
			case 41:
				if ((payload[0] & 0xFF) == 235 && (payload[1] & 0xFF) == 2) {
					buffOut.put("<forename surname>".getBytes("UTF-8"));
				} else {
					buffOut.put("<value>".getBytes("UTF-8"));
				} 
			case 44:
				if (Arrays.equals(payload, forenamePayload)) {
					buffOut.put("<forename>".getBytes("UTF-8"));
				} else if (Arrays.equals(payload, surnamePayload)) {
					buffOut.put("<surname>".getBytes("UTF-8"));
				} else {
					buffOut.put("<split:".getBytes("UTF-8"));
					int contentsSize = payload[1];
					buffOut.put(String.format("[%d]", new Object[] { Byte.valueOf(payload[1 + contentsSize + 1]) }).getBytes("UTF-8"));
					byte[] splitBuffer = new byte[contentsSize];
					System.arraycopy(payload, 2, splitBuffer, 0, splitBuffer.length - 2);
					ByteBuffer splitBB = ByteBuffer.wrap(splitBuffer);
					splitBB.position(1);
					byte[] outSplitProcessBuffer = new byte[512];
					ByteBuffer outSplitProcessBB = ByteBuffer.wrap(outSplitProcessBuffer);
					outSplitProcessBuffer = parseFFXIVString(splitBuffer).getBytes();
					buffOut.put((new String(outSplitProcessBuffer, 0, outSplitProcessBuffer.length, "UTF-8")).getBytes("UTF-8"));
					buffOut.put(">".getBytes("UTF-8"));
				} 
			case -34:
				opt1 = new byte[payload[4] - 1];
				opt2 = new byte[payload[4 + payload[4]] - 1];
				System.arraycopy(payload, 4, opt1, 0, opt1.length);
				System.arraycopy(payload, 5 + payload[3], opt2, 0, opt2.length);
				buffOut.put("<".getBytes("UTF-8"));
				if (opt1[0] == 2) {
					ByteBuffer optionPayload = ByteBuffer.wrap(opt1);
					optionPayload.get();
					processPacket(optionPayload, buffOut);
				} else {
					buffOut.put(opt1);
				} 
				buffOut.put("/".getBytes("UTF-8"));
				if (opt2[0] == 2) {
					ByteBuffer optionPayload = ByteBuffer.wrap(opt1);
					optionPayload.get();
					processPacket(optionPayload, buffOut);
				} else {
					buffOut.put(opt2);
				} 
				buffOut.put(">".getBytes("UTF-8"));
			case 26:
				if (payload[0] == 2) {
					buffOut.put("<i>".getBytes("UTF-8"));
				} else {
					buffOut.put("</i>".getBytes("UTF-8"));
				} 
			case 7:
				return;
			case 19:
				if (payload[0] == -20) {
					buffOut.put("</color>".getBytes("UTF-8"));
				} else if (payload[0] == -2) {
					buffOut.put(String.format("<color #%02X%02X%02X>", new Object[] { Byte.valueOf(payload[2]), Byte.valueOf(payload[3]), Byte.valueOf(payload[4]) }).getBytes("UTF-8"));
				} else {
					buffOut.put("<color?>".getBytes("UTF-8"));
				} 
			case 43:
				buffOut.put(String.format("<2b?:0x%x, 0x%x, 0x%x>", new Object[] { Byte.valueOf(payload[0]), Byte.valueOf(payload[1]), Byte.valueOf(payload[2]) }).getBytes("UTF-8"));
			case 40:
				exdName = new byte[payload[1] - 1];
				System.arraycopy(payload, 2, exdName, 0, exdName.length);
				buffOut.put(String.format("<ref:%s>", new Object[] { new String(exdName) }).getBytes("UTF-8"));
			case 64:
				exdName = new byte[payload[6] - 1];
				System.arraycopy(payload, 7, exdName, 0, exdName.length);
				buffOut.put(String.format("<ref:%s>", new Object[] { new String(exdName) }).getBytes("UTF-8"));
			case 8:
				payloadBB = ByteBuffer.wrap(payload);
				payloadBB.order(ByteOrder.LITTLE_ENDIAN);
				builder = new StringBuilder();
				builder.append("<if");
				builder.append("(");
				processCondition(payloadBB, builder);
				builder.append(") {");
				decode(payloadBB, builder);
				builder.append("} else {");
				decode(payloadBB, builder);
				builder.append("}>");
				buffOut.put(builder.toString().getBytes("UTF-8"));
			case 9:
				pos2 = 1;
				switchString2 = "<switch:";
				if (payload[0] == -35 || payload[0] == -24) {
					if (payload[0] == -24)
						pos2++; 
					while (true) {
						pos2++;
						int stringSize = payload[pos2];
						pos2++;
						if (stringSize - 1 != 0) {
							byte[] switchBuffer = new byte[stringSize - 1];
							System.arraycopy(payload, pos2, switchBuffer, 0, stringSize - 1);
							if (switchBuffer[0] == 2) {
								ByteBuffer switchBB = ByteBuffer.wrap(switchBuffer);
								switchBB.position(1);
								byte[] outProcessBuffer = new byte[512];
								ByteBuffer outProcessBB = ByteBuffer.wrap(outProcessBuffer);
								processPacket(switchBB, outProcessBB);
								switchString2 = switchString2 + new String(outProcessBuffer, 0, outProcessBB.position(), "UTF-8");
							} else {
								switchString2 = switchString2 + new String(switchBuffer, "UTF-8");
							} 
						} 
						pos2 += stringSize - 1;
						if (payload[pos2] == 3)
							break; 
						switchString2 = switchString2 + "/";
					} 
				} else if (payload[0] == -37) {
					switchString2 = switchString2 + "?";
				} 
				buffOut.put((switchString2 + ">").getBytes("UTF-8"));
			case 16:
				buffOut.put("<br>".getBytes("UTF-8"));
			case 49:
				buffOut.put("<item>".getBytes("UTF-8"));
			case 18:
			case 30:
				buffOut.put(String.format("<icon:%d>", new Object[] { Byte.valueOf(payload[0]) }).getBytes("UTF-8"));
			case 31:
				buffOut.put("-".getBytes("UTF-8"));
			case 32:
			case 34:
			case 35:
				buffOut.put(String.format("<value>%s</value>", new Object[] { HexUtils.bytesToHexStringWithOutSpace(payload) }).getBytes("UTF-8"));
			case 39:
				return;
		} 
		String unknownMsg = String.format("<?0x%x>", new Object[] { Integer.valueOf(type) });
		buffOut.put(unknownMsg.getBytes("UTF-8"));
	}
	
	private static void processCondition(ByteBuffer buffIn, StringBuilder builder) {
		String compareStr;
		int code = buffIn.get() & 0xFF;
		switch (code) {
			case 228:
				compareStr = "==";
				break;
			case 226:
				compareStr = "<=";
				break;
			case 224:
				compareStr = ">=";
				break;
			case 225:
				compareStr = "?";
				break;
			default:
				decode(buffIn, builder);
				return;
		} 
		decode(buffIn, builder);
		builder.append(compareStr);
		decode(buffIn, builder);
	}
	
	private static void decode(ByteBuffer buffIn, StringBuilder builder) {
		int size;
		byte[] data;
		int code = buffIn.get() & 0xFF;
		if (code < 208) {
			builder.append(" " + code + " ");
			return;
		} 
		if (code < 224) {
			builder.append(" " + code + " ");
			return;
		} 
		switch (code) {
			case 232:
				builder.append("INT:");
				decode(buffIn, builder);
				return;
			case 233:
				builder.append("PLYR:");
				decode(buffIn, builder);
				return;
			case 234:
				builder.append("STR:");
				decode(buffIn, builder);
				return;
			case 235:
				builder.append("OBJ:");
				decode(buffIn, builder);
				return;
			case 255:
				size = buffIn.get() & 0xFF;
				size = getPayloadSize(size, buffIn) - 1;
				data = new byte[size];
				buffIn.get(data);
				builder.append(parseFFXIVString(data));
				break;
		} 
	}
	
	private static void getParam(ByteBuffer buffIn, StringBuilder builder) {}
	
	private static int getPayloadSize(int payloadSize, ByteBuffer buffIn) {
		int valByte;
		int val24;
		if (payloadSize < 240)
			return payloadSize; 
		switch (payloadSize) {
			case 240:
				valByte = buffIn.get() & 0xFF;
				return valByte;
			case 241:
			case 242:
				return buffIn.getShort();
			case 250:
				val24 = 0;
				val24 |= buffIn.get() << 16;
				val24 |= buffIn.get() << 8;
				val24 |= buffIn.get();
				return val24;
			case 254:
				return buffIn.getInt();
		} 
		return payloadSize;
	}
	
	public static void main(String[] args) {
		String hex = "E8AEB2E8BFB0E2809C0208F0E0E4E802F24E4FFF13E8B1AAE7A59EE9A1BBE4BD90E4B98BE794B7FFC50208C1E4E802F24E51FF13E7BE8EE7A59EE59089E7A5A5E5A4A9E5A5B3FFA50208A1E4E802F24E52FF07E7A59EE9BE99FF9102088DE4E802F27569FF13E9BE99E7A59EE5B7B4E59388E5A786E789B9FF7102086DE4E802F27573FF0DE7A9B6E69E81E7A59EE585B5FF57020853E4E802F24E54FF07E799BDE8998EFF4302083FE4E802F24E58FF0DE5A49CE7A59EE69C88E8AFBBFF29020825E4E802F24E5AFF07E69CB1E99B80FF15020811E4E802F24E5DFF07E99D92E9BE99FF01030303030303030303E2809DE79A84E69585E4BA8B";
		String fstr = parseFFXIVString(HexUtils.hexStringToBytes(hex));
		System.out.println(new String(HexUtils.hexStringToBytes(hex)));
		System.out.println(fstr);
		String fstr2 = "讲述“<hex:0208F0E0E4E802F24E4FFF13E8B1AAE7A59EE9A1BBE4BD90E4B98BE794B7FFC50208C1E4E802F24E51FF13E7BE8EE7A59EE59089E7A5A5E5A4A9E5A5B3FFA50208A1E4E802F24E52FF07E7A59EE9BE99FF9102088DE4E802F27569FF13E9BE99E7A59EE5B7B4E59388E5A786E789B9FF7102086DE4E802F27573FF0DE7A9B6E69E81E7A59EE585B5FF57020853E4E802F24E54FF07E799BDE8998EFF4302083FE4E802F24E58FF0DE5A49CE7A59EE69C88E8AFBBFF29020825E4E802F24E5AFF07E69CB1E99B80FF15020811E4E802F24E5DFF07E99D92E9BE99FF010303030303030303>\003”的故事";
		System.out.println(new String(fstr2bytes(fstr2)));
		System.out.println(hex);
		System.out.println(HexUtils.bytesToHexStringWithOutSpace(fstr2bytes(fstr2)));
		System.out.println(HexUtils.bytesToHexString("<>".getBytes()));
	}
}
