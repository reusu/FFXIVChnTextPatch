package name.yumao.ffxiv.chn.model;

public class SqPackIndexFile {
	public long pt;
	public int id;
	public int id2;
	public long dataOffset;
	private String name;

	public SqPackIndexFile(long pt,int id, int id2, long dataOffset, boolean loadNames) {
		this.pt = pt;
		this.id = id;
		this.id2 = id2;
		this.dataOffset = dataOffset;
		if (loadNames) {
			if (id2 != -1)
				this.name = null;
			if (name == null)
				this.name = String.format("0x%x", new Object[] { Integer.valueOf(id) });
		}
	}

	public int getId() {
		return id;
	}

	public long getOffset() {
		return dataOffset;
	}

	public int getId2() {
		return id2;
	}

	public String getName() {
		return name;
	}

	public String getName2() {
		String name = null;
		if (id2 != -1)
			name = null;
		if (name == null)
			name = String.format("~%x", new Object[] { Integer.valueOf(id) });
		return name;
	}

	public long getPt() {
		return pt;
	}

	public void setPt(long pt) {
		this.pt = pt;
	}

	@Override
	public String toString() {
		return "SqPackFile ["
				+ "pt=" + pt + 
				", id=" + Integer.toHexString(id) + 
				", id2=" + Integer.toHexString(id2) + 
				", dataOffset=" + dataOffset + 
				", position=" + Integer.toHexString((int) (dataOffset * 8)) + 
				", name=" + name
				+ "]";
	}
	

}