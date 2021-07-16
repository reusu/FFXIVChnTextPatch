package name.yumao.ffxiv.chn.model;

public class TextureBlocks {
	public final int offset;
	
	public final int padding;
	
	public final int tableOffset;
	
	public final int subblockSize;
	
	public TextureBlocks(int offset, int padding, int tableOffset, int subblocksize) {
		this.offset = offset;
		this.padding = padding;
		this.tableOffset = tableOffset;
		this.subblockSize = subblocksize;
	}
}
