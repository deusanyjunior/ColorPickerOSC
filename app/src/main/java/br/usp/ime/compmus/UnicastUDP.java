/**
 * 
 */
package br.usp.ime.compmus;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import com.scurab.android.colorpicker.R;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

/**
 * @author dj
 *
 */
public class UnicastUDP implements ConnectionInterface {

	private boolean isConnected = false;
	private String name = "unicastUDP";
	private String settingName = "pref_connectionUnicastUDP";

	private String host;
	private int port;

	private OSCPortOut sender;

	/**
	 * 
	 */
	public UnicastUDP() {

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
							
		this.host = preferences.getString("pref_unicastUDPHost", 
				context.getResources().getString(
						R.string.pref_unicastUDPHostDefault));
		
		this.port = Integer.parseInt(preferences.getString("pref_unicastUDPPort", 
				context.getResources().getString(
						R.string.pref_unicastUDPPortDefault)));
	}
	
	@Override
	public boolean connect() {
		
		boolean senderStarted = this.startSender();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (!senderStarted) {

			isConnected = false;
			return false;
		}

		isConnected = true;
		return true;
	}

	@Override
	public boolean disconnect() {
		
		boolean senderStopped = stopSender();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (!senderStopped) {

			isConnected = true;
			return false;
		}

		isConnected = false;
		return true;
	}

	@Override
	public boolean isConnected() {

		return isConnected;
	}
	
	@Override
	public boolean send(String address, Packet packet) {
		
		OSCMessage message = new OSCMessage("/" + address, packet.getListContents());
		
		return send(message);
	}
	
	private boolean send(OSCMessage message) {
		
		try {
			this.sender.send(message);
			return true;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean startSender() {
		
		try {
			InetAddress address = InetAddress.getByName(this.host);
			this.sender = new OSCPortOut(address, this.port);			
			return true;
		} catch (BindException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		this.sender = null;
		return false;
	}
	
	private boolean stopSender() {

		if (this.sender != null) {

			this.sender.close();
			this.sender = null;
		}

		return false;
	}
}
