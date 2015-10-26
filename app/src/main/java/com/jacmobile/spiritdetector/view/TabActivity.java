package com.jacmobile.spiritdetector.view;

import android.os.Bundle;

import com.jacmobile.spiritdetector.R;

import butterknife.ButterKnife;
import icepick.Icicle;

/**
 * tabs -->
 * camera preview / file storage / media viewer
 */
public class TabActivity extends BaseActivity
{
    public static final String TAG = TabActivity.class.getSimpleName();

    @Icicle public boolean isCameraAvailable = false;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getAppComponent().inject(this);

        if (savedInstanceState == null) {

            setBaseContentView(R.layout.tab_activity);

            ButterKnife.bind(this);

        } else {

        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        if (requestCode == 9) {
//            startCameraPreview();
//        } else {
//            cameraFailedToStart();
//        }
    }
}
