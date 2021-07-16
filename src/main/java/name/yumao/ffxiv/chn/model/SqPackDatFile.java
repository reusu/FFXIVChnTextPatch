package name.yumao.ffxiv.chn.model;

import com.jcraft.jzlib.Inflater;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import name.yumao.ffxiv.chn.util.LERandomAccessFile;

public class SqPackDatFile {
	private LERandomAccessFile currentFilePointer;
	
	public SqPackDatFile(String path) throws FileNotFoundException {
		this.currentFilePointer = new LERandomAccessFile(path, "r");
	}
	
	public int getContentType(long offset) throws IOException {
		this.currentFilePointer.seek(offset);
		this.currentFilePointer.readInt();
		return this.currentFilePointer.readInt();
	}
	
	public byte[] extractFile(long fileOffset) throws IOException {
		return extractFile(fileOffset, null);
	}
	
	public byte[] extractFile(long fileOffset, Boolean debugMode) throws IOException {
		TextureBlocks[] blocks;
		int i;
		ContentType3Container container;
		int k, numBlocks, m;
		short y1, y2;
		int n, pos;
		byte[] mdlData;
		ByteBuffer bb;
		int i1;
		boolean debug = (debugMode == null) ? false : debugMode.booleanValue();
		this.currentFilePointer.seek(fileOffset);
		int headerLength = this.currentFilePointer.readInt();
		int contentType = this.currentFilePointer.readInt();
		int fileSize = this.currentFilePointer.readInt();
		this.currentFilePointer.readInt();
		int blockBufferSize = this.currentFilePointer.readInt() * 128;
		int blockCount = this.currentFilePointer.readInt();
		byte[] extraHeader = null;
		int extraHeaderSize = 0;
		if (debug) {
			System.out.println("================================");
			System.out.println(String.format("File @ %08x", new Object[] { Long.valueOf(fileOffset) }));
			System.out.println("================================");
			System.out.println("Header Length: " + headerLength);
			System.out.println("Content Type: " + contentType);
			System.out.println("File Size: " + fileSize);
			System.out.println("Block Buffer Size: " + blockBufferSize);
		} 
		DataBlock[][] dataBlocks = (DataBlock[][])null;
		if (debug) {
			System.out.println("================================");
			System.out.println("Block Data for this file");
			System.out.println("================================");
		} 
		switch (contentType) {
			case 4:
				blocks = new TextureBlocks[blockCount];
				dataBlocks = new DataBlock[blockCount][];
				for (i = 0; i < blockCount; i++) {
					int frameStartOffset = this.currentFilePointer.readInt();
					int frameSize = this.currentFilePointer.readInt();
					this.currentFilePointer.readInt();
					int blockTableOffset = this.currentFilePointer.readInt();
					int numSubBlocks = this.currentFilePointer.readInt();
					blocks[i] = new TextureBlocks(frameStartOffset, frameSize, blockTableOffset, numSubBlocks);
					dataBlocks[i] = new DataBlock[numSubBlocks];
				} 
				for (i = 0; i < blockCount; i++) {
					TextureBlocks block = blocks[i];
					dataBlocks[i][0] = new DataBlock(block.offset);
					int runningTotal = block.offset;
					for (int i2 = 1; i2 < block.subblockSize; i2++) {
						runningTotal += this.currentFilePointer.readShort();
						dataBlocks[i][i2] = new DataBlock(runningTotal);
					} 
					this.currentFilePointer.readShort();
				} 
				extraHeaderSize = (blocks[0]).offset;
				extraHeader = new byte[extraHeaderSize];
				this.currentFilePointer.seek(fileOffset + headerLength);
				this.currentFilePointer.read(extraHeader, 0, extraHeaderSize);
				break;
			case 3:
				container = new ContentType3Container();
				for (k = 0; k < 11; k++)
					container.chunkDecompressedSizes[k] = this.currentFilePointer.readInt(); 
				for (k = 0; k < 11; k++)
					container.chunkSizes[k] = this.currentFilePointer.readInt(); 
				for (k = 0; k < 11; k++)
					container.chunkOffsets[k] = this.currentFilePointer.readInt(); 
				for (k = 0; k < 11; k++)
					container.chunkStartBlockIndex[k] = this.currentFilePointer.readShort(); 
				numBlocks = 0;
				for (m = 0; m < 11; m++) {
					container.chunkNumBlocks[m] = this.currentFilePointer.readShort();
					numBlocks += container.chunkNumBlocks[m];
				} 
				container.blockSizes = new short[numBlocks];
				container.numMeshes = this.currentFilePointer.readShort();
				container.numMaterials = this.currentFilePointer.readShort();
				y1 = this.currentFilePointer.readShort();
				y2 = this.currentFilePointer.readShort();
				for (n = 0; n < numBlocks; n++)
					container.blockSizes[n] = this.currentFilePointer.readShort(); 
				pos = 68;
				mdlData = new byte[fileSize];
				bb = ByteBuffer.wrap(mdlData);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				bb.putShort(container.numMeshes);
				bb.putShort(container.numMaterials);
				this.currentFilePointer.seek(fileOffset + headerLength + container.chunkOffsets[0]);
				for (i1 = 0; i1 < container.blockSizes.length; i1++) {
					int lastPos = (int)this.currentFilePointer.getFilePointer();
					int blockHeaderLength2 = this.currentFilePointer.readInt();
					this.currentFilePointer.readInt();
					int compressedBlockSize2 = this.currentFilePointer.readInt();
					int decompressedBlockSize2 = this.currentFilePointer.readInt();
					byte[] decompressedBlock2 = null;
					if (compressedBlockSize2 == 32000 || decompressedBlockSize2 == 1) {
						decompressedBlock2 = new byte[decompressedBlockSize2];
						this.currentFilePointer.readFully(decompressedBlock2);
					} else {
						decompressedBlock2 = decompressBlock(compressedBlockSize2, decompressedBlockSize2);
					} 
					System.arraycopy(decompressedBlock2, 0, mdlData, pos, decompressedBlockSize2);
					pos += decompressedBlockSize2;
					this.currentFilePointer.seek((lastPos + container.blockSizes[i1]));
				} 
				return mdlData;
			case 2:
				dataBlocks = new DataBlock[1][blockCount];
				for (i1 = 0; i1 < blockCount; i1++) {
					int offset = this.currentFilePointer.readInt();
					int paddingAndSize = this.currentFilePointer.readInt();
					int padding = paddingAndSize & 0xFFFF;
					int decompressedBlockSize = paddingAndSize >> 16 & 0xFFFF;
					dataBlocks[0][i1] = new DataBlock(offset, padding, decompressedBlockSize);
					if (debug) {
						System.out.println("Block #" + i1);
						System.out.println("Offset: " + String.format("%X", new Object[] { Integer.valueOf(offset) }));
						System.out.println("Padding: " + padding);
						System.out.println("Uncompressed Size: " + decompressedBlockSize);
					} 
				} 
				break;
		} 
		byte[] decompressedFile = null;
		int currentFileOffset = -1;
		try {
			if (fileSize + extraHeaderSize < 0)
				return null; 
			decompressedFile = new byte[fileSize];
		} catch (Exception e) {
			return null;
		} 
		if (dataBlocks == null || dataBlocks[0] == null)
			return null; 
		int[] mipmapPosTable = null;
		if (extraHeader != null && contentType == 4) {
			ByteBuffer byteBuffer = ByteBuffer.wrap(extraHeader);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			byteBuffer.position(14);
			int numMipmaps = byteBuffer.getShort();
			mipmapPosTable = new int[numMipmaps];
			if (numMipmaps < blockCount)
				mipmapPosTable = new int[blockCount]; 
			byteBuffer.position(28);
			for (int i2 = 0; i2 < numMipmaps; i2++)
				mipmapPosTable[i2] = byteBuffer.getInt(); 
		} 
		for (int j = 0; j < ((contentType == 4) ? blockCount : 1); j++) {
			if (mipmapPosTable != null) {
				currentFileOffset = mipmapPosTable[j];
			} else {
				currentFileOffset = 0;
			} 
			for (int i2 = 0; i2 < (dataBlocks[j]).length; i2++) {
				this.currentFilePointer.seek(fileOffset + headerLength + (dataBlocks[j][i2]).offset);
				int blockHeaderLength = this.currentFilePointer.readInt();
				this.currentFilePointer.readInt();
				int compressedBlockSize = this.currentFilePointer.readInt();
				int decompressedBlockSize = this.currentFilePointer.readInt();
				if (debug)
					System.out.println("Decompressing block " + i2 + " @ file offset: " + this.currentFilePointer
							.getFilePointer() + " @ block offset: " + 
							String.format("%X", new Object[] { Integer.valueOf((dataBlocks[j][i2]).offset) }) + ". Compressed Size: " + compressedBlockSize + " and Decompressed Size: " + decompressedBlockSize + ". Block Size: " + (dataBlocks[j][i2]).padding); 
				byte[] decompressedBlock = null;
				if (compressedBlockSize == 32000 || decompressedBlockSize == 1) {
					decompressedBlock = new byte[decompressedBlockSize];
					this.currentFilePointer.readFully(decompressedBlock);
				} else {
					decompressedBlock = decompressBlock(compressedBlockSize, decompressedBlockSize);
				} 
				System.arraycopy(decompressedBlock, 0, decompressedFile, currentFileOffset, decompressedBlockSize);
				currentFileOffset += decompressedBlockSize;
			} 
		} 
		if (extraHeader != null)
			System.arraycopy(extraHeader, 0, decompressedFile, 0, extraHeaderSize); 
		return decompressedFile;
	}
	
