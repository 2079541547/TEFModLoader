package mt.modder.hub.axmlTools;

import java.io.IOException;
import java.io.OutputStream;

/**
 • A utility class for writing primitive data types as integers to an OutputStream.
 • Provides methods to write byte, short, char, and int values, handling endianness.
 */
public class IntWriter {

    private final OutputStream outputStream; // The underlying output stream to write to
    private final boolean bigEndian = false;  // Flag indicating if big-endian byte order is used (currently always false for little-endian)
    private int currentPosition = 0;  // Keeps track of the current position (number of bytes written)

    /**
     * Constructor for IntWriter.
     *
     * @param outputStream The OutputStream to write to.
     */
    public IntWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Writes a single byte to the output stream.
     *
     * @param b The byte to write.
     * @throws IOException If an I/O error occurs.
     */
    public void write(byte b) throws IOException {
        outputStream.write(b);
        currentPosition += 1;
    }

    /**
     * Writes a short value (2 bytes) to the output stream.
     * Handles endianness based on the 'bigEndian' flag. Currently only supports little-endian.
     *
     * @param s The short value to write.
     * @throws IOException If an I/O error occurs.
     */
    public void write(short s) throws IOException {
        if (!bigEndian) { // Little-endian
            outputStream.write(s & 0xff);           // Write the least significant byte
            outputStream.write((s >>> 8) & 0xff);    // Write the most significant byte
        } else { // Big-endian (Not implemented, throws Exception)
            outputStream.write((s >>> 8) & 0xff);   // Write the most significant byte
            outputStream.write(s & 0xff);          // Write the least significant byte
        }
        currentPosition += 2;
    }

    /**
     * Writes a char value (2 bytes) to the output stream by casting it to a short.
     *
     * @param x The char value to write.
     * @throws IOException If an I/O error occurs.
     */
    public void write(char x) throws IOException {
        write((short) x);
    }


    /**
     * Writes an integer value (4 bytes) to the output stream.
     * Handles endianness based on the 'bigEndian' flag. Currently only supports little-endian.
     *
     * @param x The integer value to write.
     * @throws IOException If an I/O error occurs.
     */
    public void write(int x) throws IOException {
		if (!bigEndian) { // Little-endian
            outputStream.write(x & 0xff);       // Write the least significant byte
            x >>>= 8;
            outputStream.write(x & 0xff);
            x >>>= 8;
            outputStream.write(x & 0xff);
            x >>>= 8;
            outputStream.write(x & 0xff);       // Write the most significant byte
        } else { // Big-endian
			outputStream.write((x >>> 24) & 0xff); // Write the most significant byte
            outputStream.write((x >>> 16) & 0xff);
            outputStream.write((x >>> 8) & 0xff);
            outputStream.write(x & 0xff);       // Write the least significant byte
        }
        currentPosition += 4;
    }
	

    /**
     * Closes the underlying OutputStream.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void close() throws IOException {
        outputStream.close();
    }

    /**
     * Gets the current position (number of bytes written).
     *
     * @return The number of bytes written so far.
     */
    public int getPosition() {
        return currentPosition;
    }
}
