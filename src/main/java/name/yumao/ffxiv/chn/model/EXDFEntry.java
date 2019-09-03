package name.yumao.ffxiv.chn.model;

import name.yumao.ffxiv.chn.util.ArrayUtil;

import java.nio.ByteBuffer;

public class EXDFEntry {
    
    private byte[] chunk;
    private byte[] string;
    private byte[] data;

    public EXDFEntry(byte[] data, int datasetChunkSize) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.chunk = new byte[datasetChunkSize];
        buffer.get(this.chunk);
        this.string = new byte[data.length - datasetChunkSize];
        buffer.get(this.string);
        this.data = ArrayUtil.append(chunk, string);
    }

    public byte getByte(int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(this.data);
        buffer.position(offset);
        return buffer.get();
    }

    public short getShort(int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(this.data);
        buffer.position(offset);
        return buffer.getShort();
    }

    public int[] getQuad(short offset) {
        ByteBuffer buffer = ByteBuffer.wrap(this.data);
        buffer.position(offset);

        int[] quad = new int[4];
        quad[0] = buffer.getShort();
        quad[1] = buffer.getShort();
        quad[2] = buffer.getShort();
        quad[3] = buffer.getShort();

        return quad;
    }

    public boolean getByteBool(int datatype, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(this.data);
        buffer.position(offset);
        int val = buffer.get();
        int shift = datatype - 25;
        int i = 1 << shift;
        val &= i;
        return (val & i) == i;
    }

    public int getInt(int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(this.data);
        buffer.position(offset);
        return buffer.getInt();
    }

    public float getFloat(short offset) {
        ByteBuffer buffer = ByteBuffer.wrap(this.data);
        buffer.position(offset);
        return buffer.getFloat();
    }

    public byte[] getString( short offset) {
        int datasetChunkSize = this.chunk.length;
        ByteBuffer buffer = ByteBuffer.wrap(this.data);
        buffer.position(offset);
        int stringOffset = buffer.getInt();

        if(datasetChunkSize + stringOffset >= buffer.limit()){
            return new byte[0];
        }

        buffer.position(datasetChunkSize + stringOffset);
        int nullTermPos = -1;
        byte in;
        do in = buffer.get();
        while (in != 0);

        nullTermPos = buffer.position() - (datasetChunkSize + stringOffset);

        byte[] stringBytes = new byte[nullTermPos - 1];
        buffer.position(datasetChunkSize + stringOffset);
        buffer.get(stringBytes);

        return stringBytes;
    }

    public boolean getBoolean(short offset) {
        byte b = getByte(offset);
        return b == 1;
    }

    public boolean getByteBool(short offset2) {
        return false;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public byte[] getString() {
        return string;
    }

    public byte[] getData() {
        return data;
    }
}