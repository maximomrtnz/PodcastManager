package maximomrtnz.podcastmanager.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by maximo on 21/08/16.
 */

public class DateUtils {

    private static String LOG_TAG = "DateUtils";

    public static final List<String> DATE_FORMATS = new ArrayList<String>(Arrays.asList("yyyy.MM.dd G 'at' HH:mm:ss z", "EEE, MMM d, ''yy", "yyyyy.MMMM.dd GGG hh:mm aaa","EEE, dd MMM yyyy HH:mm:ss Z","yyMMddHHmmssZ","yyyy-MM-dd'T'HH:mm:ss.SSSZ","yyyy-MM-dd'T'HH:mm:ss.SSSXXX","YYYY-'W'ww-u"));

    private static Map<String, SimpleDateFormat> HASHFORMATTERS = new HashMap<String, SimpleDateFormat>();

    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    private static final TimeZone defaultTimezone = TimeZone.getTimeZone("GMT");

    public static Long formatDateAsLong(Calendar cal){

        if(cal == null){
            return null;
        }
        return Long.parseLong(getDateFormat(DATE_FORMAT).format(cal.getTime()));
    }

    public static String format(Calendar calendar, String format){

        //dateFormat.setTimeZone(calendar.getTimeZone());
        return getDateFormat(format).format(calendar.getTime());
    }

    public static Calendar getCalendarFromFormattedLong(long l){
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(getDateFormat(DATE_FORMAT).parse(String.valueOf(l)));
            return c;
        } catch (ParseException e) {
            return null;
        }
    }

    public static Calendar getCalendarFromString(String date, String dateFormat) throws ParseException{

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat formatter = HASHFORMATTERS.get(dateFormat);

        if (formatter == null) {

            formatter = getDateFormat(dateFormat);

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

    public static boolean areEquals(Calendar c1, Calendar c2) {

        if(c1 == null && c2 == null){
            return true;
        }else if(c1==null || c2==null){
            return false;
        }else {

            String date1 = format(c1, "EEE, dd MMM yyyy HH:mm:ss Z");

            Log.d(LOG_TAG, date1);

            String date2 = format(c2, "EEE, dd MMM yyyy HH:mm:ss Z");

            Log.d(LOG_TAG, date2);

            return (date1.equals(date2));
        }

    }

    public static SimpleDateFormat getDateFormat(String format){
        SimpleDateFormat parser = new SimpleDateFormat(format, Locale.US);
        parser.setLenient(false);
        parser.setTimeZone(defaultTimezone);
        return parser;
    }

}
