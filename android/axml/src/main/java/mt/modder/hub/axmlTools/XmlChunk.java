package mt.modder.hub.axmlTools;

import android.content.Context;

import java.io.IOException;

/**
 * Represents the root XML chunk in a binary XML resource.
 * This chunk is the starting point of the binary XML structure, encompassing string pool,
 * resource map, and the root tag of XML file.
 */
public class XmlChunk extends Chunk<XmlChunk.Header> {

    private final ResourceMapChunk resourceMap; // Resource map chunk containing a list of resource IDs
    public final StringPoolChunk stringPool;  // String pool chunk containing a list of all unique strings used in the xml resource.
    public TagChunk content;                 // Root tag chunk, i.e., root element of the XML file.
    private final Context context;     // android context used to resolve resource ids.

    /**
     * Constructor for XmlChunk.
     *
     * @param context The Android Context to be used for resolving resource identifiers.
     */
    public XmlChunk(Context context) {
        super(null); // XML chunk doesn't have a parent.
        this.context = context;
        this.resourceMap = new ResourceMapChunk(this);
        this.stringPool = new StringPoolChunk(this);
    }

    /**
     * Header for the XmlChunk.
     * Extends Chunk.Header and defines the specific chunk type.
     */
    public class Header extends Chunk.Header {
        /**
         * Constructor for the Header.
         * Sets the chunk type to Xml.
         */
        public Header() {
            super(ChunkType.Xml);
        }

        /**
         * This is a placeholder method as it has no extra header data
         *
         * @param writer The IntWriter to write to.
         * @throws IOException If an I/O error occurs.
         */
        @Override
        public void writeEx(IntWriter writer) throws IOException {
            // No additional header data to write for XmlChunk
        }
    }

    /**
     * Prepares the chunk before writing by calculating the total size of all child chunks.
     */
    @Override
    public void preWrite() {
		// The total size includes header size, string pool size, resource map size, and content size.
        header.size = header.headerSize + content.calc() + stringPool.calc() + resourceMap.calc();
    }

	/**
	 * Writes the data of this XmlChunk to the given IntWriter.
	 *
	 * @param writer The IntWriter to write to.
	 * @throws IOException If an I/O error occurs.
	 */
    @Override
    public void writeEx(IntWriter writer) throws IOException {
        // Write the string pool chunk.
        stringPool.write(writer);
		// Write the resource map chunk.
        resourceMap.write(writer);
        // Write the content (root tag) chunk.
        content.write(writer);
    }

    /**
     * Returns the root XmlChunk of the binary xml.
     * @return returns the `XmlChunk` itself.
     */
    @Override
    public XmlChunk root() {
        return this;
    }

    private ReferenceResolver referenceResolver;

    /**
     * Gets the ReferenceResolver used to resolve resource references.
     * If no resolver is set, a `DefaultReferenceResolver` is created and returned.
     * @return The `ReferenceResolver` instance.
     */
    @Override
    public ReferenceResolver getReferenceResolver() {
        if (referenceResolver == null) referenceResolver = new DefaultReferenceResolver();
        return referenceResolver;
    }

    /**
     * Returns the current `Context` associated with the `XmlChunk`.
     * @return The `Context` instance.
     */
    @Override
    public Context getContext() {
        return context;
    }

}
