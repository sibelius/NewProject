package edu.arizona.adherence;

import android.content.Context;

import com.ubhave.datahandler.loggertypes.AbstractStoreOnlyLogger;

/**
 * Created by sibelius on 6/1/14.
 */
public class SensingStoreOnlyLogger extends AbstractStoreOnlyLogger {

    public SensingStoreOnlyLogger(Context context) {
        super(context);
    }

    @Override
    protected String getLocalStorageDirectoryName()
    {
        return "Treatment-Adherence";
    }

    @Override
    protected String getUniqueUserId()
    {
		/*
		 * Note: Should be unique to this user, not a static string
		 */
        return "TaUser";
    }

    @Override
    protected String getDeviceId()
    {
		/*
		 * Note: Should be unique to this device, not a static string
		 */
        return "TaDevice";
    }

    @Override
    protected boolean shouldPrintLogMessages() {
		/*
		 * Turn on/off Log.d messages
		 */
        return true;
    }
}
