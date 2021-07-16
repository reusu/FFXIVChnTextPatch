package name.yumao.ffxiv.chn.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

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
		this.raf.close();
	}
	
	public final FileDescriptor getFD() throws IOException {
		return this.raf.getFD();
	}
	
	public final long getFilePointer() throws IOException {
		return this.raf.getFilePointer();
	}
	
	public final long length() throws IOException {
		return this.raf.length();
	}
	
	public final int read() throws IOException {
		return this.raf.read();
	}
	
	public final int read(byte[] ba) throws IOException {
		return this.raf.read(ba);
	}
	
	public final int read(byte[] ba, int off, int len) throws IOException {
		return this.raf.read(ba, off, len);
	}
	
	public final boolean readBoolean() throws IOException {
		return this.raf.readBoolean();
	}
	
	public final byte readByte() throws IOException {
		return this.raf.readByte();
	}
	
	public final char readChar() throws IOException {
		this.raf.readFully(this.work, 0, 2);
		return (char)((this.work[1] & 0xFF) << 8 | this.work[0] & 0xFF);
	}
	
	public final double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}
	
	public final float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}
	
	public final void readFully(byte[] ba) throws IOException {
		this.raf.readFully(ba, 0, ba.length);
	}
	
	public final void readFully(byte[] ba, int off, int len) throws IOException {
		this.raf.readFully(ba, off, len);
	}
	
	public final int readInt() throws IOException {
		this.raf.readFully(this.work, 0, 4);
		return this.work[3] << 24 | (this.work[2] & 0xFF) << 16 | (this.work[1] & 0xFF) << 8 | this.work[0] & 0xFF;
	}
	
	public final String readLine() throws IOException {
		return this.raf.readLine();
	}
	
	public final long readLong() throws IOException {
		this.raf.readFully(this.work, 0, 8);
		return (this.work[7] << 56 | (this.work[6] & 0xFF) << 48 | (this.work[5] & 0xFF) << 40 | (this.work[4] & 0xFF) << 32 | (this.work[3] & 0xFF) << 24 | (this.work[2] & 0xFF) << 16 | (this.work[1] & 0xFF) << 8 | this.work[0] & 0xFF);
	}
	
	public final short readShort() throws IOException {
		this.raf.readFully(this.work, 0, 2);
		return (short)((this.work[1] & 0xFF) << 8 | this.work[0] & 0xFF);
	}
	
	public final String readUTF() throws IOException {
		return this.raf.readUTF();
	}
	
	public final int readUnsignedByte() throws IOException {
		return this.raf.readUnsignedByte();
	}
	
	public final int readUnsignedShort() throws IOException {
		this.raf.readFully(this.work, 0, 2);
		return (this.work[1] & 0xFF) << 8 | this.work[0] & 0xFF;
	}
	
	public final void seek(long pos) throws IOException {
		this.raf.seek(pos);
	}
	
	public final int skipBytes(int n) throws IOException {
		return this.raf.skipBytes(n);
	}
	
	public final synchronized void write(int ib) throws IOException {
		this.raf.write(ib);
	}
	
	public final void write(byte[] ba) throws IOException {
		this.raf.write(ba, 0, ba.length);
	}
	
	public final synchronized void write(byte[] ba, int off, int len) throws IOException {
		this.raf.write(ba, off, len);
	}
	
	public final void writeBoolean(boolean v) throws IOException {
		this.raf.writeBoolean(v);
	}
	
	public final void writeByte(int v) throws IOException {
		this.raf.writeByte(v);
	}
	
	public final void writeBytes(String s) throws IOException {
		this.raf.writeBytes(s);
	}
	
	public final void writeChar(int v) throws IOException {
		this.work[0] = (byte)v;
		this.work[1] = (byte)(v >> 8);
		this.raf.write(this.work, 0, 2);
	}
	
	public final void writeChars(String s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++)
			writeChar(s.charAt(i)); 
	}
	
	public final void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}
	
	public final void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}
	
	public final void writeInt(int v) throws IOException {
		this.work[0] = (byte)v;
		this.work[1] = (byte)(v >> 8);
		this.work[2] = (byte)(v >> 16);
		this.work[3] = (byte)(v >> 24);
		this.raf.write(this.work, 0, 4);
	}
	
	public final void writeInt2(int v) throws IOException {
		this.work[3] = (byte)v;
		this.work[2] = (byte)(v >> 8);
		this.work[1] = (byte)(v >> 16);
		this.work[0] = (byte)(v >> 24);
		this.raf.write(this.work, 0, 4);
	}
	
	public final void writeLong(long v) throws IOException {
		this.work[0] = (byte)(int)v;
		this.work[1] = (byte)(int)(v >> 8L);
		this.work[2] = (byte)(int)(v >> 16L);
		this.work[3] = (byte)(int)(v >> 24L);
		this.work[4] = (byte)(int)(v >> 32L);
		this.work[5] = (byte)(int)(v >> 40L);
		this.work[6] = (byte)(int)(v >> 48L);
		this.work[7] = (byte)(int)(v >> 56L);
		this.raf.write(this.work, 0, 8);
	}
	
	public final void writeShort(int v) throws IOException {
		this.work[0] = (byte)v;
		this.work[1] = (byte)(v >> 8);
		this.raf.write(this.work, 0, 2);
	}
	
	public final void writeUTF(String s) throws IOException {
		this.raf.writeUTF(s);
	}
}
