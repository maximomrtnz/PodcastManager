package maximomrtnz.podcastmanager.utils;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import maximomrtnz.podcastmanager.models.pojos.Channel;
import maximomrtnz.podcastmanager.models.pojos.Enclosure;
import maximomrtnz.podcastmanager.models.pojos.Item;
import maximomrtnz.podcastmanager.models.pojos.ItunesImage;
import maximomrtnz.podcastmanager.models.pojos.ItunesOwner;

/**
 * Created by Maxi on 11/26/2015.
 */
public class PodcastXMLParser {

    // We don't use XML namespaces
    private static final String ns = null;

    private static final String CHANNEL = "channel";
    private static final String ITEM = "item";


    /** Parse an Podcast XML, returning a Podacast Channel.
     *
     * @param in Podcast feed, as a stream.
     * @return Channel object.
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException on I/O error.
     */
    public Channel parse(InputStream in) throws XmlPullParserException, IOException, ParseException {

        Channel channel = null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            channel = readChannel(parser);

        } finally {
            in.close();
        }

        return channel;

    }

    /**
     * Decode a channel attached to an XmlPullParser.
     *
     * @param parser Incoming XMl
     * @return Channel objects.
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException on I/O error.
     */
    private Channel readChannel(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {

        Channel channel = new Channel();

        List<Item> items = new ArrayList();

        // Search for <channel> tags.
        parser.require(XmlPullParser.START_TAG, ns, CHANNEL);

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals(ITEM)) {

                items.add(readItem(parser));

            }else if(name.equals("link")) {

                // Example <link>http://www.example.con/podcast</link>

                channel.setLink(readBasicTag(parser, "link"));

            }else if(name.equals("title")) {

                // Example <title>Title goes here</title>
                channel.setTitle(readBasicTag(parser, "title"));

            }else if(name.equals("description")) {

                // Example <description>Description goes here</description>
                channel.setDescription(readBasicTag(parser, "description"));

            }else if(name.equals("itunes:author")) {

                // Example <itunes:author>Itunes Author goes here</itunes:author>
                channel.setItunesAuthor(readBasicTag(parser, "itunes:author"));

            }else if(name.equals("itunes:summary")) {

                // Example <itunes:summary>Itunes Summary goes here</itunes:summary>
                channel.setItunesSumary(readBasicTag(parser, "itunes:summary"));

            }else if(name.equals("pubDate")) {

                // Example <pubDate>Thu, 03 Dec 2015 12:15:47 +0000</pubDate>
                channel.setPubDate(Utils.getCalendarFromString(readBasicTag(parser, "pubDate")));

            }else if(name.equals("itunes:owner")) {

                // Example  <itunes:owner>
                //              <itunes:name>John Paul</itunes:name>
                //              <itunes:email>john@example.com</itunes:email>
                //          </itunes:owner>

                channel.setItunesOwner(readItunesOwner(parser));

            }else if(name.equals("itunes:image")){

                // Example <itunes:image href="https://example/i/10874674.jpg"/>
                channel.setItunesImage(readItunesImage(parser));

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
    private Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {

        parser.require(XmlPullParser.START_TAG, ns, ITEM);

        Item item = new Item();

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("title")){

                // Example: <title>British Comedy</title>
                item.setTitle(readBasicTag(parser, "title"));

            } else if (name.equals("link")) {

                // Example: <link>https://test.com/british-comedy</link>
                item.setLink(readBasicTag(parser, "link"));

            } else if (name.equals("itunes:author")) {

                // Example: <itunes:author>John Paul - British Comedy</itunes:author>
                item.setItunesAuthor(readBasicTag(parser, "itunes:author"));

            } else if(name.equals("itunes:image")) {

                // Example: <itunes:image href="https://example.com/1234" />
                item.setItunesImage(readItunesImage(parser));

            }else if(name.equals("itunes:duration")) {

                // Example <itunes:duration>1234</itunes:duration>

                item.setItunesDuration(Integer.valueOf(readBasicTag(parser, "itunes:duration")));

            }else if(name.equals("itunes:subtitle")) {

                // Example <itunes:subtitle>Itunes Subtitle goes here</itunes:subtitle>

                item.setItunesSubtitle(readBasicTag(parser, "itunes:subtitle"));

            }else if(name.equals("description")) {

                // Example <description>Description goes here</description>

                item.setDescription(readBasicTag(parser, "description"));

            }else if(name.equals("pubDate")){

                // Example <pubDate>Thu, 03 Dec 2015 12:14:24 +0000</pubDate>

                item.setPubDate(Utils.getCalendarFromString(readBasicTag(parser,"pubDate")));

            } else if (name.equals("enclosure")) {

                // Example <enclosure url="http://media.libsyn.com/media/podcast411/411_060325.mp3" length="11779397" type="audio/mpeg"/>
                item.setEnclosure(readEnclosure(parser));


            } else if(name.equals("itunes:summary")) {

                // Example <itunes:summary> Itunes Summary text goes here </itunes:summary>
                item.setItunesSummary(readBasicTag(parser, "itunes:summary"));

            }else {

                skip(parser);

            }

        }
        return item;
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
    private String readBasicTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return result;
    }


    /**
     * Processes link tags in the feed.
     */
    private ItunesImage readItunesImage(XmlPullParser parser) throws IOException, XmlPullParserException {

        ItunesImage itunesImage = new ItunesImage();

        parser.require(XmlPullParser.START_TAG, ns, "itunes:image");

        itunesImage.setHref(parser.getAttributeValue(null, "href"));

        while (true) {
            if (parser.nextTag() == XmlPullParser.END_TAG){
                break;
            }
            // Intentionally break; consumes any remaining sub-tags.
        }
        return itunesImage;
    }

    /**
     * Processes link tags in the feed.
     */
    private Enclosure readEnclosure(XmlPullParser parser) throws IOException, XmlPullParserException {

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


    private ItunesOwner readItunesOwner(XmlPullParser parser) throws IOException, XmlPullParserException {

        ItunesOwner itunesOwner = new ItunesOwner();

        parser.require(XmlPullParser.START_TAG, ns, "itunes:owner");

        itunesOwner.setName(parser.getAttributeValue(null, "name"));
        itunesOwner.setEmail(parser.getAttributeValue(null, "email"));

        while (true) {
            if (parser.nextTag() == XmlPullParser.END_TAG){
                break;
            }
            // Intentionally break; consumes any remaining sub-tags.
        }
        return itunesOwner;
    }

    /**
     * For the tags title and summary, extracts their text values.
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }


    /**
     * Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
     * if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
     * finds the matching END_TAG (as indicated by the value of "depth" being 0).
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
