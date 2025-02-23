package mt.modder.hub.axmlTools;

import java.io.IOException;

/**
 * Represents the end of an XML element tag in a binary XML resource.
 * This chunk is always associated with a corresponding StartTagChunk.
 */
public class EndTagChunk extends Chunk<EndTagChunk.Header> {

    /**
     * Header for the EndTagChunk.
     * Extends Chunk.NodeHeader and defines the specific chunk type and size.
     */
    public class Header extends Chunk.NodeHeader {

        /**
         * Constructor for the Header.
         * Sets the chunk type to XmlEndElement and size to 24 bytes.
         */
        public Header() {
            super(ChunkType.XmlEndElement);
            this.size = 24;
        }
    }

    private final StartTagChunk startTag; // Reference to the corresponding StartTagChunk

    /**
     * Constructor for EndTagChunk.
     *
     * @param parent  The parent Chunk.
     * @param startTag The corresponding StartTagChunk.
     */
    public EndTagChunk(Chunk parent, StartTagChunk startTag) {
        super(parent);
        this.startTag = startTag;
    }

    /**
     * Writes the data of this EndTagChunk to the given IntWriter.
     * The data includes string indices for the namespace URI and element name from the corresponding StartTagChunk.
     *
     * @param writer The IntWriter to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeEx(IntWriter writer) throws IOException {
        // Write the string index for the namespace URI using the StartTagChunk's namespace string index
        writer.write(stringIndex(null, startTag.namespace));

        // Write the string index for the element name using the StartTagChunk's name string index
        writer.write(stringIndex(null, startTag.name));
    }
}
