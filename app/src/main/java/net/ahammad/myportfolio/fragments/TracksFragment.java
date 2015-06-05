package net.ahammad.myportfolio.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.ahammad.myportfolio.FullScreenPlayerActivity;
import net.ahammad.myportfolio.MainApp;
import net.ahammad.myportfolio.R;
import net.ahammad.myportfolio.adapters.TracksAdapter;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by alahammad on 6/3/15.
 */
public class TracksFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private Tracks mTracks;
    private ProgressBar mProgressBar;
    private ListView mList;
    private TracksAdapter mAdapter;

    public static TracksFragment getInstance (String artistID){
        TracksFragment tracksFragment = new TracksFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id",artistID);
        tracksFragment.setArguments(bundle);
        return tracksFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tracks_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar =(ProgressBar)view.findViewById(R.id.progressBar2);
        mList = (ListView)view.findViewById(R.id.listview);
        mList.setOnItemClickListener(this);
        String artistID = getArguments().getString("id");
        loadTracks(artistID);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent =new Intent(getActivity(), FullScreenPlayerActivity.class);
//        intent.putExtra("pos",position);
//        MainApp.mTracks = mTracks.tracks;
//        startActivity(intent);
    }


    private void loadTracks (final String id){
        new AsyncTask<Void, Void, Tracks>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Tracks doInBackground(Void... params) {
                SpotifyApi spotifyApi=new SpotifyApi();
                SpotifyService spotifyService  = spotifyApi.getService();
                Map<String,Object> maps=new HashMap<>();;
                maps.put("country", "AR");
                return spotifyService.getArtistTopTrack(id,maps);
            }

            @Override
            protected void onPostExecute(Tracks tracks) {
                super.onPostExecute(tracks);
                mProgressBar.setVisibility(View.GONE);
                mTracks = tracks;
                mAdapter = new TracksAdapter(tracks,getActivity());
                mList.setAdapter(mAdapter);
                if (tracks.tracks.size()==0){
                    Toast.makeText(getActivity(), R.string.no_data_msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }


}