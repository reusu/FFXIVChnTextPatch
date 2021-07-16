package name.yumao.ffxiv.chn;

import java.io.File;
import name.yumao.ffxiv.chn.swing.ConfigApplicationPanel;
import name.yumao.ffxiv.chn.swing.TextPatchPanel;
import name.yumao.ffxiv.chn.util.res.Config;
/*
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import name.yumao.ffxiv.chn.model.TeemoUpdateVo;
import name.yumao.ffxiv.chn.swing.PercentPanel;
import org.apache.http.client.fluent.Request;
*/

public class FFXIVPatchMain {
	public static void main(String[] args) {
		Config.setConfigResource("conf" + File.separator + "global.properties");
		String path = Config.getProperty("GamePath");
		// List updates = new ArrayList();
		if (isFFXIVFloder(path)) {
			new TextPatchPanel(/*updates*/);
		} else {
			new ConfigApplicationPanel(/*updates*/);
		} 
	}
	
	private static boolean isFFXIVFloder(String path) {
		if (path == null)
			return false; 
		return (new File(path + File.separator + "game" + File.separator + "ffxiv.exe")).exists();
	}
}
