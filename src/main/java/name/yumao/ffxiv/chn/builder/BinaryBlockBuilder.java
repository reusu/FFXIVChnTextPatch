package name.yumao.ffxiv.chn.builder;

import name.yumao.ffxiv.chn.util.ArrayUtil;
import name.yumao.ffxiv.chn.util.JLibzSqpacklTools;
import name.yumao.ffxiv.chn.util.LERandomBytes;

import java.util.ArrayList;
import java.util.List;

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
		LERandomBytes leFile = new LERandomBytes(data);
		int uncompressedSize = (int)leFile.length();
		int partCount = (int)Math.ceil(uncompressedSize / 16000f);
		int dataHeaderLength = 24 + partCount * 8;
		if(dataHeaderLength < 128){
			dataHeaderLength = 128;
		}else{
			int paddingSize = 128 - (dataHeaderLength % 128);
			dataHeaderLength += paddingSize;
		}
		LERandomBytes dataHeader = new LERandomBytes(new byte[dataHeaderLength]);
		dataHeader.writeInt(dataHeaderLength);
		dataHeader.writeInt(2);
		dataHeader.writeInt(uncompressedSize);
		dataHeader.writeInt(0);
		dataHeader.writeInt(0);
		dataHeader.writeInt(partCount);
		byte[] tmpPart;
		for (int i = 1; i <= partCount; i++){
			if (i == partCount){
				//end of file
				tmpPart = new byte[(leFile.length() - leFile.position())];
				leFile.readFully(tmpPart);
				byte[] compr = JLibzSqpacklTools.compressBlock(tmpPart);
				int compressedSize = compr.length;
				int paddingSize = 128 - ((compressedSize + 16) % 128);
				LERandomBytes dataBody = new LERandomBytes(new byte[compressedSize + 16 + paddingSize]);
				dataBody.writeInt(16);
				dataBody.writeInt(0);
				dataBody.writeInt(compressedSize);
				dataBody.writeInt(tmpPart.length);
				dataBody.write(compr);
				dataBodys.add(dataBody);
				dataHeader.writeInt(dataOffset);
				dataHeader.writeShort(compressedSize + 16 + paddingSize);
				dataHeader.writeShort(tmpPart.length);
				dataOffset += ((compressedSize + 16) + paddingSize);
			}else{
				tmpPart = new byte[16000];
				leFile.readFully(tmpPart);
				byte[] compr = JLibzSqpacklTools.compressBlock(tmpPart);
				int compressedSize = compr.length;
				int paddingSize = 128 - ((compressedSize + 16) % 128);
				LERandomBytes dataBody = new LERandomBytes(new byte[compressedSize + 16 + paddingSize]);
				dataBody.writeInt(16);
				dataBody.writeInt(0);
				dataBody.writeInt(compressedSize);
				dataBody.writeInt(16000);
				dataBody.write(compr);
				dataBodys.add(dataBody);
				dataHeader.writeInt(dataOffset);
				dataHeader.writeShort(compressedSize + 16 + paddingSize);
				dataHeader.writeShort(16000);
				dataOffset += ((compressedSize + 16) + paddingSize);
			}
		}
		dataHeader.seek(12);
		dataHeader.writeInt(dataOffset / 128);
		dataHeader.seek(16);
		dataHeader.writeInt(dataOffset / 128);
		dataHeaders.add(dataHeader);
		
		byte[] block = new byte[0];
		for(LERandomBytes tmp:dataHeaders){
			block = ArrayUtil.append(block, tmp.getWork());
		}
		for(LERandomBytes tmp:dataBodys){
			block = ArrayUtil.append(block, tmp.getWork());
		}
		return block;
	}
}
