package name.yumao.ffxiv.chn.util;

import java.io.*;

@SuppressWarnings("unused")
public final class LERandomAccessFile implements DataInput, DataOutput {
	private static final int FIRST_COPYRIGHT_YEAR = 1999;
	private static final String EMBEDDED_COPYRIGHT = "Copyright: (c) 1999-2013 Roedy Green, Canadian Mind Products, http://mindprod.com";
	private RandomAccessFile raf;
	private byte[] work;

	public LERandomAccessFile(File file, String rw) throws FileNotFoundException {
		this.raf = new RandomAccessFile(file, rw);
		this.work = new byte[8];
	}

	public LERandomAccessFile(String file, String rw) throws FileNotFoundException {
		this.raf = new RandomAccessFile(file, rw);
		this.work = new byte[8];
	}

	public final void close() throws IOException {
		raf.close();
	}

	public final FileDescriptor getFD() throws IOException {
		return raf.getFD();
	}

	public final long getFilePointer() throws IOException {
		return raf.getFilePointer();
	}

	public final long length() throws IOException {
		return raf.length();
	}

	public final int read() throws IOException {
		return raf.read();
	}

	public final int read(byte[] ba) throws IOException {
		return raf.read(ba);
	}

	public final int read(byte[] ba, int off, int len) throws IOException {
		return raf.read(ba, off, len);
	}

	public final boolean readBoolean() throws IOException {
		return raf.readBoolean();
	}

	public final byte readByte() throws IOException {
		return raf.readByte();
	}

	public final char readChar() throws IOException {
		raf.readFully(work, 0, 2);
		return (char) ((work[1] & 0xFF) << 8 | work[0] & 0xFF);
	}

	public final double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	public final float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	public final void readFully(byte[] ba) throws IOException {
		raf.readFully(ba, 0, ba.length);
	}

	public final void readFully(byte[] ba, int off, int len) throws IOException {
		raf.readFully(ba, off, len);
	}

	public final int readInt() throws IOException {
		raf.readFully(work, 0, 4);
		return work[3] << 24 | (work[2] & 0xFF) << 16 | (work[1] & 0xFF) << 8 | work[0] & 0xFF;
	}

	public final String readLine() throws IOException {
		return raf.readLine();
	}

	public final long readLong() throws IOException {
		raf.readFully(work, 0, 8);
		return work[7] << 56 | (work[6] & 0xFF) << 48 | (work[5] & 0xFF) << 40
				| (work[4] & 0xFF) << 32 | (work[3] & 0xFF) << 24 | (work[2] & 0xFF) << 16
				| (work[1] & 0xFF) << 8 | work[0] & 0xFF;
	}

	public final short readShort() throws IOException {
		raf.readFully(work, 0, 2);
		return (short) ((work[1] & 0xFF) << 8 | work[0] & 0xFF);
	}

	public final String readUTF() throws IOException {
		return raf.readUTF();
	}

	public final int readUnsignedByte() throws IOException {
		return raf.readUnsignedByte();
	}

	public final int readUnsignedShort() throws IOException {
		raf.readFully(work, 0, 2);
		return (work[1] & 0xFF) << 8 | work[0] & 0xFF;
	}

	public final void seek(long pos) throws IOException {
		raf.seek(pos);
	}

	public final int skipBytes(int n) throws IOException {
		return raf.skipBytes(n);
	}

	public final synchronized void write(int ib) throws IOException {
		raf.write(ib);
	}

	public final void write(byte[] ba) throws IOException {
		raf.write(ba, 0, ba.length);
	}

	public final synchronized void write(byte[] ba, int off, int len) throws IOException {
		raf.write(ba, off, len);
	}

	public final void writeBoolean(boolean v) throws IOException {
		raf.writeBoolean(v);
	}

	public final void writeByte(int v) throws IOException {
		raf.writeByte(v);
	}

	public final void writeBytes(String s) throws IOException {
		raf.writeBytes(s);
	}

	public final void writeChar(int v) throws IOException {
		work[0] = ((byte) v);
		work[1] = ((byte) (v >> 8));
		raf.write(work, 0, 2);
	}

	public final void writeChars(String s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			writeChar(s.charAt(i));
		}
	}

	public final void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	public final void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	public final void writeInt(int v) throws IOException {
		work[0] = ((byte) v);
		work[1] = ((byte) (v >> 8));
		work[2] = ((byte) (v >> 16));
		work[3] = ((byte) (v >> 24));
		raf.write(work, 0, 4);
	}
	
	public final void writeInt2(int v) throws IOException {
		work[3] = ((byte) v);
		work[2] = ((byte) (v >> 8));
		work[1] = ((byte) (v >> 16));
		work[0] = ((byte) (v >> 24));
		raf.write(work, 0, 4);
	}

	public final void writeLong(long v) throws IOException {
		work[0] = ((byte) (int) v);
		work[1] = ((byte) (int) (v >> 8));
		work[2] = ((byte) (int) (v >> 16));
		work[3] = ((byte) (int) (v >> 24));
		work[4] = ((byte) (int) (v >> 32));
		work[5] = ((byte) (int) (v >> 40));
		work[6] = ((byte) (int) (v >> 48));
		work[7] = ((byte) (int) (v >> 56));
		raf.write(work, 0, 8);
	}

	public final void writeShort(int v) throws IOException {
		work[0] = ((byte) v);
		work[1] = ((byte) (v >> 8));
		raf.write(work, 0, 2);
	}

	public final void writeUTF(String s) throws IOException {
		raf.writeUTF(s);
	}
}