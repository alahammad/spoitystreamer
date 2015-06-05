package net.ahammad.myportfolio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.ahammad.myportfolio.fragments.ArtistsFragment;

/**
 * Created by alahammad on 6/4/15.
 */
public class ArtistsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);
        if (savedInstanceState==null)
        getSupportFragmentManager().beginTransaction().replace(R.id.container, ArtistsFragment.getInstance()).commit();
    }
}
