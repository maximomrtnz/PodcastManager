package maximomrtnz.podcastmanager.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.network.ItunesAppleAPI;
import maximomrtnz.podcastmanager.ui.adapters.PodcastRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.listeners.EventSendedListener;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;

public class PodcastSourcesFragment extends Fragment implements EventSendedListener {

    private static final String LOG_TAG = "PodcastSourcesFragment";
    private static final int LIMIT = 25;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Podcast> mPodcasts;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_podcast_sources,container,false);

        mPodcasts = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PodcastRecyclerViewAdapter(mPodcasts, getContext(), (RecyclerViewClickListener) getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar)v.findViewById(R.id.progress_bar);

        loadPodcastsList();

        return v;
    }

    private void loadPodcastsList(){

        mProgressBar.setVisibility(View.VISIBLE);

        // Get devise location, to get podcast acording for user location
        String lang = Locale.getDefault().getLanguage();

        // Get Podcast from Itunes API
        new ItunesAppleAPI(new ItunesAppleAPI.ItunesAppleAPIListener() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onSuccess(Object arg) {
                mPodcasts.clear();
                mPodcasts.addAll((List<Podcast>)arg);
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        }).getPodcast(LIMIT, lang);

    }

    private void loadPodcastList(String query){

        mProgressBar.setVisibility(View.VISIBLE);

        // Get Podcast from Itunes API
        new ItunesAppleAPI(new ItunesAppleAPI.ItunesAppleAPIListener() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onSuccess(Object arg) {
                mPodcasts.clear();
                mPodcasts.addAll((List<Podcast>) arg);
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        }).searchPodcast(LIMIT, String.valueOf(query));
    }

    @Override
    public void onEvent(Object o) {

        if(o != null) {
            loadPodcastList(String.valueOf(o));
        }else{
            loadPodcastsList();
        }

    }

}
