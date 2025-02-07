package mt.modder.hub.axmlTools;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a resource map chunk in a binary XML resource.
 * This chunk contains a list of resource IDs.
 */
public class ResourceMapChunk extends Chunk<ResourceMapChunk.Header> {

    /**
     * Header for the ResourceMapChunk.
     * Extends Chunk.Header and defines the specific chunk type.
     */
    public class Header extends Chunk.Header {

        /**
         * Constructor for the Header.
         * Sets the chunk type to XmlResourceMap.
         */
        public Header() {
            super(ChunkType.XmlResourceMap);
        }

        /**
         * This is a placeholder method that does nothing as it has no extra header data.
         *
         * @param writer The IntWriter to write to.
         * @throws IOException If an I/O error occurs.
         */
        @Override
        public void writeEx(IntWriter writer) throws IOException {
            // No additional header data to write for ResourceMapChunk
        }
    }

    private LinkedList<Integer> resourceIds;  // List of resource IDs
    /**
     * Constructor for ResourceMapChunk.
     *
     * @param parent The parent Chunk.
     */
    public ResourceMapChunk(Chunk parent) {
        super(parent);
    }

    /**
     * Prepares the chunk before writing by extracting resource IDs from the StringPool.
     * Calculates and sets the correct size of the header and the data of the chunk.
     */
    @Override
    public void preWrite() {
        // Get the raw strings from the string pool chunk
        List<StringPoolChunk.RawString> rawStrings = stringPool().rawStrings;
        // Initialize the resourceIds list
        resourceIds = new LinkedList<>();

        // Loop over the strings in the string pool
        for (StringPoolChunk.RawString rawString : rawStrings) {
            // Check if the string's origin has a non-negative resource ID
            if (rawString.origin.id >= 0) {
                // Add the resource ID to the resourceIds list
                resourceIds.add(rawString.origin.id);
            } else {
                // If a string with a negative ID is found, it indicates the end of resource IDs,
                // so we break out of the loop.
                break;
            }
        }
		// Calculate and set the size of the header based on the collected ids
        header.size = resourceIds.size() * 4 + header.headerSize;
    }

    /**
     * Writes the resource IDs to the given IntWriter.
     *
     * @param writer The IntWriter to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeEx(IntWriter writer) throws IOException {
		// Loop through the resourceIds list
        for (int resourceId : resourceIds) {
			// Write each resource ID to the IntWriter
            writer.write(resourceId);
        }
    }
}
