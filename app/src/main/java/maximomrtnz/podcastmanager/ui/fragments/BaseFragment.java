package maximomrtnz.podcastmanager.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.ui.activities.MainActivity;

/**
 * Created by maximo on 04/09/16.
 */

public abstract class BaseFragment extends Fragment {

    private Context mContainer;
    protected MainActivity mActivity;

    public void extractPaletteColors(Bitmap bitmap) {

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

    public abstract void loadUIComponents(View view);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MainActivity){
            mActivity = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    protected void setToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if (mActivity != null) {
            mActivity.setToolbar(toolbar);
        }

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.app_name));
        }
    }

}
