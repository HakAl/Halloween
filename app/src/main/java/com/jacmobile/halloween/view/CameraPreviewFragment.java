package com.jacmobile.halloween.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.glomadrian.velocimeterlibrary.VelocimeterView;
import com.jacmobile.halloween.R;
import com.jacmobile.halloween.model.SRecording;
import com.jacmobile.halloween.model.SensorData;
import com.jacmobile.halloween.presenter.camera.CameraFailureListener;
import com.jacmobile.halloween.presenter.camera.CameraPreviewRecorder;
import com.jacmobile.halloween.presenter.sensors.MagnetometerService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CameraPreviewFragment extends Fragment implements CameraFailureListener
{
    @Inject Bus bus;
    @Inject CameraPreviewRecorder cameraPreviewRecorder;

    @Bind(R.id.magnetometer) VelocimeterView magnetometer;
    @Bind(R.id.camera_preview) SurfaceView cameraPreview;
    @Bind(R.id.fab_record) FloatingActionButton recordButton;
    @Bind(R.id.fab_stop) FloatingActionButton stopButton;

    boolean isRecording = false;

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
        return root;
    }

    @Override public void onResume()
    {
        super.onResume();
        bus.register(this);
        MagnetometerService.start(getContext());
        if (((MainActivity) getActivity()).isCameraAvailable) {
            CameraPreviewRecorder.start(getContext());
            cameraPreviewRecorder.startCameraPreview(cameraPreview, this);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            stopButton.setOnClickListener(getCameraToggle());
            recordButton.setOnClickListener(getCameraToggle());
        }
    }

    @Override public void onPause()
    {
        super.onPause();
        bus.unregister(this);
        MagnetometerService.stop(getContext());
        if (((MainActivity) getActivity()).isCameraAvailable){
            cameraPreviewRecorder.pauseCameraPreview();
            CameraPreviewRecorder.stop(getContext());
        }
    }

    @NonNull private View.OnClickListener getCameraToggle()
    {
        return new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                if (isRecording) {
                    stopMediaRecording();
                    isRecording = false;
                    stopButton.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);
                } else {
                    recordMediaFile();
                    isRecording = true;
                    recordButton.setVisibility(View.GONE);
                    stopButton.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void recordMediaFile()
    {
        SRecording file = new SRecording(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES).getPath(), getContext().getPackageName());
        cameraPreviewRecorder.startRecording(file.getFile());
    }

    private void stopMediaRecording()
    {
        cameraPreviewRecorder.stopRecording();
    }

    @Subscribe public void event(SensorData sensorData)
    {
        magnetometer.setValue(sensorData.getAverage());
    }

    @Override public void onCameraSetupFailed()
    {
        ((MainActivity) getActivity()).setContentViewState(MultiStateViewState.ERROR);
    }
}
