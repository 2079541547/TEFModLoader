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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class IntReader {
    private boolean m_bigEndian;
    private int m_position;
    private InputStream m_stream;

    public IntReader() {
    }

    public IntReader(InputStream inputStream, boolean bigEndian) {
        reset(inputStream, bigEndian);
    }

    public final int available() throws IOException {
        return this.m_stream.available();
    }

    public final void close() {
        InputStream inputStream = this.m_stream;
        if (inputStream == null) {
            return;
        }
        try {
            inputStream.close();
        } catch (IOException e) {
        }
        reset(null, false);
    }

    public final int getPosition() {
        return this.m_position;
    }

    public final InputStream getStream() {
        return this.m_stream;
    }

    public final boolean isBigEndian() {
        return this.m_bigEndian;
    }

    public final int readByte() throws IOException {
        return readInt(1);
    }

    public final byte[] readByteArray(int length) throws IOException {
        byte[] array = new byte[length];
        int read = m_stream.read(array);
        m_position += read;
        if (read != length) {
            throw new EOFException();
        }
        return array;
    }

    public final int readInt() throws IOException {
        return readInt(4);
    }

    public final int readInt(int length) throws IOException {
        if (length < 0 || length > 4) {
            throw new IllegalArgumentException();
        }
        int result = 0;
        if (m_bigEndian) {
            for (int i = (length - 1) * 8; i >= 0; i -= 8) {
                int b = m_stream.read();
                if (b == -1) {
                    throw new EOFException();
                }
                m_position += 1;
                result |= (b << i);
            }
        } else {
            length *= 8;
            for (int i = 0; i != length; i += 8) {
                int b = m_stream.read();
                if (b == -1) {
                    throw new EOFException();
                }
                m_position += 1;
                result |= (b << i);
            }
        }
        return result;
    }

    public final void readIntArray(int[] array, int offset, int length) throws IOException {
        for (; length > 0; length -= 1) {
            array[offset++] = readInt();
        }
    }

    public final int[] readIntArray(int length) throws IOException {
        int[] array = new int[length];
        readIntArray(array, 0, length);
        return array;
    }

    public final int readShort() throws IOException {
        return readInt(2);
    }

    public final void reset(InputStream inputStream, boolean z) {
        this.m_stream = inputStream;
        this.m_bigEndian = z;
        this.m_position = 0;
    }

    public final void setBigEndian(boolean z) {
        this.m_bigEndian = z;
    }

    public final void skip(int bytes) throws IOException {
        if (bytes <= 0) {
            return;
        }
        long skip = this.m_stream.skip(bytes);
        this.m_position = (int) (this.m_position + skip);
        if (skip != bytes) {
            throw new EOFException();
        }
    }

    public final void skipInt() throws IOException {
        skip(4);
    }
}
