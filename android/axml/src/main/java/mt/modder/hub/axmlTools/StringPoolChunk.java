package mt.modder.hub.axmlTools;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a string pool chunk in a binary XML resource.
 * This chunk stores a collection of unique strings used throughout the resource file.
 */
public class StringPoolChunk extends Chunk<StringPoolChunk.Header> {

    ArrayList<RawString> rawStrings;     // List of raw string data
    int[] styleOffsets;                   // Array of offsets for style information (currently unused)

    /**
     * Represents the encoding type used in the string pool.
     */
    public enum Encoding {
        UNICODE,    // Unicode encoding
        UTF8        // UTF-8 encoding
		}

    private int[] stringOffsets;        // Array of offsets for each string in the pool
    Encoding encoding = Encoding.UNICODE; // Default encoding for the string pool

    /**
     * Constructor for StringPoolChunk.
     *
     * @param parent The parent Chunk.
     */
    public StringPoolChunk(Chunk parent) {
        super(parent);
    }

    /**
     * Header for the StringPoolChunk.
     * Extends Chunk.Header and defines the specific chunk type and size.
     */
    public class Header extends Chunk.Header {
        public int stringCount;          // Number of strings in the pool
        public int styleCount;           // Number of styles in the pool (currently unused)
        public int flags;                // Flags related to the string pool (e.g. encoding)
        public int stringPoolOffset;     // Offset to the start of string data in the pool
        public int stylePoolOffset;      // Offset to the start of style data in the pool (currently unused)

		/**
		 * Constructor for Header, sets chunk type to StringPool.
		 */
        public Header() {
            super(ChunkType.StringPool);
        }

		/**
		 * Writes the header data to the given IntWriter.
		 *
		 * @param writer The IntWriter to write to.
		 * @throws IOException If an I/O error occurs.
		 */
        @Override
        public void writeEx(IntWriter writer) throws IOException {
            writer.write(stringCount);
            writer.write(styleCount);
            writer.write(flags);
            writer.write(stringPoolOffset);
            writer.write(stylePoolOffset);
        }
    }


	/**
	* Represents a raw string entry in the string pool.
	* It stores character data (for Unicode strings) or byte data (for UTF-8 strings),
	* along with metadata such as length and padding.
	*/
	public static class RawString {

		StringItem origin;        // The original StringItem from which this RawString is derived.
		char[] charData;          // Character data for Unicode strings.
		byte[] byteData;          // Byte data for UTF-8 encoded strings.


		/**
		 * Calculates the length of the string represented by this raw string.
		 *
		 * @return The length of the string.
		 */
		int length() {
			if (charData != null) return charData.length; // Returns the length of char data for unicode strings
			return origin.string.length(); // Returns the length of the original string for UTF8 encoded strings.
		}


		/**
		 * Calculates the required padding size based on the type of string encoding (Unicode or UTF-8).
		 *
		 * @return The number of padding bytes needed.
		 */
		int padding() {
			if (charData != null) {
				return (charData.length * 2) & 3;  // For Unicode, padding is based on char data length.
			}else{
				return byteData.length & 3;     // For UTF-8, padding is based on byte data length.
			}
		}

		/**
		 * Calculates the total size of the raw string data including padding.
		 *
		 * @return The total size in bytes.
		 */
		int size() {
			if (charData != null) {
				return charData.length * 2 + 4 + padding(); // For Unicode: length * 2 (for each char) + 4 (size + null terminator) + padding
			} else {
				return byteData.length + 3 + padding();     // For UTF-8: length + 3 (size + null terminator) + padding
			}
		}

