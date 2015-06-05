package net.ahammad.myportfolio.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by alahammad on 6/3/15.
 */
public interface FragmentInterface {

    void changeFragment(Fragment fragment);
     void changeFragment(Fragment fragment,boolean addTobackStack);
}
