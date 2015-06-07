package net.ahammad.myportfolio;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

/**
 * Created by alahammad on 6/6/15.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener  {
    private static final int NOTIFICATION_ID =1 ;
    MediaPlayer mediaPlayer;
   public static final String URL="url";
    public  static final String SONG_NAME = "song_name";

    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_STOP = "STOP";

    private static PlayerService _instance;
    public static PlayerService getInstance (){
        return _instance;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        _instance= this;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            String url = intent.getStringExtra(URL);
            String songName = intent.getStringExtra(SONG_NAME);
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

    public void start (){
        if (!mediaPlayer.isPlaying()) {

            mediaPlayer.start();
        }
    }

    public void seekTo (int value){
        mediaPlayer.seekTo(value);
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    public boolean isPlaying (){
        return mediaPlayer.isPlaying();
    }

    public void pause (){
        if (mediaPlayer!=null)
            mediaPlayer.pause();
    }

    public void stop (){
        mediaPlayer.stop();
    }

    public void onDestroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    public void onCompletion(MediaPlayer _mediaPlayer) {
        stopSelf();
    }



    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.stop();
        return true;
    }

}
