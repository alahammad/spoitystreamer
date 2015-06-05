package net.ahammad.myportfolio.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by alahammad on 6/3/15.
 */
public class BaseFragment extends Fragment {

    public FragmentInterface fragmentInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try{
//            fragmentInterface= (FragmentInterface) activity;
//        }catch (ClassCastException ex){
//            throw new ClassCastException(activity.toString() + " must implement FragmentInterface");
//        }
    }
}
