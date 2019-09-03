package name.yumao.ffxiv.chn.thread;

import name.yumao.ffxiv.chn.replace.ReplaceEXDF;
import name.yumao.ffxiv.chn.replace.ReplaceFont;
import name.yumao.ffxiv.chn.swing.PercentPanel;
import name.yumao.ffxiv.chn.swing.TextPatchPanel;

import javax.swing.*;
import java.io.File;

public class ReplaceThread implements Runnable{

    private String resourceFolder;
    private TextPatchPanel textPatchPanel;

    public ReplaceThread(String resourceFolder, TextPatchPanel textPatchPanel){
        this.resourceFolder = resourceFolder;
        this.textPatchPanel = textPatchPanel;
    }
    @Override
    public void run() {
        try {
            textPatchPanel.replaceButton.setEnabled(false);
            PercentPanel percentPanel = new PercentPanel("提莫苑|资源汉化");
            //字体补丁
            new ReplaceFont(resourceFolder + File.separator + "000000.win32.index", "resource" + File.separator + "font").replace();
            //汉化补丁
            if(new File("resource" + File.separator + "text" + File.separator + "0a0000.win32.index").exists()) {
                new ReplaceEXDF(resourceFolder + File.separator + "0a0000.win32.index", "resource" + File.separator + "text" + File.separator + "0a0000.win32.index", percentPanel).replace();
            }
            JOptionPane.showMessageDialog(null, "<html><body>汉化完毕</body></html>", "提示", JOptionPane.PLAIN_MESSAGE);
            percentPanel.dispose();
            textPatchPanel.replaceButton.setEnabled(true);
        }catch (Exception exception){
            JOptionPane.showMessageDialog(null, "<html><body>程序跑飞啦！</body></html>", "汉化错误", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }
}
