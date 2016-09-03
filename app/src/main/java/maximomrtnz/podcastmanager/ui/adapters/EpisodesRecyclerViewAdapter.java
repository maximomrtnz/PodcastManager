package maximomrtnz.podcastmanager.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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
import maximomrtnz.podcastmanager.database.EpisodeConverter;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;
import maximomrtnz.podcastmanager.utils.DateUtils;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 20/06/16.
 */

public class EpisodesRecyclerViewAdapter extends RecyclerView.Adapter<EpisodesRecyclerViewAdapter.EpisodeViewHolder> {

    private static String LOG_TAG = "RecyclerViewAdapter";

    private RecyclerViewClickListener mItemListener;
    private List<Episode> mDataset;
    private ImageLoader mImageLoader;
    private Cursor mCursor;
    private Context mContext;

    public EpisodesRecyclerViewAdapter(List<Episode> dataset, Context context, RecyclerViewClickListener itemListener){
        this.mDataset = dataset;
        this.mImageLoader = new ImageLoader(context);
        this.mItemListener = itemListener;
        this.mContext = context;
    }


    public EpisodesRecyclerViewAdapter(Context context, RecyclerViewClickListener itemListener){
        this.mImageLoader = new ImageLoader(context);
        this.mItemListener = itemListener;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        if(mCursor!=null) {
            return mCursor.getCount();
        }else if(mDataset!=null){
            return mDataset.size();
        }
        return 0;
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.episode_sources_item, viewGroup, false);

        final EpisodeViewHolder evh = new EpisodeViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = evh.getLayoutPosition();
                // Set Podcast Into View's tag
                v.setTag(getItem(position));
                if (mItemListener != null) mItemListener.onRecyclerViewListClicked(v, position);
            }
        });

        return evh;
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder personViewHolder, int i) {

        Episode episode = getItem(i);

        mImageLoader.displayImage(episode.getImageUrl(), personViewHolder.mEpisodeImage);

        if(episode.getPlayed()!=null && episode.getPlayed()){
            Utils.applyGrayscale(personViewHolder.mEpisodeImage);
            personViewHolder.mEpisodeTitle.setTextColor(mContext.getResources().getColor(R.color.podcastTitlePlayedListColor));
        }else{
            personViewHolder.mEpisodeImage.setColorFilter(null);
            personViewHolder.mEpisodeTitle.setTextColor(mContext.getResources().getColor(R.color.podcastTitleListColor));
        }

        if(episode.getDescription()!=null){
            personViewHolder.mEpisodeDescription.setText(episode.getDescription());
        }

        personViewHolder.mEpisodeTitle.setText(episode.getTitle());
        personViewHolder.mEpisodeDuration.setText(episode.getItunesDuration());
        personViewHolder.mEpisodePubDate.setText(DateUtils.format(episode.getPubDate(),"MMM d, yyyy"));

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {

        CardView mCV;
        TextView mEpisodeTitle;
        TextView mEpisodeDescription;
        TextView mEpisodeDuration;
        TextView mEpisodePubDate;
        ImageView mEpisodeImage;

        EpisodeViewHolder(View itemView) {
            super(itemView);
            mCV = (CardView)itemView.findViewById(R.id.cv);
            mEpisodeTitle = (TextView)itemView.findViewById(R.id.episode_title);
            mEpisodeDescription = (TextView) itemView.findViewById(R.id.episode_description);
            mEpisodeDuration = (TextView) itemView.findViewById(R.id.episode_duration);
            mEpisodePubDate = (TextView) itemView.findViewById(R.id.episode_pub_date);
            mEpisodeImage = (ImageView)itemView.findViewById(R.id.episode_image);
        }

    }

    public Episode getItem(int position){

        Episode episode = null;

        if(mCursor != null) {

            mCursor.moveToPosition(position);

            episode = new EpisodeConverter().loadFrom(mCursor);


        }else{

            episode = mDataset.get(position);

        }

        return episode;
    }

    public void setCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

}
