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

    public File getFile(String url){
        String filename = Utils.md5Encode(url);
        File f = new File(cacheDir, filename);
        return f;
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
}
