package mt.modder.hub.axmlTools;

import android.text.TextUtils;

import java.io.IOException;

import android.text.TextUtils;
import java.io.IOException;

// Represents an attribute within a start tag in an AXML document.
public class AttrChunk extends Chunk<Chunk.EmptyHeader> {

    // Reference to the StartTagChunk this attribute belongs to.
    private final StartTagChunk parentStartTagChunk;
    // The prefix for the attribute (e.g., "android" in android:name).
    public String attributePrefix;
    // The local name of the attribute (e.g., "name" in android:name).
    public String attributeName;
    // The namespace URI for the attribute (e.g., http://schemas.android.com/apk/res/android).
    public String attributeNamespace;
    // The raw string value of the attribute (before type conversion).
    public String attributeRawValue;

    // Constructor for AttrChunk
    public AttrChunk(StartTagChunk parentStartTagChunk) {
        super(parentStartTagChunk); // Call superclass constructor.
        this.parentStartTagChunk = parentStartTagChunk; // Set the parent StartTagChunk.
        header.size = 20; // Set the initial size of the header
    }

    // Holds the value of the attribute.
    public ValueChunk attributeValue = new ValueChunk(this);

    // Performs pre-write calculations for the attribute value.
    @Override
    public void preWrite() {
        attributeValue.calc(); // Calculates the value to be written
    }

    // Writes the attribute chunk to the output.
    @Override
    public void writeEx(IntWriter writer) throws IOException {
        // Write the index of the namespace URI. Use null if the namespace is empty
        writer.write(parentStartTagChunk.stringIndex(null, TextUtils.isEmpty(attributeNamespace) ? null : attributeNamespace));
        // Write the index of the attribute name in string pool of the parent chunk
        writer.write(parentStartTagChunk.stringIndex(attributeNamespace, attributeName));
        // Write rawValue index if the attribute type is string. Otherwise write -1 to indicate no string index
        if (attributeValue.valueType == 0x03)
            writer.write(parentStartTagChunk.stringIndex(null, attributeRawValue));
        else
            writer.write(-1);

        // Write the attribute value.
        attributeValue.write(writer);
    }
	
}
