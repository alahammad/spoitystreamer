package net.ahammad.myportfolio.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.ahammad.myportfolio.R;
import net.ahammad.myportfolio.TracksActivity;
import net.ahammad.myportfolio.adapters.ArtistAdapter;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * Created by alahammad on 6/4/15.
 */
public class ArtistsFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Artist artist);
    }

    public Callbacks mCallBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (Callbacks) activity;
        }catch (IllegalStateException e){

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallBack = null;
    }

    private ProgressBar mProgressBar;
    private EditText mArtistName;
    private ArtistAdapter mAdapter;
    private ListView mListView;

    public static ArtistsFragment getInstance (){
        ArtistsFragment artistsFragment =new ArtistsFragment();
        return artistsFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.artist_list,container,false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        mListView = (ListView)view.findViewById(R.id.view);
        mListView.setOnItemClickListener(this);
        mArtistName = (EditText)view.findViewById(R.id.et_artist_name);
        mArtistName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(mArtistName.getText().toString()))
                        search(mArtistName.getText().toString());
                    return true;
                }
                return false;
            }
        });
        if (savedInstanceState!=null){
            if (mAdapter!=null)
                mListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(getActivity(),TracksActivity.class);
//        intent.putExtra("name",mAdapter.getItem(position).name);
//        intent.putExtra("id",mAdapter.getItem(position).id);
//        getActivity().startActivity(intent);
        mCallBack.onItemSelected(mAdapter.getItem(position));
    }


    private void search(final String name){
        new AsyncTask<Void, Void, ArtistsPager>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected ArtistsPager doInBackground(Void... params) {
                try {
                    SpotifyApi api = new SpotifyApi();
                    SpotifyService spotify = api.getService();
                    ArtistsPager results = spotify.searchArtists(name);
                    return results;
                }catch (RetrofitError error){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(ArtistsPager artistsPager) {
                super.onPostExecute(artistsPager);
                if (artistsPager != null) {
                mProgressBar.setVisibility(View.GONE);
                mAdapter = new ArtistAdapter(artistsPager, getActivity());
                mListView.setAdapter(mAdapter);
                if (artistsPager.artists.items.size() == 0)
                    Toast.makeText(getActivity(), R.string.no_data_msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {

        mListView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }
}
