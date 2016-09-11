package maximomrtnz.podcastmanager.ui.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.adapters.ViewPagerAdapter;
import maximomrtnz.podcastmanager.ui.fragments.BaseFragment;
import maximomrtnz.podcastmanager.ui.fragments.PlayerFragment;
import maximomrtnz.podcastmanager.ui.fragments.PodcastFragment;
import maximomrtnz.podcastmanager.ui.fragments.SubscriptionsFragment;
import maximomrtnz.podcastmanager.ui.fragments.TopChartsFragment;
import maximomrtnz.podcastmanager.ui.listeners.EventSendedListener;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;
import maximomrtnz.podcastmanager.ui.views.SlidingTabLayout;
import maximomrtnz.podcastmanager.utils.EpisodePlaylist;
import maximomrtnz.podcastmanager.utils.JsonUtil;

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener{

    private static String LOG_TAG = "MainActivity";
    private static final int BROWSE_FRAGMENT_INDEX = 1;

    private SlidingUpPanelLayout mSlidingLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private PlayerFragment mPlayerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        loadUIComponents();

        setUpNavDrawer();

        showTopCharts();

    }

    @Override
    public void loadUIComponents() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        //set layout slide listener
        mSlidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        //mSlidingLayout.addPanelSlideListener(onSlideListener());

        mPlayerFragment = new PlayerFragment();

        // Add audio player to layout
        showBaseFragment(mPlayerFragment,R.id.fragment_audio_player_container,"FRAGMENT_PLAYER");

        // TODO: Check if we were playing a episode hide instead
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Create search view for search PodCast
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem,this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, MainActivity.class)));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        Log.d(LOG_TAG, "onQueryTextSubmit-->"+query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        Log.d(LOG_TAG, "onQueryTextChange-->"+newText);
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //post(BROWSE_FRAGMENT_INDEX, query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
        }
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
                    case R.id.action_playlist:
                        showPlaylist();
                        break;
                    case R.id.action_favorites:
                        showFavorites();
                        break;
                    case R.id.action_settings:
                        showSettingsScreen();
                        break;
                    case R.id.action_search:
                        showSearch();
                        break;
                    case R.id.action_invite_friends:
                        openInvitePage();
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

    private void showPlaylist(){

    }

    private void showFavorites(){

    }

    private void showSettingsScreen(){

    }

    private void showSearch(){

    }

    private void showRatingPlayStore(){

    }

    private void openInvitePage(){

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

    public void showEpisode(Podcast podcast, Episode episode){

        EpisodePlaylist.getInstance().createMediaMetadata(podcast,episode);

        // Show SlidingLayout
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        mPlayerFragment.onMediaItemSelected(EpisodePlaylist.getInstance().getMediaItems().get(0));

    }

}
