package maximomrtnz.podcastmanager.ui.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.fragments.BaseFragment;
import maximomrtnz.podcastmanager.ui.fragments.PlayQueueFragment;
import maximomrtnz.podcastmanager.ui.fragments.PlayerFragment;
import maximomrtnz.podcastmanager.ui.fragments.PodcastFragment;
import maximomrtnz.podcastmanager.ui.fragments.SearchFragment;
import maximomrtnz.podcastmanager.ui.fragments.SubscriptionsFragment;
import maximomrtnz.podcastmanager.ui.fragments.TopChartsFragment;
import maximomrtnz.podcastmanager.utils.JsonUtil;

public class MainActivity extends BaseActivity implements MenuItemCompat.OnActionExpandListener{

    private static String LOG_TAG = "MainActivity";

    private SlidingUpPanelLayout mSlidingLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private PlayerFragment mPlayerFragment;
    private Bundle mSavedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        loadUIComponents();

        setUpNavDrawer();

        showTopCharts();

        // Save SavedInstanceState to use it when the PlayerFragment will be ready
        mSavedInstanceState = savedInstanceState;

    }

    @Override
    public void loadUIComponents() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        //set layout slide listener
        mSlidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        hidePlayerFragment();

        mPlayerFragment = new PlayerFragment();

        // Add audio player to layout
        showBaseFragment(mPlayerFragment,R.id.fragment_audio_player_container,"FRAGMENT_PLAYER");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }


    private void setUpNavDrawer() {

        mNavigationView.setCheckedItem(R.id.action_top_charts);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.action_subcriptions:
                        showSubcriptions();
                        break;
                    case R.id.action_top_charts:
                        showTopCharts();
                        break;
                    case R.id.action_downloads:
                        showDownloadedEpisodes();
                        break;
                    case R.id.action_play_queue:
                        showPlayQueue();
                        break;
                    case R.id.action_settings:
                        showSettingsScreen();
                        break;
                    case R.id.action_search:
                        showSearch();
                        break;
                    case R.id.action_rate_app:
                        showRatingPlayStore();
                        break;
                    default:

                }
                mDrawerLayout.closeDrawers();
                return true;
            }


        });
    }

    private void showSubcriptions(){
        BaseFragment f = new SubscriptionsFragment();
        showBaseFragment(f, R.id.fragment_container,"FRAGMENT_SUBSCRIPTIONS");
    }

    private void showTopCharts(){
        BaseFragment f = new TopChartsFragment();
        showBaseFragment(f, R.id.fragment_container,"FRAGMENT_TOP_CHARTS");
    }

    private void showDownloadedEpisodes(){

    }

    private void showPlayQueue(){
        BaseFragment f = new PlayQueueFragment();
        showBaseFragment(f, R.id.fragment_container,"FRAGMENT_PLAY_QUEUE");
    }

    private void showSettingsScreen(){

    }

    private void showSearch(){
        BaseFragment f = new SearchFragment();
        showBaseFragment(f, R.id.fragment_container,"FRAGMENT_SEARCH");
    }

    private void showRatingPlayStore(){

    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void showPodcast(Podcast podcast){

        Bundle bundle = new Bundle();

        bundle.putString("podcast", JsonUtil.getInstance().toJson(podcast));

        BaseFragment f = new PodcastFragment();

        f.setArguments(bundle);

        showBaseFragment(f, R.id.fragment_container,"FRAGMENT_PODCAST");

    }

    public void playEpisode(Podcast podcast, Episode episode){
        mPlayerFragment.play(podcast,episode);
    }

    public void playEpisode(Episode episode){
        mPlayerFragment.play(episode);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);

    }

    public void hidePlayerFragment(){
        Log.d(LOG_TAG,"HIDE PLAYER FRAGMENT");
        if(mSlidingLayout!=null) {
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }

    public void showPlayerFragment(){
        Log.d(LOG_TAG,"SHOW PLAYER FRAGMENT");
        if(mSlidingLayout!=null) {
            if (mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
