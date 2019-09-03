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

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * This class will be compiled into the binary jar-in-jar-loader.zip. This ZIP is used for the
 * "Runnable JAR File Exporter"
 * 
 * @since 3.5
 */
public class RsrcURLStreamHandlerFactory implements URLStreamHandlerFactory {

	private ClassLoader classLoader;
	private URLStreamHandlerFactory chainFac;
	
	public RsrcURLStreamHandlerFactory(ClassLoader cl) {
		this.classLoader = cl;
	}

	public URLStreamHandler createURLStreamHandler(String protocol) {
		if (JIJConstants.INTERNAL_URL_PROTOCOL.equals(protocol)) 
			return new RsrcURLStreamHandler(classLoader);
		if (chainFac != null)
			return chainFac.createURLStreamHandler(protocol);
		return null;
	}
	
	/**
	 * Allow one other URLStreamHandler to be added.
	 * URL.setURLStreamHandlerFactory does not allow
	 * multiple factories to be added.
	 * The chained factory is called for all other protocols,
	 * except "rsrc". Use null to clear previously set Handler. 
	 * @param fac another factory to be chained with ours.
	 */
	public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
		chainFac = fac;
	}
	
}
