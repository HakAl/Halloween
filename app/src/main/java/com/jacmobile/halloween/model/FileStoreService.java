package com.jacmobile.halloween.model;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jacmobile.halloween.util.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Key file listing all saved files. Write to a file named "keys" the name of each file saved in a
 * comma-separated list.
 * <p/>
 * fn,fn,fn,fn
 */
public class FileStoreService extends Service
{
    public static final String DATA_INDEX = "keys";
    private static final String FILE_PREFIX = "sd_";
    public static final String ERROR = "e";

    public static void start(Context context, String data)
    {
        Intent intent = new Intent(context, FileStoreService.class);
        intent.putExtra(FILE_PREFIX, data);
        context.startService(intent);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent != null) {
            String data = intent.getStringExtra(FILE_PREFIX);
            if (!TextUtils.isEmpty(data)) {
                addToDataIndex(data);
            } else {
                stopSelf();
            }
        } else {
            stopSelf();
        }

        return START_STICKY;
    }

    private void addToDataIndex(String data)
    {
        String fnToWrite = FILE_PREFIX + System.currentTimeMillis();
        String dataList = read(DATA_INDEX);
        delete(DATA_INDEX);
        if (dataList.equals(ERROR)) {
            Logger.exception("No key file stored. Create a new one.");
            // no key file stored, make one
            write(DATA_INDEX, fnToWrite + ",");
        } else {
            write(DATA_INDEX, dataList + "," + fnToWrite + ",");
        }
        write(fnToWrite, data);
        stopSelf();
    }

    private void delete(String fn)
    {
        deleteFile(fn);
    }

    private String read(String fn)
    {
        try {
            FileInputStream mInput = openFileInput(fn);
            byte[] data = new byte[128];
            mInput.read(data);
            mInput.close();
            String display = new String(data);
            return display.trim();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    private void write(String fn, String data)
    {
        try {
            FileOutputStream mOutput = openFileOutput(fn, Activity.MODE_PRIVATE);
            mOutput.write(data.getBytes());
            mOutput.flush();
            mOutput.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Nullable @Override public IBinder onBind(Intent intent)
    {
        return null;
    }
}
