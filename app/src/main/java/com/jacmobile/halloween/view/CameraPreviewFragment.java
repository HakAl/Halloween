package com.jacmobile.halloween.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.XYPlot;
import com.github.glomadrian.velocimeterlibrary.VelocimeterView;
import com.jacmobile.halloween.R;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.jacmobile.halloween.model.SensorData;
import com.jacmobile.halloween.presenter.camera.CameraPreviewRecorder;
import com.jacmobile.halloween.presenter.sensors.MagnetometerService;
import com.jacmobile.halloween.util.Logger;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class CameraPreviewFragment extends Fragment
{
    @Inject CameraPreviewRecorder cameraPreviewRecorder;

    @Inject Bus bus;

    @Bind(R.id.magnetometer) VelocimeterView magnetometer;
    @Bind(R.id.camera_preview) SurfaceView cameraPreview;
//    @Bind(R.id.fab_record) FloatingActionButton cameraToggle;

    private SensorData sensorData;

    public static CameraPreviewFragment newInstance()
    {
        return new CameraPreviewFragment();
    }

    @Override public void onAttach(Context context)
    {
        super.onAttach(context);
        ((BaseActivity) getActivity()).getAppComponent().inject(this);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.camera_preview_fragment, container, false);
        ButterKnife.bind(this, root);
        if (((MainActivity)getActivity()).isCameraAvailable) cameraPreviewRecorder.onCreate(cameraPreview);
        return root;
    }

    @Override public void onResume()
    {
        super.onResume();
        bus.register(this);
        getContext().startService(new Intent(getContext(), MagnetometerService.class));
        if (((MainActivity)getActivity()).isCameraAvailable) cameraPreviewRecorder.onResume();
    }

    @Override public void onPause()
    {
        super.onPause();
        bus.unregister(this);
        getContext().stopService(new Intent(getContext(), MagnetometerService.class));
        cameraPreviewRecorder.onPause();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        cameraPreviewRecorder.onDestroy();
    }

    @Subscribe public void event(SensorData sensorData)
    {
        this.sensorData = sensorData;
        magnetometer.setValue(sensorData.getAverage());
    }
}
