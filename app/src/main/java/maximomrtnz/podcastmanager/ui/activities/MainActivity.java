package maximomrtnz.podcastmanager.ui.activities;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.adapters.ViewPagerAdapter;
import maximomrtnz.podcastmanager.ui.listeners.EventSendedListener;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;
import maximomrtnz.podcastmanager.ui.views.SlidingTabLayout;
import maximomrtnz.podcastmanager.utils.Utils;

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener, RecyclerViewClickListener{

    private static String LOG_TAG = "MainActivity";
    private static final int NUMBER_OF_TABS =2;
    private static final int BROWSE_FRAGMENT_INDEX = 1;



    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mTabs;
    private CharSequence mTitles[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        loadUI();

    }

    @Override
    public void loadUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set tab titles
        mTitles = new CharSequence[]{getString(R.string.label_your_podcasts),getString(R.string.label_discover)};

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        mViewPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager(),mTitles,NUMBER_OF_TABS);

        // Assigning ViewPager View and setting the adapter
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mViewPagerAdapter);

        // Assiging the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);

        mTabs.setDistributeEvenly(true);

        //mTabs.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }

        });

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mViewPager);
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
            post(BROWSE_FRAGMENT_INDEX, query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
        }
    }

    private void post(int position, Object message){

        Fragment f = mViewPagerAdapter.getRegisteredFragment(position);

        if(f == null){
            return;
        }

        ((EventSendedListener)f).onEvent(message);

    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        post(BROWSE_FRAGMENT_INDEX, null);
        return true;
    }

    @Override
    public void onRecyclerViewListClicked(View v, int position) {

        Podcast podcast = (Podcast)v.getTag();

        Log.d(LOG_TAG, "Podcast Recived -->"+((Podcast)v.getTag()).getTitle());

        Intent i = new Intent(getApplicationContext(), PodcastActivity.class);

        // Pass podcast data
        podcast.loadTo(i);

        startActivity(i);

        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

}