		/**
		 * Writes the raw string data to the given IntWriter.
		 *
		 * @param writer The IntWriter to write to.
		 * @throws IOException If an I/O error occurs.
		 */
		void write(IntWriter writer) throws IOException {
			int startPosition = writer.getPosition();
			if (charData != null) {  // For Unicode strings
				writer.write((short) length());        // Write the string length
				for (char c : charData) writer.write(c); // Write each character of the string.
				writer.write((short) 0);                // Write the null terminator.
				if (padding() == 2) writer.write((short) 0); // Write padding if needed.
			} else {    // For UTF-8 strings
				writer.write((byte) length());        // Write the length of the string.
				writer.write((byte) byteData.length); // Write the length of byte data
				for (byte c : byteData) writer.write(c); // write the data.
				writer.write((byte) 0);                 // Write null terminator.
				int paddingSize = padding();
				for (int i = 0; i < paddingSize; ++i) writer.write((byte) 0); // Write padding bytes if needed.
			}
			assert size() == writer.getPosition() - startPosition : size() + "," + (writer.getPosition() - startPosition);
		}
	}

    /**
     * Prepares the chunk before writing by populating the raw string list and calculating offsets.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void preWrite() {
		rawStrings = new ArrayList<>();
		LinkedList<Integer> offsets = new LinkedList<>();
        int currentOffset = 0;
        int i = 0;

        // Populate the raw strings based on the selected encoding type.
        if (encoding == Encoding.UNICODE) {
			for (LinkedList<StringItem> stringItems : map.values()) {
                for (StringItem stringItem : stringItems) {
					RawString rawString = new RawString();
                    rawString.charData = stringItem.string.toCharArray();
                    rawString.origin = stringItem;
                    rawStrings.add(rawString);
                }
            }
        } else { // UTF-8
            for (LinkedList<StringItem> stringItems : map.values()) {
                for (StringItem stringItem : stringItems) {
					RawString rawString = new RawString();
                    rawString.byteData = stringItem.string.getBytes(StandardCharsets.UTF_8);
                    rawString.origin = stringItem;
                    rawStrings.add(rawString);
                }
            }
        }

		// Sort raw strings based on their id values.
        Collections.sort(rawStrings, new Comparator<RawString>() {
				@Override
				public int compare(RawString lhs, RawString rhs) {
					int l = lhs.origin.id;
					int r = rhs.origin.id;
					if (l == -1) l = Integer.MAX_VALUE;
					if (r == -1) r = Integer.MAX_VALUE;
					return l - r;
				}
			});


        // Calculate string offsets
        for (RawString rawString : rawStrings) {
            offsets.add(currentOffset);
            currentOffset += rawString.size();
        }

        // Setting header properties
        header.stringCount = rawStrings.size();
        header.styleCount = 0;
        header.size = currentOffset + header.headerSize + header.stringCount * 4 + header.styleCount * 4;
        header.stringPoolOffset = offsets.size() * 4 + header.headerSize;
        header.stylePoolOffset = 0;
        stringOffsets = new int[offsets.size()];

        if (encoding == Encoding.UTF8) header.flags |= 0x100;
        // Save string offsets to array.
        for (int offset:offsets) stringOffsets[i++] = offset;

        styleOffsets = new int[0];  // No styles supported for now
    }

    /**
     * Writes the string pool data to the given IntWriter.
     *
     * @param writer The IntWriter to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeEx(IntWriter writer) throws IOException {
		// Write string offsets.
        for (int offset : stringOffsets) writer.write(offset);
		// Write style offsets.
        for (int offset : styleOffsets) writer.write(offset);
		// Write raw string data.
        for (RawString rawString : rawStrings) rawString.write(writer);
    }


    /**
     * Represents a string item, containing the actual string value.
     */
    public class StringItem {
        public String namespace;   // Namespace of the string (can be null)
        public String string;     // The actual string value
        public int id = -1;         // Resource ID for the string, initially -1

        /**
         * Constructor for StringItem when string doesn't have a namespace.
         *
         * @param string The string value.
         */
        public StringItem(String string) {
            this.string = string;
            this.namespace = null;
        }

