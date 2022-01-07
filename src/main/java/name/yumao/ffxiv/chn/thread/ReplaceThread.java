package name.yumao.ffxiv.chn.thread;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;
import name.yumao.ffxiv.chn.replace.ReplaceEXDF;
import name.yumao.ffxiv.chn.replace.ReplaceFont;
import name.yumao.ffxiv.chn.swing.PercentPanel;
import name.yumao.ffxiv.chn.swing.TextPatchPanel;
import name.yumao.ffxiv.chn.util.res.Config;

public class ReplaceThread implements Runnable {
	private String resourceFolder;
	private TextPatchPanel textPatchPanel;
	// private List<TeemoUpdateVo> updates;
	private String slang;
	
	public ReplaceThread(String resourceFolder, TextPatchPanel textPatchPanel) {
		this.resourceFolder = resourceFolder;
		this.textPatchPanel = textPatchPanel;
		// this.updates = updates;
		this.slang = Config.getProperty("SLanguage");
	}
	
	public void run() {
		Logger log = Logger.getLogger("GPLogger");
		
		try {
			
			this.textPatchPanel.replaceButton.setEnabled(false);
			/*
			List<TeemoUpdateVo> diffFiles = UpdateUtil.diffChnFile(this.updates);
			if (diffFiles.size() > 0 && !UpdateUtil.updateFile(diffFiles)) {
				JOptionPane.showMessageDialog(null, "资源更新失败", "网络错误", 0);
				System.exit(1);
			} 
			*/
			PercentPanel percentPanel = new PercentPanel("漢化進度");
			new ReplaceFont(this.resourceFolder + File.separator + "000000.win32.index", "resource" + File.separator + "font", percentPanel).replace();
			if ((new File("resource" + File.separator + "rawexd" + File.separator + "Achievement.csv")).exists()) {
				(new ReplaceEXDF(this.resourceFolder + File.separator + "0a0000.win32.index", "resource" + File.separator + "rawexd" + File.separator + "Achievement.csv", percentPanel)).replace();
			} else if ((new File("resource" + File.separator + "text" + File.separator + "0a0000.win32.index")).exists()) {
				(new ReplaceEXDF(this.resourceFolder + File.separator + "0a0000.win32.index", "resource" + File.separator + "text" + File.separator + "0a0000.win32.index", percentPanel)).replace();
			} else {
				System.out.println("No resource files detected!");
				log.severe("No resource files detected!");
			}
			JOptionPane.showMessageDialog(null, "<html><body>漢化完畢</body></html>", "提示", -1);
			log.info("Patch finished.");
			percentPanel.dispose();
			this.textPatchPanel.replaceButton.setEnabled(true);
		} catch (Exception exception) {
			JOptionPane.showMessageDialog(null, "<html><body>程式錯誤！</body></html>", "漢化錯誤", 0);
			log.severe("Patch failed!");
			log.severe(exception.getMessage());
			exception.printStackTrace();
		} 
	}
}
