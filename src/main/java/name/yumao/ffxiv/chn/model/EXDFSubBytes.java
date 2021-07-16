package name.yumao.ffxiv.chn.model;

import java.util.ArrayList;
import java.util.List;
import name.yumao.ffxiv.chn.util.ArrayUtil;

public class EXDFSubBytes {
	private byte[] sysMarkStart = new byte[] { 2 };
	
	private byte[] sysMarkFinish = new byte[] { 3 };
	
	private byte[] data;
	
	private byte[] subData;
	
	public EXDFSubBytes(byte[] data) {
		this.data = data;
	}
	
	public List<byte[]> splitStringLine() {
		List<byte[]> subDatas = (List)new ArrayList<>();
		boolean isSysBlock = false;
		int subLength = 0;
		this.subData = new byte[this.data.length];
		for (int i = 0; i < this.data.length; i++) {
			if (!isSysBlock && this.data[i] != 2)
				this.subData[subLength++] = this.data[i]; 
			if (!isSysBlock && this.data[i] == 2) {
				isSysBlock = true;
				subDatas.add(ArrayUtil.copyAarry(this.data, subLength));
				subLength = 0;
				this.subData = new byte[this.data.length];
			} 
			if (isSysBlock == true && this.data[i] == 3)
				isSysBlock = false; 
		} 
		return subDatas;
	}
	
	public boolean hasSysmark() {
		return !(ArrayUtil.indexOf(this.data, this.sysMarkStart) == -1 && ArrayUtil.indexOf(this.data, this.sysMarkFinish) == -1);
	}
}
