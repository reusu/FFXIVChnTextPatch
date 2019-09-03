package name.yumao.ffxiv.chn.model;

import name.yumao.ffxiv.chn.util.LERandomAccessFile;

import java.io.IOException;
import java.util.HashMap;

public class SqPackIndexFolder {
	private int numFiles;
	private long fileIndexOffset;
	private String name;
	private HashMap<Integer, SqPackIndexFile> files;

	public SqPackIndexFolder(int id, int numFiles, long fileIndexOffset) {
		this.numFiles = numFiles;
		this.files = new HashMap<Integer, SqPackIndexFile>();
		this.fileIndexOffset = fileIndexOffset;
		this.name = null;
		if (this.name == null)
			this.name = String.format("0x%x", new Object[] { Integer.valueOf(id) });
	}

	public void readFiles(LERandomAccessFile ref, boolean isIndex2) throws IOException {
		ref.seek(fileIndexOffset);

		for (int i = 0; i < numFiles; i++) {
			long pt = ref.getFilePointer();
			if (!isIndex2) {
				//Hash to the file name
				int id = ref.readInt();
				//Hash to the file path
				int id2 = ref.readInt();
				//Multiply by 0x08, points to compressed data in .dat file
				long dataOffset = ref.readInt();
				//Padded to a total segment entry size of 16 bytes.
				ref.readInt();
				files.put(id, new SqPackIndexFile(pt, id, id2, dataOffset, true));
			} else {
				//Hash to the file name
				int id = ref.readInt();
				//Multiply by 0x08, points to compressed data in .dat file
				long dataOffset = ref.readInt();
				files.put(id, new SqPackIndexFile(pt, id, -1, dataOffset, true));
			}
		}
	}

	public String getName() {
		return name;
	}

	public HashMap<Integer, SqPackIndexFile> getFiles() {
		return files;
	}

}