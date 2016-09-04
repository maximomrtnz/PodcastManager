package maximomrtnz.podcastmanager.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by maximo on 03/09/16.
 */

public class DownloadManagerHelper {

    public static String LOG_TAG = "DownloadManagerHelper";

    public DownloadManager mDownloadManager;
    public Context mContext;

    public DownloadManagerHelper(Context context){
        mContext = context;
        mDownloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
    }


    public long download(String downloadUrl, String dir, String fileName, String title, String description){

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        request.setVisibleInDownloadsUi(false);

        if(!TextUtils.isEmpty(title)) {
            request.setTitle(title);
        }

        if(!TextUtils.isEmpty(description)) {
            request.setDescription(description);
        }

        if(!TextUtils.isEmpty(dir) && !TextUtils.isEmpty(fileName)) {
            request.setDestinationInExternalPublicDir(dir, fileName);
        }


        return mDownloadManager.enqueue(request);

    }

    public float getProgressPercentage(long requestId) {

        float downloadedBytes = 0;
        float totalBytes = 0;
        float percentage = 0;

        try {

            Cursor c = mDownloadManager.query(new DownloadManager.Query().setFilterById(requestId));

            if (c.moveToFirst()) {

                downloadedBytes = (int) c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                totalBytes = (int) c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            }

            percentage = (downloadedBytes * 100 / totalBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (int)percentage;
    }

    public int getDownloadStatus(long requestId){

        int status = -1;

        DownloadManager.Query query = new DownloadManager.Query().setFilterById(requestId);

        if(query!=null) {

            query.setFilterByStatus(DownloadManager.STATUS_FAILED | DownloadManager.STATUS_PAUSED | DownloadManager.STATUS_SUCCESSFUL | DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_PENDING);

            Cursor c = mDownloadManager.query(query);

            if (c.moveToFirst()) {
                status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }

        }
        return status;

    }

    public void deleteDownload(long requestId){
        mDownloadManager.remove(requestId);
    }


}
