package name.yumao.ffxiv.chn.builder;

import name.yumao.ffxiv.chn.util.ArrayUtil;
import name.yumao.ffxiv.chn.util.JLibzSqpacklTools;
import name.yumao.ffxiv.chn.util.LERandomBytes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;


public class TexBlockBuilder {
	byte[] data ;
	public TexBlockBuilder(byte[] data) {
		this.data = data;
	}

	public byte[] buildBlock() throws Exception {
		
		int dataOffset = 0;
		List<LERandomBytes> dataBodys = new ArrayList<>();
		int mipOffsetIndex = 80;
		LERandomBytes leData = new LERandomBytes(data);
		leData.seek(0x1c);
		mipOffsetIndex = leData.readInt();
		leData.seek(0);
		// tex exHeader
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
		bb.position(0x1c);
		for (int i = 0; i < mipCount; i++)	{	
			mipmapOffsets[i] = bb.getInt();	
		}
		// data header
		int uncompressedSize = (int)leData.length();
		int partCount = (int)Math.ceil(uncompressedSize / 16000f);
		int dataHeaderLength = 24 + mipCount * 20 + partCount * 2;
		if(dataHeaderLength < 128){
			dataHeaderLength = 128;
		}else{
			int paddingSize = 128 - (dataHeaderLength % 128);
			dataHeaderLength += paddingSize;
		}
		
		int uncompMipSize = (width * height);
		//0x1440
		if(textureType == 0x1440) {
			uncompMipSize = (width * height) * 2;
		}
		
		LERandomBytes dataHeader = new LERandomBytes(new byte[dataHeaderLength]);
		dataHeader.writeInt(dataHeaderLength);
		dataHeader.writeInt(4);
		dataHeader.writeInt(uncompressedSize);
		dataHeader.writeInt(0);
		dataHeader.writeInt(0);
		dataHeader.writeInt(mipCount);
		
		for (int o = 0; o < mipCount; o++) {
			dataHeader.writeInt(mipOffsetIndex);
			//all body length
			int lengthPos = dataHeader.position();
			dataHeader.writeInt(0);
			dataHeader.writeInt(uncompMipSize);
			dataHeader.writeInt(0);
			dataHeader.writeInt(partCount);	
			byte[] tmpPart;
			for (int i = 1; i <= partCount; i++){
				if (i == partCount){
					//end of file
					tmpPart = new byte[(int)(leData.length() - leData.position())];
					leData.readFully(tmpPart);
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
					dataHeader.writeShort(compressedSize + 16 + paddingSize);
					dataOffset += ((compressedSize + 16) + paddingSize);
				}else{
					tmpPart = new byte[16000];
					leData.readFully(tmpPart);
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
					dataHeader.writeShort(compressedSize + 16 + paddingSize);
					dataOffset += ((compressedSize + 16) + paddingSize);
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
		for(LERandomBytes tmp:dataBodys){
			block = ArrayUtil.append(block, tmp.getWork());
		}
		int paddingSize = 128 - (block.length % 128);
		for(int i = 0 ; i < paddingSize ; i ++) {
			block = ArrayUtil.append(block, new byte[]{0x00});
		}
		
		return block;
		
	}
}
