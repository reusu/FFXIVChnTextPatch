package name.yumao.ffxiv.chn.util.res;

import java.io.*;
import java.net.*;
import java.util.Properties;

public class ConfigResource {
	private String configPath = "/Config.properties";
	private InputStream configStream;
	private InputStreamReader configReader;
	private Properties props;

	public String getProperty(String name) {
		return this.props.getProperty(name);
	}

	public void setProperty(String name, String value) {
		if (this.props == null) {
			this.props = new Properties();
		}
		this.props.put(name, value);
	}

	public void loadConfig() throws IOException {
		this.loadConfig(configPath);
	}

	public void loadConfig(String fileName) throws IOException {
		if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
			this.configStream = new URL("configStream").openStream();
			this.props = new Properties();
			this.props.load(this.configStream);
		} else {
			this.configReader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
			this.props = new Properties();
			this.props.load(this.configReader);
			this.configPath = fileName;
		}
	}

	public void saveProperty() throws IOException {
		this.saveProperty(configPath);
	}

	public void saveProperty(String fileName) throws IOException {
		this.props.store(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"), null);
	}

}
