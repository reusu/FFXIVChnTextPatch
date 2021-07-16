package name.yumao.ffxiv.chn.util;

public class LERandomBytes {
	private int point = 0;
	
	private int spoint = 0;
	
	private byte[] work = new byte[0];
	
	private boolean bigending = false;
	
	private boolean increment = false;
	
	public LERandomBytes() {
		this.increment = true;
	}
	
	public LERandomBytes(byte[] work) {
		this.work = work;
	}
	
	public LERandomBytes(byte[] work, boolean bigending) {
		this.work = work;
		this.bigending = bigending;
	}
	
	public LERandomBytes(byte[] work, boolean bigending, boolean increment) {
		this.work = work;
		this.bigending = bigending;
		this.increment = increment;
	}
	
	public void setWork(byte[] work) {
		this.point = 0;
		this.work = work;
	}
	
	public byte[] getWork() {
		return this.work;
	}
	
	public int length() {
		return this.work.length;
	}
	
	public void setBigending(boolean bigending) {
		this.bigending = bigending;
	}
	
	public void setIncrement(boolean increment) {
		this.increment = increment;
	}
	
	public boolean hasRemaining() {
		return (this.point < this.work.length);
	}
	
	public int mark() {
		this.spoint = this.point;
		return this.point;
	}
	
	public int reset() {
		this.point = this.spoint;
		return this.spoint;
	}
	
	public int position() {
		return this.point;
	}
	
	public void position(int pos) {
		seek(pos);
	}
	
	public void seek(int pos) {
		this.point = pos;
	}
	
	public void skip() {
		this.point++;
	}
	
	public void skip(int count) {
		for (int i = 0; i < count; i++)
			this.point++; 
	}
	
	public void clear() {
		this.point = 0;
		this.spoint = 0;
		this.work = new byte[0];
	}
	
	public void readFully(byte[] bytes) {
		System.arraycopy(this.work, this.point, bytes, 0, bytes.length);
		this.point += bytes.length;
	}
	
	public byte[] read(int pos, int len) {
		byte[] tmp = new byte[len];
		System.arraycopy(this.work, pos, tmp, 0, len);
		this.point += len;
		return tmp;
	}
	
	public byte read() {
		return readByte();
	}
	
	public byte readByte() {
		return this.work[this.point++];
	}
	
	public boolean readBoolean() {
		return (this.work[this.point++] == 1);
	}
	
