package name.yumao.ffxiv.chn.model;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import name.yumao.ffxiv.chn.util.LERandomAccessFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SqPackDatFile {
	private LERandomAccessFile currentFilePointer;

	public SqPackDatFile(String path) throws FileNotFoundException {
		this.currentFilePointer = new LERandomAccessFile(path, "r");
	}

	public int getContentType(long offset) throws IOException {
		currentFilePointer.seek(offset);
		// Header Length
		currentFilePointer.readInt();
		// 0x01 - Empty Placeholder, 0x02 - Binary, 0x03 - Model, 0x04 - Texture
		return currentFilePointer.readInt();
	}

	public byte[] extractFile(long fileOffset) throws IOException {
		return extractFile(fileOffset, null);
	}
	
	@SuppressWarnings("unused")
	public byte[] extractFile(long fileOffset, Boolean debugMode) throws IOException {
		boolean debug = (debugMode == null ? false : debugMode);

		currentFilePointer.seek(fileOffset);
		// Header Length
		int headerLength = currentFilePointer.readInt();
		// Content Type
		int contentType = currentFilePointer.readInt();
		// Uncompressed Size
		int fileSize = currentFilePointer.readInt();
		// Unknown
		currentFilePointer.readInt();
		// Block Buffer Size
		int blockBufferSize = currentFilePointer.readInt() * 128;
		// Num Blocks
		int blockCount = currentFilePointer.readInt();

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

		DataBlock[][] dataBlocks = null;

		if (debug) {
			System.out.println("================================");
			System.out.println("Block Data for this file");
			System.out.println("================================");
		}

		switch (contentType) {
		case 4:
			TextureBlocks[] blocks = new TextureBlocks[blockCount];

			dataBlocks = new DataBlock[blockCount][];

			for (int i = 0; i < blockCount; i++) {
				int frameStartOffset = this.currentFilePointer.readInt();
				int frameSize = this.currentFilePointer.readInt();
				this.currentFilePointer.readInt();
				int blockTableOffset = this.currentFilePointer.readInt();
				int numSubBlocks = this.currentFilePointer.readInt();
				blocks[i] = new TextureBlocks(frameStartOffset, frameSize, blockTableOffset, numSubBlocks);
				dataBlocks[i] = new DataBlock[numSubBlocks];
			}

			for (int i = 0; i < blockCount; i++) {
				TextureBlocks block = blocks[i];
				dataBlocks[i][0] = new DataBlock(block.offset);
				int runningTotal = block.offset;
				for (int j = 1; j < block.subblockSize; j++) {
					runningTotal += this.currentFilePointer.readShort();
					dataBlocks[i][j] = new DataBlock(runningTotal);
				}
				this.currentFilePointer.readShort();

			}

			extraHeaderSize = blocks[0].offset;
			extraHeader = new byte[extraHeaderSize];
			this.currentFilePointer.seek(fileOffset + headerLength);
			this.currentFilePointer.read(extraHeader, 0, extraHeaderSize);

			break;
		case 3:
			ContentType3Container container = new ContentType3Container();

			for (int i = 0; i < 11; i++)
				container.chunkDecompressedSizes[i] = this.currentFilePointer.readInt();
			for (int i = 0; i < 11; i++)
				container.chunkSizes[i] = this.currentFilePointer.readInt();
			for (int i = 0; i < 11; i++)
				container.chunkOffsets[i] = this.currentFilePointer.readInt();
			for (int i = 0; i < 11; i++)
				container.chunkStartBlockIndex[i] = this.currentFilePointer.readShort();
			int numBlocks = 0;
			for (int i = 0; i < 11; i++) {
				container.chunkNumBlocks[i] = this.currentFilePointer.readShort();
				numBlocks += container.chunkNumBlocks[i];
			}

			container.blockSizes = new short[numBlocks];

			container.numMeshes = this.currentFilePointer.readShort();
			container.numMaterials = this.currentFilePointer.readShort();
			short y1 = this.currentFilePointer.readShort();
			short y2 = this.currentFilePointer.readShort();

			for (int i = 0; i < numBlocks; i++) {
				container.blockSizes[i] = this.currentFilePointer.readShort();
			}

			int pos = 68;
			byte[] mdlData = new byte[fileSize];
			ByteBuffer bb = ByteBuffer.wrap(mdlData);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putShort(container.numMeshes);
			bb.putShort(container.numMaterials);
			this.currentFilePointer.seek(fileOffset + headerLength + container.chunkOffsets[0]);
			for (int i = 0; i < container.blockSizes.length; i++) {
				int lastPos = (int) this.currentFilePointer.getFilePointer();

				int blockHeaderLength2 = this.currentFilePointer.readInt();
				this.currentFilePointer.readInt();
				int compressedBlockSize2 = this.currentFilePointer.readInt();
				int decompressedBlockSize2 = this.currentFilePointer.readInt();

				byte[] decompressedBlock2 = null;
				if ((compressedBlockSize2 == 32000) || (decompressedBlockSize2 == 1)) {
					decompressedBlock2 = new byte[decompressedBlockSize2];
					this.currentFilePointer.readFully(decompressedBlock2);
				} else {
					decompressedBlock2 = decompressBlock(compressedBlockSize2, decompressedBlockSize2);
				}
				System.arraycopy(decompressedBlock2, 0, mdlData, pos, decompressedBlockSize2);
				pos += decompressedBlockSize2;

				this.currentFilePointer.seek(lastPos + container.blockSizes[i]);
			}

			return mdlData;
		case 2:
			dataBlocks = new DataBlock[1][blockCount];

			for (int i = 0; i < blockCount; i++) {
				int offset = this.currentFilePointer.readInt();
				int paddingAndSize = this.currentFilePointer.readInt();
				int padding = paddingAndSize & 0xFFFF;
				int decompressedBlockSize = paddingAndSize >> 16 & 0xFFFF;

				dataBlocks[0][i] = new DataBlock(offset, padding, decompressedBlockSize);

				if (debug) {
					System.out.println("Block #" + i);
					System.out.println("Offset: " + String.format("%X", new Object[] { Integer.valueOf(offset) }));
					System.out.println("Padding: " + padding);
					System.out.println("Uncompressed Size: " + decompressedBlockSize);
				}
			}

		}

		byte[] decompressedFile = null;
		int currentFileOffset = -1;
		try {
			if (fileSize + extraHeaderSize < 0) {
				return null;
			}
			decompressedFile = new byte[fileSize];
		} catch (Exception e) {
			return null;
		}

		if ((dataBlocks == null) || (dataBlocks[0] == null)) {
			return null;
		}
		int[] mipmapPosTable = null;

		if ((extraHeader != null) && (contentType == 4)) {
			ByteBuffer bb = ByteBuffer.wrap(extraHeader);
			bb.order(ByteOrder.LITTLE_ENDIAN);

			bb.position(14);
			int numMipmaps = bb.getShort();
			mipmapPosTable = new int[numMipmaps];

			if (numMipmaps < blockCount) {
				mipmapPosTable = new int[blockCount];
			}
			bb.position(28);
			for (int i = 0; i < numMipmaps; i++) {
				mipmapPosTable[i] = bb.getInt();
			}
		}

		for (int j = 0; j < (contentType == 4 ? blockCount : 1); j++) {
			if (mipmapPosTable != null)
				currentFileOffset = mipmapPosTable[j];
			else {
				currentFileOffset = 0;
			}
			for (int i = 0; i < dataBlocks[j].length; i++) {
				this.currentFilePointer.seek(fileOffset + headerLength + dataBlocks[j][i].offset);
				int blockHeaderLength = this.currentFilePointer.readInt();
				this.currentFilePointer.readInt();
				int compressedBlockSize = this.currentFilePointer.readInt();
				int decompressedBlockSize = this.currentFilePointer.readInt();

				if (debug) {
					System.out.println("Decompressing block " + i + " @ file offset: "
							+ this.currentFilePointer.getFilePointer() + " @ block offset: "
							+ String.format("%X", new Object[] { Integer.valueOf(dataBlocks[j][i].offset) })
							+ ". Compressed Size: " + compressedBlockSize + " and Decompressed Size: "
							+ decompressedBlockSize + ". Block Size: " + dataBlocks[j][i].padding);
				}
				byte[] decompressedBlock = null;
				if ((compressedBlockSize == 32000) || (decompressedBlockSize == 1)) {
					decompressedBlock = new byte[decompressedBlockSize];
					this.currentFilePointer.readFully(decompressedBlock);
				} else {
					decompressedBlock = decompressBlock(compressedBlockSize, decompressedBlockSize);
				}
				System.arraycopy(decompressedBlock, 0, decompressedFile, currentFileOffset, decompressedBlockSize);
				currentFileOffset += decompressedBlockSize;
			}
		}
		if (extraHeader != null) {
			System.arraycopy(extraHeader, 0, decompressedFile, 0, extraHeaderSize);
		}
		return decompressedFile;
	}

	@SuppressWarnings("deprecation")
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

			while ((inflater.total_out < decompressedSize) && (inflater.total_in < gzipedData.length)) {
				inflater.avail_in = (inflater.avail_out = 1);

				err = inflater.inflate(JZlib.Z_NO_FLUSH);
				if (err == JZlib.Z_STREAM_END)
					break;
				CHECK_ERR(inflater, err, "inflate");
			}

			err = inflater.end();
			CHECK_ERR(inflater, err, "inflateEnd");

			inflater.finished();

			return decompressedData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private static void CHECK_ERR(Inflater z, int err, String msg) {
		if (err != 0) {
			if (z.msg != null)
				System.out.print(z.msg + " ");
			System.out.println(msg + " error: " + err);
			System.exit(1);
		}
	}

	@SuppressWarnings("unused")
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
		currentFilePointer.close();
	}
	
	public static void main(String[] args) throws Exception {
		SqPackDatFile spdf = new SqPackDatFile("E:\\output\\font_3DEE6AC0");
		spdf.extractFile(0);
		
		
	}
}
