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

package mt.modder.hub.axmlTools.utils;

public interface AttributeSet {
    int getAttributeCount();

    String getAttributeName(int index);

    String getAttributeValue(int index);

    String getPositionDescription();

    int getAttributeNameResource(int index);

    int getAttributeListValue(int index, String options[], int defaultValue);

    boolean getAttributeBooleanValue(int index, boolean defaultValue);

    int getAttributeResourceValue(int index, int defaultValue);

    int getAttributeIntValue(int index, int defaultValue);

    int getAttributeUnsignedIntValue(int index, int defaultValue);

    float getAttributeFloatValue(int index, float defaultValue);

    String getIdAttribute();

    String getClassAttribute();

    int getIdAttributeResourceValue(int index);

    int getStyleAttribute();

    String getAttributeValue(String namespace, String attribute);

    int getAttributeListValue(String namespace, String attribute,
                              String options[], int defaultValue);

    boolean getAttributeBooleanValue(String namespace, String attribute,
                                     boolean defaultValue);

    int getAttributeResourceValue(String namespace, String attribute,
                                  int defaultValue);

    int getAttributeIntValue(String namespace, String attribute,
                             int defaultValue);

    int getAttributeUnsignedIntValue(String namespace, String attribute,
                                     int defaultValue);

    float getAttributeFloatValue(String namespace, String attribute,
                                 float defaultValue);

    // TODO: remove
    int getAttributeValueType(int index);

    int getAttributeValueData(int index);
}
