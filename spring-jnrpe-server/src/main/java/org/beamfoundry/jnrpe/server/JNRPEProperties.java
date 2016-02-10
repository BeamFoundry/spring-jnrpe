package org.beamfoundry.jnrpe.server;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "jnrpe.server")
public class JNRPEProperties {

	/**
	 * Default port used when the configured port is {@code null}.
	 */
	public static final int DEFAULT_PORT = 5666;
	
    /**
     * Default read timeout is 10 seconds.
     */
    private static final int DEFAULT_READ_TIMEOUT = 10;

    /**
     * Default write timeout is 60 seconds.
     */
    private static final int DEFAULT_WRITE_TIMEOUT = 60;
    
    /**
     * Default number of accepted connections.
     */
    private static final int DEFAULT_MAX_ACCEPTED_CONNECTIONS = 128;
    
    /**
     * Default boolean to start server
     */
    private static final boolean ENABLE_SERVER = true;
    
    
    /**
     * The list of accepted hosts.
     */
    private Collection<String> acceptedHosts = new ArrayList<String>();
    
    /**
     * The list of bind addresses.
     */
    private Collection<String> bindAddresses = new ArrayList<String>();
    
    /**
     * Sets if macros ($ARGxx$) should be expanded or not.
     */
    private boolean acceptParams = false;
    
    /**
     * The JNRPE charset.
     */
    private Charset charset = Charset.defaultCharset();

    /**
     * Read timeout in seconds.
     */
    private int readTimeout = DEFAULT_READ_TIMEOUT;

    /**
     * Write timeout in seconds.
     */
    private int writeTimeout = DEFAULT_WRITE_TIMEOUT;
    
    /**
     * Max connection
     */
    private int maxConnections = DEFAULT_MAX_ACCEPTED_CONNECTIONS;
    
    /**
     * Enable jNRPE Server
     */
    
    private boolean enable = ENABLE_SERVER;
    
    private Map<String,String> commands = new HashMap<>();


	public String getCharset() {
		return charset.name();
	}


	public Collection<String> getAcceptedHosts() {
		return acceptedHosts;
	}


	public Collection<String> getBindAddresses() {
		return bindAddresses;
	}


	public boolean isAcceptParams() {
		return acceptParams;
	}


	public int getReadTimeout() {
		return readTimeout;
	}


	public int getWriteTimeout() {
		return writeTimeout;
	}


	public int getMaxConnections() {
		return maxConnections;
	}


	public Map<String, String> getCommands() {
		return commands;
	}


	public void setAcceptedHosts(Collection<String> acceptedHosts) {
		this.acceptedHosts = acceptedHosts;
	}


	public void setBindAddresses(Collection<String> bindAddresses) {
		this.bindAddresses = bindAddresses;
	}


	public void setAcceptParams(boolean acceptParams) {
		this.acceptParams = acceptParams;
	}


	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}


	public void setWriteTimeout(int writeTimeout) {
		this.writeTimeout = writeTimeout;
	}


	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}


	public void setCommands(Map<String, String> commands) {
		this.commands = commands;
	}


	public boolean getEnable() {
		return enable;
	}


	public void setEnable(boolean enable) {
		this.enable = enable;
	}
    
}
