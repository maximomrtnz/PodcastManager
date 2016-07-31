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
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;

/**
 * Created by maximo on 20/06/16.
 */

public class PodcastRecyclerViewAdapter extends RecyclerView.Adapter<PodcastRecyclerViewAdapter.PodcastViewHolder> {

    private static String LOG_TAG = "RecyclerViewAdapter";
    private static RecyclerViewClickListener mItemListener;

    private List<Podcast> mDataset;
    private ImageLoader mImageLoader;

    public PodcastRecyclerViewAdapter(List<Podcast> mDataset, Context context, RecyclerViewClickListener itemListener){
        this.mDataset = mDataset;
        this.mImageLoader = new ImageLoader(context);
        mItemListener = itemListener;
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.podcast_sources_item, viewGroup, false);
        PodcastViewHolder pvh = new PodcastViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder personViewHolder, int i) {

        personViewHolder.mPodcastTitle.setText(mDataset.get(i).getTitle());
        personViewHolder.mPodcast = mDataset.get(i);

        mImageLoader.displayImage(mDataset.get(i).getImageUrl(),personViewHolder.mPodcastImage);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PodcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView mCV;
        TextView mPodcastTitle;
        ImageView mPodcastImage;
        Podcast mPodcast;

        PodcastViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mCV = (CardView)itemView.findViewById(R.id.cv);
            mPodcastTitle = (TextView)itemView.findViewById(R.id.podcast_title);
            mPodcastImage = (ImageView)itemView.findViewById(R.id.podcast_image);
        }

        @Override
        public void onClick(View view) {
            Log.d(LOG_TAG, "onClick " + this.getLayoutPosition() + mPodcast.getTitle());
            view.setTag(mPodcast);
            mItemListener.onRecyclerViewListClicked(view,getLayoutPosition());
        }

    }

}
