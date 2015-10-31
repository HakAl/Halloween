package com.jacmobile.halloween.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;

public class Sound
{
    private static final float DEFAULT_VOLUME = 0.5F;

    private Context context;
    private int resId = -1;
    private float volume = -1;

    public static class Builder
    {
        private Context context;
        private int resId = -1;
        private float volume = -1;

        public Builder with(@NonNull Context context)
        {
            this.context = context.getApplicationContext();
            return this;
        }

        public Builder resId(int resId)
        {
            if (resId < 0) throw new InvalidResourceException();
            this.resId = resId;
            return this;
        }

        public Builder volume(float volume)
        {
            if (volume < 0) volume = DEFAULT_VOLUME;
            this.volume = volume;
            return this;
        }

        public Sound build()
        {
            return new Sound(this);
        }
    }

    private Sound(Builder builder)
    {
        this.context = builder.context;
        this.resId = builder.resId;
        this.volume = builder.volume;
    }

    public void play()
    {
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
        try {
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mediaPlayer.release();
                }
            });
            mediaPlayer.start();
        } catch (Throwable t) {
            Logger.exception(t.toString());
        }
    }

    static class InvalidResourceException extends RuntimeException
    {
        public InvalidResourceException()
        {
            super(InvalidResourceException.class.getCanonicalName()
                    + "\nYou must provide a sound file resource ID to play.");
        }
    }
}
