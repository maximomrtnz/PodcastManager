package maximomrtnz.podcastmanager.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.network.ItunesAppleAPI;
import maximomrtnz.podcastmanager.ui.adapters.PodcastRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;

/**
 * Created by maximo on 22/10/16.
 */

public class SearchFragment extends BaseFragment implements RecyclerViewClickListener {

    private static String LOG_TAG = "SearchFragment";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;
    private List<Podcast> mPodcasts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View v =inflater.inflate(R.layout.fragment_search,container,false);

        loadUIComponents(v);

        setToolbar(v);

        return v;

    }

    @Override
    public void loadUIComponents(View view) {

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PodcastRecyclerViewAdapter(mPodcasts, getContext(), this);

        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.menu_seach_fragment, menu);

        MenuItem item = menu.findItem(R.id.action_search);

        SearchView sv = new SearchView(getActivity());

        // modifying the text inside edittext component
        TextView textView = (TextView) sv.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        textView.setHint(getString(R.string.label_search));
        textView.setHintTextColor(getResources().getColor(R.color.background_grey));
        textView.setTextColor(getResources().getColor(R.color.white));

        // implementing the listener
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() < 4) {
                    Toast.makeText(getActivity(),
                            "Your search query must not be less than 3 characters",
                            Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    doSearch(s);
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        item.setActionView(sv);

    }

    private void doSearch(String query){

        mProgressBar.setVisibility(View.VISIBLE);

        new ItunesAppleAPI(new ItunesAppleAPI.ItunesAppleAPIListener() {
            @Override
            public void onError(Exception e) {

                mProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onSuccess(Object arg) {

                mProgressBar.setVisibility(View.GONE);

                mPodcasts.clear();

                mPodcasts.addAll((List<Podcast>)arg);

                mAdapter.notifyDataSetChanged();

            }
        }).searchPodcast(25,query);

    }

    @Override
    public void onRecyclerViewListClicked(View v, int position) {

        Podcast podcast = (Podcast)v.getTag();

        if(mActivity!=null) {
            mActivity.showPodcast(podcast);
        }

    }

}
