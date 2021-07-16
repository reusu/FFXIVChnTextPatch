package name.yumao.ffxiv.chn.builder;

import java.util.ArrayList;
import java.util.List;
import name.yumao.ffxiv.chn.util.ArrayUtil;
import name.yumao.ffxiv.chn.util.JLibzSqpacklTools;
import name.yumao.ffxiv.chn.util.LERandomBytes;

public class BinaryBlockBuilder {
	private byte[] data;
	
	private int dataOffset;
	
	public BinaryBlockBuilder(byte[] data) {
		this.data = data;
		this.dataOffset = 0;
	}
	
	public byte[] buildBlock() throws Exception {
		List<LERandomBytes> dataHeaders = new ArrayList<>();
		List<LERandomBytes> dataBodys = new ArrayList<>();
		LERandomBytes leFile = new LERandomBytes(this.data);
		int uncompressedSize = leFile.length();
		int partCount = (int)Math.ceil((uncompressedSize / 16000.0F));
		int dataHeaderLength = 24 + partCount * 8;
		if (dataHeaderLength < 128) {
			dataHeaderLength = 128;
		} else {
			int paddingSize = 128 - dataHeaderLength % 128;
			dataHeaderLength += paddingSize;
		} 
		LERandomBytes dataHeader = new LERandomBytes(new byte[dataHeaderLength]);
		dataHeader.writeInt(dataHeaderLength);
		dataHeader.writeInt(2);
		dataHeader.writeInt(uncompressedSize);
		dataHeader.writeInt(0);
		dataHeader.writeInt(0);
		dataHeader.writeInt(partCount);
		for (int i = 1; i <= partCount; i++) {
			if (i == partCount) {
				byte[] tmpPart = new byte[leFile.length() - leFile.position()];
				leFile.readFully(tmpPart);
				byte[] compr = JLibzSqpacklTools.compressBlock(tmpPart);
				int compressedSize = compr.length;
				int paddingSize = 128 - (compressedSize + 16) % 128;
				LERandomBytes dataBody = new LERandomBytes(new byte[compressedSize + 16 + paddingSize]);
				dataBody.writeInt(16);
				dataBody.writeInt(0);
				dataBody.writeInt(compressedSize);
				dataBody.writeInt(tmpPart.length);
				dataBody.write(compr);
				dataBodys.add(dataBody);
				dataHeader.writeInt(this.dataOffset);
				dataHeader.writeShort(compressedSize + 16 + paddingSize);
				dataHeader.writeShort(tmpPart.length);
				this.dataOffset += compressedSize + 16 + paddingSize;
			} else {
				byte[] tmpPart = new byte[16000];
				leFile.readFully(tmpPart);
				byte[] compr = JLibzSqpacklTools.compressBlock(tmpPart);
				int compressedSize = compr.length;
				int paddingSize = 128 - (compressedSize + 16) % 128;
				LERandomBytes dataBody = new LERandomBytes(new byte[compressedSize + 16 + paddingSize]);
				dataBody.writeInt(16);
				dataBody.writeInt(0);
				dataBody.writeInt(compressedSize);
				dataBody.writeInt(16000);
				dataBody.write(compr);
				dataBodys.add(dataBody);
				dataHeader.writeInt(this.dataOffset);
				dataHeader.writeShort(compressedSize + 16 + paddingSize);
				dataHeader.writeShort(16000);
				this.dataOffset += compressedSize + 16 + paddingSize;
			} 
		} 
		dataHeader.seek(12);
		dataHeader.writeInt(this.dataOffset / 128);
		dataHeader.seek(16);
		dataHeader.writeInt(this.dataOffset / 128);
		dataHeaders.add(dataHeader);
		byte[] block = new byte[0];
		for (LERandomBytes tmp : dataHeaders)
			block = ArrayUtil.append(block, tmp.getWork()); 
		for (LERandomBytes tmp : dataBodys)
			block = ArrayUtil.append(block, tmp.getWork()); 
		return block;
	}
}