        /**
         * Constructor for StringItem with specified namespace and string
         *
         * @param namespace The namespace of the string
         * @param string The string value.
         */
        public StringItem(String namespace, String string) {
            this.string = string;
            this.namespace = namespace;
            genId(); // generate id when string with namespace is being created
        }

        /**
         * Sets the namespace for this string item and generates resource id.
         *
         * @param namespace The namespace to set.
         */
        public void setNamespace(String namespace) {
            this.namespace = namespace;
            genId(); //generate id when string is being added with name space.
        }

        /**
         * Generates a resource ID for the string if a valid namespace is set.
         */
        @SuppressLint("DiscouragedApi")
        public void genId() {
            if (namespace == null) return; // return if no namespace.

            // Extract package from namespace uri.
            String pkg = "http://schemas.android.com/apk/res-auto".equals(namespace) ? getContext().getPackageName() :
				namespace.startsWith("http://schemas.android.com/apk/res/") ? namespace.substring("http://schemas.android.com/apk/res/".length()) : null;
            if (pkg == null) return; // return if invalid package name.
            id = getContext().getResources().getIdentifier(string, "attr", pkg);
        }
    }

    private final HashMap<String, LinkedList<StringItem>> map = new HashMap<>(); // Map of strings to string items list.

    /**
     * String pre-processing method.
     *
     * @param s The string to pre-process.
     * @return The processed string (currently returns the original string).
     */
    private String preHandleString(String s) {
        return s; // This method is a placeholder for pre-processing of string data.
    }

    /**
     * Adds a string to the string pool without a namespace.
     *
     * @param s The string to add.
     */
    public void addString(String s) {
        s = preHandleString(s); // preprocess before adding it to the string pool.
		LinkedList<StringItem> list = map.get(s);
		// check for existing string before creating a new one.
        if (list == null) map.put(s, list = new LinkedList<>());
        if (!list.isEmpty()) return; // return if string exists.
        StringItem item = new StringItem(s);
        list.add(item);
    }

    /**
     * Adds a string to the string pool with a namespace.
     *
     * @param namespace The namespace of the string.
     * @param s        The string to add.
     */
    public void addString(String namespace, String s) {
        namespace = preHandleString(namespace);  // preprocess before adding it to the string pool.
        s = preHandleString(s); // preprocess before adding it to the string pool.
		LinkedList<StringItem> list = map.get(s); // get list of existing string items from the string pool.
		// create a new list if it doesn't exist.
        if (list == null) map.put(s, list = new LinkedList<>());
        // if string with the namespace exists, add namespace if needed else return.
        for (StringItem e : list) if (e.namespace == null || e.namespace.equals(namespace)) {
				e.setNamespace(namespace);
				return;
			}
        // If the same string is not present with the given namespace, a new string item is created and added to list.
		StringItem item = new StringItem(namespace, s);
        list.add(item);
    }


    /**
     * Returns the index of a string in the string pool, based on the given namespace and string.
     *
     * @param namespace The namespace of the string (can be null).
     * @param s        The string to find.
     * @return The index of the string, or -1 if not found.
     * @throws RuntimeException if the string was not found in the pool.
     */
    @Override
    public int stringIndex(String namespace, String s) {
        namespace = preHandleString(namespace); //preprocess namespace.
        s = preHandleString(s); // preprocess the string before lookup
        if (s == null) return -1; // return -1 if string is null.
        int l = rawStrings.size(); // get the size of the raw string list
        for (int i = 0; i < l; ++i) {
            StringItem item = rawStrings.get(i).origin;
            // compare the string and namespace for string item at current index
            if (s.equals(item.string) && (TextUtils.isEmpty(namespace) || namespace.equals(item.namespace))) return i;
        }
		// if the string is empty return -1
        if (TextUtils.isEmpty(s)) return -1;
		// throw exception if the string was not found in the string pool.
        throw new RuntimeException("String: '" + s + "' not found");
    }
}

