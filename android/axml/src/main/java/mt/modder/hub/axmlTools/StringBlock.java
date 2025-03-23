/*
* AxmlPrinter - An Advanced Axml Printer available with proper xml style/format feature
* Copyright 2024, developer-krushna
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are
* met:
*
*     * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above
* copyright notice, this list of conditions and the following disclaimer
* in the documentation and/or other materials provided with the
* distribution.
*     * Neither the name of developer-krushna nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
* A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
* OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
* DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
* THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


*     Please contact Krushna by email mt.modder.hub@gmail.com if you need
*     additional information or have any questions
*/

package mt.modder.hub.axmlTools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.nio.*;
import mt.modder.hub.axmlTools.escaper.*;

public class StringBlock {
	private static final int CHUNK_TYPE = 0x001C0001; // Type identifier for the chunk
	public static final int UTF8_FLAG = 0x00000100; // Flag for UTF-8 encoding
	private boolean isUTF8; // Flag to indicate if the strings are UTF-8 encoded
	private int[] stringOffsets; // Offsets for the start of each string
	private int[] strings; // Array containing the actual string data
	private int[] styleOffsets; // Offsets for the start of each style
	private int[] styles; // Array containing the style data


	private StringBlock() {
	}

	// Retrieves a byte from an int array at a specified index
	private static int getByte(int[] array, int index) {
		return (array[index / 4] >>> ((index % 4) * 8)) & 255;
	}

