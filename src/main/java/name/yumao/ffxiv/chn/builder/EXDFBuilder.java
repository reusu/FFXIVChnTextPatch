package name.yumao.ffxiv.chn.builder;

import name.yumao.ffxiv.chn.util.ArrayUtil;
import name.yumao.ffxiv.chn.util.LERandomBytes;

import java.util.*;
import java.util.Map.Entry;


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

		for (Entry<Integer, byte[]> entry : exdfEntry.entrySet()) {
			headerSize += 8;
			bodySize += entry.getValue().length + 6;
		}
		// headerOffset
		dataOffset += (8 * 4);
		// offsetOffset
		dataOffset += headerSize;
		LERandomBytes header = new LERandomBytes(new byte[8 * 4], true, false);
		LERandomBytes dataHeader = new LERandomBytes(new byte[headerSize], true, false);
		LERandomBytes dataBodys = new LERandomBytes(new byte[bodySize], true, false);
		List<Entry<Integer, byte[]>> list = new ArrayList<>(exdfEntry.entrySet());
		Collections.sort(list, new Comparator<Entry<Integer, byte[]>>() {
			//升序排序
			public int compare(Entry<Integer, byte[]> o1, Entry<Integer, byte[]> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		for (Entry<Integer, byte[]> entry : list) {
			Integer index = entry.getKey();
			byte[] data = entry.getValue();
			// header
			dataHeader.writeInt(index);
			dataHeader.writeInt(dataOffset);
			// data
			dataBodys.writeInt(data.length);
			dataBodys.writeShort(1);
			dataBodys.write(data);
			// possition
			dataOffset += data.length + 4 + 2;
		}
		header.write(new byte[] { 0x45, 0x58, 0x44, 0x46, 0x00, 0x02, 0x00, 0x00 });
		header.writeInt((int) dataHeader.length());
		header.writeInt((int) dataBodys.length());

		return ArrayUtil.append(ArrayUtil.append(header.getWork(), dataHeader.getWork()), dataBodys.getWork());
	}
}
