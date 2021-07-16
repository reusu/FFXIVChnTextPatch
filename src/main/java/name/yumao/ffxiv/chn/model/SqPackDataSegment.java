package name.yumao.ffxiv.chn.model;

public class SqPackDataSegment {
	private int offset;
	
	private int size;
	
	private byte[] sha1 = new byte[20];
	
	public SqPackDataSegment(int offset, int size, byte[] sha1) {
		this.offset = offset;
		this.size = size;
		this.sha1 = sha1;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public byte[] getSha1() {
		return this.sha1;
	}
}