	// Converts a segment of an int array into a byte array
	private static byte[] getByteArray(int[] array, int offset, int length) {
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = (byte) getByte(array, offset + i);
		}
		return bytes;
	}

	// Determines the size of the length field based on the encoding
	private int getLengthFieldSize(int[] array, int offset) {
		if (!this.isUTF8) {
			return (32768 & getShort(array, offset)) != 0 ? 4 : 2;
		}
		int size = (getByte(array, offset) & 128) != 0 ? 2 + 1 : 2;
		return (getByte(array, offset) & 128) != 0 ? size + 1 : size;
	}

	// Retrieves a short value from an int array at a specified offset
	private static final int getShort(int[] array, int offset) {
		int value = array[offset / 4];
		if ((offset % 4) / 2 == 0) {
			return (value & 0xFFFF);
		} else {
			return (value >>> 16);
		}
	}

	// Retrieves the length of a string from the array
	private int getStringLength(int[] array, int offset) {
		if (!this.isUTF8) {
			int value = getShort(array, offset);
			if ((32768 & value) != 0) {
				return getShort(array, offset + 2) | ((value & 32767) << 16);
			}
			return value;
		}
		if ((getByte(array, offset) & 128) != 0) {
			offset++;
		}
		int nextOffset = offset + 1;
		int byte1 = getByte(array, nextOffset);
		return (byte1 & 128) != 0 ? ((byte1 & 127) << 8) | getByte(array, nextOffset + 1) : byte1;
	}

	// Retrieves the style array for a given index
	private int[] getStyle(int index) {
		if (styleOffsets == null || styles == null || index >= styleOffsets.length) {
			return null;
		}
		int offsetIndex = styleOffsets[index] / 4;
		int count = 0;
		int offsetIndex2 = offsetIndex;
		while (true) {
			if (offsetIndex2 >= styles.length || styles[offsetIndex2] == -1) {
				break;
			}
			count++;
			offsetIndex2++;
		}
		if (count == 0 || count % 3 != 0) {
			return null;
		}
		int[] style = new int[count];
		int offsetIndex3 = offsetIndex;
		int styleIndex = 0;
		while (true) {
			if (offsetIndex3 >= styles.length || styles[offsetIndex3] == -1) {
				break;
			}
			style[styleIndex++] = styles[offsetIndex3++];
		}
		return style;
	}

	// Reads a StringBlock from the given IntReader
	public static StringBlock read(IntReader intReader) throws IOException {
		ChunkUtil.readCheckType(intReader, CHUNK_TYPE); // Check the chunk type
		int chunkSize = intReader.readInt(); // Total size of the chunk
		int stringCount = intReader.readInt(); // Number of strings
		int styleCount = intReader.readInt(); // Number of styles
		int flags = intReader.readInt(); // Flags (including UTF-8 flag)
		int stringDataOffset = intReader.readInt(); // Offset to string data
		int stylesOffset = intReader.readInt(); // Offset to styles data

		StringBlock stringBlock = new StringBlock();
		stringBlock.isUTF8 = (flags & UTF8_FLAG) != 0; // Determine if strings are UTF-8 encoded
		stringBlock.stringOffsets = intReader.readIntArray(stringCount); // Read string offsets
		if (styleCount != 0) {
			stringBlock.styleOffsets = intReader.readIntArray(styleCount); // Read style offsets
		}

		int stringDataSize = (stylesOffset == 0 ? chunkSize : stylesOffset) - stringDataOffset;
		if (stringDataSize % 4 == 0) {
			stringBlock.strings = intReader.readIntArray(stringDataSize / 4); // Read string data
			if (stylesOffset != 0) {
				int stylesDataSize = chunkSize - stylesOffset;
				if (stylesDataSize % 4 != 0) {
					throw new IOException("Style data size is not multiple of 4 (" + stylesDataSize + ").");
				}
				stringBlock.styles = intReader.readIntArray(stylesDataSize / 4); // Read styles data
			}
			return stringBlock;
		}
		throw new IOException("String data size is not multiple of 4 (" + stringDataSize + ").");
	}

	// Finds the index of a string in the string block
	public int find(String str) {
		if (str == null) {
			return -1;
		}
		for (int i = 0; i < stringOffsets.length; i++) {
			int offset = stringOffsets[i];
			int length = getShort(strings, offset);
			if (length == str.length()) {
				int j = 0;
				while (j != length) {
					offset += 2;
					if (str.charAt(j) != getShort(strings, offset)) {
						break;
					}
					j++;
				}
				if (j == length) {
					return i;
				}
			}
		}
		return -1;
	}

	// Gets the string at the specified index
	public CharSequence get(int index) {
		return getString(index);
	}

	// Gets the count of strings in the string block
	public int getCount() {
		if (stringOffsets != null) {
			return stringOffsets.length;
		}
		return 0;
	}

	// Gets the HTML representation of the string at the specified index
	public String getHTML(int index) {
		String str = getString(index);
		if (str == null) {
			return null;
		}
		int[] style = getStyle(index);
		if (style == null) {
			return str;
		}
		StringBuilder htmlBuilder = new StringBuilder(str.length() + 32);
		int currentIndex = 0;
		while (true) {
			int nextStyleIndex = -1;
			for (int i = 0; i < style.length; i += 3) {
				if (style[i + 1] != -1 && (nextStyleIndex == -1 || style[nextStyleIndex + 1] > style[i + 1])) {
					nextStyleIndex = i;
				}
			}
			int nextStylePosition = nextStyleIndex != -1 ? style[nextStyleIndex + 1] : str.length();
			for (int i = 0; i < style.length; i += 3) {
				int end = style[i + 2];
				if (end != -1 && end < nextStylePosition) {
					if (currentIndex <= end) {
						htmlBuilder.append(str, currentIndex, end + 1);
						currentIndex = end + 1;
					}
					style[i + 2] = -1;
					htmlBuilder.append("</").append(getString(style[i])).append('>');
				}
			}
			if (currentIndex < nextStylePosition) {
				htmlBuilder.append(str, currentIndex, nextStylePosition);
				currentIndex = nextStylePosition;
			}
			if (nextStyleIndex == -1) {
				return htmlBuilder.toString();
			}
			htmlBuilder.append('<').append(getString(style[nextStyleIndex])).append('>');
			style[nextStyleIndex + 1] = -1;
		}
	}

	// Retrieves the string at the specified index
	public String getString(int index) {
		if (index < 0 || stringOffsets == null || index >= stringOffsets.length) {
			return null;
		}
		int offset = stringOffsets[index];
		int length = getStringLength(strings, offset);
		int lengthFieldSize = offset + getLengthFieldSize(strings, offset);
		Charset charset = this.isUTF8 ? StandardCharsets.UTF_8 : StandardCharsets.UTF_16LE;
		if (!this.isUTF8) {
			length <<= 1;
		}
		String originalString = new String(getByteArray(strings, lengthFieldSize, length), 0, length, charset);
		return XmlEscaper.escapeXml10(originalString);
	}


}

