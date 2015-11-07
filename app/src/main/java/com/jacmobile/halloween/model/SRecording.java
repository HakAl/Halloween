package com.jacmobile.halloween.model;

import com.jacmobile.halloween.util.FileStorage;

import org.joda.time.DateTime;

import java.io.File;
import java.util.Date;

public class SRecording
{
    public static final String TAG = SRecording.class.getSimpleName();
    public static final String FILE_SEPERATOR = "_";
    public static final String FILE_EXTENSION = ".mp4";

    private final File file;
    private final DateTime dt = new DateTime(new Date());

    public SRecording(String directoryPath, String directoryName)
    {
        this.file = new FileStorage.Builder()
                .directoryPath(directoryPath)
                .directoryName(directoryName)
                .fileName(TAG+FILE_SEPERATOR +
                        dt.getYear()+
                        dt.getMonthOfYear()+
                        dt.getDayOfMonth()+
                        dt.getHourOfDay()+
                        dt.getMinuteOfDay()+
                        dt.getSecondOfDay()+FILE_EXTENSION)
                .createFile();
    }

    public File getFile()
    {
        return file;
    }
}