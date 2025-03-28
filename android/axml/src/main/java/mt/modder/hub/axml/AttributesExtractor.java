/*
 * Axml2xml - An Advanced Axml compiler
 * Copyright 2025, developer-krushna
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
 * DATA, OR PROFITS; OR BUSINESS NTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


 *     Please contact Krushna by email mt.modder.hub@gmail.com if you need
 *     additional information or have any questions
 */

package mt.modder.hub.axml;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*Idea from Jadx(By https://github.com/skylot/jadx)*/
public class AttributesExtractor {

	private static final String ATTR_XML = "/assets/attrs.xml";
	private static final String MANIFEST_ATTR_XML = "/assets/attrs_manifest.xml";

	private enum MAttrType {
		ENUM, FLAG
		}

	private static class MAttr {
		private final MAttrType type;
		private final Map<Long, String> values = new LinkedHashMap<>();

		public MAttr(MAttrType type) {
			this.type = type;
		}

		public MAttrType getType() {
			return type;
		}

		public Map<Long, String> getValues() {
			return values;
		}

		@NonNull
		@Override
		public String toString() {
			return "[" + type + ", " + values + ']';
		}
	}

	private final Map<String, MAttr> attrMap = new HashMap<>();

	private final Map<String, MAttr> appAttrMap = new HashMap<>();

	private static AttributesExtractor instance;

	public static AttributesExtractor getInstance() {
		if (instance == null) {
			try {
				instance = new AttributesExtractor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	private AttributesExtractor() {
		parseAll();
	}

	private void parseAll() {
		parse(loadXML(ATTR_XML));
		parse(loadXML(MANIFEST_ATTR_XML));

	}

	private Document loadXML(String xml) {
		Document doc;
		try {
			InputStream xmlStream = AttributesExtractor.class.getResourceAsStream(xml);
			if (xmlStream == null) {
				throw new RuntimeException(xml + " not found in classpath");
			}
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = dBuilder.parse(xmlStream);
		} catch (Exception e) {
			throw new RuntimeException("Xml load error, file: " + xml, e);
		}
		return doc;
	}

	private void parse(Document doc) {
		NodeList nodeList = doc.getChildNodes();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node node = nodeList.item(count);
			if (node.getNodeType() == Node.ELEMENT_NODE
				&& node.hasChildNodes()) {
				parseAttrList(node.getChildNodes());
			}
		}
	}

	private void parseAttrList(NodeList nodeList) {
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE
				&& tempNode.hasAttributes()
				&& tempNode.hasChildNodes()) {
				String name = null;
				NamedNodeMap nodeMap = tempNode.getAttributes();
				for (int i = 0; i < nodeMap.getLength(); i++) {
					Node node = nodeMap.item(i);
					if (node.getNodeName().equals("name")) {
						name = node.getNodeValue();
						break;
					}
				}
				if (name != null && tempNode.getNodeName().equals("attr")) {
					parseValues(name, tempNode.getChildNodes());
				} else {
					parseAttrList(tempNode.getChildNodes());
				}
			}
		}
	}

	private void parseValues(String name, NodeList nodeList) {
		MAttr attr = null;
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE && tempNode.hasAttributes()) {
				if (attr == null) {
					if (tempNode.getNodeName().equals("enum")) {
						attr = new MAttr(MAttrType.ENUM);
					} else if (tempNode.getNodeName().equals("flag")) {
						attr = new MAttr(MAttrType.FLAG);
					}
					if (attr == null) {
						return;
					}
					attrMap.put(name, attr);
				}
				NamedNodeMap attributes = tempNode.getAttributes();
				Node nameNode = attributes.getNamedItem("name");
				if (nameNode != null) {
					Node valueNode = attributes.getNamedItem("value");
					if (valueNode != null) {
						try {
							long key;
							String nodeValue = valueNode.getNodeValue();
							if (nodeValue.startsWith("0x")) {
								nodeValue = nodeValue.substring(2);
								key = Long.parseLong(nodeValue, 16);
							} else {
								key = Long.parseLong(nodeValue);
							}
							attr.getValues().put(key, nameNode.getNodeValue());
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public String decode(String attrName, long value) {
		MAttr attr = attrMap.get(attrName);
		if (attr == null) {
			attr = appAttrMap.get(attrName);
			if (attr == null) {
				return null;
			}
		}
		Log.d(attrName,"" + value);
		System.out.println(attrName+ " : " + value);
		if (attr.getType() == MAttrType.ENUM) {
			return attr.getValues().get(value);
		} else if (attr.getType() == MAttrType.FLAG) {
			List<String> flagList = new ArrayList<>();
			List<Long> attrKeys = new ArrayList<>(attr.getValues().keySet());
			attrKeys.sort((a, b) -> {
                return Long.compare(b, a); // for descending order
            });
			for (Long key : attrKeys) {
				String attrValue = attr.getValues().get(key);
				if (value == key) {
					flagList.add(attrValue);
					break;
				} else if ((key != 0) && ((value & key) == key)) {
					flagList.add(attrValue);
					value ^= key;
				}
			}

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < flagList.size(); i++) {
				if (i > 0) {
					sb.append("|");
				}
				sb.append(flagList.get(i));
			}
			return sb.toString();
		}
		return null;
	
	}
	
	public Long encode(String attrName, String valueString) {
		MAttr attr = attrMap.get(attrName);
		if (attr == null) {
			attr = appAttrMap.get(attrName);
			if (attr == null) {
				return null;
			}
		}

		if (attr.getType() == MAttrType.ENUM) {
			// Reverse lookup for ENUM
			for (Map.Entry<Long, String> entry : attr.getValues().entrySet()) {
				if (entry.getValue().equals(valueString)) {
					return entry.getKey();
				}
			}
		} else if (attr.getType() == MAttrType.FLAG) {
			// Handle FLAG encoding
			String[] flags = valueString.split("\\|");
			long encodedValue = 0;
			Map<String, Long> reverseMap = new HashMap<>();
            for (Map.Entry<Long, String> entry : attr.getValues().entrySet()) {
				reverseMap.put(entry.getValue(), entry.getKey());
			}
            for (String flag : flags) {
				if (reverseMap.containsKey(flag)) {
					encodedValue |= reverseMap.get(flag);
				} else {
					return null; // Handle not found flag
				}
			}
			return encodedValue;
		}
		return null;
	}
}

