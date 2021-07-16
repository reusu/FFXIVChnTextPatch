package name.yumao.ffxiv.chn.model;

public class SqPackIndexFile {
	public long pt;
	
	public int id;
	
	public int id2;
	
	public long dataOffset;
	
	private String name;
	
	public SqPackIndexFile(long pt, int id, int id2, long dataOffset, boolean loadNames) {
		this.pt = pt;
		this.id = id;
		this.id2 = id2;
		this.dataOffset = dataOffset;
		if (loadNames) {
			if (id2 != -1)
				this.name = null; 
			if (this.name == null)
				this.name = String.format("0x%x", new Object[] { Integer.valueOf(id) }); 
		} 
	}
	
	public int getId() {
		return this.id;
	}
	
	public long getOffset() {
		return this.dataOffset;
	}
	
	public int getId2() {
		return this.id2;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getName2() {
		String name = null;
		if (this.id2 != -1)
			name = null; 
		if (name == null)
			name = String.format("~%x", new Object[] { Integer.valueOf(this.id) }); 
		return name;
	}
	
	public long getPt() {
		return this.pt;
	}
	
	public void setPt(long pt) {
		this.pt = pt;
	}
	
	public String toString() {
		return "SqPackFile [pt=" + this.pt + ", id=" + 
			
			Integer.toHexString(this.id) + ", id2=" + 
			Integer.toHexString(this.id2) + ", dataOffset=" + this.dataOffset + ", position=" + 
			
			Integer.toHexString((int)(this.dataOffset * 8L)) + ", name=" + this.name + "]";
	}
}
