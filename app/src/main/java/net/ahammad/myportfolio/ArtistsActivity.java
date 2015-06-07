package net.ahammad.myportfolio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import net.ahammad.myportfolio.fragments.ArtistsFragment;
import net.ahammad.myportfolio.fragments.PlayerFragment;
import net.ahammad.myportfolio.fragments.TracksFragment;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by alahammad on 6/4/15.
 */
public class ArtistsActivity extends AppCompatActivity implements ArtistsFragment.Callbacks{

    private boolean mTwoPane;

    public static final String IS_TWO_PANE = "isTwoPane";
    public static final String ID = "id";
    public static final String NAME= "name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);
        if (findViewById(R.id.artists_container) != null) {
            mTwoPane = true;

            ((ArtistsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);


        }else {
            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction().replace(R.id.container, ArtistsFragment.getInstance()).commit();
        }
    }

    @Override
    public void onItemSelected(Artist artist) {
        if (mTwoPane){
            getSupportFragmentManager().beginTransaction().replace(R.id.artists_container,TracksFragment.getInstance(artist.id,mTwoPane)).commit();
        }else {
            Intent intent = new Intent(this,TracksActivity.class);
            intent.putExtra(NAME,artist.name);
            intent.putExtra(ID,artist.id);
            intent.putExtra(IS_TWO_PANE,mTwoPane);
            this.startActivity(intent);

        }
    }
}
