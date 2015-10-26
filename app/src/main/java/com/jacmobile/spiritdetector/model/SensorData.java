package com.jacmobile.spiritdetector.model;

public class SensorData
{
    public float x;
    public float y;
    public float z;
    public int accuracy;
    public long timeStamp;

    public SensorData() {}

    public SensorData(String toParse)
    {
        fromString(toParse);
    }

    public SensorData(float x, float y, float z, int accuracy, long timeStamp)
    {
        this.x = x;
        this.y = y;
        this.z = z;
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
                ", accuracy=" + accuracy +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
