package edu.arizona.adherence;

import android.content.Context;
import android.util.Log;

import com.ubhave.dataformatter.DataFormatter;
import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.SensorDataListener;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorInterface;
import com.ubhave.sensormanager.sensors.SensorUtils;

import java.util.ArrayList;

/**
 * Created by sibelius on 6/1/14.
 */
public class SensingListener implements SensorDataListener {

    private ESSensorManager sensorManager;
    private AbstractDataLogger dataLogger;
    private Context context;
    private ArrayList<Integer> mSubscriptions;

    public SensingListener(final Context context, final AbstractDataLogger logger) {
        this.context = context;
        this.dataLogger = logger;
        try {
            sensorManager = ESSensorManager.getSensorManager(context);

        } catch(ESException e) {
            sensorManager = null;
            e.printStackTrace();
        }
    }

    public boolean startSensing()
    {
        int subscriptionId;
        mSubscriptions = new ArrayList<Integer>();
        ArrayList<SensorInterface> sensors = SensorUtils.getAllSensors(context);
        try
        {
            for (SensorInterface aSensor : sensors ) {
                subscriptionId = sensorManager.subscribeToSensorData(aSensor.getSensorType(), this);
                mSubscriptions.add(subscriptionId);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean stopSensing()
    {
        try
        {
            for (Integer subscriptionId : mSubscriptions) {
                Log.d("Sensor", "Unsubscribing id = " + subscriptionId);
                sensorManager.unsubscribeFromSensorData(subscriptionId);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDataSensed(SensorData data) {
        DataFormatter formatter = DataFormatter.getJSONFormatter(context, data.getSensorType());
        Log.d("Data Sensed", formatter.toString(data));
        dataLogger.logSensorData(data);
    }

    @Override
    public void onCrossingLowBatteryThreshold(boolean isBelowThreshold) {
        // Nothing for example app
    }
}
