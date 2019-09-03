/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ferenc Hechler - initial API and implementation
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 219530 [jar application] add Jar-in-Jar ClassLoader option
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 262746 [jar exporter] Create a builder for jar-in-jar-loader.zip
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 262748 [jar exporter] extract constants for string literals in JarRsrcLoader et al.
 *******************************************************************************/
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This class will be compiled into the binary jar-in-jar-loader.zip. This ZIP is used for the "Runnable JAR File
 * Exporter"
 * 
 * @since 3.5
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JarRsrcLoader {
	private static class ManifestInfo {
		String rsrcMainClass;
		String[] rsrcClassPath;
		private Set<String> allFilters = new TreeSet<>();
		private Map<String, List<String>> libsForFilter = new TreeMap<>();

		final static String CLASS_PATH_FILTER_MATCH_ON = "Class-Path-Filter-Match-On-";

		public void configureConditionalPath(Attributes attrs) {
			for (Entry<Object, Object> entry : attrs.entrySet()) {
				String key = entry.getKey().toString();
				String value = (String) entry.getValue();
				if (key.startsWith(CLASS_PATH_FILTER_MATCH_ON)) {
					String[] filters = splitSpaces(value, ' ');
					String osFilter = key.substring(CLASS_PATH_FILTER_MATCH_ON.length());
					debug("classpath declared filter [" + osFilter + "] for libraries [" + value + "]");
					allFilters.addAll(Arrays.asList(filters));
					libsForFilter.put(osFilter, Arrays.asList(filters));
				}
			}
		}

		public boolean accept(String library, String actualOsFilter) {
			boolean notConditionalLibrarySoAcceptIt = true;
			// debug("analyse " + library);
			for (String libFilter : allFilters) {
				if (library.contains(libFilter)) {
					// debug("found libFilter=" + libFilter + " but valid for [" + libsForFilter.get(actualOsFilter)
					// + "]");
					notConditionalLibrarySoAcceptIt = false;
					boolean alreadyAccepted = libsForFilter.get(actualOsFilter).contains(libFilter);
					if (alreadyAccepted)
						return true;
				}
			}
			return notConditionalLibrarySoAcceptIt;
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
		ManifestInfo mi = readManifestInfo();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(cl));
		List<String> original = Arrays.asList(mi.rsrcClassPath);
		List<String> all = filter(mi, original);

		URL[] rsrcUrls = new URL[all.size()];
		for (int i = 0; i < all.size(); i++) {
			String rsrcPath = all.get(i);
			if (rsrcPath.endsWith(JIJConstants.PATH_SEPARATOR))
				rsrcUrls[i] = new URL(JIJConstants.INTERNAL_URL_PROTOCOL_WITH_COLON + rsrcPath);
			else
				rsrcUrls[i] = new URL(JIJConstants.JAR_INTERNAL_URL_PROTOCOL_WITH_COLON + rsrcPath
						+ JIJConstants.JAR_INTERNAL_SEPARATOR);
		}
		debug("final classpath "+Arrays.asList(rsrcUrls));
		ClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, null);
		Thread.currentThread().setContextClassLoader(jceClassLoader);
		try {
			Class c = Class.forName(mi.rsrcMainClass, true, jceClassLoader);
			Method main = c.getMethod(JIJConstants.MAIN_METHOD_NAME, new Class[] { args.getClass() });
			main.invoke((Object) null, new Object[] { args });
		} catch (UnsatisfiedLinkError e) {
			throw new Error(
					"The conditional loaded libraries wheren't filtered properly. The initial libs where \n initial=["
							+ original + "] while the filtered ones \nfiltered=[" + all + "]",
					e);
		}
	}

	private static ManifestInfo readManifestInfo() throws IOException {
		Enumeration resEnum;
		resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
		while (resEnum.hasMoreElements()) {
			try {
				URL url = (URL) resEnum.nextElement();
				InputStream is = url.openStream();
				if (is != null) {
					debug("reading from " + url);
					ManifestInfo result = new ManifestInfo();
					Manifest manifest = new Manifest(is);
					Attributes mainAttribs = manifest.getMainAttributes();
					result.rsrcMainClass = mainAttribs.getValue(JIJConstants.REDIRECTED_MAIN_CLASS_MANIFEST_NAME);
					String rsrcCP = mainAttribs.getValue(JIJConstants.REDIRECTED_CLASS_PATH_MANIFEST_NAME);
					if (rsrcCP == null)
						rsrcCP = JIJConstants.DEFAULT_REDIRECTED_CLASSPATH;
					result.rsrcClassPath = splitSpaces(rsrcCP, ' ');
					result.configureConditionalPath(mainAttribs);
					if ((result.rsrcMainClass != null) && !result.rsrcMainClass.trim().equals("")) //$NON-NLS-1$
						return result;
				}
			} catch (Exception e) {
				warn("Wrong manifests on classpath", e);
			}
		}
		System.err.println(
				"Missing attributes for JarRsrcLoader in Manifest (" + JIJConstants.REDIRECTED_MAIN_CLASS_MANIFEST_NAME //$NON-NLS-1$
						+ ", " + JIJConstants.REDIRECTED_CLASS_PATH_MANIFEST_NAME + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		return null;
	}

	private static void debug(String message) {
//		System.out.println(message);
	}
	private static void warn(String message, Exception e) {
		System.err.println(message);
		e.printStackTrace(System.err);
	}

	private static List<String> filter(ManifestInfo mi, List<String> all) {
		String osName = System.getProperty("os.name").toLowerCase(Locale.US);
		String osArch = System.getProperty("os.arch").toLowerCase(Locale.US);
		// String osVersion = System.getProperty("os.version").toLowerCase(Locale.US);

		String libFilter = condition(osName, osArch);
		debug("accepting only the libraries not matched at all or the ones declared with ["
				+ ManifestInfo.CLASS_PATH_FILTER_MATCH_ON + libFilter + "]");
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

	/** see http://maven.apache.org/enforcer/enforcer-rules/requireOS.html */
	private static String condition(String osName, String osArch) {
		return ("osName=" + osName + "---osArch=" + osArch).replaceAll("[^a-zA-Z0-9\\-]", "-");
	}

	/**
	 * JDK 1.3.1 does not support String.split(), so we have to do it manually. Skip all spaces (tabs are not handled)
	 * 
	 * @param line
	 *            the line to split
	 * @return array of strings
	 */
	private static String[] splitSpaces(String line, char separator) {
		if (line == null)
			return null;
		List result = new ArrayList();
		int firstPos = 0;
		while (firstPos < line.length()) {
			int lastPos = line.indexOf(separator, firstPos);
			if (lastPos == -1)
				lastPos = line.length();
			if (lastPos > firstPos) {
				result.add(line.substring(firstPos, lastPos));
			}
			firstPos = lastPos + 1;
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

}
/*
 * Conditional-Class-Path-linux-i386: org.eclipse.swt.gtk.linux.x86 Conditional-Class-Path-linux-x86-64:
 * org.eclipse.swt.gtk.linux.x86_64 Conditional-Class-Path-linux-amd64: org.eclipse.swt.gtk.linux.x86_64
 * Conditional-Class-Path-windows-7-x86: org.eclipse.swt.win32.win32.x86 Conditional-Class-Path-windows-x86:
 * org.eclipse.swt.win32.win32.x86 Conditional-Class-Path-windows-x86-64: org.eclipse.swt.win32.win32.x86_64
 * Conditional-Class-Path-windows-amd64: org.eclipse.swt.win32.win32.x86_64 Conditional-Class-Path-mac-os-x-i386:
 * org.eclipse.swt.cocoa.macosx Conditional-Class-Path-mac-os-x-x86-64: org.eclipse.swt.cocoa.macosx.x86_64
 */