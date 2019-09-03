package name.yumao.ffxiv.chn.thread;

import name.yumao.ffxiv.chn.swing.PercentPanel;
import name.yumao.ffxiv.chn.swing.TextPatchPanel;
import name.yumao.ffxiv.chn.util.FileUtil;

import javax.swing.*;
import java.io.File;

public class RollbackThread implements Runnable{

    private String resourceFolder;
    private TextPatchPanel textPatchPanel;
    private boolean showDialog;

    public RollbackThread(String resourceFolder, TextPatchPanel textPatchPanel, boolean showDialog){
        this.resourceFolder = resourceFolder;
        this.textPatchPanel = textPatchPanel;
        this.showDialog = showDialog;
    }
    @Override
    public void run() {
        try {
            textPatchPanel.rollbackButton.setEnabled(false);
            String[] resourceNames = {"000000.win32.dat0", "000000.win32.index", "000000.win32.index2", "0a0000.win32.dat0", "0a0000.win32.index", "0a0000.win32.index2"};
            int fileCount = 0;
            PercentPanel percentPanel = new PercentPanel("提莫苑|资源还原");
            for(String resourceName :resourceNames){
                percentPanel.percentShow((double)(++fileCount) / (double)resourceNames.length);
                File backupFile = new File("backup" + File.separator + resourceName);
                if(backupFile.exists() && backupFile.isFile()){
                    FileUtil.copyTo(backupFile, resourceFolder + File.separator + backupFile.getName());
                }
            }
            percentPanel.dispose();
            if (showDialog) {
                JOptionPane.showMessageDialog(null, "<html><body>还原完毕</body></html>", "提示", JOptionPane.PLAIN_MESSAGE);
            }
            textPatchPanel.rollbackButton.setEnabled(true);
            textPatchPanel.replaceButton.setEnabled(true);
        }catch (Exception exception){
            JOptionPane.showMessageDialog(null, "<html><body>程序跑飞啦！</body></html>", "还原错误", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }
}