	public char readChar() {
		byte[] tmp = new byte[2];
		readFully(tmp);
		if (this.bigending)
			return (char)((tmp[0] & 0xFF) << 8 | tmp[1] & 0xFF); 
		return (char)((tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF);
	}
	
	public short readShort() {
		byte[] tmp = new byte[2];
		readFully(tmp);
		if (this.bigending)
			return (short)((tmp[0] & 0xFF) << 8 | tmp[1] & 0xFF); 
		return (short)((tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF);
	}
	
	public int readUnsignedShort() {
		return readInt16();
	}
	
	public int readInt8() {
		byte[] tmp = new byte[1];
		readFully(tmp);
		return tmp[0] & 0xFF;
	}
	
	public int readInt16() {
		byte[] tmp = new byte[2];
		readFully(tmp);
		if (this.bigending)
			return (tmp[0] & 0xFF) << 8 | tmp[1] & 0xFF; 
		return (tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF;
	}
	
	public int readInt24() {
		byte[] tmp = new byte[3];
		readFully(tmp);
		if (this.bigending)
			return (tmp[0] & 0xFF) << 16 | (tmp[1] & 0xFF) << 8 | tmp[2] & 0xFF; 
		return (tmp[2] & 0xFF) << 16 | (tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF;
	}
	
	public int readInt() {
		return readInt32();
	}
	
	public int readInt32() {
		byte[] tmp = new byte[4];
		readFully(tmp);
		if (this.bigending)
			return (tmp[0] & 0xFF) << 24 | (tmp[1] & 0xFF) << 16 | (tmp[2] & 0xFF) << 8 | tmp[3] & 0xFF; 
		return (tmp[3] & 0xFF) << 24 | (tmp[2] & 0xFF) << 16 | (tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF;
	}
	
	public long readLong() {
		byte[] tmp = new byte[8];
		readFully(tmp);
		if (this.bigending)
			return (tmp[0] & 0xFF000000 | (tmp[1] & 0xFF) << 48 | (tmp[2] & 0xFF) << 40 | (tmp[3] & 0xFF) << 32 | (tmp[4] & 0xFF) << 24 | (tmp[5] & 0xFF) << 16 | (tmp[6] & 0xFF) << 8 | tmp[7] & 0xFF); 
		return (tmp[7] & 0xFF000000 | (tmp[6] & 0xFF) << 48 | (tmp[5] & 0xFF) << 40 | (tmp[4] & 0xFF) << 32 | (tmp[3] & 0xFF) << 24 | (tmp[2] & 0xFF) << 16 | (tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF);
	}
	
	private void increment(byte[] bytes) {
		if (this.increment && this.work.length - this.point < bytes.length) {
			byte[] nwork = new byte[this.point + bytes.length];
			System.arraycopy(this.work, 0, nwork, 0, this.point);
			this.work = nwork;
		} 
	}
	
	public void write(byte[] bytes) {
		write(bytes, this.point, bytes.length);
	}
	
	public void write(byte[] bytes, int pos) {
		write(bytes, pos, bytes.length);
	}
	
	public void write(byte[] bytes, int pos, int len) {
		increment(bytes);
		System.arraycopy(bytes, 0, this.work, pos, len);
		this.point += len;
	}
	
	public void write(byte b) {
		writeByte(b);
	}
	
	public void writeByte(byte b) {
		byte[] tmp = { b };
		increment(tmp);
		write(tmp);
	}
	
	public void write(char c) {
		writeChar(c);
	}
	
	public void writeChar(char c) {
		byte[] tmp = new byte[2];
		tmp[0] = (byte)c;
		tmp[1] = (byte)(c >> 8);
		increment(tmp);
		write(tmp);
	}
	
	public void write(String s) {
		writeString(s);
	}
	
	public void writeString(String s) {
		try {
			write(s.getBytes("UTF-8"));
		} catch (Exception e) {
			for (char c : s.toCharArray())
				writeChar(c); 
		} 
	}
	
	public void write(short v) {
		writeShort(v);
	}
	
	public void writeShort(short v) {
		writeInt16(v);
	}
	
	public void writeShort(int v) {
		writeInt16(v);
	}
	
	public void writeInt16(int v) {
		byte[] tmp = new byte[2];
		if (this.bigending) {
			tmp[1] = (byte)v;
			tmp[0] = (byte)(v >> 8);
		} else {
			tmp[0] = (byte)v;
			tmp[1] = (byte)(v >> 8);
		} 
		increment(tmp);
		write(tmp);
	}
	
	public void writeInt24(int v) {
		byte[] tmp = new byte[3];
		if (this.bigending) {
			tmp[2] = (byte)v;
			tmp[1] = (byte)(v >> 8);
			tmp[0] = (byte)(v >> 16);
		} else {
			tmp[0] = (byte)v;
			tmp[1] = (byte)(v >> 8);
			tmp[2] = (byte)(v >> 16);
		} 
		increment(tmp);
		write(tmp);
	}
	
	public void write(int v) {
		writeInt(v);
	}
	
	public void writeInt(int v) {
		writeInt32(v);
	}
	
	public void writeInt32(int v) {
		byte[] tmp = new byte[4];
		if (this.bigending) {
			tmp[3] = (byte)v;
			tmp[2] = (byte)(v >> 8);
			tmp[1] = (byte)(v >> 16);
			tmp[0] = (byte)(v >> 24);
		} else {
			tmp[0] = (byte)v;
			tmp[1] = (byte)(v >> 8);
			tmp[2] = (byte)(v >> 16);
			tmp[3] = (byte)(v >> 24);
		} 
		increment(tmp);
		write(tmp);
	}
	
	public void write(long v) {
		writeLong(v);
	}
	
	public void writeLong(long v) {
		byte[] tmp = new byte[8];
		if (this.bigending) {
			tmp[7] = (byte)(int)v;
			tmp[6] = (byte)(int)(v >> 8L);
			tmp[5] = (byte)(int)(v >> 16L);
			tmp[4] = (byte)(int)(v >> 24L);
			tmp[3] = (byte)(int)(v >> 32L);
			tmp[2] = (byte)(int)(v >> 40L);
			tmp[1] = (byte)(int)(v >> 48L);
			tmp[0] = (byte)(int)(v >> 56L);
		} else {
			tmp[0] = (byte)(int)v;
			tmp[1] = (byte)(int)(v >> 8L);
			tmp[2] = (byte)(int)(v >> 16L);
			tmp[3] = (byte)(int)(v >> 24L);
			tmp[4] = (byte)(int)(v >> 32L);
			tmp[5] = (byte)(int)(v >> 40L);
			tmp[6] = (byte)(int)(v >> 48L);
			tmp[7] = (byte)(int)(v >> 56L);
		} 
		increment(tmp);
		write(tmp);
	}
}
