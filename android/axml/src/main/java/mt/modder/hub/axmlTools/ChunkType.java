package mt.modder.hub.axmlTools;

/**
 • Enumerates the different types of chunks found in an Android binary XML (AXML) document.
 • Each constant represents a different type of data that can appear in an AXML file.
 */
enum ChunkType {

    /** Represents a null chunk, typically used for padding or as a placeholder. */
    Null(0x0000, 0xFFFF),
    /** Represents a chunk containing the string pool. */
    StringPool(0x0001, 0x1C),
    /** Represents the root XML chunk. */
    Xml(0x0003, 0x08),
    /** Represents the start of a namespace declaration. */
    XmlStartNamespace(0x0100, 0x10),
    /** Represents the end of a namespace declaration. */
    XmlEndNamespace(0x0101, 0x10),
    /** Represents the start of an XML element (tag). */
    XmlStartElement(0x0102, 0x10),
    /** Represents the end of an XML element (tag). */
    XmlEndElement(0x0103, 0x10),
    /** Represents a chunk for mapping resource IDs. */
    XmlResourceMap(0x0180, 0x08);

    /** The type identifier of the chunk. */
    public final short type;
    /** The size of the header for this chunk type, in bytes. */
    public final short headerSize;

    // Constructor to initialize type and headerSize
    ChunkType(int type, int headerSize) {
        this.type = (short) type;
        this.headerSize = (short) headerSize;
    }

}
