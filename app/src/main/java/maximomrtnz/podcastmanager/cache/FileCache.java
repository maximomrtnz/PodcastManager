package maximomrtnz.podcastmanager.cache;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.net.URLEncoder;

import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 23/07/16.
 */

public class FileCache {

    private File mCacheDir;

    public FileCache(Context context, String directory){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            mCacheDir = context.getExternalFilesDir(directory);
        }else {
            mCacheDir = context.getFilesDir();
        }

        Log.d("IS EXTERNAL",android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)+"");
        Log.d("DIRECTORY",mCacheDir.getAbsolutePath());

        if(!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
    }

    public File getFile(String filename){
        File f = new File(mCacheDir, filename);
        return f;
    }

    public boolean deleteFile(String fileName) {
        File file = new File(mCacheDir, fileName);
        return file.delete();
    }

    public void clear(){
        File[] files=mCacheDir.listFiles();
        if(files==null) {
            return;
        }
        for(File f:files) {
            f.delete();
        }
    }

    public static String getCacheFileName(String url){
        return Utils.md5Encode(url);
    }

    public static String getAudioFileName(String url){
        String fileName = getCacheFileName(url);
        if(url.toLowerCase().contains(".mp3")){
            fileName +=".mp3";
        }else if(url.toLowerCase().contains(".ogg")){
            fileName +=".ogg";
        }else if(url.toLowerCase().contains(".wav")){
            fileName +=".wav";
        }
        return  fileName;
    }
}
