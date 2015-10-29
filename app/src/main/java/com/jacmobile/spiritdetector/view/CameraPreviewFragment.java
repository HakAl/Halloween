package com.jacmobile.spiritdetector.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.jacmobile.spiritdetector.R;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import camera.CameraPreviewRecorder;

public class CameraPreviewFragment extends Fragment
{
    @Inject CameraPreviewRecorder cameraPreviewRecorder;

    @Bind(R.id.camera_preview) SurfaceView cameraPreview;

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
        cameraPreviewRecorder.onCreate(cameraPreview);
        return root;
    }

    @Override public void onPause()
    {
        super.onPause();
        cameraPreviewRecorder.onPause();
    }

    @Override public void onResume()
    {
        super.onResume();
        cameraPreviewRecorder.onResume();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        cameraPreviewRecorder.onDestroy();
    }
}
