package name.yumao.ffxiv.chn.builder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import name.yumao.ffxiv.chn.util.ArrayUtil;
import name.yumao.ffxiv.chn.util.JLibzSqpacklTools;
import name.yumao.ffxiv.chn.util.LERandomBytes;

public class TexBlockBuilder {
	byte[] data;
	
	public TexBlockBuilder(byte[] data) {
		this.data = data;
	}
	
	public byte[] buildBlock() throws Exception {
		int dataOffset = 0;
		List<LERandomBytes> dataBodys = new ArrayList<>();
		int mipOffsetIndex = 80;
		LERandomBytes leData = new LERandomBytes(this.data);
		leData.seek(28);
		mipOffsetIndex = leData.readInt();
		leData.seek(0);
		byte[] texHeader = new byte[mipOffsetIndex];
		leData.readFully(texHeader);
		ByteBuffer bb = ByteBuffer.wrap(texHeader);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.getInt();
		int textureType = bb.getShort();
		bb.getShort();
		int width = bb.getShort();
		int height = bb.getShort();
		bb.getShort();
		int mipCount = bb.getShort();
		int[] mipmapOffsets = new int[mipCount];
		bb.position(28);
		for (int i = 0; i < mipCount; i++)
			mipmapOffsets[i] = bb.getInt(); 
		int uncompressedSize = leData.length();
		int partCount = (int)Math.ceil((uncompressedSize / 16000.0F));
		int dataHeaderLength = 24 + mipCount * 20 + partCount * 2;
		if (dataHeaderLength < 128) {
			dataHeaderLength = 128;
		} else {
			int k = 128 - dataHeaderLength % 128;
			dataHeaderLength += k;
		} 
		int uncompMipSize = width * height;
		if (textureType == 5184)
			uncompMipSize = width * height * 2; 
		LERandomBytes dataHeader = new LERandomBytes(new byte[dataHeaderLength]);
		dataHeader.writeInt(dataHeaderLength);
		dataHeader.writeInt(4);
		dataHeader.writeInt(uncompressedSize);
		dataHeader.writeInt(0);
		dataHeader.writeInt(0);
		dataHeader.writeInt(mipCount);
		for (int o = 0; o < mipCount; o++) {
			dataHeader.writeInt(mipOffsetIndex);
			int lengthPos = dataHeader.position();
			dataHeader.writeInt(0);
			dataHeader.writeInt(uncompMipSize);
			dataHeader.writeInt(0);
			dataHeader.writeInt(partCount);
			for (int k = 1; k <= partCount; k++) {
				if (k == partCount) {
					byte[] tmpPart = new byte[leData.length() - leData.position()];
					leData.readFully(tmpPart);
					byte[] compr = JLibzSqpacklTools.compressBlock(tmpPart);
					int compressedSize = compr.length;
					int m = 128 - (compressedSize + 16) % 128;
					LERandomBytes dataBody = new LERandomBytes(new byte[compressedSize + 16 + m]);
					dataBody.writeInt(16);
					dataBody.writeInt(0);
					dataBody.writeInt(compressedSize);
					dataBody.writeInt(tmpPart.length);
					dataBody.write(compr);
					dataBodys.add(dataBody);
					dataHeader.writeShort(compressedSize + 16 + m);
					dataOffset += compressedSize + 16 + m;
				} else {
					byte[] tmpPart = new byte[16000];
					leData.readFully(tmpPart);
					byte[] compr = JLibzSqpacklTools.compressBlock(tmpPart);
					int compressedSize = compr.length;
					int m = 128 - (compressedSize + 16) % 128;
					LERandomBytes dataBody = new LERandomBytes(new byte[compressedSize + 16 + m]);
					dataBody.writeInt(16);
					dataBody.writeInt(0);
					dataBody.writeInt(compressedSize);
					dataBody.writeInt(16000);
					dataBody.write(compr);
					dataBodys.add(dataBody);
					dataHeader.writeShort(compressedSize + 16 + m);
					dataOffset += compressedSize + 16 + m;
				} 
			} 
			dataHeader.seek(12);
			dataHeader.writeInt(dataOffset / 128);
			dataHeader.seek(16);
			dataHeader.writeInt(dataOffset / 128);
			dataHeader.seek(lengthPos);
			dataHeader.writeInt(dataOffset + texHeader.length);
			dataOffset = 0;
		} 
		byte[] block = new byte[0];
		block = ArrayUtil.append(block, dataHeader.getWork());
		block = ArrayUtil.append(block, texHeader);
		for (LERandomBytes tmp : dataBodys)
			block = ArrayUtil.append(block, tmp.getWork()); 
		int paddingSize = 128 - block.length % 128;
		for (int j = 0; j < paddingSize; j++) {
			block = ArrayUtil.append(block, new byte[] { 0 });
		} 
		return block;
	}
}
