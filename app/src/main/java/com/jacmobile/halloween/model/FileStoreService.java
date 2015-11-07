package com.jacmobile.halloween.model;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jacmobile.halloween.util.Logger;
import com.squareup.otto.Subscribe;

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

    String fnToWrite;
    boolean isIndexWrite;

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
                write(data);
            } else {
                stopSelf();
            }
        } else {
            stopSelf();
        }

        return START_STICKY;
    }

    public void write(String data)
    {
        Logger.exception(data);
        fnToWrite = FILE_PREFIX + System.currentTimeMillis();
        new SimpleFileIOTask(getApplicationContext()).execute(fnToWrite, SimpleFileIOTask.WRITE, data);
    }

    @Subscribe public void writeComplete()
    {
        if (isIndexWrite) {
            stopSelf();
        } else {
            new SimpleFileIOTask(getApplicationContext()).execute(DATA_INDEX, SimpleFileIOTask.READ);
        }
    }

    private void readComplete(String fileIO)
    {
        isIndexWrite = true;
        if (fileIO.equals(SimpleFileIOTask.ERROR)) {
            Logger.exception("no key file stored, make one");
            // no key file stored, make one
            new SimpleFileIOTask(getApplicationContext()).execute(DATA_INDEX, SimpleFileIOTask.WRITE, fnToWrite + ",");
        } else {
            new SimpleFileIOTask(getApplicationContext()).execute(DATA_INDEX, SimpleFileIOTask.WRITE, fileIO + "," + fnToWrite + ",");
        }
        stopSelf();
    }

    @Nullable @Override public IBinder onBind(Intent intent)
    {
        return null;
    }

    public class SimpleFileIOTask extends AsyncTask<String, Void, String>
    {
        public static final String READ = "R";
        public static final String WRITE = "W";
        public static final String DELETE = "D";
        public static final String ERROR = "E";

        Context context;
        boolean isRead;

        public SimpleFileIOTask(Context context)
        {
            this.context = context.getApplicationContext();
        }

        @Override protected String doInBackground(String... params)
        {
            if (params.length < 2) return ERROR;

            switch (params[1]) {
                case READ:
                    isRead = true;
                    return read(params[0]);
                case WRITE:
                    write(params[0], params[2]);
                    return WRITE;
                case DELETE:
                    delete(params[0]);
                    return DELETE;
                default:
                    return ERROR;
            }
        }

        @Override protected void onPostExecute(String s)
        {
            if (isRead) {
                readComplete(s);
            } else {
                writeComplete();
            }
            super.onPostExecute(s);
        }

        private void delete(String fn)
        {
            context.deleteFile(fn);
        }

        private String read(String fn)
        {
            try {
                FileInputStream mInput = context.openFileInput(fn);
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
                FileOutputStream mOutput = context.openFileOutput(fn, Activity.MODE_PRIVATE);
                mOutput.write(data.getBytes());
                mOutput.flush();
                mOutput.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
