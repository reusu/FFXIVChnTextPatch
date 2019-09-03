package name.yumao.ffxiv.chn;

import name.yumao.ffxiv.chn.swing.ConfigApplicationPanel;
import name.yumao.ffxiv.chn.swing.TextPatchPanel;
import name.yumao.ffxiv.chn.util.res.Config;

import java.io.File;

public class FFXIVPatchMain {
    public static void main(String[] args) {
        Config.setConfigResource("conf" + File.separator + "global.properties");
        String path = Config.getProperty("GamePath");

        if(isFFXIVFloder(path)){
            new TextPatchPanel();
        }else{
            new ConfigApplicationPanel();
        }
    }
    private static boolean isFFXIVFloder(String path){
        if (path == null) return false;
        return new File(path + File.separator + "game" + File.separator + "ffxiv.exe").exists();
    }
}
