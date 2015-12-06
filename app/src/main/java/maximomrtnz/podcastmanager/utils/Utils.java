package maximomrtnz.podcastmanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maxi on 11/22/2015.
 */
public class Utils {

    public static final List<String> DATE_FORMATS = new ArrayList<String>(Arrays.asList("yyyy.MM.dd G 'at' HH:mm:ss z", "EEE, MMM d, ''yy", "yyyyy.MMMM.dd GGG hh:mm aaa","EEE, d MMM yyyy HH:mm:ss Z","yyMMddHHmmssZ","yyyy-MM-dd'T'HH:mm:ss.SSSZ","yyyy-MM-dd'T'HH:mm:ss.SSSXXX","YYYY-'W'ww-u"));

    private static Map<String, SimpleDateFormat> HASHFORMATTERS = new HashMap<String, SimpleDateFormat>();

    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    public static long formatDateAsLong(Calendar cal){
        return Long.parseLong(dateFormat.format(cal.getTime()));
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

}
