package name.yumao.ffxiv.chn;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
		
		// logger setup
		Logger log = Logger.getLogger("GPLogger");
		log.setUseParentHandlers(false);
		log.setLevel(Level.ALL);
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		try {
			FileHandler fh = new FileHandler("debug.log");
			fh.setEncoding("UTF-8");
			log.addHandler(fh);
	        fh.setFormatter(new SimpleFormatter());  
		} catch (IOException e) {  
	        e.printStackTrace();  
	    }
		
		if (isFFXIVFloder(path)) {
			new TextPatchPanel();
		} else {
			new ConfigApplicationPanel();
		} 
	}
	
	private static boolean isFFXIVFloder(String path) {
		if (path == null)
			return false; 
		return (new File(path + File.separator + "game" + File.separator + "ffxiv.exe")).exists();
	}
}
