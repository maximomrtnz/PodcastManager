package maximomrtnz.podcastmanager.cache;

import android.content.Context;

import java.io.File;
import java.net.URLEncoder;

import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 23/07/16.
 */

public class FileCache {

    private File cacheDir;

    public FileCache(Context context, String directory){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), Constants.DIRECTORIES.ROOT+"/"+directory);
        }else {
            cacheDir = context.getCacheDir();
        }
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public File getFile(String filename){
        File f = new File(cacheDir, filename);
        return f;
    }

    public boolean deleteFile(String fileName) {
        File file = new File(cacheDir, fileName);
        return file.delete();
    }

    public void clear(){
        File[] files=cacheDir.listFiles();
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
        return fileName;
    }
}
