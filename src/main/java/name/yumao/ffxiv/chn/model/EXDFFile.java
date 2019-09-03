package name.yumao.ffxiv.chn.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class EXDFFile {
	private byte[] data;
	private HashMap<Integer,byte[]> entrys = new HashMap<Integer, byte[]>();
	
	public EXDFFile(byte[] data) throws IOException {
		loadEXDF(data);
	}

	public EXDFFile(String path) throws IOException, FileNotFoundException {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();
		loadEXDF(data);
	}

	private void loadEXDF(byte[] data) throws IOException {
		this.data = data;
		ByteBuffer buffer = ByteBuffer.wrap(data);
		try {
			int magic = buffer.getInt();
			int version = buffer.getShort();

			if ((magic != 1163412550) || (version != 2)) {
				throw new IOException("Not a EXDF");
			}
			buffer.getShort();

			int offsetTableSize = buffer.getInt();

			buffer.position(32);

			
			for (int i = 0; i < offsetTableSize / 8; i++){
				
				EXDHashEntry hashEntry = loadHashEntry(this.data, buffer.getInt(), buffer.getInt());
				entrys.put(hashEntry.getIndex(), hashEntry.getData());

			}

		} catch (BufferUnderflowException localBufferUnderflowException) {
		} catch (BufferOverflowException localBufferOverflowException) {
		}
	}

	private EXDHashEntry loadHashEntry(byte[] data, int index, int offset) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.position(offset);
		int size = buffer.getInt();
		byte[] entry = new byte[size];
		buffer.getShort();
		buffer.get(entry);
		return new EXDHashEntry(index, entry);
	}

	public HashMap<Integer,byte[]> getEntrys() {
		return this.entrys;
	}

}