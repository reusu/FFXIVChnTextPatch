package name.yumao.ffxiv.chn.model;

public class ContentType4Container {
	public TextureBlocks[] blocks;
	public long[] blockOffsets;

	public ContentType4Container(int blockCount) {
		this.blocks = new TextureBlocks[blockCount];
		this.blockOffsets = new long[blockCount];
	}
}