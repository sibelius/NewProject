package edu.arizona.adherence;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ubhave.datahandler.loggertypes.AbstractDataLogger;

public class MainActivityBackup extends Activity {

    private boolean isSensing;
    private SensingListener sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isSensing = false;

        AbstractDataLogger dataLogger = new SensingStoreOnlyLogger(this);
        sensor = new SensingListener(this, dataLogger);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (isSensing)
        {
            switchSensing(findViewById(R.id.sensing_button));
        }
    }

    public void switchActivity(final View view) {

    }

    /*
	 * Listener for UI button
	 */
    public void switchSensing(final View view)
    {
        isSensing = !isSensing;
        if (switchSensing())
        {
            switchButtonText((Button) view);
        }
    }

    private void switchButtonText(final Button sensingButton)
    {
        if (isSensing)
        {
            sensingButton.setText(R.string.button_stopSensing);
        }
        else
        {
            sensingButton.setText(R.string.button_startSensing);
        }
    }

    private boolean switchSensing()
    {
        if (sensor != null)
        {
            if (isSensing)
            {
                return sensor.startSensing();
            }
            else
            {
                return sensor.stopSensing();
            }
        }
        else
        {
            return false;
        }
    }
}
