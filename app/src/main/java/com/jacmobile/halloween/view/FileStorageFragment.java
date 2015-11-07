package com.jacmobile.halloween.view;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.jacmobile.halloween.model.FileStoreService;
import com.jacmobile.halloween.model.SensorData;
import com.jacmobile.halloween.util.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileStorageFragment extends Fragment
{
    boolean readingIndex;
    private ArrayList<SensorData> sensorDatas;

    public static FileStorageFragment newInstance()
    {
        return new FileStorageFragment();
    }

    @Override public void onAttach(Context context)
    {
        super.onAttach(context);
        ((BaseActivity) getActivity()).getAppComponent().inject(this);
    }

    @Override public void onResume()
    {
        super.onResume();
        readingIndex = true;
        new SimpleFileIOTask().execute(FileStoreService.DATA_INDEX);
    }

    @Override public void onPause()
    {
        super.onPause();
    }

    private void readComplete(String s)
    {
        if (readingIndex) {
            readingIndex = false;
            String[] index = s.split(",");
            for (String i : index) {
                Logger.exception(i);
            }
        } else {
            if (!TextUtils.isEmpty(s)) {
                String[] raw = s.split(",");
                sensorDatas = new ArrayList<>(raw.length);
                for (String r : raw) {
                    sensorDatas.add(new SensorData(r));
                }
            }
        }
    }

    class SimpleFileIOTask extends AsyncTask<String, Void, String>
    {
        public static final String ERROR = "E";

        @Override protected String doInBackground(String... params)
        {
            return read(params[0]);
        }

        @Override protected void onPostExecute(String s)
        {
            readComplete(s);
            super.onPostExecute(s);
        }

        private String read(String fn)
        {
            try {
                FileInputStream mInput = getActivity().getApplicationContext().openFileInput(fn);
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
    }
}
