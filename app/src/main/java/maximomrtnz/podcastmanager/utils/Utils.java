package maximomrtnz.podcastmanager.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
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

    public static final List<String> DATE_FORMATS = new ArrayList<String>(Arrays.asList("yyyy.MM.dd G 'at' HH:mm:ss z", "EEE, MMM d, ''yy", "yyyyy.MMMM.dd GGG hh:mm aaa","EEE, d MMM yyyy HH:mm:ss Z","yyMMddHHmmssZ","yyyy-MM-dd'T'HH:mm:ss.SSSZ","yyyy-MM-dd'T'HH:mm:ss.SSSXXX","YYYY-'W'ww-u"));

    private static Map<String, SimpleDateFormat> HASHFORMATTERS = new HashMap<String, SimpleDateFormat>();

    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    public static Long formatDateAsLong(Calendar cal){
        if(cal == null){
            return null;
        }
        return Long.parseLong(dateFormat.format(cal.getTime()));
    }

    public static String format(Calendar calendar, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(calendar.getTimeZone());
        return dateFormat.format(calendar.getTime());
    }

    public static Calendar getCalendarFromFormattedLong(long l){
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(dateFormat.parse(String.valueOf(l)));
            return c;
        } catch (ParseException e) {
            return null;
        }
    }

    public static Calendar getCalendarFromString(String date, String dateFormat) throws ParseException{

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat formatter = HASHFORMATTERS.get(dateFormat);

        if (formatter == null) {
            formatter = new SimpleDateFormat(dateFormat);
            HASHFORMATTERS.put(dateFormat, formatter);
        }

        cal.setTime(formatter.parse(date));

        return cal;

    }

    public static Calendar getCalendarFromString(String date){

        Calendar cal = null;

        if(date == null){
            return cal;
        }

        int i = 0;

        while (i < DATE_FORMATS.size() && cal == null){

            String dateFormat = DATE_FORMATS.get(i);

            try{

                cal = getCalendarFromString(date, dateFormat);
                break;

            }catch(ParseException e){}

            i++;

        }

        return cal;

    }

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




}
