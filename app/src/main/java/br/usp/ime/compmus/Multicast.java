/**
 * 
 */
package br.usp.ime.compmus;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.preference.PreferenceManager;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import com.scurab.android.colorpicker.R;
/**
 * @author dj
 *
 */
public class Multicast implements ConnectionInterface {

	private boolean isConnected = false;
	private String name = "multicast";
	private String settingName = "pref_connectionMulticast";

	private String host;
	private int port;
	private int ttl;
	private static MulticastLock multicastLock;
	private int networkInterfaceID;
	private NetworkInterface networkInterface;

	private OSCPortOut sender;

	/**
	 *
	 */
	public Multicast() {

	}
	
	@Override 
	public String getName() {
		
		return this.name;
	}

	@Override
	public String getSettingName() {
		
		return this.settingName;
	}
	
	@Override
	public void loadSettings(Context context) {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		this.host = preferences.getString("pref_multicastHost",
				context.getResources().getString(
						R.string.pref_multicastHostDefault));

		this.port = Integer.parseInt(preferences.getString("pref_multicastPort",
				context.getResources().getString(
						R.string.pref_multicastPortDefault)));

		this.ttl = Integer.parseInt(preferences.getString("pref_multicastTTL",
				context.getResources().getString(
						R.string.pref_multicastTTLDefault)));

		this.networkInterfaceID = Integer.parseInt(preferences.getString("pref_multicastInterface",
				context.getResources().getString(
						R.string.pref_multicastInterfaceDefault)));

		this.networkInterface = MobileDevice.getNetworkInterfaces().
				get(this.networkInterfaceID);

		this.acquireMulticastLock(context);
	}
	
	@Override
	public boolean connect() {
		
		boolean senderStarted = this.startSender();

		if (!senderStarted) {

			isConnected = false;
			return false;
		}

		isConnected = true;
		return true;
	}

	@Override
	public boolean disconnect() {
		
		stopSender();
		releaseMulticastLock();
		isConnected = false;
		return true;
	}

	@Override
	/**
	 *
	 */
	public boolean isConnected() {

		return isConnected;
	}
	
	@Override
	public boolean send(String address, Packet packet) {
		
		OSCMessage message = new OSCMessage("/" + address, packet.getListContents());
		
		return send(message);
	}

	/**
	 *
	 * @param message
	 * @return true when the message is sent without problems
	 */
	private boolean send(OSCMessage message) {
		
		try {
			this.sender.send(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 *
	 * @return true when started
	 */
	private boolean startSender() {
		
		try {
			InetAddress address = InetAddress.getByName(this.host);
			MulticastSocket multicastSocketSender = new MulticastSocket(this.port);
			multicastSocketSender.setReuseAddress(true);
			multicastSocketSender.setTimeToLive(this.ttl);
			multicastSocketSender.joinGroup(new InetSocketAddress(address, this.port), networkInterface);
			this.sender = new OSCPortOut(address, this.port, multicastSocketSender);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.sender = null;
		return false;
	}

	/**
	 *
	 */
	private void stopSender() {

		if (this.sender != null) {

			this.sender.close();
			this.sender = null;
		}
	}

	/**
	 *
	 * @param ctx
	 * @return
	 */
	public boolean acquireMulticastLock(Context ctx) {

		WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {

			multicastLock = wifiManager.createMulticastLock(this.getName());
			multicastLock.setReferenceCounted(false);
			multicastLock.acquire();
			return true;
		} else {

			return releaseMulticastLock();
		}
	}

	/**
	 *
	 * @return
	 */
	public static boolean releaseMulticastLock() {

		if (multicastLock != null && multicastLock.isHeld()) {

			multicastLock.release();
			return true;
		}
		return false;
	}
}
