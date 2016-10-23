package maximomrtnz.podcastmanager.ui.activities;

import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.ui.fragments.BaseFragment;

/**
 * Created by maximo on 07/08/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Global method to show dialog fragment
     * @param newFragment  the DialogFragment you want to show
     */
    public void showDialogFragment(DialogFragment newFragment) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        // save transaction to the back stack
        ft.addToBackStack("dialog");
        newFragment.show(ft, "dialog");
    }

    public void showBaseFragment(BaseFragment newFragment, int container, String tag){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(container, newFragment, tag);
        ft.commit();
    }

    public abstract void loadUIComponents();

    public void showBaseFragment(BaseFragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(fragment)
                .commit();
    }

    public void hideBaseFragment(BaseFragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(fragment)
                .commit();
    }

}
