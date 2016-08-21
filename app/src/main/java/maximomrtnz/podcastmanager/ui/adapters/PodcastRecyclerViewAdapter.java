package maximomrtnz.podcastmanager.ui.adapters;

import android.content.Context;
import android.database.Cursor;
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

    private RecyclerViewClickListener mItemListener;
    private List<Podcast> mDataset;
    private ImageLoader mImageLoader;
    private Cursor mCursor;

    public PodcastRecyclerViewAdapter(List<Podcast> mDataset, Context context, RecyclerViewClickListener itemListener){
        this.mDataset = mDataset;
        this.mImageLoader = new ImageLoader(context);
        this.mItemListener = itemListener;
    }

    public PodcastRecyclerViewAdapter(Context context, RecyclerViewClickListener itemListener){
        this.mImageLoader = new ImageLoader(context);
        this.mItemListener = itemListener;
    }

    @Override
    public int getItemCount() {
        if(mDataset!=null) {
            return mDataset.size();
        }else if(mCursor!=null){
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.podcast_sources_item, viewGroup, false);

        final PodcastViewHolder pvh = new PodcastViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pvh.getLayoutPosition();
                // Set Podcast Into View's tag
                v.setTag(getItem(position));
                if (mItemListener != null) mItemListener.onRecyclerViewListClicked(v, position);
            }
        });

        return pvh;
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder podcastViewHolder, int i) {

        Podcast podcast = getItem(i);

        podcastViewHolder.mPodcastTitle.setText(podcast.getTitle());
        podcastViewHolder.mPodcastAuthor.setText(podcast.getItunesAuthor());

        mImageLoader.displayImage(podcast.getImageUrl(),podcastViewHolder.mPodcastImage);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PodcastViewHolder extends RecyclerView.ViewHolder{

        CardView mCV;
        TextView mPodcastTitle;
        TextView mPodcastAuthor;
        ImageView mPodcastImage;

        PodcastViewHolder(View itemView) {
            super(itemView);
            mCV = (CardView)itemView.findViewById(R.id.cv);
            mPodcastTitle = (TextView)itemView.findViewById(R.id.podcast_title);
            mPodcastAuthor = (TextView)itemView.findViewById(R.id.podcast_author);
            mPodcastImage = (ImageView)itemView.findViewById(R.id.podcast_image);
        }

    }

    public void setCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Podcast getItem(int position){

        Podcast podcast = null;

        if(mDataset != null) {

            podcast = mDataset.get(position);

        }else{

            podcast = new Podcast();

            mCursor.moveToPosition(position);

            podcast.loadFrom(mCursor);

        }

        return podcast;
    }

}
