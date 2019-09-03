package name.yumao.ffxiv.chn.model;

import name.yumao.ffxiv.chn.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

public class EXDFSubBytes {

	private byte[] sysMarkStart = new byte[] { 0x02 };
	private byte[] sysMarkFinish = new byte[] { 0x03 };

	private byte[] data;
	private byte[] subData;

	public EXDFSubBytes(byte[] data) {
		this.data = data;
	}

	public List<byte[]> splitStringLine() {
		List<byte[]> subDatas = new ArrayList<byte[]>();
		boolean isSysBlock = false;
		int subLength = 0;
		subData = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			if (isSysBlock == false && data[i] != 0x02) {
				subData[subLength++] = data[i];
			}
			if (isSysBlock == false && data[i] == 0x02) {
				isSysBlock = true;
				subDatas.add(ArrayUtil.copyAarry(data, subLength));
				subLength = 0;
				subData = new byte[data.length];
			}
			if (isSysBlock == true && data[i] == 0x03) {
				isSysBlock = false;
			}
		}
		return subDatas;
	}

	public boolean hasSysmark() {
		return (ArrayUtil.indexOf(data, sysMarkStart) == -1 && ArrayUtil.indexOf(data, sysMarkFinish) == -1) ? 
				false : true;
	}
}
