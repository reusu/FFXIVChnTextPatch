package name.yumao.ffxiv.chn.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import name.yumao.ffxiv.chn.model.Language;
import name.yumao.ffxiv.chn.util.res.Config;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

public class FFXIVLauncher {
	private static String WIN_AGENT = "SQEXAuthor/2.0.0(Windows 6.2; ja-jp)";
	
	private static String MAC_AGENT = "macSQEXAuthor/2.0.0(MacOSX; ja-jp)";
	
	private static String PS4_AGENT = "ps4SQEXAuthor/2.0.0(PS4; ja-jp)";
	
	private static String LANGUAGE = Language.toCode(Config.getProperty("SLanguage"));
	
	private static String[] BOOT_FILES = new String[] { "ffxivboot.exe", "ffxivboot64.exe", "ffxivlauncher.exe", "ffxivlauncher64.exe", "ffxivupdater.exe", "ffxivupdater64.exe" };
	
	public static String getlaunchParams(String user, String pwd, String otp) {
		try {
			String bootMode = Config.getProperty("boot", "BootMode");
			String isSteam = bootMode.equalsIgnoreCase("steam") ? "1" : "0";
			String userAgent = WIN_AGENT;
			if (bootMode.equalsIgnoreCase("mac"))
				userAgent = MAC_AGENT; 
			if (bootMode.equalsIgnoreCase("ps4"))
				userAgent = PS4_AGENT; 
			String index = Request.Get("https://ffxiv-login.square-enix.com/oauth/ffxivarr/login/top?lng=" + LANGUAGE + "&rgn=3&isft=0&issteam=" + isSteam).connectTimeout(5000).socketTimeout(60000).addHeader("User-Agent", userAgent).execute().returnContent().toString();
			Pattern pattern = Pattern.compile("<\\s*input .* name=\"_STORED_\" value=\"(?<stored>.*)\">");
			Matcher matcher = pattern.matcher(index);
			if (matcher.find()) {
				String stored = matcher.group("stored");
				String login = Request.Post("https://ffxiv-login.square-enix.com/oauth/ffxivarr/login/login.send").connectTimeout(5000).socketTimeout(60000).addHeader("User-Agent", userAgent).addHeader("Referer", "https://ffxiv-login.square-enix.com/oauth/ffxivarr/login/top?lng=" + LANGUAGE + "&rgn=3&isft=0&issteam=" + isSteam).addHeader("Content-Type", "application/x-www-form-urlencoded").bodyForm(Form.form().add("_STORED_", stored).add("sqexid", user).add("password", pwd).add("otppw", otp).build()).execute().returnContent().toString();
				pattern = Pattern.compile("window\\.external\\.user\\(\"login=auth,(?<launchParams>.*)\"\\);");
				matcher = pattern.matcher(login);
				if (matcher.find()) {
					String launchParams = matcher.group("launchParams");
					return launchParams;
				} 
				return "0,err,can not find launchParams";
			} 
			return "0,err,can not find stored";
		} catch (Exception e) {
			return "0,err,launchParams thread exception";
		} 
	}
	
	public static String getSid(String launchParams) {
		String[] params = launchParams.split(",");
		String session = params[2];
		int region = Integer.valueOf(params[6]).intValue();
		String gamePath = Config.getProperty("GamePath");
		String bootMode = Config.getProperty("boot", "BootMode");
		String userAgent = WIN_AGENT;
		if (bootMode.equalsIgnoreCase("mac"))
			userAgent = MAC_AGENT; 
		if (bootMode.equalsIgnoreCase("ps4"))
			userAgent = PS4_AGENT; 
		String hash = "";
		File boot = new File(gamePath + File.separator + "boot");
		for (int i = 0; i < BOOT_FILES.length; i++) {
			String bootFile = BOOT_FILES[i];
			hash = hash + bootFile + "/" + (new File(boot + File.separator + bootFile)).length() + "/" + SHA1.getFileSHA1(new File(boot + File.separator + bootFile));
			if (i != BOOT_FILES.length - 1)
				hash = hash + ","; 
		} 
		String gameVer = "";
		try {
			File gameVerFile = new File(gamePath + File.separator + "game" + File.separator + "ffxivgame.ver");
			if (gameVerFile.exists()) {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(gameVerFile));
				gameVer = bufferedReader.readLine();
			} 
		} catch (Exception exception) {}
		try {
			HttpResponse response = Request.Post("https://patch-gamever.ffxiv.com/http/win32/ffxivneo_release_game/" + gameVer + "/" + session).connectTimeout(5000).socketTimeout(60000).addHeader("X-Hash-Check", "enabled").addHeader("User-Agent", userAgent).addHeader("Referer", "https://ffxiv-login.square-enix.com/oauth/ffxivarr/login/top?lng=" + LANGUAGE + "&rgn=" + region).bodyString(hash, ContentType.APPLICATION_FORM_URLENCODED).execute().returnResponse();
			String sid = response.getFirstHeader("X-Patch-Unique-Id").getValue();
			return sid + "," + response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			return "0,200";
		} 
	}
	
	public static void launchClient(String launchParams, String sid) {
		String exePath, params[] = launchParams.split(",");
		String session = params[2];
		int region = Integer.valueOf(params[6]).intValue();
		boolean terms = !params[4].equals("0");
		boolean playable = !params[10].equals("0");
		int maxex = Integer.valueOf(params[14]).intValue();
		String gamePath = Config.getProperty("GamePath");
		String langCode = Language.toLCode(Config.getProperty("SLanguage"));
		String bootMode = Config.getProperty("boot", "BootMode");
		String dxMode = Config.getProperty("boot", "DirectX");
		String isSteam = bootMode.equalsIgnoreCase("steam") ? "IsSteam=1" : "";
		String gameVer = "";
		try {
			File gameVerFile = new File(gamePath + File.separator + "game" + File.separator + "ffxivgame.ver");
			if (gameVerFile.exists()) {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(gameVerFile));
				gameVer = bufferedReader.readLine();
			} 
		} catch (Exception exception) {}
		if (dxMode.equalsIgnoreCase("dx9")) {
			exePath = gamePath + File.separator + "game" + File.separator + "ffxiv.exe";
		} else {
			exePath = gamePath + File.separator + "game" + File.separator + "ffxiv_dx11.exe";
		} 
		File exeDirectory = new File(gamePath + File.separator + "boot");
		String[] exeArray = { exePath, "DEV.DataPathType=1", "DEV.MaxEntitledExpansionID=" + maxex, "DEV.TestSID=" + sid, "DEV.UseSqPack=1", "SYS.Region=" + region, "language=" + langCode, "ver=" + gameVer, isSteam };
		try {
			Process process = Runtime.getRuntime().exec(exeArray, (String[])null, exeDirectory);
		} catch (Exception exception) {}
	}
	
	public static void launchBoot() {
		String gamePath = Config.getProperty("GamePath");
		String bootMode = Config.getProperty("boot", "BootMode");
		String isSteam = bootMode.equalsIgnoreCase("steam") ? "-issteam" : "";
		String exePath = gamePath + File.separator + "boot" + File.separator + "ffxivboot.exe";
		File exeDirectory = new File(gamePath + File.separator + "boot");
		String[] exeArray = { exePath, isSteam };
		try {
			Process process = Runtime.getRuntime().exec(exeArray, (String[])null, exeDirectory);
		} catch (Exception exception) {}
	}
}
