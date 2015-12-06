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
	String PUSH = "colorpickerosc-color";

	/**
	 * Use this method to get the name of the connection interface.
	 * @return connection interface name
	 */
	String getName();
	
	/**
	 * Use this method to get the name of the setting defined on the preferences.
	 * This name is used to know if this connection is going to be used during the tests.
	 * @return setting name
	 */
	String getSettingName();
	
	/** Load connection settings from shared preferences.
	 */
	void loadSettings(Context context);

	/**
	 * Try to connect using this connection method.
	 * @return true if connected
	 */
	boolean connect();
	
	/**
	 * Try to disconnect.
	 * @return true if disconnected
	 */
	boolean disconnect();

	/**
	 * Check the connection;
	 * @return true if is connected
	 */
	boolean isConnected();

	/**
	 * Send data through this connection method.
	 * @return true if the data has been sent
	 */
	boolean send(String address, Packet packet);
}
