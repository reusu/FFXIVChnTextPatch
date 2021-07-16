package name.yumao.ffxiv.chn.model;

public class DataBlock {
	public final int offset;
	
	public final int padding;
	
	public final int decompressedSize;
	
	public DataBlock(int offset, int padding, int decompressedSize) {
		this.offset = offset;
		this.padding = padding;
		this.decompressedSize = decompressedSize;
	}
	
	public DataBlock(int offset) {
		this.offset = offset;
		this.padding = -1;
		this.decompressedSize = -1;
	}
}
