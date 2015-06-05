/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ahammad.myportfolio;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Track;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * A full screen player that shows the current playing music with a background image
 * depicting the album art. The activity also has controls to seek/pause/play the audio.
 */
public class FullScreenPlayerActivity extends AppCompatActivity {
    private static final String TAG = "Player";
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private ImageView mSkipPrev;
    private ImageView mSkipNext;
    private ImageView mPlayPause;
    private TextView mStart;
    private TextView mEnd;
    private SeekBar mSeekbar;
//    private TextView mLine1;
//    private TextView mLine2;
//    private TextView mLine3;
    private ProgressBar mLoading;
    private View mControllers;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;

    private String mCurrentArtUrl;
    private Handler mHandler = new Handler();
    private MediaBrowser mMediaBrowser;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService mExecutorService =
        Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;
    private PlaybackState mLastPlaybackState;

    MediaPlayer mediaPlayer;
    private ImageView mArtwork;
    private Handler myHandler = new Handler();;
    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;


    private List<Track> mTracks;
    private int mCuttrentPos;
    private TextView mArtistName,mAlblumName,mTrackName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_player);

//        mPauseDrawable = getDrawable(R.drawable.ic_pause_white_48dp);
//        mPlayDrawable = getDrawable(R.drawable.ic_play_arrow_white_48dp);
        mTracks = MainApp.mTracks;
        mPlayPause = (ImageView) findViewById(R.id.imageView1);
        mSkipNext = (ImageView) findViewById(R.id.next);
        mSkipPrev = (ImageView) findViewById(R.id.prev);
        mStart = (TextView) findViewById(R.id.startText);
        mEnd = (TextView) findViewById(R.id.endText);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar1);
        mArtwork = (ImageView)findViewById(R.id.iv_artwork);
        mArtistName = (TextView)findViewById(R.id.tv_artist_name);
        mAlblumName = (TextView)findViewById(R.id.tv_album_name);
        mTrackName = (TextView)findViewById(R.id.tv_track_name);
        mLoading = (ProgressBar) findViewById(R.id.progressBar1);
//        mControllers = findViewById(R.id.controllers);

        mSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MediaController.TransportControls controls =
//                    getMediaController().getTransportControls();
//                controls.skipToNext();
            mCuttrentPos++;
                play(mCuttrentPos);
            }
        });

        mSkipPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MediaController.TransportControls controls =
//                    getMediaController().getTransportControls();
//                controls.skipToPrevious();
                mCuttrentPos--;
                play(mCuttrentPos);
            }
        });
        mCuttrentPos=getIntent().getIntExtra("pos",-1);
        play(mCuttrentPos);
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();

                    finalTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();

                    if (oneTimeOnly == 0) {
                        mSeekbar.setMax((int) finalTime);
                        oneTimeOnly = 1;
                    }
                    mStart.setText(String.format("0:%d",
                                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                    );

//                    mEnd.setText(String.format("%d sec",
//                                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
//                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
//                    );

                    mEnd.setText("0:30");

                    mSeekbar.setProgress((int) startTime);
                    myHandler.postDelayed(UpdateSongTime, 100);
                    mPlayPause.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                }else {
                    mediaPlayer.pause();
                    mPlayPause.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));

                }
//
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mStart.setText(formatMillis(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
//                getMediaController().getTransportControls().seekTo(seekBar.getProgress());
//                scheduleSeekbarUpdate();
            }
        });

        // Only update from the intent if we are not recreating from a config change:
        if (savedInstanceState == null) {
            updateFromParams(getIntent());
        }

