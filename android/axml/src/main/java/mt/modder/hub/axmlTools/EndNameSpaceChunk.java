package mt.modder.hub.axmlTools;

import java.io.IOException;

/**
 * Represents the end of a namespace declaration in an XML chunk.
 * This chunk is paired with a corresponding StartNameSpaceChunk.
 */
public class EndNameSpaceChunk extends Chunk<EndNameSpaceChunk.Header> {

    private final StartNameSpaceChunk startNamespace; // Reference to the corresponding start namespace chunk

    /**
     * Header for the EndNameSpaceChunk.
     * Extends Chunk.NodeHeader and defines the specific chunk type and size.
     */
    public class Header extends Chunk.NodeHeader {
        /**
         * Constructor for the Header.
         * Sets the chunk type to XmlEndNamespace and size to 0x18 bytes.
         */
        public Header() {
            super(ChunkType.XmlEndNamespace);
            size = 0x18;
        }
    }

    /**
     * Constructor for EndNameSpaceChunk.
     *
     * @param parent       The parent Chunk.
     * @param startNamespace The corresponding StartNameSpaceChunk.
     */
    public EndNameSpaceChunk(Chunk parent, StartNameSpaceChunk startNamespace) {
        super(parent);
        this.startNamespace = startNamespace;
    }

    /**
     * Writes the data of this EndNameSpaceChunk to the given IntWriter.
     * This method delegates the writing of namespace information to the corresponding StartNameSpaceChunk.
     *
     * @param writer The IntWriter to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeEx(IntWriter writer) throws IOException {
        // Delegate writing to the corresponding StartNameSpaceChunk
        startNamespace.writeEx(writer);
    }
}

