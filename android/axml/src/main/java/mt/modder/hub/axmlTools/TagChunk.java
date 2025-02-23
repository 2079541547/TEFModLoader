package mt.modder.hub.axmlTools;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a tag (element) in an XML document, encompassing its start tag,
 * end tag, namespace declarations, and nested content. This chunk is part of the binary XML resource format.
 */
public class TagChunk extends Chunk<Chunk.EmptyHeader> {

    private final List<StartNameSpaceChunk> startNameSpaces; // List of namespace declarations for this tag
    private final StartTagChunk startTag;              // Represents the start tag of this element
    private final List<TagChunk> childTags = new LinkedList<>();  // Child tag elements present inside this tag
    private final EndTagChunk endTag;                   // Represents the end tag of this element
    public List<EndNameSpaceChunk> endNameSpaces;       // List of namespace un-declarations for this tag

    /**
     * Constructor for TagChunk.
     *
     * @param parent The parent Chunk. Can be either an XmlChunk or another TagChunk.
     * @param parser An XmlPullParser for parsing XML data.
     * @throws XmlPullParserException If an error occurs while parsing the XML.
     * @throws IllegalArgumentException If the parent is not an XmlChunk or a TagChunk.
     */
    public TagChunk(Chunk parent, XmlPullParser parser) throws XmlPullParserException {
        super(parent);

		// Add this TagChunk to its parent
        if (parent instanceof TagChunk) {
            ((TagChunk) parent).childTags.add(this);
        } else if (parent instanceof XmlChunk) {
            ((XmlChunk) parent).content = this; //Set it as child of XmlChunk
        } else {
            throw new IllegalArgumentException("Parent must be XmlChunk or TagChunk");
        }

        // Create a StartTagChunk and EndTagChunk for the current element.
        startTag = new StartTagChunk(this, parser);
        endTag = new EndTagChunk(this, startTag);
        startNameSpaces = startTag.startNamespaces; // Get the start namespace list.
        endNameSpaces = new LinkedList<>();

        // Create an EndNameSpaceChunk for each StartNameSpaceChunk.
        for (StartNameSpaceChunk startNamespace : startNameSpaces) {
			endNameSpaces.add(new EndNameSpaceChunk(this, startNamespace));
        }
        // Set the line number for both start and end tags.
        endTag.header.lineNumber = startTag.header.lineNumber = parser.getLineNumber();
    }

    /**
     * Prepares the chunk before writing by calculating the size of each chunk.
     */
    @Override
    public void preWrite() {
        int totalSize = 0;

        // Calculate size of all child chunks and add to total size
        for (StartNameSpaceChunk startNamespace : startNameSpaces) totalSize += startNamespace.calc();
        for (EndNameSpaceChunk endNamespace : endNameSpaces) totalSize += endNamespace.calc();
        totalSize += startTag.calc();
        totalSize += endTag.calc();
        for (TagChunk childTag : childTags) totalSize += childTag.calc();
        header.size = totalSize; // Set header size to overall size.
    }

    /**
     * Writes the data of this TagChunk to the given IntWriter.
     *
     * @param writer The IntWriter to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeEx(IntWriter writer) throws IOException {
		// Write all start namespace chunks.
        for (StartNameSpaceChunk startNamespace : startNameSpaces) startNamespace.write(writer);

        // Write the start tag.
        startTag.write(writer);

        // Write all child tags.
        for (TagChunk childTag : childTags) childTag.write(writer);

        // Write the end tag.
        endTag.write(writer);
		// Write all end namespace chunks.
        for (EndNameSpaceChunk endNamespace : endNameSpaces) endNamespace.write(writer);
    }
}
