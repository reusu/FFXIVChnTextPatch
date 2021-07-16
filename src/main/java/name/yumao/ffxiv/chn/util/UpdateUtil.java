package name.yumao.ffxiv.chn.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import name.yumao.ffxiv.chn.model.TeemoUpdateVo;
import name.yumao.ffxiv.chn.swing.PercentPanel;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.fluent.Request;

public class UpdateUtil {
	public static List<TeemoUpdateVo> diffChnFile(List<TeemoUpdateVo> updates) throws Exception {
		List<TeemoUpdateVo> diffUpdate = new ArrayList<>();
		for (TeemoUpdateVo teemoUpdateVo : updates) {
			if (teemoUpdateVo.getPath().startsWith("conf") || teemoUpdateVo.getPath().startsWith("resource")) {
				String filePath = teemoUpdateVo.getPath();
				filePath = filePath.replace("/", File.separator);
				File chnFile = new File(filePath);
				if (chnFile.exists()) {
					if (!DigestUtils.md5Hex(new FileInputStream(chnFile)).equalsIgnoreCase(teemoUpdateVo.getHash()))
						diffUpdate.add(teemoUpdateVo); 
					continue;
				} 
				diffUpdate.add(teemoUpdateVo);
			} 
		} 
		return diffUpdate;
	}
	
	public static List<TeemoUpdateVo> diffkBackUpFile(List<TeemoUpdateVo> updates) throws Exception {
		List<TeemoUpdateVo> diffUpdate = new ArrayList<>();
		for (TeemoUpdateVo teemoUpdateVo : updates) {
			if (teemoUpdateVo.getPath().startsWith("backup")) {
				String filePath = teemoUpdateVo.getPath();
				filePath = filePath.replace("/", File.separator);
				File backupFile = new File(filePath);
				if (backupFile.exists()) {
					if (!DigestUtils.md5Hex(new FileInputStream(backupFile)).equalsIgnoreCase(teemoUpdateVo.getHash()))
						diffUpdate.add(teemoUpdateVo); 
					continue;
				} 
				diffUpdate.add(teemoUpdateVo);
			} 
		} 
		return diffUpdate;
	}
	
	public static boolean updateFile(List<TeemoUpdateVo> diffFiles) {
		PercentPanel percentPanel = new PercentPanel("提莫苑|资源更新");
		try {
			int fileCount = 0;
			for (TeemoUpdateVo teemoUpdateVo : diffFiles) {
				percentPanel.percentShow(++fileCount / diffFiles.size());
				String filePath = teemoUpdateVo.getPath();
				filePath = filePath.replace("/", File.separator);
				File file = new File(filePath);
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs(); 
				if (file.exists())
					file.delete(); 
				Request.Get(teemoUpdateVo.getUrl())
					.connectTimeout(5000)
					.socketTimeout(30000)
					.execute()
					.saveContent(file);
			} 
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			percentPanel.dispose();
		} 
	}
}
