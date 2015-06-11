package net.ahammad.myportfolio.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.ahammad.myportfolio.MainApp;
import net.ahammad.myportfolio.PlayerService;
import net.ahammad.myportfolio.R;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Track;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by alahammad on 6/6/15.
 */
public class PlayerFragment extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "Player";

    private ImageView mSkipPrev;
    private ImageView mSkipNext;
    private ImageView mPlayPause;
    private TextView mStart;
    private TextView mEnd;
    private SeekBar mSeekbar;

    private ProgressBar mLoading;

    private ImageView mArtwork;
    private Handler myHandler = new Handler();
    ;
    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;


    private List<Track> mTracks;
    private int mCuttrentPos;
    private TextView mArtistName, mAlblumName, mTrackName;
    private MediaPlayer mediaPlayer;

    public static PlayerFragment getInstance(int pos) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", pos);
        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.setArguments(bundle);
        return playerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_full_player, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Only update from the intent if we are not recreating from a config change:
        if (savedInstanceState == null) {
            mTracks = MainApp.mTracks;
            mPlayPause = (ImageView) view.findViewById(R.id.imageView1);
            mSkipNext = (ImageView) view.findViewById(R.id.next);
            mSkipPrev = (ImageView) view.findViewById(R.id.prev);
            mStart = (TextView) view.findViewById(R.id.startText);
            mEnd = (TextView) view.findViewById(R.id.endText);
            mSeekbar = (SeekBar) view.findViewById(R.id.seekBar1);
            mArtwork = (ImageView) view.findViewById(R.id.iv_artwork);
            mArtistName = (TextView) view.findViewById(R.id.tv_artist_name);
            mAlblumName = (TextView) view.findViewById(R.id.tv_album_name);
            mTrackName = (TextView) view.findViewById(R.id.tv_track_name);
            mLoading = (ProgressBar) view.findViewById(R.id.progressBar1);

            mSkipNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCuttrentPos++;
                    play(mCuttrentPos);
                }
            });

            mSkipPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCuttrentPos--;
                    play(mCuttrentPos);
                }
            });

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
                        mEnd.setText("0:30");

                        mSeekbar.setProgress((int) startTime);
                        myHandler.postDelayed(UpdateSongTime, 100);
                        mPlayPause.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    } else {
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
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            });



        }
        mCuttrentPos = getArguments().getInt("pos", -1);
        play(mCuttrentPos);
    }


    private void play(int position) {
        // stop previus player
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mPlayPause.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
        }
        if (position < 1) {
            mSkipPrev.setVisibility(View.INVISIBLE);
        } else mSkipPrev.setVisibility(VISIBLE);

        if (position >= mTracks.size() - 1) mSkipNext.setVisibility(INVISIBLE);
        else mSkipNext.setVisibility(VISIBLE);

        if (position >= 0 && position < MainApp.mTracks.size()) {
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

    private void setDetails() {
        try {
            mArtistName.setText(mTracks.get(mCuttrentPos).artists.get(0).name);
            mTrackName.setText(mTracks.get(mCuttrentPos).name);
            mAlblumName.setText(mTracks.get(mCuttrentPos).album.name);
        } catch (IndexOutOfBoundsException e) {
            //error in array
        }

    }

    private void showArtworkImage() {
        String url = mTracks.get(mCuttrentPos).album.images.get(0).url;
        Picasso.with(getActivity()).load(url).into(mArtwork);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            mStart.setText(String.format("0:%d",

                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            mSeekbar.setProgress((int) startTime);
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
        if (hours > 0) {
            time = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)});
        } else {
            time = String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
        }

        return time;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacks(UpdateSongTime);
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
    }



    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        return dialog;
    }

    @Override
    public void onClick(View v) {

    }




}
