package maximomrtnz.podcastmanager.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by maximo on 17/06/16.
 */

public class AsyncHttpClient {

    public static final String LOG_TAG = "AsyncHttpClient";

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_RANGE = "Content-Range";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_JSON = "application/json";


    private final AsyncHttpClient.AsyncHttpClientListener mListener;
    private String mUrl;
    private Integer mTimeout;

    public interface AsyncHttpClientListener{
        void onError(int mErrorCode, String mErrorResponse);
        void onSuccess(String mResponse);
    }

    public AsyncHttpClient(String mUrl, AsyncHttpClient.AsyncHttpClientListener mListener){
        this.mUrl = mUrl;
        this.mListener = mListener;
    }

    public void doGet(Map<String, String> mHeader){

        HttpURLConnection conn = null;

        try {

            URL url = new URL(mUrl);

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            if(mTimeout != null){
                // Set time out
                conn.setConnectTimeout(mTimeout);
            }

            if(mHeader != null) {
                for (String key : mHeader.keySet()) {
                    conn.setRequestProperty(key, mHeader.get(key));
                }
            }

            new HTTPRequestHandler().execute(conn);

        }catch (MalformedURLException e){
            Log.e(LOG_TAG, e.getMessage());
        }catch (IOException e){
            Log.e(LOG_TAG, e.getMessage());
        }

    }

    public class HTTPRequestHandler extends AsyncTask<HttpURLConnection, Void, Void>{

        private String mResponseBody;
        private int mResponseCode;


        @Override
        protected Void doInBackground(HttpURLConnection... httpURLConnections) {

            HttpURLConnection conn = httpURLConnections[0];

            InputStreamReader in = null;

            StringBuilder response = new StringBuilder();

            try {

                conn.connect();

                if (conn.getResponseCode() < 300) { // SUCCESS
                    in = new InputStreamReader(conn.getInputStream());
                } else { // ERROR
                    in = new InputStreamReader(conn.getErrorStream());
                }

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    response.append(buff, 0, read);
                }

                mResponseBody = response.toString();
                mResponseCode = conn.getResponseCode();

            }catch (IOException e){
                Log.e(LOG_TAG, e.getMessage());
            }finally {
                conn.disconnect();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(mResponseCode < 300){
                mListener.onSuccess(mResponseBody);
            }else{
                mListener.onError(mResponseCode, mResponseBody);
            }

        }
    }

}
