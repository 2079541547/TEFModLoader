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

package mt.modder.hub.axmlTools.arsc;

import mt.modder.hub.arsc.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/*
Author @developer-krushna
Thanks to ChatGPT for helping me to explain if arsc file is obfuscated with random names
and also helped me to add comments for better understanding
*/
public class ResourceIdExtractor {
    private Map<String, String> idToNameCache = new HashMap<>();

    // Method to load and parse the ARSC file
	public void loadArscData(InputStream arsc) throws Exception {
		BinaryResourceFile resourceFile = BinaryResourceFile.fromInputStream(arsc);
		List<Chunk> chunks = resourceFile.getChunks();
		
		for (Chunk chunk : chunks) {
			if (chunk instanceof ResourceTableChunk) {
				ResourceTableChunk resourceTableChunk = (ResourceTableChunk) chunk;
				for (PackageChunk packageChunk : resourceTableChunk.getPackages()) {
					StringPoolChunk keyStringPool = packageChunk.getKeyStringPool();
					for (TypeChunk typeChunk : packageChunk.getTypeChunks()) {
						for (Map.Entry<Integer, TypeChunk.Entry> entry : typeChunk.getEntries().entrySet()) {
							BinaryResourceIdentifier binaryResourceIdentifier = BinaryResourceIdentifier.create(packageChunk.getId(), typeChunk.getId(), (int) entry.getKey());

							String hexId = binaryResourceIdentifier.toString();
							String resourceTypeName = typeChunk.getTypeName(); // Get resource type name directly

							// Extract data for different resource types
							String extractedData = extractResourceData(resourceTypeName, keyStringPool, entry, resourceTableChunk);

							// Cache the extracted data based on the hex ID
							idToNameCache.put(hexId, extractedData);
						}
					}
				}
			}
		}

	}

	private String extractResourceData(String resourceTypeName, StringPoolChunk keyStringPool, Map.Entry<Integer, TypeChunk.Entry> entry, ResourceTableChunk resourceTableChunk) {
		String extractedData = null;

		if (resourceTypeName.equals("attr") || resourceTypeName.equals("style") || resourceTypeName.equals("color")) {
			// Handle @attr or @style
			String key = keyStringPool.getString(entry.getValue().keyIndex());
			extractedData = resourceTypeName + "/" + key;
		} else {
			// Handle other resource types
			if (entry.getValue().value() == null || entry.getValue().value().data() > resourceTableChunk.getStringPool().getStringCount() || entry.getValue().value().data() < 0) {
				extractedData = null; // Handle invalid data
			} else {
				String key = keyStringPool.getString(entry.getValue().keyIndex());
				extractedData = resourceTypeName + "/" + key;
			}
		}

		return extractedData;
	}

    // Method to retrieve the name for a given hex ID
    public String getNameForHexId(String hexId) {
		return idToNameCache.get("0x" + hexId);
    }
	
}
