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
    final static int START_BYTE 		   = 0x02;
    final static int END_BYTE 			   = 0x03;

    final static int TYPE_NONE             = 0x00;
    final static int TYPE_TIME             = 0x07;
    final static int TYPE_IF               = 0x08;
    final static int TYPE_SWITCH           = 0x09;
    final static int TYPE_NEWLINE          = 0x10;
    final static int TYPE_ICON             = 0x12;
    final static int TYPE_COLOR            = 0x13;

    final static int TYPE_ITALICS          = 0x1a;
    final static int TYPE_INDENT           = 0x1d;
    final static int TYPE_ICON2            = 0x1e;
    final static int TYPE_DASH             = 0x1f;

    final static int TYPE_SERVER_VALUE0    = 0x20;
    final static int TYPE_SERVER_VALUE1    = 0x21;
    final static int TYPE_SERVER_VALUE2    = 0x22;
    final static int TYPE_SERVER_VALUE3    = 0x23;
    final static int TYPE_SERVER_VALUE4    = 0x24;
    final static int TYPE_SERVER_VALUE5    = 0x25;
    final static int TYPE_SERVER_VALUE6    = 0x26;

    final static int TYPE_PLAYERLINK       = 0x27;

    final static int TYPE_REFERENCE        = 0x28;
    final static int TYPE_INFO             = 0x29;

    final static int TYPE_LINK             = 0x2b;
    final static int TYPE_SPLIT            = 0x2c;

    final static int TYPE_REFERENCE_JA     = 0x30;
    final static int TYPE_REFERENCE_EN     = 0x31;
    final static int TYPE_REFERENCE_DE     = 0x32;
    final static int TYPE_REFERENCE_FR     = 0x33;
    final static int TYPE_REFERENCE2       = 0x40;

    final static int TYPE_ITEM_LOOKUP      = 0x31;

    final static int TYPE_STYLE            = 0x48;
    final static int TYPE_STYLE2           = 0x49;

    final static int INFO_NAME = 235;
    final static int INFO_GENDER = 233;

    final static int SIZE_DATATYPE_BYTE    = 0xF0;
    final static int SIZE_DATATYPE_BYTE256 = 0xF1;
    final static int SIZE_DATATYPE_INT16   = 0xF2;
    final static int SIZE_DATATYPE_INT24   = 0xFA;
    final static int SIZE_DATATYPE_INT32   = 0xFE;

    final static int DECODE_BYTE	   	   = 0xF0;
    final static int DECODE_INT16_MINUS1   = 0xF1;
    final static int DECODE_INT16_1   	   = 0xF2;
    final static int DECODE_INT16_2   	   = 0xF4;
    final static int DECODE_INT24_MINUS1   = 0xF5;
    final static int DECODE_INT24          = 0xF6;
    final static int DECODE_INT24_1   	   = 0xFA;
    final static int DECODE_INT24_2   	   = 0xFD;
    final static int DECODE_INT32   	   = 0xFE;
    final static int DECODE_VARIABLE   	   = 0xFF;

    final static int COMPARISON_GE   	   = 0xE0;
    final static int COMPARISON_UN   	   = 0xE1;
    final static int COMPARISON_LE   	   = 0xE2;
    final static int COMPARISON_NEQ   	   = 0xE3;
    final static int COMPARISON_EQ   	   = 0xE4;

    final static int INFO_INTEGER		   = 0xE8;
    final static int INFO_PLAYER		   = 0xE9;
    final static int INFO_STRING		   = 0xEA;
    final static int INFO_OBJECT		   = 0xEB;

    //Hard Coded Payloads that are known
    final static byte forenamePayload[] = {-1, 7, 2, 41, 3, -21, 2, 3, -1, 2, 32, 2, 3};
    final static byte surnamePayload[] = {-1, 7, 2, 41, 3, -21, 2, 3, -1, 2, 32, 3, 3};

    public static String bytes2fstr(byte[] bytes){
        return parseFFXIVString(bytes);
    }
    public static String parseFFXIVString(byte[] bytes){
        try{
            LERandomBytes inBytes = new LERandomBytes(bytes, true, false);
            LERandomBytes outBytes = new LERandomBytes();
            while (inBytes.hasRemaining()){
                byte b = inBytes.readByte();
                if (b == START_BYTE) {
                    parsePayload(inBytes, outBytes);
                } else {
                    outBytes.writeByte(b);
                }
            }
            return new String(outBytes.getWork(), "UTF-8");
        }catch (Exception e){
            return "<unk:" + HexUtils.bytesToHexStringWithOutSpace(bytes) + ">";
        }
    }

    private static void parsePayload(LERandomBytes inBytes, LERandomBytes outBytes) {
        int possition = inBytes.position();
        int type = inBytes.readByte() & 0xFF;
        int size = getBodySize((inBytes.readByte() & 0xFF), inBytes);

        byte[] body = new byte[size - 1];
        inBytes.readFully(body);
        inBytes.skip();

        long fullLength = inBytes.position() - possition + 1;
        byte[] full = new byte[(int)fullLength];
        inBytes.seek(possition - 1);
        inBytes.readFully(full);

        String outString = null;
        switch (type){
//            case TYPE_NONE:
//                // 无格式
//                outString = "<none:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_TIME:
//                // 时间
//                outString = "<time:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_IF:
//                break;
//            case TYPE_SWITCH:
//                break;
//            case TYPE_NEWLINE:
//                // 换行
//                outString = "<br>";
//                outBytes.write(outString);
//                break;
//            case TYPE_ICON:
//                // 图标1
//                outString = "<icon1:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_COLOR:
//                // 颜色
//
//                if(body.length == 1 && body[0] == (byte)0xEC){
//                    outString = "</color>";
//                }else {
//                    outString = "<color:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                }
//                outBytes.write(outString);
//                break;
//            case TYPE_ITALICS:
//                // 斜体
//                outString = "<italics:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_INDENT:
//                // 缩进
//                outString = "<indent:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_ICON2:
//                // 图标2
//                outString = "<icon2:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_DASH:
//                // 破折号
//                outString = "<dash>";
//                outBytes.write(outString);
//                break;
//
//            // 服务器传值
//            case TYPE_SERVER_VALUE0:
//                outString = "<value0:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_SERVER_VALUE1:
//                outString = "<value1:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_SERVER_VALUE2:
//                outString = "<value2:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_SERVER_VALUE3:
//                outString = "<value3:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_SERVER_VALUE4:
//                outString = "<value4:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_SERVER_VALUE5:
//                outString = "<value5:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_SERVER_VALUE6:
//                outString = "<value6:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//
//            case TYPE_PLAYERLINK:
//                // 玩家链接
//                outString = "<plink:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_REFERENCE:
//                // 引用
//                outString = "<ref:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_INFO:
//                // 信息
//                outString = "<info:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_LINK:
//                // 链接
//                outString = "<link:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//            case TYPE_SPLIT:
//                // 分割
//                outString = "<split:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                outBytes.write(outString);
//                break;
//
//            case TYPE_STYLE:
//                if(body.length == 1 && body[0] == (byte)0x01){
//                    outString = "</style>";
//                }else {
//                    outString = "<style:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                }
//                outBytes.write(outString);
//                break;
//            case TYPE_STYLE2:
//                if(body.length == 1 && body[0] == (byte)0x01){
//                    outString = "</style2>";
//                }else {
//                    outString = "<style2:" + HexUtils.bytesToHexStringWithOutSpace(body) + ">";
//                }
//                outBytes.write(outString);
//                break;
            default:
                outString = "<hex:" + HexUtils.bytesToHexStringWithOutSpace(full) + ">";
                outBytes.write(outString);
        }
    }

    private static int getBodySize(int payloadSize, LERandomBytes inBytes) {
        if (payloadSize < 0xF0)
            return payloadSize;
        switch (payloadSize){
            case SIZE_DATATYPE_BYTE:
                return inBytes.readInt8();
            case SIZE_DATATYPE_BYTE256:
            case SIZE_DATATYPE_INT16:
                return inBytes.readInt16();
            case SIZE_DATATYPE_INT24:
                return inBytes.readInt24();
            case SIZE_DATATYPE_INT32:
                return inBytes.readInt32();
        }
        return payloadSize;
    }

    public static byte[] fstr2bytes(String fstr){
        try {
            if (fstr.contains("<hex:") || fstr.contains("<unk:")){
                LERandomBytes inBytes = new LERandomBytes(fstr.getBytes("UTF-8"), true, false);
                LERandomBytes outBytes = new LERandomBytes();
                Vector<byte[]> outBytesVector = new Vector<byte[]>();

                byte[] newFFXIVStringBytes = new byte[0];
                while (inBytes.hasRemaining()){
                    byte b = inBytes.readByte();
                    if (b == (byte)0x3C) {
                        outBytesVector.add(outBytes.getWork());
                        outBytes.clear();
                        parsePayload2(inBytes, outBytes);
                        outBytesVector.add(outBytes.getWork());
                        outBytes.clear();
                    } else {
                        outBytes.writeByte(b);
                    }
                }
                outBytesVector.add(outBytes.getWork());
                outBytes.clear();
                for(byte[] bytes: outBytesVector){
                    if (bytes.length > 0) {
                        newFFXIVStringBytes = ArrayUtil.append(newFFXIVStringBytes, bytes);
                    }
                }
                return newFFXIVStringBytes;
            } else {
                return fstr.getBytes("UTF-8");
            }
        } catch (Exception e) {
            return fstr.getBytes();
        }
    }
    private static void parsePayload2(LERandomBytes inBytes, LERandomBytes outBytes) throws Exception{
        int start = inBytes.position() - 1;
        int end = 0;
        while (inBytes.hasRemaining()){
            byte b = inBytes.readByte();
            if (b == (byte)0x3E) {
                end = inBytes.position();
            }
        }
        inBytes.seek(start);
        byte[] hexStrBytes = new byte[end - start];
        inBytes.readFully(hexStrBytes);
        String hexStr = new String(hexStrBytes, "UTF-8");
        String hexBytesStr = hexStr.substring(hexStr.indexOf(":") + 1 , hexStr.length() - 1);
        outBytes.write(HexUtils.hexStringToBytes(hexBytesStr));
    }
    public static String parseFFXIVString2(byte[] stringBytes) {

        try{
            byte[] newStringBytes = new byte[stringBytes.length*4];

            ByteBuffer buffIn = ByteBuffer.wrap(stringBytes);
            buffIn.order(ByteOrder.LITTLE_ENDIAN);
            ByteBuffer buffOut = ByteBuffer.wrap(newStringBytes);
            buffIn.order(ByteOrder.LITTLE_ENDIAN);

            while (buffIn.hasRemaining())
            {
                byte b = buffIn.get();

                if (b == START_BYTE)
                    try {
                        processPacket(buffIn, buffOut);
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                else
                    buffOut.put(b);
            }

            try {
                return new String(newStringBytes, 0, buffOut.position(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {try {
            return "<ERROR Parsing: "+ new String(stringBytes, "UTF-8")+">";
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }}
        return "ERROR";
    }

    private static void processPacket(ByteBuffer buffIn, ByteBuffer buffOut) throws UnsupportedEncodingException {
        int type = buffIn.get() & 0xFF;
        int payloadSize = buffIn.get() & 0xFF;

        if (payloadSize == 0)
        {
//            switch (type)
//            {
//                case TYPE_NEWLINE:
//                    buffOut.put("<br>".getBytes("UTF-8"));
//                    break;
//            }
            return;
        }

        payloadSize = getPayloadSize(payloadSize, buffIn);

        byte[] payload = new byte[payloadSize];

        buffIn.get(payload);
        // drop 0x03
//        buffIn.get();

        switch (type)
        {
            case (byte) 223:
                for (int i = 0; i < payload.length; i++)
                    System.out.print(String.format("0x%x ", payload[i]));
                System.out.print("\n");

                break;
            case TYPE_INFO:
                if ((((int)payload[0])&0xFF) == 0xEB && (((int)payload[1])&0xFF) == 0x02)
                    buffOut.put("<forename surname>".getBytes("UTF-8"));
                else
                    buffOut.put("<value>".getBytes("UTF-8"));
                break;
            case TYPE_SPLIT:

                if (Arrays.equals(payload, forenamePayload))
                {
                    buffOut.put("<forename>".getBytes("UTF-8"));
                    break;
                }
                else if (Arrays.equals(payload, surnamePayload))
                {
                    buffOut.put("<surname>".getBytes("UTF-8"));
                    break;
                }

                buffOut.put("<split:".getBytes("UTF-8"));

                int contentsSize = payload[1];

                buffOut.put(String.format("[%d]", payload[1 + contentsSize + 1]).getBytes("UTF-8"));

                byte splitBuffer[] = new byte[contentsSize];
                System.arraycopy(payload, 2, splitBuffer, 0, splitBuffer.length-2);
                ByteBuffer splitBB = ByteBuffer.wrap(splitBuffer);
                splitBB.position(1);
                byte[] outSplitProcessBuffer = new byte[512];
                ByteBuffer outSplitProcessBB = ByteBuffer.wrap(outSplitProcessBuffer);
                outSplitProcessBuffer = parseFFXIVString(splitBuffer).getBytes();
                buffOut.put((new String(outSplitProcessBuffer, 0, outSplitProcessBuffer.length, "UTF-8").getBytes("UTF-8")));

                buffOut.put(">".getBytes("UTF-8"));
                break;
            case (byte) 222:
                byte opt1[] = new byte[payload[4]-1];
                byte opt2[] = new byte[payload[4+payload[4]]-1];

                System.arraycopy(payload, 4, opt1, 0, opt1.length);
                System.arraycopy(payload, 5+payload[3], opt2, 0, opt2.length);

                buffOut.put("<".getBytes("UTF-8"));
                if (opt1[0] == 0x02)
                {
                    ByteBuffer optionPayload = ByteBuffer.wrap(opt1);
                    optionPayload.get(); //Skip start flag
                    processPacket(optionPayload, buffOut);
                }
                else
                    buffOut.put(opt1);
                buffOut.put("/".getBytes("UTF-8"));
                if (opt2[0] == 0x02)
                {
                    ByteBuffer optionPayload = ByteBuffer.wrap(opt1);
                    optionPayload.get(); //Skip start flag
                    processPacket(optionPayload, buffOut);
                }
                else
                    buffOut.put(opt2);
                buffOut.put(">".getBytes("UTF-8"));
                break;
            case TYPE_ITALICS:
                if (payload[0] == 2)
                    buffOut.put("<i>".getBytes("UTF-8"));
                else
                    buffOut.put("</i>".getBytes("UTF-8"));
                break;
            case TYPE_TIME:
                break;
            case TYPE_COLOR:
                if (payload[0] == -20)
                    buffOut.put("</color>".getBytes("UTF-8"));
                else if (payload[0] == -2)
                    buffOut.put(String.format("<color #%02X%02X%02X>", payload[2], payload[3], payload[4]).getBytes("UTF-8"));
                else buffOut.put("<color?>".getBytes("UTF-8"));
                break;
            case TYPE_LINK:

			/*byte unknownBuffer[] = new byte[payload[1]];
			System.arraycopy(payload, 2, unknownBuffer, 0, unknownBuffer.length);
			ByteBuffer unknownBB = ByteBuffer.wrap(unknownBuffer);
			unknownBB.position(1);
			byte[] outUnknownProcessBuffer = new byte[512];
			ByteBuffer outUnknownProcessBB = ByteBuffer.wrap(outUnknownProcessBuffer);
			processPacket(unknownBB, outUnknownProcessBB);*/

                buffOut.put(String.format("<2b?:0x%x, 0x%x, 0x%x>", payload[0], payload[1], payload[2]).getBytes("UTF-8"));

                break;
            case TYPE_REFERENCE:
                byte exdName[] = new byte[payload[1]-1];
                System.arraycopy(payload, 2, exdName, 0, exdName.length);
                buffOut.put(String.format("<ref:%s>", new String(exdName)).getBytes("UTF-8"));
                break;
            case TYPE_REFERENCE2:
                exdName = new byte[payload[6]-1];
                System.arraycopy(payload, 7, exdName, 0, exdName.length);
                buffOut.put(String.format("<ref:%s>", new String(exdName)).getBytes("UTF-8"));
                break;
            case TYPE_IF:

                ByteBuffer payloadBB = ByteBuffer.wrap(payload);
                payloadBB.order(ByteOrder.LITTLE_ENDIAN);

                StringBuilder builder = new StringBuilder();

                builder.append("<if");

                builder.append("(");
                processCondition(payloadBB, builder);
                builder.append(") {");
                decode(payloadBB, builder);
                builder.append("} else {");
                decode(payloadBB, builder);
                builder.append("}>");

                buffOut.put(builder.toString().getBytes("UTF-8"));

                break;
            case TYPE_SWITCH:
                int pos2 = 1;
                String switchString2 = "<switch:";

                if (payload[0] == -35 || payload[0] == -24){
                    if (payload[0] == -24)
                        pos2++;
                    while (true)
                    {
                        pos2++;
                        int stringSize = payload[pos2];
                        pos2++;
                        if (stringSize-1 != 0){
                            byte switchBuffer[] = new byte[stringSize-1];
                            System.arraycopy(payload, pos2, switchBuffer, 0, stringSize-1);
                            if (switchBuffer[0] == 0x02)
                            {
                                ByteBuffer switchBB = ByteBuffer.wrap(switchBuffer);
                                switchBB.position(1);
                                byte[] outProcessBuffer = new byte[512];
                                ByteBuffer outProcessBB = ByteBuffer.wrap(outProcessBuffer);
                                processPacket(switchBB, outProcessBB);
                                switchString2 += new String(outProcessBuffer, 0, outProcessBB.position(), "UTF-8");
                            }
                            else
                                switchString2 += new String(switchBuffer, "UTF-8");
                        }
                        pos2+=stringSize-1;
                        if (payload[pos2] == 0x03)
                            break;
                        switchString2 += "/";
                    }
                }
                else if (payload[0] == -37)
                {
                    switchString2 += "?";
                }

                buffOut.put((switchString2+">").getBytes("UTF-8"));
                break;
            case TYPE_NEWLINE:
                buffOut.put("<br>".getBytes("UTF-8"));
                break;
            case TYPE_ITEM_LOOKUP:
                buffOut.put("<item>".getBytes("UTF-8"));
                break;
            case TYPE_ICON:
            case TYPE_ICON2:
                buffOut.put(String.format("<icon:%d>", payload[0]).getBytes("UTF-8"));
                break;
            case TYPE_DASH:
                buffOut.put("-".getBytes("UTF-8"));
                break;
            case TYPE_SERVER_VALUE0:
            case TYPE_SERVER_VALUE2:
                //case TYPE_SERVER_VALUE3:
            case TYPE_SERVER_VALUE3:
                //case TYPE_SERVER_VALUE5:
                buffOut.put(String.format("<value>%s</value>", HexUtils.bytesToHexStringWithOutSpace(payload)).getBytes("UTF-8"));
                break;
            case TYPE_PLAYERLINK:
                break;
            default:
                String unknownMsg = String.format("<?0x%x>", type);
                buffOut.put(unknownMsg.getBytes("UTF-8"));
                break;
        }

    }

    private static void processCondition(ByteBuffer buffIn, StringBuilder builder) {

        String compareStr;
        int code = buffIn.get() & 0xFF;
        switch (code)
        {
            case COMPARISON_EQ:
                compareStr = "==";
                break;
            case COMPARISON_LE:
                compareStr = "<=";
                break;
            case COMPARISON_GE:
                compareStr = ">=";
                break;
            case COMPARISON_UN:
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

    private static void decode(ByteBuffer buffIn, StringBuilder builder)
    {
        int code = buffIn.get() & 0xFF;

        if (code < 0xD0)
        {
            builder.append(" " + code + " ");
            return;
        }
        else if (code < 0xE0)
        {
            builder.append(" " + code + " ");
            return;
        }

        switch (code)
        {
            case INFO_INTEGER:
                builder.append("INT:");
                decode(buffIn, builder);
                return;
            case INFO_PLAYER:
                builder.append("PLYR:");
                decode(buffIn, builder);
                return;
            case INFO_STRING:
                builder.append("STR:");
                decode(buffIn, builder);
                return;
            case INFO_OBJECT:
                builder.append("OBJ:");
                decode(buffIn, builder);
                return;
            case DECODE_VARIABLE:
                int size = buffIn.get() & 0xFF;
                size = getPayloadSize(size, buffIn)-1;
                byte data[] = new byte[size];
                buffIn.get(data);
                builder.append(parseFFXIVString(data));
                break;
        }
    }

    private static void getParam(ByteBuffer buffIn, StringBuilder builder)
    {

    }

    private static int getPayloadSize(int payloadSize, ByteBuffer buffIn) {

        if (payloadSize < (int)0xF0)
            return payloadSize;

        switch (payloadSize)
        {
            case SIZE_DATATYPE_BYTE:
            {
                int valByte = buffIn.get() & 0xFF;
                return valByte;
            }
            case SIZE_DATATYPE_BYTE256:
            case SIZE_DATATYPE_INT16:
                return buffIn.getShort();
            case SIZE_DATATYPE_INT24:
                int val24 = 0;
                val24 |= buffIn.get() << 16;
                val24 |= buffIn.get() << 8;
                val24 |= buffIn.get();
                return val24;
            case SIZE_DATATYPE_INT32:
                return buffIn.getInt();
        }

        return payloadSize;
    }

    public static void main(String[] args) {
        String hex = "E8AEB2E8BFB0E2809C0208F0E0E4E802F24E4FFF13E8B1AAE7A59EE9A1BBE4BD90E4B98BE794B7FFC50208C1E4E802F24E51FF13E7BE8EE7A59EE59089E7A5A5E5A4A9E5A5B3FFA50208A1E4E802F24E52FF07E7A59EE9BE99FF9102088DE4E802F27569FF13E9BE99E7A59EE5B7B4E59388E5A786E789B9FF7102086DE4E802F27573FF0DE7A9B6E69E81E7A59EE585B5FF57020853E4E802F24E54FF07E799BDE8998EFF4302083FE4E802F24E58FF0DE5A49CE7A59EE69C88E8AFBBFF29020825E4E802F24E5AFF07E69CB1E99B80FF15020811E4E802F24E5DFF07E99D92E9BE99FF01030303030303030303E2809DE79A84E69585E4BA8B";
        String fstr = FFXIVString.parseFFXIVString(HexUtils.hexStringToBytes(hex));
        System.out.println(new String(HexUtils.hexStringToBytes(hex)));
        System.out.println(fstr);
        String fstr2 = "讲述“<hex:0208F0E0E4E802F24E4FFF13E8B1AAE7A59EE9A1BBE4BD90E4B98BE794B7FFC50208C1E4E802F24E51FF13E7BE8EE7A59EE59089E7A5A5E5A4A9E5A5B3FFA50208A1E4E802F24E52FF07E7A59EE9BE99FF9102088DE4E802F27569FF13E9BE99E7A59EE5B7B4E59388E5A786E789B9FF7102086DE4E802F27573FF0DE7A9B6E69E81E7A59EE585B5FF57020853E4E802F24E54FF07E799BDE8998EFF4302083FE4E802F24E58FF0DE5A49CE7A59EE69C88E8AFBBFF29020825E4E802F24E5AFF07E69CB1E99B80FF15020811E4E802F24E5DFF07E99D92E9BE99FF010303030303030303>\u0003”的故事";
        System.out.println(new String(FFXIVString.fstr2bytes(fstr2)));
        System.out.println(hex);
        System.out.println(HexUtils.bytesToHexStringWithOutSpace(FFXIVString.fstr2bytes(fstr2)));
        System.out.println(HexUtils.bytesToHexString("<>".getBytes()));
    }
}
