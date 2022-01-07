package name.yumao.ffxiv.chn.thread;

import java.io.File;
// import java.util.List;
import javax.swing.JOptionPane;
// import name.yumao.ffxiv.chn.model.TeemoUpdateVo;
// import name.yumao.ffxiv.chn.util.UpdateUtil;
import name.yumao.ffxiv.chn.swing.PercentPanel;
import name.yumao.ffxiv.chn.swing.TextPatchPanel;
import name.yumao.ffxiv.chn.util.FileUtil;

public class RollbackThread implements Runnable {

	private String resourceFolder;
	private TextPatchPanel textPatchPanel;
	// private List<TeemoUpdateVo> updates;
	private boolean showDialog;
	
	public RollbackThread(String resourceFolder, TextPatchPanel textPatchPanel, /*List<TeemoUpdateVo> updates,*/ boolean showDialog) {
		this.resourceFolder = resourceFolder;
		this.textPatchPanel = textPatchPanel;
		// this.updates = updates;
		this.showDialog = showDialog;
	}
	
	public void run() {
		try {
			this.textPatchPanel.rollbackButton.setEnabled(false);
			/*
			List<TeemoUpdateVo> diffFiles = UpdateUtil.diffkBackUpFile(this.updates);
			if (diffFiles.size() > 0) {
				int backDownCheck = JOptionPane.showConfirmDialog(null, "备份文件HASH验证失败，是否云端下载覆盖?", "备份检查", 0);
				if (backDownCheck == 0 && !UpdateUtil.updateFile(diffFiles)) {
					JOptionPane.showMessageDialog(null, "资源更新失败", "网络错误", 0);
					System.exit(1);
				} 
			} 
			*/
			String[] resourceNames = { "000000.win32.dat0", "000000.win32.index", "000000.win32.index2", "0a0000.win32.dat0", "0a0000.win32.index", "0a0000.win32.index2" };
			int fileCount = 0;
			PercentPanel percentPanel = new PercentPanel("資源還原");
			percentPanel.progressShow("正在還原……", "");
			for (String resourceName : resourceNames) {
				percentPanel.percentShow(++fileCount / resourceNames.length);
				File backupFile = new File("backup" + File.separator + resourceName);
				if (backupFile.exists() && backupFile.isFile())
					FileUtil.copyTo(backupFile, this.resourceFolder + File.separator + backupFile.getName()); 
			} 
			percentPanel.dispose();
			if (this.showDialog)
				JOptionPane.showMessageDialog(null, "<html><body>還原完畢</body></html>", "提示", -1); 
			this.textPatchPanel.rollbackButton.setEnabled(true);
			this.textPatchPanel.replaceButton.setEnabled(true);
		} catch (Exception exception) {
			JOptionPane.showMessageDialog(null, "<html><body>程式錯誤！</body></html>", "還原錯誤", 0);
			exception.printStackTrace();
		} 
	}
}
