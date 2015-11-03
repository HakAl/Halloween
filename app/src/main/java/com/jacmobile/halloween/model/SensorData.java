package com.jacmobile.halloween.model;

public class SensorData
{
    private float x;
    private float y;
    private float z;
    private float average;
    private int accuracy;
    private long timeStamp;

    public SensorData() {}

    public SensorData(String toParse)
    {
        fromString(toParse);
    }

    public SensorData(float x, float y, float z, float average, int accuracy, long timeStamp)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.average = average;
        this.accuracy = accuracy;
        this.timeStamp = timeStamp;
    }

    public void fromString(String toParse) {
        String[] temp = toParse.replace("{","").replace("}","").split("\'");
        for (String s : temp) {
            String[] pair = s.split("=");
            if (pair.length == 2) {
                if (pair[0].equals("x")) x = Float.parseFloat(pair[1]);
                if (pair[0].equals("y")) y = Float.parseFloat(pair[1]);
                if (pair[0].equals("z")) z = Float.parseFloat(pair[1]);
                if (pair[0].equals("average")) average = Integer.parseInt(pair[1]);
                if (pair[0].equals("accuracy")) accuracy = Integer.parseInt(pair[1]);
                if (pair[0].equals("timeStamp")) timeStamp = Long.parseLong(pair[1]);
            }
        }
    }

    @Override public String toString()
    {
        return "{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", average=" + average +
                ", accuracy=" + accuracy +
                ", timeStamp=" + timeStamp +
                '}';
    }

    public float getX()
    {
        return x;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public float getY()
    {
        return y;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getZ()
    {
        return z;
    }

    public void setZ(float z)
    {
        this.z = z;
    }

    public float getAverage()
    {
        return average;
    }

    public void setAverage(float average)
    {
        this.average = average;
    }

    public int getAccuracy()
    {
        return accuracy;
    }

    public void setAccuracy(int accuracy)
    {
        this.accuracy = accuracy;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public void clear()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.average = 0;
        this.accuracy = 0;
        this.timeStamp = 0;
    }
}
