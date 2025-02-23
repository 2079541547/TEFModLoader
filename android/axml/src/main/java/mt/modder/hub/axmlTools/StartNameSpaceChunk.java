package mt.modder.hub.axmlTools;

import java.io.IOException;

/**
 * Represents the start of a namespace declaration in an XML chunk.
 * This chunk stores the prefix and URI of the namespace.
 */
public class StartNameSpaceChunk extends Chunk<StartNameSpaceChunk.Header> {

    public String namespacePrefix; // Namespace prefix
    public String namespaceUri;    // Namespace URI

    /**
     * Constructor for StartNameSpaceChunk.
     *
     * @param parent The parent Chunk.
     */
    public StartNameSpaceChunk(Chunk parent) {
        super(parent);
    }

    /**
     * Header for the StartNameSpaceChunk.
     * Extends Chunk.NodeHeader and defines the specific chunk type and size.
     */
    public class Header extends Chunk.NodeHeader {
        /**
         * Constructor for the Header.
         * Sets the chunk type to XmlStartNamespace and size to 0x18 bytes.
         */
        public Header() {
            super(ChunkType.XmlStartNamespace);
            size = 0x18;
        }
    }
    /**
     * Sets the namespace prefix.
     *
     * @param prefix The namespace prefix to set.
     */
    public void setNamespacePrefix(String prefix) {
        this.namespacePrefix = prefix;
    }
    /**
     * Sets the namespace URI.
     *
     * @param uri The namespace URI to set.
     */
    public void setNamespaceUri(String uri) {
        this.namespaceUri = uri;
    }

    /**
     * Writes the data of this StartNameSpaceChunk to the given IntWriter.
     * The data includes string indices for the namespace prefix and URI.
     *
     * @param writer The IntWriter to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeEx(IntWriter writer) throws IOException {
        // Write the string index for the namespace prefix
        writer.write(stringIndex(null, namespacePrefix));

        // Write the string index for the namespace URI
        writer.write(stringIndex(null, namespaceUri));
    }
}