//        mMediaBrowser = new MediaBrowser(this,
//            new ComponentName(this, MusicService.class), mConnectionCallback, null);
    }

    private void play(int position){
        // stop previus player
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mPlayPause.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
        }
        if (position<1){
            mSkipPrev.setVisibility(View.INVISIBLE);
        }else mSkipPrev.setVisibility(VISIBLE);

        if (position>=mTracks.size()-1)mSkipNext.setVisibility(INVISIBLE);
        else mSkipNext.setVisibility(VISIBLE);

        if (position>=0 && position<MainApp.mTracks.size()) {
            String url = mTracks.get(position).preview_url;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mLoading.setVisibility(VISIBLE);
                mArtwork.setVisibility(View.GONE);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mPlayPause.setVisibility(VISIBLE);
                        mLoading.setVisibility(View.GONE);
                        mArtwork.setVisibility(VISIBLE);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            showArtworkImage();
            setDetails();
        }

    }

    private void setDetails (){
        try {
            mArtistName.setText(mTracks.get(mCuttrentPos).artists.get(0).name);
            mTrackName.setText(mTracks.get(mCuttrentPos).name);
            mAlblumName.setText(mTracks.get(mCuttrentPos).album.name);
        }catch (IndexOutOfBoundsException e){
            //error in array
        }

    }

    private void showArtworkImage (){
        String url=mTracks.get(mCuttrentPos).album.images.get(0).url;
        Picasso.with(this).load(url).into(mArtwork);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            mStart.setText(String.format("0:%d",

                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            mSeekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };



    // added by me
    public static String formatMillis(int millisec) {
        int seconds = millisec / 1000;
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;
        String time;
        if(hours > 0) {
            time = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)});
        } else {
            time = String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
        }

        return time;
    }

    private void connectToSession(MediaSession.Token token) {
        MediaController mediaController = new MediaController(FullScreenPlayerActivity.this, token);
        if (mediaController.getMetadata() == null) {
            finish();
            return;
        }
        setMediaController(mediaController);
        PlaybackState state = mediaController.getPlaybackState();
//        updatePlaybackState(state);
        MediaMetadata metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
        updateProgress();
        if (state != null && (state.getState() == PlaybackState.STATE_PLAYING ||
                state.getState() == PlaybackState.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }
    }

    private void updateFromParams(Intent intent) {
        if (intent != null) {
//            MediaDescription description = intent.getParcelableExtra(
//                MusicPlayerActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
//            if (description != null) {
//                updateMediaDescription(description);
//            }
        }
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMediaBrowser != null) {
            mMediaBrowser.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaBrowser != null) {
            mMediaBrowser.disconnect();
        }

        // me
         myHandler.removeCallbacks(UpdateSongTime);
        if (mediaPlayer!=null){
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mExecutorService.shutdown();
    }


    private void updateMediaDescription(MediaDescription description) {
        if (description == null) {
            return;
        }
        Log.d(TAG, "updateMediaDescription called ");
//        mLine1.setText(description.getTitle());
//        mLine2.setText(description.getSubtitle());
    }

    private void updateDuration(MediaMetadata metadata) {
        if (metadata == null) {
            return;
        }
        Log.d(TAG, "updateDuration called ");
        int duration = (int) metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
        mSeekbar.setMax(duration);
        mEnd.setText(formatMillis(duration));
    }

    private void updatePlaybackState(PlaybackState state) {
//        if (state == null) {
//            return;
//        }
//        mLastPlaybackState = state;
//        String castName = getMediaController()
//                .getExtras().getString(MusicService.EXTRA_CONNECTED_CAST);
//        String line3Text = "";
//        if (castName != null) {
//            line3Text = getResources()
//                    .getString(R.string.casting_to_device, castName);
//        }
//        mLine3.setText(line3Text);
//
//        switch (state.getState()) {
//            case PlaybackState.STATE_PLAYING:
//                mLoading.setVisibility(INVISIBLE);
//                mPlayPause.setVisibility(VISIBLE);
//                mPlayPause.setImageDrawable(mPauseDrawable);
//                mControllers.setVisibility(VISIBLE);
//                scheduleSeekbarUpdate();
//                break;
//            case PlaybackState.STATE_PAUSED:
//                mControllers.setVisibility(VISIBLE);
//                mLoading.setVisibility(INVISIBLE);
//                mPlayPause.setVisibility(VISIBLE);
//                mPlayPause.setImageDrawable(mPlayDrawable);
//                stopSeekbarUpdate();
//                break;
//            case PlaybackState.STATE_NONE:
//            case PlaybackState.STATE_STOPPED:
//                mLoading.setVisibility(INVISIBLE);
//                mPlayPause.setVisibility(VISIBLE);
//                mPlayPause.setImageDrawable(mPlayDrawable);
//                stopSeekbarUpdate();
//                break;
//            case PlaybackState.STATE_BUFFERING:
//                mPlayPause.setVisibility(INVISIBLE);
//                mLoading.setVisibility(VISIBLE);
//                mLine3.setText(R.string.loading);
//                stopSeekbarUpdate();
//                break;
//            default:
//                LogHelper.d(TAG, "Unhandled state ", state.getState());
//        }
//
//        mSkipNext.setVisibility((state.getActions() & PlaybackState.ACTION_SKIP_TO_NEXT) == 0
//            ? INVISIBLE : VISIBLE );
//        mSkipPrev.setVisibility((state.getActions() & PlaybackState.ACTION_SKIP_TO_PREVIOUS) == 0
//            ? INVISIBLE : VISIBLE );
    }

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() != PlaybackState.STATE_PAUSED) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaController.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        mSeekbar.setProgress((int) currentPosition);
    }
}
