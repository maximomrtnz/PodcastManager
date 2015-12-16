package maximomrtnz.podcastmanager.view.adapters;

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
import maximomrtnz.podcastmanager.models.pojos.Channel;

/**
 * Created by Maxi on 12/12/2015.
 */
public class ChannelsRecicleViewAdapter extends RecyclerView.Adapter<ChannelsRecicleViewAdapter.ChannelViewHolder> {

    private List<Channel> channels;

    public ChannelsRecicleViewAdapter(List<Channel> channels){
        this.channels = channels;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row, parent, false);
        ChannelViewHolder cvh = new ChannelViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        holder.channelTitle.setText(channels.get(position).getTitle());
        holder.channelSummary.setText(channels.get(position).getItunesSumary());
        //holder.channelImage.setImageResource(channels.get(position).getItunesImage().getHref());
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView channelTitle;
        TextView channelSummary;
        ImageView channelImage;

        public ChannelViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            channelTitle = (TextView)itemView.findViewById(R.id.channel_title);
            channelSummary = (TextView)itemView.findViewById(R.id.channel_summary);
            channelImage = (ImageView)itemView.findViewById(R.id.channel_image);
        }
    }

}
