package name.yumao.ffxiv.chn.model;

public class ContentType3Container {
	public int[] chunkDecompressedSizes = new int[11];
	
	public int[] chunkSizes = new int[11];
	
	public int[] chunkOffsets = new int[11];
	
	public int[] chunkStartBlockIndex = new int[11];
	
	public int[] chunkNumBlocks = new int[11];
	
	public short[] blockSizes;
	
	public short numMeshes;
	
	public short numMaterials;
}
