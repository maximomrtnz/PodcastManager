package maximomrtnz.podcastmanager.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maximomrtnz.podcastmanager.broadcastreceivers.AlarmReceiver;

/**
 * Created by Maxi on 11/22/2015.
 */
public class Utils {

    private static final String LOG_TAG = "Utils";

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size=1024;
        try {
            byte[] bytes= new byte[buffer_size];
            int count = 0;
            do{
                count = is.read(bytes, 0, buffer_size);
                if(count!=-1) {
                    os.write(bytes, 0, count);
                }
            }while(count!=-1);
        }
        catch(Exception ex){
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    public static String  formatSeconds(Integer seconds){

        int h =  seconds / 3600;
        int m = (seconds - (h*3600))/60;
        int s = (seconds - (h*3600) - (m*60));

        return (h<=9?"0"+h:h)+":"+(m<=9?"0"+m:m)+":"+(s<=9?"0"+s:s);
    }

    public static String  formatSeconds(long seconds){

        long h =  seconds / 3600;
        long m = (seconds - (h*3600))/60;
        long s = (seconds - (h*3600) - (m*60));

        return (h<=9?"0"+h:h)+":"+(m<=9?"0"+m:m)+":"+(s<=9?"0"+s:s);
    }

    public static void scheduleTask(Context context, long repeatTime){

        Log.d(LOG_TAG,"Scheduling Task");

        // Set the alarm to fire Background Cron Task, because we need to do it after every re boot
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1);

        // fetch every hour seconds
        // InexactRepeating allows Android to optimize the energy consumption
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatTime, pendingIntent);
    }

    public static String md5Encode(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void applyGrayscale(ImageView imageView){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

}
