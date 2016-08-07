package maximomrtnz.podcastmanager.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import maximomrtnz.podcastmanager.models.pojos.Podcast;

/**
 * Created by maximo on 17/06/16.
 */

public class ItunesAppleAPI{

    public interface ItunesAppleAPIListener{
        void onError(Exception e);
        void onSuccess(Object arg);
    }

    private final ItunesAppleAPI.ItunesAppleAPIListener mListener;

    private static final String LOG_TAG = "ItunesAppleAPI";

    private final static String API_BASE = "https://itunes.apple.com";

    public ItunesAppleAPI(ItunesAppleAPIListener mListener){
        this.mListener = mListener;
    }

    public void getPodcast(int limit, String language){

        Map<String, String> headers = new HashMap<>();

        headers.put(AsyncHttpClient.HEADER_ACCEPT, AsyncHttpClient.APPLICATION_JSON);

        new AsyncHttpClient(API_BASE+"/"+language+"/rss/toppodcasts/limit="+String.valueOf(limit)+"/explicit=true/json", new AsyncHttpClient.AsyncHttpClientListener() {

            @Override
            public void onError(int mErrorCode, String mErrorResponse) {
                Log.d(LOG_TAG, ""+mErrorCode);
                Log.d(LOG_TAG, mErrorResponse);
            }

            @Override
            public void onSuccess(String mResponse) {

                Log.d(LOG_TAG, mResponse);

                List<Podcast> podcasts = new ArrayList<>();

                try {

                    JSONObject response = new JSONObject(mResponse);

                    // Parse received JSON into Podcast Object
                    JSONArray  entries = response.getJSONObject("feed").getJSONArray("entry");

                    for(int i = 0; i < entries.length() ; i++){

                        // Each entry represents one podcast
                        JSONObject entry = entries.getJSONObject(i);

                        Podcast podcast = new Podcast();

                        podcast.setTitle(entry.getJSONObject("title").getString("label"));

                        JSONArray images =  entry.getJSONArray("im:image");

                        for(int j = 0; j < images.length(); j++) {
                            JSONObject image = images.getJSONObject(j);
                            String height = image.getJSONObject("attributes").getString("height");
                            if(Integer.parseInt(height) >= 100) {
                                podcast.setImageUrl(image.getString("label"));
                                break;
                            }
                        }

                        podcast.setFeedUrl(API_BASE+"/lookup?id=" + entry.getJSONObject("id").getJSONObject("attributes").getString("im:id"));

                        podcasts.add(podcast);
                    }

                    mListener.onSuccess(podcasts);

                }catch (JSONException e){
                    Log.e(LOG_TAG, e.getMessage());
                }catch (Exception e){
                    Log.e(LOG_TAG, e.getMessage());
                }

            }
        }).doGet(headers);

    }

    public void searchPodcast(int limit, String query){

        String encodedQuery = null;

        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            Log.d(LOG_TAG,e.getMessage());
        }

        Map<String, String> headers = new HashMap<>();

        headers.put(AsyncHttpClient.HEADER_ACCEPT, AsyncHttpClient.APPLICATION_JSON);

        new AsyncHttpClient(API_BASE+"/search?media=podcast&term="+encodedQuery+"&limit="+limit, new AsyncHttpClient.AsyncHttpClientListener() {

            @Override
            public void onError(int mErrorCode, String mErrorResponse) {
                Log.d(LOG_TAG, ""+mErrorCode);
                Log.d(LOG_TAG, mErrorResponse);
            }

            @Override
            public void onSuccess(String mResponse) {

                Log.d(LOG_TAG, mResponse);

                List<Podcast> podcasts = new ArrayList<>();

                try {

                    JSONObject response = new JSONObject(mResponse);

                    // Parse received JSON into Podcast Object
                    JSONArray  entries = response.getJSONArray("results");

                    for(int i = 0; i < entries.length() ; i++){

                        // Each entry represents one podcast
                        JSONObject entry = entries.getJSONObject(i);

                        Podcast podcast = new Podcast();

                        podcast.setTitle(entry.getString("trackName"));

                        podcast.setImageUrl(entry.getString("artworkUrl600"));

                        podcast.setFeedUrl(entry.getString("feedUrl"));

                        podcasts.add(podcast);
                    }

                    mListener.onSuccess(podcasts);

                }catch (JSONException e){
                    Log.e(LOG_TAG, e.getMessage());
                }catch (Exception e){
                    Log.e(LOG_TAG, e.getMessage());
                }

            }
        }).doGet(headers);

    }

    public void getUrlFeed(String urlFeed){

        Map<String, String> headers = new HashMap<>();

        headers.put(AsyncHttpClient.HEADER_ACCEPT, AsyncHttpClient.APPLICATION_JSON);

        new AsyncHttpClient(urlFeed, new AsyncHttpClient.AsyncHttpClientListener() {

            @Override
            public void onError(int mErrorCode, String mErrorResponse) {
                Log.d(LOG_TAG, ""+mErrorCode);
                Log.d(LOG_TAG, mErrorResponse);
            }

            @Override
            public void onSuccess(String mResponse) {

                Log.d(LOG_TAG, mResponse);

                String urlFeed = null;

                try {

                    JSONObject response = new JSONObject(mResponse);

                    // Parse received JSON into Podcast Object
                    JSONArray  entries = response.getJSONArray("results");

                    mListener.onSuccess(entries.getJSONObject(0).getString("feedUrl"));

                }catch (JSONException e){
                    Log.e(LOG_TAG, e.getMessage());
                }catch (Exception e){
                    Log.e(LOG_TAG, e.getMessage());
                }

            }
        }).doGet(headers);

    }

}
