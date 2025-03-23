
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


// Idea copied from : https://github.com/hsiafan/apk-parser

package mt.modder.hub.axmlTools.utils;

import java.util.*;
import java.io.*;

public class Attribute {
	
	public static class AttrIds {
		
		private static final Map<Integer, String> ids = loadSystemAttrIds();
		
		public static String getString(long id) {
			String value = ids.get((int) id);
			if (value == null) {
				value = "" + id;
			}
			return value;
		}
	}
	
	public static Map<Integer, String> loadSystemAttrIds() {
		try{
			BufferedReader reader = toReader("/assets/r_values.ini");
			Map<Integer, String> map = new HashMap<>();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] items = line.trim().split("=");
				if (items.length != 2) {
					continue;
				}
				String name = items[0].trim();
				Integer id = Integer.valueOf(items[1].trim());
				map.put(id, name);
			}
			return map;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static BufferedReader toReader(String path) {
		return new BufferedReader(new InputStreamReader(Attribute.class.getResourceAsStream(path)));
	}
	
}
