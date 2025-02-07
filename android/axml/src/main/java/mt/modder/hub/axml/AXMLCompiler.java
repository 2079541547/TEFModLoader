package mt.modder.hub.axml;

import android.content.Context;

import mt.modder.hub.axmlTools.Chunk;
import mt.modder.hub.axmlTools.IntWriter;
import mt.modder.hub.axmlTools.StringPoolChunk;
import mt.modder.hub.axmlTools.TagChunk;
import mt.modder.hub.axmlTools.XmlChunk;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

/**
 •  A compiler that converts XML text into AXML (Android Binary XML) byte arrays.
 •  Uses XmlPullParser to parse the text XML and converts the same to AXML bytes.
 */
public class AXMLCompiler {

    /**
     *  Converts an XML string into an AXML byte array.
     *
     * @param context The Android Context to be used for resolving resource identifiers.
     * @param xml     The XML string that needs to be converted to AXML format.
     * @return An AXML byte array.
     * @throws XmlPullParserException If there is an error parsing XML document.
     * @throws IOException            If there is an issue while writing to output stream.
     */
    public byte[] axml2Xml(Context context, String xml) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); // Create a new XmlPullParserFactory
        factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true); // Enable processing of namespaces
        XmlPullParser parser = factory.newPullParser();   // Create a new XmlPullParser
        parser.setInput(new StringReader(xml));    // Set input of the parser from XML string
        return encode(context, parser);            // Call encode method to encode XML to AXML
    }

    /**
     *  Encodes XML data from an XmlPullParser into an AXML byte array.
     *
     * @param context The Android Context to be used for resolving resource identifiers.
     * @param parser  XmlPullParser used to parse the input XML string.
     * @return An AXML byte array.
     * @throws XmlPullParserException If there is an error while parsing XML.
     * @throws IOException If there is an error while writing to output stream.
     */
    private static byte[] encode(Context context, XmlPullParser parser) throws XmlPullParserException, IOException {
        XmlChunk xmlChunk = new XmlChunk(context);  // Create root XmlChunk
        TagChunk currentTag = null; // reference of the current tag in the tree.

        // Start parsing the XML Document till the end
        for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
					// For each start tag create a TagChunk. set the current tag reference.
                    currentTag = new TagChunk(currentTag == null ? xmlChunk : currentTag, parser);
                    break;
                case XmlPullParser.END_TAG:
					// When an end tag is encountered, update the reference to the parent.
                    Chunk parentChunk = currentTag.getParent();
                    currentTag = parentChunk instanceof TagChunk ? (TagChunk) parentChunk : null;
                    break;
                default: // Ignore all other types
                    break;
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();   // Create an output stream to store AXML data.
		IntWriter writer = new IntWriter(outputStream);    // Create an IntWriter to write the AXML data into stream.

        // Write the XML chunk and it's child chunk data to output stream.
        xmlChunk.write(writer);
        writer.close(); // close the writer.
        return outputStream.toByteArray(); // Return the generated AXML byte array
    }
}
