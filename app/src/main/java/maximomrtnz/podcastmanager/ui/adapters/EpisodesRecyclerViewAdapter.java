package maximomrtnz.podcastmanager.ui.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.FileCache;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.database.EpisodeConverter;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.models.pojos.Episode;
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

        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.episode_sources_item, viewGroup, false);

        final EpisodeViewHolder evh = new EpisodeViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = evh.getLayoutPosition();
                // Set Podcast Into View's tag
                view.setTag(getItem(position));
                if (mItemListener != null) mItemListener.onRecyclerViewListClicked(view, position);
            }
        });

        v.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                evh.mDownloadButton.setVisibility(View.VISIBLE);
                evh.mCancelButton.setVisibility(View.GONE);

                int position = evh.getLayoutPosition();

                Episode episode = getItem(position);

                DownloadManager downloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);

                if(episode.getDownloadId()!=null) {

                    downloadManager.remove(episode.getDownloadId());

                    episode.setDownloadId(null);

                    // Save into DB
                    mContext.getContentResolver().insert(
                            PodcastManagerContentProvider.EPISODE_CONTENT_URI,
                            new EpisodeConverter().loadToContentValue(episode)
                    );

                }
            }
        });

        v.findViewById(R.id.button_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                evh.mDownloadButton.setVisibility(View.GONE);
                evh.mCancelButton.setVisibility(View.VISIBLE);


                int position = evh.getLayoutPosition();

                Episode episode = getItem(position);

                DownloadManager downloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(episode.getEpisodeUrl()));

                //file type
                //request.setMimeType("application/mp3");

                //if you want to download only over wifi
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

                // we just want to download silently
                request.setVisibleInDownloadsUi(false);

                //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setDestinationInExternalFilesDir(mContext, null, "Audios/"+FileCache.getAudioFileName(episode.getEpisodeUrl()));

                //to set title of download
                request.setTitle(episode.getTitle());

                Log.d(LOG_TAG,""+episode.getPodcastId());

                //if your download is visible in history of downloads
                request.setVisibleInDownloadsUi(false);

                //you should store unique queue id
                long queueId = downloadManager.enqueue(request);

                episode.setDownloadId(queueId);

                // Save into DB
                mContext.getContentResolver().insert(
                        PodcastManagerContentProvider.EPISODE_CONTENT_URI,
                        new EpisodeConverter().loadToContentValue(episode)
                );

            }
        });

        return evh;
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder episodeViewHolder, int i) {

        Episode episode = getItem(i);

        mImageLoader.displayImage(episode.getImageUrl(), episodeViewHolder.mEpisodeImage);

        if(episode.getPlayed()!=null && episode.getPlayed()){
            Utils.applyGrayscale(episodeViewHolder.mEpisodeImage);
            episodeViewHolder.mEpisodeTitle.setTextColor(mContext.getResources().getColor(R.color.podcastTitlePlayedListColor));
        }else{
            episodeViewHolder.mEpisodeImage.setColorFilter(null);
            episodeViewHolder.mEpisodeTitle.setTextColor(mContext.getResources().getColor(R.color.podcastTitleListColor));
        }

        if(episode.getDescription()!=null){
            episodeViewHolder.mEpisodeDescription.setText(episode.getDescription());
        }

        episodeViewHolder.mEpisodeTitle.setText(episode.getTitle());
        episodeViewHolder.mEpisodeDuration.setText(episode.getItunesDuration());
        episodeViewHolder.mEpisodePubDate.setText(DateUtils.format(episode.getPubDate(),"MMM d, yyyy"));
        episodeViewHolder.mProgressBar.setVisibility(View.GONE);


        episodeViewHolder.mDeleteButton.setVisibility(View.GONE);
        episodeViewHolder.mCancelButton.setVisibility(View.GONE);
        episodeViewHolder.mDownloadButton.setVisibility(View.VISIBLE);
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
        ImageButton mDownloadButton;
        ImageButton mCancelButton;
        ImageButton mDeleteButton;
        ProgressBar mProgressBar;

        EpisodeViewHolder(View itemView) {
            super(itemView);
            mCV = (CardView)itemView.findViewById(R.id.cv);
            mEpisodeTitle = (TextView)itemView.findViewById(R.id.episode_title);
            mEpisodeDescription = (TextView) itemView.findViewById(R.id.episode_description);
            mEpisodeDuration = (TextView) itemView.findViewById(R.id.episode_duration);
            mEpisodePubDate = (TextView) itemView.findViewById(R.id.episode_pub_date);
            mEpisodeImage = (ImageView)itemView.findViewById(R.id.episode_image);
            mDownloadButton = (ImageButton)itemView.findViewById(R.id.button_download);
            mCancelButton = (ImageButton)itemView.findViewById(R.id.button_cancel);
            mDeleteButton = (ImageButton)itemView.findViewById(R.id.button_delete);
            mProgressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar);
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
