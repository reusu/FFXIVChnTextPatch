package name.yumao.ffxiv.chn.model;

import name.yumao.ffxiv.chn.util.FFXIVString;

public class EXDStringLocate {
	private String fileName;
	
	private Integer index;
	
	private Integer strCount;
	
	private byte[] strBody;
	
	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Integer getIndex() {
		return this.index;
	}
	
	public void setIndex(Integer index) {
		this.index = index;
	}
	
	public Integer getStrCount() {
		return this.strCount;
	}
	
	public void setStrCount(Integer strCount) {
		this.strCount = strCount;
	}
	
	public byte[] getStrBody() {
		return this.strBody;
	}
	
	public void setStrBody(byte[] strBody) {
		this.strBody = strBody;
	}
	
	public String getFFXIVString() {
		return FFXIVString.parseFFXIVString(this.strBody);
	}
}
