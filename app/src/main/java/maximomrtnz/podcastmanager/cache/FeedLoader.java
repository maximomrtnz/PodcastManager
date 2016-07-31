package maximomrtnz.podcastmanager.cache;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.utils.PodcastXMLParser;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 26/07/16.
 */

public class FeedLoader {

    public interface FeedLoaderListener {
        void onFeedLoader(Podcast podcast);
    }

    private static final String LOG_TAG = "FeedLoader";
    private static final String DIRECTORY = "Feeds";

    private FileCache mFileCache;
    private MemoryCache mMemoryCache = new MemoryCache();
    private ExecutorService mExecutorService;
    private Handler mHandler = new Handler(); // handler to pass data to UI Thread
    private FeedLoaderListener mFeedLoaderListener;

    public FeedLoader(Context context, FeedLoaderListener feedLoaderListener){
        mFileCache = new FileCache(context, DIRECTORY);
        mExecutorService = Executors.newFixedThreadPool(1);
        mFeedLoaderListener = feedLoaderListener;
    }

    public void loadFeed(String url){

        Podcast podcast = (Podcast) mMemoryCache.get(url);

        if(podcast!=null) {

            // Return InputStream to called
            if(mFeedLoaderListener!=null){
                mFeedLoaderListener.onFeedLoader(podcast);
            }

        }else{

            // Download and storage Feed
            mExecutorService.execute(new FeedDownloader(url));

        }
    }

    private Podcast getFeed(String url) {

        File f = mFileCache.getFile(url);

        Podcast podcast = decodePodcast(f);

        // From SD Cache
        if(podcast!=null){
            // Set feed URL
            podcast.setFeedUrl(url);
            return podcast;
        }

        // From web
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.copyStream(is, os);
            os.close();
            conn.disconnect();
            podcast = decodePodcast(f);
            // Set feed URL
            podcast.setFeedUrl(url);
            return podcast;
        } catch (Throwable ex){
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError) {
                mMemoryCache.clear();
            }
            return null;
        }
    }


    private Podcast decodePodcast(File f){
        try {
            InputStream is = new FileInputStream(f);
            return PodcastXMLParser.parse(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }catch (XmlPullParserException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }catch (ParseException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public class FeedDownloader implements Runnable {

        private String mUrl;

        public FeedDownloader(String url){
            mUrl = url;
        }

        @Override
        public void run() {
            try{
                Podcast podcast = getFeed(mUrl);
                mMemoryCache.put(mUrl, podcast);
                mHandler.post(new FeedSender(podcast));
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }

    //Used to send Feed to UI thread
    class FeedSender implements Runnable {

        private Podcast mPodcast;

        public FeedSender(Podcast podcast){
            mPodcast = podcast;
        }

        public void run() {

            if (mPodcast != null) {
                mFeedLoaderListener.onFeedLoader(mPodcast);
            } else {
                //photoToLoad.imageView.setImageResource(stub_id);
            }

        }

    }


}
