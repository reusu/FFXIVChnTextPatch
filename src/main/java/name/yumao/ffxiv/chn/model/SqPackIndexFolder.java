package name.yumao.ffxiv.chn.model;

import java.io.IOException;
import java.util.HashMap;
import name.yumao.ffxiv.chn.util.LERandomAccessFile;

public class SqPackIndexFolder {

	private int numFiles;
	private long fileIndexOffset;
	private String name;
	private HashMap<Integer, SqPackIndexFile> files;
	
	public SqPackIndexFolder(int id, int numFiles, long fileIndexOffset) {
		this.numFiles = numFiles;
		this.files = new HashMap<>();
		this.fileIndexOffset = fileIndexOffset;
		this.name = null;
		if (this.name == null)
			this.name = String.format("0x%x", new Object[] { Integer.valueOf(id) }); 
	}
	
	public void readFiles(LERandomAccessFile ref, boolean isIndex2) throws IOException {
		ref.seek(this.fileIndexOffset);
		for (int i = 0; i < this.numFiles; i++) {
			long pt = ref.getFilePointer();
			if (!isIndex2) {
				int id = ref.readInt();
				int id2 = ref.readInt();
				long dataOffset = ref.readInt();
				ref.readInt();
				this.files.put(Integer.valueOf(id), new SqPackIndexFile(pt, id, id2, dataOffset, true));
			} else {
				int id = ref.readInt();
				long dataOffset = ref.readInt();
				this.files.put(Integer.valueOf(id), new SqPackIndexFile(pt, id, -1, dataOffset, true));
			} 
		} 
	}
	
	public String getName() {
		return this.name;
	}
	
	public HashMap<Integer, SqPackIndexFile> getFiles() {
		return this.files;
	}
}
