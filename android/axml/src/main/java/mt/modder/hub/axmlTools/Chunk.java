package mt.modder.hub.axmlTools;

import android.content.Context;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

// Represents a generic chunk of data in a binary AXML document.
public abstract class Chunk<H extends Chunk.Header> {

    // Indicates whether the size of this chunk has been calculated.
    private boolean isSizeCalculated = false;
    // Android context used by this chunk.
    protected Context context;
    // Parent chunk to this chunk. Null if this is root.
    private final Chunk parentChunk;
    // Header for this chunk.
    public H header;

    // Abstract base class for chunk headers.
    public abstract static class Header {
        // Chunk type identifier.
        short chunkType;
        // Size of the header in bytes.
        short headerSize;
        // Full size of the chunk (including header and data).
        int size;

        // Constructor to set type and header size.
        public Header(ChunkType chunkType){
			this.chunkType = chunkType.type;
			this.headerSize = chunkType.headerSize;
        }

        // Writes the header to output stream.
        public void write(IntWriter writer) throws IOException {
            writer.write(chunkType);
            writer.write(headerSize);
            writer.write(size);
            writeEx(writer);
        }

        // Writes the specific data portion of the header to stream.
        public abstract void writeEx(IntWriter writer) throws IOException;
    }

    // Abstract base class for chunk headers that are associated with nodes.
    public abstract static class NodeHeader extends Header{

        // Line number of this node
        public int lineNumber=1;
        // Comment associated with node.
        public int commentIndex=-1;

        // Constructor that sets the header size and calls parent constructor
        public NodeHeader(ChunkType chunkType) {
            super(chunkType);
            headerSize = 0x10;
        }

        // Writes the node header data to the output stream.
        @Override
        public void write(IntWriter writer) throws IOException {
            writer.write(chunkType);
            writer.write(headerSize);
            writer.write(size);
            writer.write(lineNumber);
            writer.write(commentIndex);
            writeEx(writer);
        }

        // Extension point for child classes to write additional header data.
        @Override
        public void writeEx(IntWriter writer) throws IOException {
        }
    }

    // Represents an empty chunk header.
    public class EmptyHeader extends Header {
        //Constructor
        public EmptyHeader() {
            super(ChunkType.Null);
        }
        // Method stub to write extended header data. No operation required here.
        @Override
        public void writeEx(IntWriter writer) throws IOException {
        }
        // Method stub to write header data. No operation required here.
        @Override
        public void write(IntWriter writer) throws IOException {
        }
    }

	// Constructor for Chunk
    public Chunk(Chunk parentChunk) {
        this.parentChunk = parentChunk;
        try {
            // Dynamically get the header type parameter
            Class<H> headerClass = (Class<H>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Constructor<?>[] constructors = headerClass.getConstructors();
            // Iterate over all available constructors
            for (Constructor<?> constructor : constructors) {
                Type[] paramTypes = constructor.getParameterTypes();
                // Look for a constructor that takes a Chunk as a parameter.
                if (paramTypes.length == 1 && Chunk.class.isAssignableFrom((Class<?>) paramTypes[0]))
				// Create a new instance of the header
                    header = (H) constructor.newInstance(this);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	// Writes the entire chunk, including header and data to stream.
    public void write(IntWriter writer) throws IOException {
        int currentPos = writer.getPosition(); // Get current writer position
        calc(); // Calculate chunk size
        header.write(writer); // Write the header
        writeEx(writer);  // Write the specific data portion of the chunk
        // Assertion to check if chunk size matches the size written
        assert writer.getPosition() - currentPos == header.size : (writer.getPosition() - currentPos) + " instead of " + header.size + " bytes were written:" + getClass().getName();
    }

    // Returns the parent chunk of this chunk
    public Chunk getParent() {
        return parentChunk;
    }
    // Returns the Android context.
    public Context getContext() {
		// If this chunk has context, return it, otherwise try to get parent's context.
        if (context != null) return context;
        return getParent().getContext();
    }

    // Calculates the size of the chunk before writing
    public int calc() {
        // If the chunk size has not been calculated.
        if (!isSizeCalculated) {
            preWrite(); // perform any pre write operations.
            isSizeCalculated = true; // set flag as calculated
        }
        return header.size;
    }

    // Returns root chunk
    public XmlChunk root() {
        return getParent().root();
    }

    // Method to get the string pool index for the string.
    public int stringIndex(String namespace, String str) {
        return stringPool().stringIndex(namespace, str);
    }

    // Method to get string pool chunk.
    public StringPoolChunk stringPool() {
        return root().stringPool;
    }

    // Method to get the reference resolver
    public ReferenceResolver getReferenceResolver() {
        return root().getReferenceResolver();
    }

    // Method for pre writing operations, can be implemented in child classes.
    public void preWrite() {}
	// Abstract method for child classes to write chunk specific data.
    public abstract void writeEx(IntWriter writer) throws IOException;
}

