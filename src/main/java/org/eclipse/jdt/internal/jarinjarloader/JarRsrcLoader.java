package org.eclipse.jdt.internal.jarinjarloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class JarRsrcLoader {
	private static class ManifestInfo {
		String rsrcMainClass;
		
		String[] rsrcClassPath;
		
		private Set<String> allFilters = new TreeSet<>();
		
		private Map<String, List<String>> libsForFilter = new TreeMap<>();
		
		static final String CLASS_PATH_FILTER_MATCH_ON = "Class-Path-Filter-Match-On-";
		
		public void configureConditionalPath(Attributes attrs) {
			for (Map.Entry<Object, Object> entry : attrs.entrySet()) {
				String key = entry.getKey().toString();
				String value = (String)entry.getValue();
				if (key.startsWith("Class-Path-Filter-Match-On-")) {
					String[] filters = JarRsrcLoader.splitSpaces(value, ' ');
					String osFilter = key.substring("Class-Path-Filter-Match-On-".length());
					JarRsrcLoader.debug("classpath declared filter [" + osFilter + "] for libraries [" + value + "]");
					this.allFilters.addAll(Arrays.asList(filters));
					this.libsForFilter.put(osFilter, Arrays.asList(filters));
				} 
			} 
		}
		
		public boolean accept(String library, String actualOsFilter) {
			boolean notConditionalLibrarySoAcceptIt = true;
			for (String libFilter : this.allFilters) {
				if (library.contains(libFilter)) {
					notConditionalLibrarySoAcceptIt = false;
					boolean alreadyAccepted = ((List)this.libsForFilter.get(actualOsFilter)).contains(libFilter);
					if (alreadyAccepted)
						return true; 
				} 
			} 
			return notConditionalLibrarySoAcceptIt;
		}
		
		private ManifestInfo() {}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
		ManifestInfo mi = readManifestInfo();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(cl));
		List<String> original = Arrays.asList(mi.rsrcClassPath);
		List<String> all = filter(mi, original);
		URL[] rsrcUrls = new URL[all.size()];
		for (int i = 0; i < all.size(); i++) {
			String rsrcPath = all.get(i);
			if (rsrcPath.endsWith("/")) {
				rsrcUrls[i] = new URL("rsrc:" + rsrcPath);
			} else {
				rsrcUrls[i] = new URL("jar:rsrc:" + rsrcPath + "!/");
			} 
		} 
		debug("final classpath " + Arrays.<URL>asList(rsrcUrls));
		ClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, null);
		Thread.currentThread().setContextClassLoader(jceClassLoader);
		try {
			Class<?> c = Class.forName(mi.rsrcMainClass, true, jceClassLoader);
			Method main = c.getMethod("main", new Class[] { args.getClass() });
			main.invoke(null, new Object[] { args });
		} catch (UnsatisfiedLinkError e) {
			throw new Error("The conditional loaded libraries wheren't filtered properly. The initial libs where \n initial=[" + original + "] while the filtered ones \nfiltered=[" + all + "]", e);
		} 
	}
	
	private static ManifestInfo readManifestInfo() throws IOException {
		Enumeration<URL> resEnum = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
		while (resEnum.hasMoreElements()) {
			try {
				URL url = resEnum.nextElement();
				InputStream is = url.openStream();
				if (is != null) {
					debug("reading from " + url);
					ManifestInfo result = new ManifestInfo();
					Manifest manifest = new Manifest(is);
					Attributes mainAttribs = manifest.getMainAttributes();
					result.rsrcMainClass = mainAttribs.getValue("Rsrc-Main-Class");
					String rsrcCP = mainAttribs.getValue("Class-Path");
					if (rsrcCP == null)
						rsrcCP = ""; 
					result.rsrcClassPath = splitSpaces(rsrcCP, ' ');
					result.configureConditionalPath(mainAttribs);
					if (result.rsrcMainClass != null && !result.rsrcMainClass.trim().equals(""))
						return result; 
				} 
			} catch (Exception e) {
				warn("Wrong manifests on classpath", e);
			} 
		} 
		System.err.println("Missing attributes for JarRsrcLoader in Manifest (Rsrc-Main-Class, Class-Path)");
		return null;
	}
	
	private static void debug(String message) {}
	
	private static void warn(String message, Exception e) {
		System.err.println(message);
		e.printStackTrace(System.err);
	}
	
	private static List<String> filter(ManifestInfo mi, List<String> all) {
		String osName = System.getProperty("os.name").toLowerCase(Locale.US);
		String osArch = System.getProperty("os.arch").toLowerCase(Locale.US);
		String libFilter = condition(osName, osArch);
		debug("accepting only the libraries not matched at all or the ones declared with [Class-Path-Filter-Match-On-" + libFilter + "]");
		List<String> result = new ArrayList<>();
		debug("analysing libraries [" + all + "]");
		for (String library : all) {
			boolean accepted = mi.accept(library, libFilter);
			if (accepted)
				result.add(library); 
			debug((accepted ? "accept" : "ignore") + " library [" + library + "]");
		} 
		debug("filtered final libraries [" + result + "]");
		return result;
	}
	
	private static String condition(String osName, String osArch) {
		return ("osName=" + osName + "---osArch=" + osArch).replaceAll("[^a-zA-Z0-9\\-]", "-");
	}
	
	private static String[] splitSpaces(String line, char separator) {
		if (line == null)
			return null; 
		List<String> result = new ArrayList();
		int firstPos = 0;
		while (firstPos < line.length()) {
			int lastPos = line.indexOf(separator, firstPos);
			if (lastPos == -1)
				lastPos = line.length(); 
			if (lastPos > firstPos)
				result.add(line.substring(firstPos, lastPos)); 
			firstPos = lastPos + 1;
		} 
		return result.<String>toArray(new String[result.size()]);
	}
}
