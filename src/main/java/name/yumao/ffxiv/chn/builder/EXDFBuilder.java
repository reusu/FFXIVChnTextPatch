package name.yumao.ffxiv.chn.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import name.yumao.ffxiv.chn.util.ArrayUtil;
import name.yumao.ffxiv.chn.util.LERandomBytes;

public class EXDFBuilder {
	private HashMap<Integer, byte[]> exdfEntry;
	
	private int dataOffset;
	
	private int headerSize;
	
	private int bodySize;
	
	public EXDFBuilder(HashMap<Integer, byte[]> exdfEntry) {
		this.exdfEntry = exdfEntry;
		this.dataOffset = 0;
		this.headerSize = 0;
		this.bodySize = 0;
	}
	
	public byte[] buildExdf() throws Exception {
		for (Map.Entry<Integer, byte[]> entry : this.exdfEntry.entrySet()) {
			this.headerSize += 8;
			this.bodySize += ((byte[])entry.getValue()).length + 6;
		} 
		this.dataOffset += 32;
		this.dataOffset += this.headerSize;
		LERandomBytes header = new LERandomBytes(new byte[32], true, false);
		LERandomBytes dataHeader = new LERandomBytes(new byte[this.headerSize], true, false);
		LERandomBytes dataBodys = new LERandomBytes(new byte[this.bodySize], true, false);
		List<Map.Entry<Integer, byte[]>> list = new ArrayList<>((Collection)this.exdfEntry.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, byte[]>>() {
					public int compare(Map.Entry<Integer, byte[]> o1, Map.Entry<Integer, byte[]> o2) {
						return ((Integer)o1.getKey()).compareTo(o2.getKey());
					}
				});
		for (Map.Entry<Integer, byte[]> entry : list) {
			Integer index = entry.getKey();
			byte[] data = entry.getValue();
			dataHeader.writeInt(index.intValue());
			dataHeader.writeInt(this.dataOffset);
			dataBodys.writeInt(data.length);
			dataBodys.writeShort(1);
			dataBodys.write(data);
			this.dataOffset += data.length + 4 + 2;
		} 
		header.write(new byte[] { 69, 88, 68, 70, 0, 2, 0, 0 });
		header.writeInt(dataHeader.length());
		header.writeInt(dataBodys.length());
		return ArrayUtil.append(ArrayUtil.append(header.getWork(), dataHeader.getWork()), dataBodys.getWork());
	}
}
