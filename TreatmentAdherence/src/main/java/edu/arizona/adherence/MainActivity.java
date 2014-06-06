package edu.arizona.adherence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ubhave.datahandler.loggertypes.AbstractDataLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {

    private static final String TAG_LABEL = "Label";

    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_EVENT = "event";
    private static final String TAG_ACTIVITY_START = "start";
    private static final String TAG_ACTIVITY_STOP = "stop";
    private final static String TAG_TIMESTAMP = "timestamp";
    private final static String TAG_LOCAL_TIME = "localTime";

    private static final String PREFS_NAME = "TreatmentPrefs";

    private boolean isSensing;
    private SharedPreferences mSettings;
    private Button sensingButton;
    private Button activityButton;
    private Spinner mActivities;
    private boolean isPerforming; // Check whether the user is performing the activity

    private AbstractDataLogger dataLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isSensing = false;
        isPerforming = false;

        mSettings = getSharedPreferences(PREFS_NAME, 0);
        sensingButton = (Button) findViewById(R.id.sensing_button);
        activityButton = (Button) findViewById(R.id.activity_button);

        mActivities = (Spinner) findViewById(R.id.spinner_activity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.activity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mActivities.setAdapter(adapter);

        dataLogger = new SensingStoreOnlyLogger(this);

        //AbstractDataLogger dataLogger = new SensingStoreOnlyLogger(this);
        //sensor = new SensingListener(this, dataLogger);
        //Intent startServiceIntent = new Intent(this, SensingService.class);
        //startService(startServiceIntent);

        if (isMyServiceRunning(SensingService.class)) {
            isSensing = true;
            switchButtonText();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();

        editor.clear();
        editor.putBoolean("sensing", isSensing);
        editor.putBoolean("performing", isPerforming);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        isSensing = mSettings.getBoolean("sensing", false);
        isPerforming = mSettings.getBoolean("performing", false);

        Log.d("isSensing", String.valueOf(isSensing));
        Log.d("isPerforming", String.valueOf(isPerforming));
        switchButtonText();

        if(isPerforming) {
            activityButton.setText(R.string.button_stopActivity);
            sensingButton.setEnabled(false);
            mActivities.setEnabled(false);
        } else {
            activityButton.setText(R.string.button_startActivity);
            sensingButton.setEnabled(true);
            mActivities.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSettings.edit().clear();
    }

    /*
             * Listener for UI button
             */
    public void switchSensing(final View view)
    {
        isSensing = !isSensing;
        if (switchSensing()) {
            switchButtonText();
        }
    }

    private void switchButtonText()
    {
        if (isSensing)
        {
            sensingButton.setText(R.string.button_stopSensing);
            activityButton.setEnabled(true);
        }
        else
        {
            sensingButton.setText(R.string.button_startSensing);
            activityButton.setEnabled(false);
        }
    }

    private boolean switchSensing()
    {
        if (isSensing) {
            Intent startServiceIntent = new Intent(this, SensingService.class);
            startService(startServiceIntent);
        } else {
            Intent stopServiceIntent = new Intent(this, SensingService.class);
            stopService(stopServiceIntent);
        }
        return true;
    }

    @SuppressLint("SimpleDateFormat")
    private String localTime()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zZ");
        return dateFormat.format(calendar.getTime());
    }

    //
    public void switchActivity(final View view) {
        if (!isPerforming) {
            JSONObject json = new JSONObject();
            try {
                json.put(TAG_ACTIVITY, mActivities.getSelectedItem().toString());
                json.put(TAG_EVENT, TAG_ACTIVITY_START);
                json.put(TAG_TIMESTAMP, System.currentTimeMillis());
                json.put(TAG_LOCAL_TIME, localTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataLogger.logExtra(TAG_LABEL, json);

            activityButton.setText(R.string.button_stopActivity);
            sensingButton.setEnabled(false);
            mActivities.setEnabled(false);
            Toast toast = Toast.makeText(this, "Activity Started", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            JSONObject json = new JSONObject();
            try {
                json.put(TAG_ACTIVITY, mActivities.getSelectedItem().toString());
                json.put(TAG_EVENT, TAG_ACTIVITY_STOP);
                json.put(TAG_TIMESTAMP, System.currentTimeMillis());
                json.put(TAG_LOCAL_TIME, localTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataLogger.logExtra(TAG_LABEL, json);

            activityButton.setText(R.string.button_startActivity);
            sensingButton.setEnabled(true);
            mActivities.setEnabled(true);
            Toast toast = Toast.makeText(this, "Activity Stopped", Toast.LENGTH_SHORT);
            toast.show();
        }
        isPerforming = !isPerforming;
    }
}
