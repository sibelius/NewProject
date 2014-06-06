package edu.arizona.adherence.sensormanager.sensors;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.config.GlobalConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;
import com.ubhave.sensormanager.sensors.pull.AbstractPullSensor;

import edu.arizona.adherence.sensormanager.data.ZephyrData;
import edu.arizona.adherence.sensormanager.process.ZephyrProcessor;

/**
 * Created by sibelius on 6/5/14.
 */
public class ZephyrSensor extends AbstractPullSensor {

    private static final String TAG = "ZephyrSensor";
    private static final String PERMISSION_BLUETOOTH = "android.permission.BLUETOOTH";
    private static final String PERMISSION_BT_ADMIN = "android.permission.BLUETOOTH_ADMIN";

    public final static String SENSOR_NAME_ZEPHYR = "Zephyr";

    private static ZephyrSensor zephyrSensor;
    private static Object lock = new Object();

    private BluetoothAdapter bluetooth = null;
    private ZephyrData zephyrData;
    private int mBatteryChargeInd;
    private int mHeartRate; // Beats per minute
    private int mHeartBeatNum;
    private int[] mHeartBeatTS;
    private double mDistance;
    private double mInstantSpeed;
    private int mStrides;

    public static ZephyrSensor getZephyrSensor(final Context context) throws ESException {
        if (zephyrSensor == null) {
            synchronized (lock) {
                if (zephyrSensor == null) {
                    if (allPermissionsGranted(context, new String[]{PERMISSION_BLUETOOTH, PERMISSION_BT_ADMIN})) {
                        zephyrSensor = new ZephyrSensor(context);
                    } else {
                        throw new ESException(ESException.PERMISSION_DENIED, SENSOR_NAME_ZEPHYR);
                    }
                }
            }
        }
        return zephyrSensor;
    }

    private ZephyrSensor(Context context) {
        super (context);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth == null)
        {
            if (GlobalConfig.shouldLog())
            {
                Log.d(TAG, "Device does not support Bluetooth");
            }
            return;
        }
    }

    @Override
    protected SensorData getMostRecentRawData() {
        return zephyrData;
    }

    @Override
    protected void processSensorData() {
        // TODO complete processSensorData
        ZephyrProcessor processor = (ZephyrProcessor) getProcessor();
        //processor.process(pullSenseStartTimestamp, sensorConfig.clone());
    }

    @Override
    protected boolean startSensing() {
        // TODO startSensing algorithm
        if (!bluetooth.isEnabled()) {
            bluetooth.enable();
        }

        return false;
    }

    @Override
    protected void stopSensing() {
        // TODO stopSensing algorithm
        if (bluetooth != null) {
            bluetooth.cancelDiscovery();
            bluetooth.disable();
        }
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getSensorType() {
        return SensorUtils.SENSOR_TYPE_ZEPHYR;
    }
}
