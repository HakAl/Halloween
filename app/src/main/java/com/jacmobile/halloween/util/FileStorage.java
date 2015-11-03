package com.jacmobile.halloween.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;

/**
 * Create a File in the specified directory (File.getPath()).
 * <p/>
 * Optionally, provide a directory name to create a new directory to put the file in.
 *
 * Usage:
 * File newFile = FileStorage.Builder
 *                 .directoryPath(Environment.getDataDirectory().getPath())
 *                 .directoryName("newDirectory")
 *                 .fileName("Foo.csv")
 *                 .createFile();
 */
public class FileStorage
{
    public static final String TAG = FileStorage.class.getSimpleName();

    private String fileName;
    private String directoryPath;
    private String directoryName;

    public static class Builder
    {
        private Uri uri;
        private String fileName;
        private String directoryPath;
        private String directoryName;

        public Builder fileName(String fileName)
        {
            this.fileName = fileName;
            return this;
        }

        public Builder directoryPath(String directoryPath)
        {
            this.directoryPath = directoryPath;
            return this;
        }

        public Builder directoryName(String directoryName)
        {
            this.directoryName = directoryName;
            return this;
        }

        @Nullable public File createFile(Uri fileUri)
        {
            this.uri = fileUri;
            return new FileStorage(this).newFile();
        }

        @Nullable public File createFile()
        {
            return new FileStorage(this).newFile();
        }
    }

    private FileStorage(Builder builder)
    {
        if (TextUtils.isEmpty(builder.directoryPath)) {
            throw new FileStorageConfigurationException();
        }
        this.directoryName = builder.directoryName;
        this.fileName = builder.fileName;
        this.directoryPath = builder.directoryPath;
    }

    @Nullable private File newFile()
    {
        File file = getFile();

        if (!file.exists()) {
            if (!file.mkdirs()) {
                Logger.exception(TAG + "\nFailed to create File.");
                return null;
            }
        }

        return file;
    }

    @NonNull private File getFile()
    {
        return TextUtils.isEmpty(directoryName)
                ? new File(directoryPath, fileName)
                : new File(directoryPath, directoryName + File.separator + fileName);
    }

    private class FileStorageConfigurationException extends RuntimeException
    {
        public FileStorageConfigurationException()
        {
            super(TAG + "\nMust define a directoryPath and directoryName for the File");
        }
    }
}
