package net.ahammad.myportfolio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import net.ahammad.myportfolio.fragments.TracksFragment;
import net.ahammad.myportfolio.fragments._PlayerFragment;

/**
 * Created by alahammad on 6/3/15.
 */
public class TracksActivity extends AppCompatActivity {

    private boolean isTwoPan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);
        if (savedInstanceState==null) {
            isTwoPan = getIntent().getBooleanExtra(ArtistsActivity.IS_TWO_PANE,false);
            showPlayingFragment();
        }
    }

    private void showPlayingFragment (){
        String artistID = getIntent().getStringExtra(ArtistsActivity.ID);
        String artistName = getIntent().getStringExtra(ArtistsActivity.NAME);
        configActionBar(artistName);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, TracksFragment.getInstance(artistID, isTwoPan)).commit();

    }


    private void configActionBar(String artistName){
        ActionBar actionBar =  getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.top_ten);
        actionBar.setSubtitle(artistName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isSongPlaying()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.tracks_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                finish();
                return true;
            case R.id.action_playnow:
                showPlayingNow();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPlayingNow (){
        if (isTwoPan) {

            _PlayerFragment playerFragment = _PlayerFragment.getInstance(BackgroundAudioService.getInstance().getCurrentSong());
            playerFragment.show(getSupportFragmentManager(), "dialog");
        } else {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("pos", BackgroundAudioService.getInstance().getCurrentSong());
            startActivity(intent);
        }
    }

    private boolean isSongPlaying (){
        if (BackgroundAudioService.getInstance()!=null){
            return BackgroundAudioService.getInstance().isPlaying();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }
}
