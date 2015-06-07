package net.ahammad.myportfolio.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.ahammad.myportfolio.R;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by alahammad on 6/7/15.
 */
public class TestFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test,container,false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Picasso.with(getActivity()).load("http://www.parkeasier.com/wp-content/uploads/2015/05/android-for-wallpaper-8.png").into(imageView);
    }
}
