package name.yumao.ffxiv.chn.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    public static void copyToFolder(File srcFile, String dstFolder) {
        try {
            File folder = new File(dstFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (srcFile.exists()) {
                File dstFile = new File(folder.getAbsolutePath() + File.separator + srcFile.getName());
                copyTo(srcFile, dstFile);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void copyTo(File srcFile, String dstPath) {
        try {
            if (srcFile.exists()) {
                Files.copy(srcFile.toPath(), new File(dstPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void copyTo(File srcFile, File dstFile) {
        try {
            if (srcFile.exists()) {
                Files.copy(srcFile.toPath(), dstFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
