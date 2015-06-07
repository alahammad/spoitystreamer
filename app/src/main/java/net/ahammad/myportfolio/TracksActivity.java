package net.ahammad.myportfolio;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.ahammad.myportfolio.fragments.TracksFragment;

/**
 * Created by alahammad on 6/3/15.
 */
public class TracksActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);
        if (savedInstanceState==null) {
            String artistID = getIntent().getStringExtra(ArtistsActivity.ID);
            String artistName = getIntent().getStringExtra(ArtistsActivity.NAME);
            configActionBar(artistName);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, TracksFragment.getInstance(artistID,getIntent().getBooleanExtra(ArtistsActivity.IS_TWO_PANE,false))).commit();
        }
    }

    private void configActionBar(String artistName){
        ActionBar actionBar =  getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.top_ten);
        actionBar.setSubtitle(artistName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
