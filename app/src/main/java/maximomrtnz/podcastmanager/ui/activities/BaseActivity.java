package maximomrtnz.podcastmanager.ui.activities;

import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;

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

    public abstract void loadUI();

    protected void extractPaletteColors(Bitmap bitmap) {

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

              @Override
              public void onGenerated(Palette palette) {

                  if(palette == null){
                      return;
                  }

                  Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

                  if(vibrantSwatch==null){
                      return;
                  }

                  int primaryColor = vibrantSwatch.getRgb();
                  int accentColor = vibrantSwatch.getBodyTextColor();

                  setPrimaryColor(primaryColor);

                  setAccentColor(accentColor);

              }

            }

        );

    }

    public void setPrimaryColor(int color){}

    public void setAccentColor(int color){};

}
