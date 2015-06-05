package net.ahammad.myportfolio.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by alahammad on 6/3/15.
 */
public class BaseFragment extends Fragment {

    public FragmentInterface fragmentInterface;

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
