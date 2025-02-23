package mt.modder.hub.axmlTools;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Represents the start of an XML element tag in a binary XML resource.
 * This chunk stores information about the tag, its attributes, and associated namespaces.
 */
public class StartTagChunk extends Chunk<StartTagChunk.Header> {

    String name;              // Element name
    String prefix;            // Element prefix
    String namespace;         // Element namespace URI
    short attributeStart = 20;    // Starting position of attributes data
    short attributeSize = 20;     // Size of each attribute record
    short idAttributeIndex = 0;   // Index of the attribute representing the ID
    short styleAttributeIndex = 0;  // Index of the attribute representing the style
    short classAttributeIndex = 0; // Index of the attribute representing the class
    LinkedList<AttrChunk> attributes = new LinkedList<>(); // List of attribute chunks
    List<StartNameSpaceChunk> startNamespaces = new Stack<>(); // List of start namespace chunks

    /**
     * Header for the StartTagChunk.
     * Extends Chunk.NodeHeader and defines the specific chunk type.
     */
    public class Header extends Chunk.NodeHeader {
        /**
         * Constructor for the Header.
         * Sets the chunk type to XmlStartElement.
         */
        public Header() {
            super(ChunkType.XmlStartElement);
        }
    }

    /**
     * Constructor for StartTagChunk.
     *
     * @param parent The parent Chunk.
     * @param parser The XmlPullParser to parse data from.
     * @throws XmlPullParserException If an error occurs while parsing the XML.
     */
    public StartTagChunk(Chunk parent, XmlPullParser parser) throws XmlPullParserException {
        super(parent);
        this.name = parser.getName(); // Get the element name from parser.
        stringPool().addString(name); // Add the name to the string pool.
        this.prefix = parser.getPrefix(); // Get the element prefix from the parser.
        this.namespace = parser.getNamespace(); // Get the namespace URI from parser
        int attributeCount = parser.getAttributeCount(); // Get the number of attributes.

        // Iterate through each attribute to create an AttrChunk for each attribute
        for (short i = 0; i < attributeCount; ++i) {
			String attributePrefix = parser.getAttributePrefix(i);
            String attributeNamespace = parser.getAttributeNamespace(i);
            String attributeName = parser.getAttributeName(i);
            String attributeValue = parser.getAttributeValue(i);

            AttrChunk attribute = new AttrChunk(this);
            attribute.attributePrefix = attributePrefix; // set attribute prefix.
            attribute.attributeNamespace = attributeNamespace; // set attribute namespace.
            attribute.attributeRawValue = attributeValue; // set attribute value.
            attribute.attributeName = attributeName; // set attribute name.
            stringPool().addString(attributeNamespace, attributeName); // Add the attribute namespace and name to the string pool.
            this.attributes.add(attribute); // Add the attribute chunk to the list.

            // Check for the "id", "style" and "class" attributes and save their respective indices.
            if ("id".equals(attributeName) && "http://schemas.android.com/apk/res/android".equals(attributeNamespace)) {
                this.idAttributeIndex = i;
            } else if (attributePrefix == null && "style".equals(attributeName)) {
                this.styleAttributeIndex = i;
            } else if (attributePrefix == null && "class".equals(attributeName)) {
                this.classAttributeIndex = i;
            }
        }

        // Process namespace declarations
        int namespaceStart = parser.getNamespaceCount(parser.getDepth() - 1);
        int namespaceEnd = parser.getNamespaceCount(parser.getDepth());
        for (int i = namespaceStart; i < namespaceEnd; i++) {
			StartNameSpaceChunk startNamespaceChunk = new StartNameSpaceChunk(parent);
            startNamespaceChunk.setNamespacePrefix(parser.getNamespacePrefix(i)); // set name space prefix.
            stringPool().addString(null, startNamespaceChunk.namespacePrefix); // add to string pool.
			startNamespaceChunk.setNamespaceUri(parser.getNamespaceUri(i)); // set namespace uri
			stringPool().addString(null, startNamespaceChunk.namespaceUri); // add to string pool.
            startNamespaces.add(startNamespaceChunk); // Add start namespace chunk to the list.
        }
    }

    /**
     * Prepares the chunk before writing by calculating the size of each attribute.
     * Also calculates and sets the header size of the chunk.
     */
    @Override
    public void preWrite() {
		// Calculate the size of each attribute chunk.
        for (AttrChunk attribute : attributes) {
            attribute.calc();
        }
		// Calculate the overall size of the StartTagChunk based on the number of attributes,
		// the header size is 36 bytes and each attribute takes additional 20 bytes.
        header.size = 36 + 20 * attributes.size();
    }


    /**
     * Writes the data of this StartTagChunk to the given IntWriter.
     *
     * @param writer The IntWriter to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeEx(IntWriter writer) throws IOException {
        // Write the string index for the namespace URI.
        writer.write(stringIndex(null, namespace));
        // Write the string index for the element name.
        writer.write(stringIndex(null, name));
        // Write the starting position of the attributes data.
        writer.write(attributeStart);
        // Write the size of each attribute record.
        writer.write(attributeSize);
        // Write the number of attributes.
        writer.write((short) attributes.size());
		// Write the index of the ID attribute.
        writer.write(idAttributeIndex);
		// Write the index of the class attribute.
        writer.write(classAttributeIndex);
		// Write the index of the style attribute.
        writer.write(styleAttributeIndex);
		// Write each attribute data.
        for (AttrChunk attribute : attributes) {
            attribute.write(writer);
        }
    }
}