	private byte[] decompressBlock(int compressedSize, int decompressedSize) throws IOException {
		byte[] decompressedData = new byte[decompressedSize];
		byte[] gzipedData = new byte[compressedSize + 6];
		gzipedData[0] = 120;
		gzipedData[1] = -100;
		this.currentFilePointer.readFully(gzipedData, 2, compressedSize);
		int checksum = adler32(gzipedData, 2, compressedSize);
		byte[] checksumByte = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(checksum).array();
		System.arraycopy(checksumByte, 0, gzipedData, 2 + compressedSize, 4);
		try {
			Inflater inflater = new Inflater();
			inflater.setInput(gzipedData);
			inflater.setOutput(decompressedData);
			int err = inflater.init();
			CHECK_ERR(inflater, err, "inflateInit");
			while (inflater.total_out < decompressedSize && inflater.total_in < gzipedData.length) {
				inflater.avail_in = inflater.avail_out = 1;
				err = inflater.inflate(0);
				if (err == 1)
					break; 
				CHECK_ERR(inflater, err, "inflate");
			} 
			err = inflater.end();
			CHECK_ERR(inflater, err, "inflateEnd");
			inflater.finished();
			return decompressedData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	private static void CHECK_ERR(Inflater z, int err, String msg) {
		if (err != 0) {
			if (z.msg != null)
				System.out.print(z.msg + " "); 
			System.out.println(msg + " error: " + err);
			System.exit(1);
		} 
	}
	
	private int adler32(byte[] bytes, int offset, int size) {
		int a32mod = 65521;
		int s1 = 1;
		int s2 = 0;
		for (int i = offset; i < size; i++) {
			int b = bytes[i];
			s1 = (s1 + b) % 65521;
			s2 = (s2 + s1) % 65521;
		} 
		return (s2 << 16) + s1;
	}
	
	public void close() throws IOException {
		this.currentFilePointer.close();
	}
	
	public static void main(String[] args) throws Exception {
		SqPackDatFile spdf = new SqPackDatFile("E:\\output\\font_3DEE6AC0");
		spdf.extractFile(0L);
	}
}
