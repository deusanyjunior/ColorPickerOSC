package com.scurab.android.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.ref.WeakReference;

import br.usp.ime.compmus.ConnectionInterface;
import br.usp.ime.compmus.Packet;
import br.usp.ime.compmus.SettingsActivity;
import br.usp.ime.compmus.UnicastUDP;


public class MainActivity extends Activity {

    final Messenger uiMessenger = new Messenger(new UiHandler(this));
    private Context context = this;
    private GradientView mTop;
    private GradientView mBottom;
    private int alpha = 0;
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private ToggleButton toggleConnection;
    private RadioGroup radioGroupMode;
    private SeekBar seekBarAlpha;
    private ConnectionInterface[] connections = {new UnicastUDP()};
    private ConnectionInterface connection = null;
    private boolean isARGB = true;
    private ToggleButton.OnCheckedChangeListener toggleConnectionOnCheckedChangeListener = new ToggleButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {

                connect();
            } else {

                disconnect();
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener radioGroupModeOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if (checkedId == R.id.radioButtonARGB) {

                setIsARGB(true);
                updateValues();
            } else if (checkedId == R.id.radioButtonRGBA){

                setIsARGB(false);
                updateValues();
            }
        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarAlphaOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        int alphaValue = 255;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            alphaValue = progress;
            setAlpha(alphaValue);
            updateValues();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    // Widgets

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_main, null);
        setContentView(view);

        loadWidgets();

        mTop = (GradientView)findViewById(R.id.top);
        mBottom = (GradientView)findViewById(R.id.bottom);
        mTop.setBrightnessGradientView(mBottom);
        mBottom.setOnColorChangedListener(new GradientView.OnColorChangedListener() {

            @Override
            public void onColorChanged(GradientView view, int color) {

//                setAlpha(Color.alpha(color));
                setRed(Color.red(color));
                setGreen(Color.green(color));
                setBlue(Color.blue(color));
                updateValues();
            }
        });

        int color = 0xFF394572;
        mTop.setColor(color);
    }


    // Toggle Connection

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    // RadioGroup Mode

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;

            default:
                break;
        }
        return true;
    }


    // SeekBar Alpha

    private void loadWidgets() {

        toggleConnection = (ToggleButton) findViewById(R.id.toggleConnection);
        toggleConnection.setOnCheckedChangeListener(toggleConnectionOnCheckedChangeListener);

        radioGroupMode = (RadioGroup) findViewById(R.id.radioGroupMode);
        radioGroupMode.setOnCheckedChangeListener(radioGroupModeOnCheckedChangeListener);

        seekBarAlpha = (SeekBar) findViewById(R.id.seekBarAlpha);
        seekBarAlpha.setMax(255);
        seekBarAlpha.setProgress(0);
        seekBarAlpha.setOnSeekBarChangeListener(seekBarAlphaOnSeekBarChangeListener);
    }


    // Colors

    private boolean getIsARGB() {
        return isARGB;
    }

    synchronized private void setIsARGB(boolean isARGB) {

        this.isARGB = isARGB;
    }

    private int getAlpha() {
        return alpha;
    }

    synchronized private void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    private int getRed() {
        return red;
    }

    synchronized private void setRed(int red) {
        this.red = red;
    }

    private int getGreen() {
        return green;
    }

    synchronized private void setGreen(int green) {
        this.green = green;
    }

    private int getBlue() {
        return blue;
    }

    synchronized private void setBlue(int blue) {
        this.blue = blue;
    }


    // Update all values

    synchronized private void updateValues() {

        // Alpha Red Green Blue
        if (isARGB) {

            if(connection != null && connection.isConnected()) {

                new AsyncTaskSend().execute(getAlpha(), getRed(), getGreen(), getBlue());
            }
        }
        // Red Green Blue Alpha
        else {

            if(connection != null && connection.isConnected()) {

                new AsyncTaskSend().execute(getRed(), getGreen(), getBlue(), getAlpha());
            }
        }
    }

    public void handleUiMessage(Message msg) {

        switch (msg.arg1) {

            case 0:
                toggleConnection.setChecked(false);
                Toast.makeText(context, "NOT connected.",Toast.LENGTH_LONG).show();
                break;
            case 1:
                toggleConnection.setChecked(true);
                Toast.makeText(context, "Connected.",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void connect() {

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        for (ConnectionInterface connection : connections) {

            if (preferences.getString("pref_listConnections", " ").equals(connection.getSettingName())) {

                AsyncTaskConnect connectTask = new AsyncTaskConnect();
                this.connection = connection;
                connectTask.execute(this.connection);
                break;
            }
        }
    }

            // Connection codes

    private void disconnect() {

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        for (ConnectionInterface connection : connections) {

            if (preferences.getString("pref_listConnections", " ").equals(connection.getSettingName())) {

                AsyncTaskDisconnect disconnectTask = new AsyncTaskDisconnect();
                this.connection = connection;
                disconnectTask.execute(this.connection);
                break;
            }
        }
    }

    // UiHandler
    static class UiHandler extends android.os.Handler {

        public final WeakReference<MainActivity> parent;

        public UiHandler(MainActivity activity) {

            this.parent = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            MainActivity activity = parent.get();
            if (activity != null) {

                activity.handleUiMessage(msg);
            }
        }
    }

    private class AsyncTaskConnect extends AsyncTask<ConnectionInterface, Void, Void> {

        @Override
        protected Void doInBackground(ConnectionInterface... params) {

            ConnectionInterface connection = params[0];
            connection.loadSettings(context);
            connection.connect();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (connection.isConnected()) {

                try {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    uiMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {

                try {
                    Message msg = new Message();
                    msg.arg1 = 0;
                    uiMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private class AsyncTaskDisconnect extends AsyncTask<ConnectionInterface, Void, Void> {

        @Override
        protected Void doInBackground(ConnectionInterface... params) {

            ConnectionInterface connection = params[0];
            connection.disconnect();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (connection.isConnected()) {

                try {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    uiMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {

                try {
                    Message msg = new Message();
                    msg.arg1 = 0;
                    uiMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private class AsyncTaskSend extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            connection.send(ConnectionInterface.PUSH, new Packet(params[0], params[1], params[2], params[3]));
            return null;
        }
    }

}
