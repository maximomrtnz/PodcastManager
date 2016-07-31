package maximomrtnz.podcastmanager.utils;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import maximomrtnz.podcastmanager.models.pojos.Enclosure;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;

/**
 * Created by Maxi on 11/26/2015.
 */
public class PodcastXMLParser {

    // We don't use XML namespaces
    private static final String ns = null;
    private static final String CHANNEL = "channel";
    private static final String ITEM = "item";
    private static final String RSS = "rss";


    /** Parse an Podcast XML, returning a Podacast Channel.
     *
     * @param in Podcast feed, as a stream.
     * @return Channel object.
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException on I/O error.
     */
    public static Podcast parse(InputStream in) throws XmlPullParserException, IOException, ParseException {

        Podcast podcast = null;

        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            podcast = readPodcast(parser);

        } finally {
            in.close();
        }

        return podcast;

    }

    private static Podcast readPodcast(XmlPullParser parser)throws XmlPullParserException, IOException, ParseException {

        Podcast podcast = null;

        // Search for <rss> tags.
        parser.require(XmlPullParser.START_TAG, ns, RSS);

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals(CHANNEL)) {

                podcast = readChannel(parser);

            } else {

                skip(parser);

            }

        }

        return podcast;

    }

    /**
     * Decode a channel attached to an XmlPullParser.
     *
     * @param parser Incoming XMl
     * @return Channel objects.
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException on I/O error.
     */
    private static Podcast readChannel(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {

        Podcast channel = new Podcast();

        List<Episode> episodes = new ArrayList();

        channel.setEpisodes(episodes);

        // Search for <channel> tags.
        parser.require(XmlPullParser.START_TAG, ns, CHANNEL);

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals(ITEM)) {

                episodes.add(readItem(parser));

            }else if(name.equals("link")) {

                // Example <link>http://www.example.con/podcast</link>

                channel.setLink(readBasicTag(parser, "link"));

            }else if(name.equals("title")) {

                // Example <title>Title goes here</title>
                channel.setTitle(readBasicTag(parser, "title"));

            }else if(name.equals("description")) {

                // Example <description>Description goes here</description>
                channel.setDescription(readBasicTag(parser, "description"));

            }else if(name.equals("copyright")) {

                // Example <copyright>2014</copyright>
                channel.setCopyright(readBasicTag(parser, "copyright"));

            }else if(name.equals("language")) {

                // Example <language>en</language>
                channel.setLanguage(readBasicTag(parser, "language"));

            }else if(name.equals("itunes:author")) {

                // Example <itunes:author>Itunes Author goes here</itunes:author>
                channel.setItunesAuthor(readBasicTag(parser, "itunes:author"));

            }else if(name.equals("itunes:summary")) {

                // Example <itunes:summary>Itunes Summary goes here</itunes:summary>
                channel.setItunesSumary(readBasicTag(parser, "itunes:summary"));

            }else if(name.equals("pubDate")) {

                // Example <pubDate>Thu, 03 Dec 2015 12:15:47 +0000</pubDate>
                channel.setPubDate(Utils.getCalendarFromString(readBasicTag(parser, "pubDate")));

            }else if(name.equals("itunes:image")){

                // Example <itunes:image href="https://example/i/10874674.jpg"/>
                channel.setImageUrl(readItunesImage(parser));

            }else if(name.equals("lastBuildDate")){

                // Example <lastBuildDate>Fri, 29 Jul 2016 03:01:27 -0400</lastBuildDate>
                channel.setLastBuildDate(Utils.getCalendarFromString(readBasicTag(parser, "lastBuildDate")));

            }else{

                skip(parser);

            }

        }

        return channel;

    }


    /**
     * Parses the contents of an item. If it encounters a title, summary, or link tag, hands them
     * off to their respective "read" methods for processing. Otherwise, skips the tag.
     */
    private static Episode readItem(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {

        parser.require(XmlPullParser.START_TAG, ns, ITEM);

        Episode episode = new Episode();

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("title")){

                // Example: <title>British Comedy</title>
                episode.setTitle(readBasicTag(parser, "title"));

            } else if (name.equals("link")) {

                // Example: <link>https://test.com/british-comedy</link>
                episode.setLink(readBasicTag(parser, "link"));

            } else if (name.equals("itunes:author")) {

                // Example: <itunes:author>John Paul - British Comedy</itunes:author>
                episode.setItunesAuthor(readBasicTag(parser, "itunes:author"));

            } else if(name.equals("itunes:image")) {

                // Example: <itunes:image href="https://example.com/1234" />
                episode.setImageUrl(readItunesImage(parser));

            }else if(name.equals("itunes:duration")) {

                // Example <itunes:duration>1234</itunes:duration>

                episode.setItunesDuration(readDuration(parser));

            }else if(name.equals("itunes:subtitle")) {

                // Example <itunes:subtitle>Itunes Subtitle goes here</itunes:subtitle>

                episode.setItunesSubtitle(readBasicTag(parser, "itunes:subtitle"));

            }else if(name.equals("description")) {

                // Example <description>Description goes here</description>

                episode.setDescription(readBasicTag(parser, "description"));

            }else if(name.equals("pubDate")){

                // Example <pubDate>Thu, 03 Dec 2015 12:14:24 +0000</pubDate>

                episode.setPubDate(Utils.getCalendarFromString(readBasicTag(parser,"pubDate")));

            } else if (name.equals("enclosure")) {

                // Example <enclosure url="http://media.libsyn.com/media/podcast411/411_060325.mp3" length="11779397" type="audio/mpeg"/>
                episode.setEnclosure(readEnclosure(parser));


            } else if(name.equals("itunes:summary")) {

                // Example <itunes:summary> Itunes Summary text goes here </itunes:summary>
                episode.setItunesSummary(readBasicTag(parser, "itunes:summary"));

            }else {

                skip(parser);

            }

        }
        return episode;
    }


    /**
     * Reads the body of a basic XML tag, which is guaranteed not to contain any nested elements.
     *
     * <p>You probably want to call readTag().
     *
     * @param parser Current parser object
     * @param tag XML element tag name to parse
     * @return Body of the specified tag
     * @throws java.io.IOException
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    private static String readBasicTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return result;
    }


    /**
     * Processes link tags in the feed.
     */
    private static String readItunesImage(XmlPullParser parser) throws IOException, XmlPullParserException {

        String href;

        parser.require(XmlPullParser.START_TAG, ns, "itunes:image");

        href = parser.getAttributeValue(null, "href");

        while (true) {
            if (parser.nextTag() == XmlPullParser.END_TAG){
                break;
            }
            // Intentionally break; consumes any remaining sub-tags.
        }
        return href;
    }

    /**
     * Processes link tags in the feed.
     */
    private static Enclosure readEnclosure(XmlPullParser parser) throws IOException, XmlPullParserException {

        Enclosure enclosure = new Enclosure();

        parser.require(XmlPullParser.START_TAG, ns, "enclosure");

        enclosure.setLength(Long.getLong(parser.getAttributeValue(null, "length")));
        enclosure.setType(parser.getAttributeValue(null, "type"));
        enclosure.setUrl(parser.getAttributeValue(null, "url"));

        while (true) {
            if (parser.nextTag() == XmlPullParser.END_TAG){
                break;
            }
            // Intentionally break; consumes any remaining sub-tags.
        }
        return enclosure;
    }

    /**
     * For the tags title and summary, extracts their text values.
     */
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * For the tags duration extracts it text values.
     */
    private static String readDuration(XmlPullParser parser) throws IOException, XmlPullParserException{
        String duration = readBasicTag(parser, "itunes:duration");
        if(duration.indexOf(":")!=-1){ // Format hh:mm:ss
            return duration;
        }else{
            return Utils.formatSeconds(Integer.valueOf(duration));
        }
    }

    /**
     * Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
     * if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
     * finds the matching END_TAG (as indicated by the value of "depth" being 0).
     */
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
