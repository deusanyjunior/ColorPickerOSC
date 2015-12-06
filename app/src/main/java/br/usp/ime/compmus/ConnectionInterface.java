/**
 * 
 */
package br.usp.ime.compmus;

import android.content.Context;

/**
 * Represents a connection method to exchange data.
 * @author dj
 */
public interface ConnectionInterface {

	/*
	 * Address of push messages or push channel
	 */
	public static final String PUSH = "mobcolorpicker-color";

	/**
	 * Use this method to get the name of the connection interface.
	 * @return connection interface name
	 */
	public String getName();
	
	/**
	 * Use this method to get the name of the setting defined on the preferences.
	 * This name is used to know if this connection is going to be used during the tests.
	 * @return setting name
	 */
	public String getSettingName();
	
	/** Load connection settings from shared preferences.
	 */
	public void loadSettings(Context context);

	/**
	 * Try to connect using this connection method.
	 * @return true if connected
	 */
	public boolean connect();
	
	/**
	 * Try to disconnect.
	 * @return true if disconnected
	 */
	public boolean disconnect();

	/**
	 * Check the connection;
	 * @return
	 */
	public boolean isConnected();

	/**
	 * Send data through this connection method.
	 * @return true if the data has been sent
	 */
	public boolean send(String address, Packet packet);
}
