/*******************************************************************************
 * Copyright (c) 2015, 2018 Red Hat.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/

package org.eclipse.linuxtools.internal.docker.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TCP Connection settings
 */
public class TCPConnectionSettings extends BaseConnectionSettings {

	/**
	 * the host to connect to (a URI representation, including 'tcp' scheme and
	 * port number).
	 */
	private final String host;
	private String ipaddr;

	public final Pattern ipaddrPattern = Pattern
			.compile("[a-z]*://([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)[:]*[0-9]*");

	public final Pattern httpPattern = Pattern.compile(
			"^(https?)://([-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]):[0-9]*");

	/**
	 * absolute path to folder containing the certificates (ca.pem, key.pem and
	 * cert.pem).
	 */
	private final String pathToCertificates;

	/**
	 * Constructor
	 * 
	 * @param host
	 *            host to connect to
	 * @param pathToCertificates
	 *            absolute path to folder containing the certificates
	 */
	public TCPConnectionSettings(final String host,
			final String pathToCertificates) {
		super();
		this.host = new HostBuilder(host).enableTLS(pathToCertificates);
		this.pathToCertificates = pathToCertificates;
	}

	@Override
	public BindingType getType() {
		return BindingType.TCP_CONNECTION;
	}

	@Override
	public Object[] getProperties() {
		return new Object[] {
				new Object[] { "Type", this.getType().toString() }, //$NON-NLS-1$
				new Object[] { "Host", this.getHost() }, //$NON-NLS-1$
				new Object[] { "Certificates", //$NON-NLS-1$
						this.getPathToCertificates() }, };
	}

	/**
	 * @return the ip address for the host
	 */
	public String getAddr() {
		if (ipaddr == null) {
			Matcher m = ipaddrPattern.matcher(host);
			if (m.matches()) {
				ipaddr = m.group(1);
			} else {
				Matcher m2 = httpPattern.matcher(host);
				if (m2.matches()) {
					try {
						ipaddr = InetAddress.getByName(m2.group(2))
								.getHostAddress();
					} catch (UnknownHostException e) {
						return ""; //$NON-NLS-1$
					}
				} else {
					return ""; //$NON-NLS-1$
				}
			}
		}
		return ipaddr;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	public boolean hasHost() {
		return this.host != null && !this.host.isEmpty();
	}

	/**
	 * @return the tlsVerify
	 */
	public boolean isTlsVerify() {
		return this.pathToCertificates != null
				&& !this.pathToCertificates.isEmpty();
	}

	/**
	 * @return the pathToCertificates
	 */
	public String getPathToCertificates() {
		return pathToCertificates;
	}

	/**
	 * A utility class to build the actual {@code host} field from the given
	 * input by setting the correct {@code http} or {@code https} scheme.
	 */
	private static class HostBuilder {

		private static String HTTP_SCHEME = "http://"; //$NON-NLS-1$

		private static String TCP_SCHEME = "tcp://"; //$NON-NLS-1$

		private static String HTTPS_SCHEME = "https://"; //$NON-NLS-1$

		private final String host;

		public HostBuilder(final String host) {
			if (host == null || host.isEmpty()) {
				this.host = "";
			} else if (!host.matches("\\w+://.*")) { //$NON-NLS-1$
				this.host = HTTP_SCHEME + host;
			} else {
				this.host = host.replace(TCP_SCHEME, HTTP_SCHEME);
			}
		}

		public String enableTLS(final String pathToCertificates) {
			if (pathToCertificates == null || pathToCertificates.isEmpty()) {
				return this.host;
			}
			// enforce 'https'
			return this.host.replace(HTTP_SCHEME, HTTPS_SCHEME);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((pathToCertificates == null) ? 0
				: pathToCertificates.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TCPConnectionSettings other = (TCPConnectionSettings) obj;
		if (host == null) {
			if (other.host != null) {
				return false;
			}
		} else if (!host.equals(other.host)) {
			return false;
		}
		if (pathToCertificates == null) {
			if (other.pathToCertificates != null) {
				return false;
			}
		} else if (!pathToCertificates.equals(other.pathToCertificates)) {
			return false;
		}
		return true;
	}


}
