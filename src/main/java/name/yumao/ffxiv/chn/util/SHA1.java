package name.yumao.ffxiv.chn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class SHA1 {
	public static String getFileSHA1(File file) {
		MessageDigest md = null;
		FileInputStream fis = null;
		StringBuilder sha1Str = new StringBuilder();
		try {
			fis = new FileInputStream(file);
			MappedByteBuffer mbb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, file.length());
			md = MessageDigest.getInstance("SHA-1");
			md.update(mbb);
			byte[] digest = md.digest();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2)
					sha1Str.append(0); 
				sha1Str.append(shaHex);
			} 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException iOException) {} 
		} 
		return sha1Str.toString();
	}
}
