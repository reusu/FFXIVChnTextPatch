package name.yumao.ffxiv.chn.model;

import java.io.IOException;
import java.util.HashMap;
import name.yumao.ffxiv.chn.util.LERandomAccessFile;

public class SqPackIndex {
	private String indexPath;
	
	public SqPackIndex(String indexPath) {
		this.indexPath = indexPath;
	}
	
	public HashMap<Integer, SqPackIndexFolder> resloveIndex() throws Exception {
		// index file
		SqPackDataSegment[] segments = new SqPackDataSegment[4];
		LERandomAccessFile ref = new LERandomAccessFile(this.indexPath, "r");
		int sqpackHeaderLength = checkSqPackHeader(ref);
		resloveSegments(ref, sqpackHeaderLength, segments);
		// Segment 1 is usually files, Segment 2/3 is unknown, Segment 4 is folders
		HashMap<Integer, SqPackIndexFolder> indexMap = new HashMap<>();
		if (segments[3] != null && segments[3].getOffset() != 0) {
			int offset = segments[3].getOffset();
			int size = segments[3].getSize();
			int numFolders = size / 16;
			for (int i = 0; i < numFolders; i++) {
				ref.seek((offset + i * 16));
				// Hash to the folder name
				int id = ref.readInt();
				// Offset to file list in segment 1
				int fileIndexOffset = ref.readInt();
				// Total size of all file segments for this folder. To find #
				// files, divide by 0x10 (16).
				int folderSize = ref.readInt();
				// Padded to a total segment entry size of 16 bytes.
				int numFiles = folderSize / 16;
				ref.readInt();
				
				indexMap.put(Integer.valueOf(id), new SqPackIndexFolder(id, numFiles, fileIndexOffset));
				((SqPackIndexFolder)indexMap.get(Integer.valueOf(id))).readFiles(ref, false);
			} 
		} else {
			int numFiles = (this.indexPath.contains("index2") ? 2 : 1) * segments[0].getSize() / 16;
			indexMap.put(Integer.valueOf(0), new SqPackIndexFolder(0, numFiles, segments[0].getOffset()));
			((SqPackIndexFolder)indexMap.get(Integer.valueOf(0))).readFiles(ref, this.indexPath.contains("index2"));
		} 
		// close index file
		ref.close();
		return indexMap;
	}
	
	public static int checkSqPackHeader(LERandomAccessFile ref) throws IOException {
		byte[] buffer = new byte[6];
		ref.readFully(buffer, 0, 6);
		// "SqPack", followed by 0's (12 bytes)
		if (buffer[0] != 0x53 || buffer[1] != 0x71 || buffer[2] != 0x50 || buffer[3] != 0x61 || buffer[4] != 0x63 || buffer[5] != 0x6B) {
			ref.close();
			throw new IOException("Not a SqPack file");
		} 
		// Header Length
		ref.seek(0xCL);
		int headerLength = ref.readInt();
		// Unknown
		ref.readInt();
		// Type 0: SQDB, Type 1: Data, Type 2: Index
		int type = ref.readInt();
		if (type != 2)
			throw new IOException("Not a index"); 
		return headerLength;
	}
	
	@SuppressWarnings("unused")
	private static int resloveSegments(LERandomAccessFile ref, int segmentHeaderStart, SqPackDataSegment[] segments) throws IOException {
		ref.seek(segmentHeaderStart);
		// Header Length
		int headerLength = ref.readInt();
		// Each 4 Segments
		for (int i = 0; i < segments.length; i++) {
			// For segment 2 it's the number of dats for this archive (dat0,
			// dat1, etc), for others unknown.
			int firstVal = ref.readInt();
			// Offset to the segment
			int offset = ref.readInt();
			// How large a segment is
			int size = ref.readInt();
			// Hash of the segment... [Segment Offset] to [Segment Offset] +
			// [Segment Size]
			byte[] sha1 = new byte[20];
			ref.readFully(sha1, 0, 20);
			segments[i] = new SqPackDataSegment(offset, size, sha1);
			if (i == 0)
				ref.skipBytes(4);
			ref.skipBytes(40);
		}
		return headerLength;
	}
}
