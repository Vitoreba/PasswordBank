package com.vitor.testecedro.util;

import android.support.v4.app.Fragment;

import com.vitor.testecedro.controller.fragments.PersonFragment;
import com.vitor.testecedro.controller.fragments.SitesListFragment;

public class MainFragmentTabsManager {

    public enum MainFragmentType {
        MAIN_FRAGMENT_HOME,
        MAIN_FRAGMENT_PERSON
    }

    private SitesListFragment mHomeFragment;
    private PersonFragment mPersonFragment;

    private Fragment mCurrentSelectedFragment = null;

    //Singleton
    private static MainFragmentTabsManager mInstance = null;

    private MainFragmentTabsManager(){ }

    public static MainFragmentTabsManager getInstance(){
        if(mInstance == null) {
            mInstance = new MainFragmentTabsManager();
        }
        return mInstance;
    }


    public void setCurrentSelectedFragment(Fragment fragment){
        mCurrentSelectedFragment = fragment;
    }

    public Fragment getCurrentSelectedFragment(){
        return mCurrentSelectedFragment;
    }

    private SitesListFragment getHomeFragment() {
        if(mHomeFragment == null){
            mHomeFragment = mHomeFragment.newInstance();
            mHomeFragment.setRetainInstance(true);
        }
        return mHomeFragment;
    }

    private PersonFragment getPersonFragment() {
        if(mPersonFragment == null){
            mPersonFragment = mPersonFragment.newInstance();
            mPersonFragment.setRetainInstance(true);
        }
        return mPersonFragment;
    }


    public Boolean verifyFragmentInstantiatedByType(MainFragmentType fragmentType) {
        switch (fragmentType) {
            case MAIN_FRAGMENT_HOME:
                return (mHomeFragment != null);
            case MAIN_FRAGMENT_PERSON:
                return (mPersonFragment != null);
            default:
                return false;
        }
    }

    public Fragment getMainFragmentByType(MainFragmentType fragmentType) {
        Fragment fragment = null;
        switch (fragmentType) {
            case MAIN_FRAGMENT_HOME:
                fragment = getHomeFragment();
                break;
            case MAIN_FRAGMENT_PERSON:
                fragment = getPersonFragment();
                break;
            default:
                break;
        }

        return fragment;
    }

    public void clearAllFragments(){
        mHomeFragment = null;
        mPersonFragment = null;
    }

}