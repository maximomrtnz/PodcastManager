package maximomrtnz.podcastmanager.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;

/**
 * Created by maximo on 20/06/16.
 */

public class EpisodesRecyclerViewAdapter extends RecyclerView.Adapter<EpisodesRecyclerViewAdapter.PodcastViewHolder> {

    private static String LOG_TAG = "RecyclerViewAdapter";
    private static RecyclerViewClickListener mItemListener;

    private List<Episode> mDataset;
    private Podcast mPodcast;
    private ImageLoader mImageLoader;

    public EpisodesRecyclerViewAdapter(List<Episode> dataset, Podcast podcast, Context context, RecyclerViewClickListener itemListener){
        this.mDataset = dataset;
        this.mImageLoader = new ImageLoader(context);
        this.mPodcast = podcast;
        mItemListener = itemListener;
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.episode_sources_item, viewGroup, false);
        PodcastViewHolder pvh = new PodcastViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder personViewHolder, int i) {

        String description = mDataset.get(i).getDescription();

        if(mDataset.get(i).getImageUrl()!=null) {
            mImageLoader.displayImage(mDataset.get(i).getImageUrl(), personViewHolder.mEpisodeImage);
        }else{
            mImageLoader.displayImage(mPodcast.getImageUrl(), personViewHolder.mEpisodeImage);
        }

        if(description!=null){
            personViewHolder.mEpisodeDescription.setText(description);
        }

        personViewHolder.mEpisodeTitle.setText(mDataset.get(i).getTitle());
        personViewHolder.mEpisodeDuration.setText(mDataset.get(i).getItunesDuration());
        personViewHolder.mEpisode = mDataset.get(i);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PodcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView mCV;
        TextView mEpisodeTitle;
        TextView mEpisodeDescription;
        TextView mEpisodeDuration;
        ImageView mEpisodeImage;
        Episode mEpisode;

        PodcastViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mCV = (CardView)itemView.findViewById(R.id.cv);
            mEpisodeTitle = (TextView)itemView.findViewById(R.id.episode_title);
            mEpisodeDescription = (TextView) itemView.findViewById(R.id.episode_description);
            mEpisodeDuration = (TextView) itemView.findViewById(R.id.episode_duration);
            mEpisodeImage = (ImageView)itemView.findViewById(R.id.episode_image);
        }

        @Override
        public void onClick(View view) {
            Log.d(LOG_TAG, "onClick " + this.getLayoutPosition() + mEpisode.getTitle());
            view.setTag(mEpisode);
            mItemListener.onRecyclerViewListClicked(view,getLayoutPosition());
        }

    }

}
