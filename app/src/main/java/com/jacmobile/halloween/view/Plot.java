package com.jacmobile.halloween.view;

import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.jacmobile.halloween.R;
import com.jacmobile.halloween.presenter.sensors.Magnetometer;

import java.text.DecimalFormat;

public class Plot
{
    public static final int HISTORY_SIZE = 66;

    private Redrawer drawer;
    private SimpleXYSeries xSeries = null;
    private SimpleXYSeries ySeries = null;
    private SimpleXYSeries zSeries = null;
    private XYPlot sensorPlot;

    public void initialize(ViewGroup viewGroup)
    {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.sensor_plot, viewGroup, false);

        this.sensorPlot = (XYPlot) view.findViewById(R.id.sensor_plot);

        sensorPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        sensorPlot.setDomainStepValue(10);
        sensorPlot.setTicksPerRangeLabel(3);
        sensorPlot.setDomainValueFormat(new DecimalFormat("#"));
        sensorPlot.setRangeValueFormat(new DecimalFormat("#"));
        sensorPlot.setRangeBoundaries(Magnetometer.MAGNETIC_FIELD_EARTH_MIN,
                Magnetometer.MAGNETIC_FIELD_EARTH_MAX, BoundaryMode.FIXED);

        sensorPlot.setRangeLabel(view.getContext().getString(R.string.units));
        sensorPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
        drawer = new Redrawer(sensorPlot, HISTORY_SIZE, false);
        xSeries = new SimpleXYSeries("X");
        ySeries = new SimpleXYSeries("Y");
        zSeries = new SimpleXYSeries("Z");
        xSeries.useImplicitXVals();
        ySeries.useImplicitXVals();
        zSeries.useImplicitXVals();

        sensorPlot.addSeries(xSeries, new LineAndPointFormatter(
                context.getResources().getColor(R.color.red), null, null, null));
        sensorPlot.addSeries(ySeries, new LineAndPointFormatter(
                context.getResources().getColor(R.color.blue), null, null, null));
        sensorPlot.addSeries(zSeries, new LineAndPointFormatter(
                context.getResources().getColor(R.color.neon_green), null, null, null));
    }

    public void onResume()
    {
        this.drawer.start();
    }

    public void onPause()
    {
        this.drawer.pause();
    }

    public void onDestroy()
    {
        this.drawer.finish();
    }

    public void updateSeries(float... data)
    {
        xSeries.setTitle(String.format("%.0f", data[0]));
        ySeries.setTitle(String.format("%.0f", data[1]));
        zSeries.setTitle(String.format("%.0f", data[2]));
        if (zSeries.size() > HISTORY_SIZE) {
            zSeries.removeFirst();
            ySeries.removeFirst();
            xSeries.removeFirst();
        }
        xSeries.addLast(null, data[0]);
        ySeries.addLast(null, data[1]);
        zSeries.addLast(null, data[2]);
    }
}
