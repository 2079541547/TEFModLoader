/*
 * AxmlPrinter - An Advanced Axml Printer available with proper xml style/format feature
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
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


 *     Please contact Krushna by email mt.modder.hub@gmail.com if you need
 *     additional information or have any questions
 */

package mt.modder.hub.axml;

import java.io.*;
import java.util.*;
import mt.modder.hub.axmlTools.*;
import mt.modder.hub.axmlTools.utils.*;
import org.xmlpull.v1.*;
import java.util.regex.*;
import mt.modder.hub.axmlTools.arsc.*;
import mt.modder.hub.axmlTools.utils.TypedValue;

import java.nio.charset.*;

/**
 * AXMLPrinter
 * A tool for decompiling Android binary XML files into human-readable XML format.
 * Author: @developer-krushna
 * Thanks to ChatGPT for adding comments. :)
 */
public final class AXMLPrinter {

	private static final String COPYRIGHT = "AXMLPrinter\nCopyright (C) developer-krushna [https://github.com/developer-krushna/](krushnachandramaharna57@gmail.com)\nThis project is distributed under the Apache License v2.0 license";

	// Constants for conversion factors and unit strings
	private static final float MANTISSA_MULT =
	1.0f / (1 << TypedValue.COMPLEX_MANTISSA_SHIFT);

    private static final float[] RADIX_MULTS = new float[]{
		1.0f * MANTISSA_MULT /* 0.00390625f */, 
		1.0f / (1 << 7) * MANTISSA_MULT /* 3.051758E-5f */,
		1.0f / (1 << 15) * MANTISSA_MULT /* 1.192093E-7f */, 
		1.0f / (1 << 23) * MANTISSA_MULT /* 4.656613E-10f */
    };
    private static final String[] DIMENSION_UNIT_STRS = new String[]{
		"px", "dp", "sp", "pt", "in", "mm"
    };
    private static final String[] FRACTION_UNIT_STRS = new String[]{
		"%", "%p"
    };
	

	private boolean enableId2Name = false;// Enable ID to name translation
	private boolean enableAttributeConversion = false; // Enable attribute value conversion

	private ResourceIdExtractor systemResourceFile = new ResourceIdExtractor(); // Handles system resource IDs
	private ResourceIdExtractor customResourceFile = new ResourceIdExtractor();// Handles custom resource IDs
	public String CUSTOM_ATTRIBUTE_TAG = "_Custom";
	public String SYSTEM_ATTRIBUTE_TAG = "_Systumm"; //😂
	public boolean isCustomResourceFileExist = false; // Indicates if a custom resource file exists
	public boolean isResObfuscationCheckDone = false;
	public boolean isResObfuscated = false;

	private final NamespaceChecker namespaceChecker = new NamespaceChecker();
	public String ANDROID_PREFIX = "android";
	public String APP_PREFIX = "app";
	public String AUTO_NAMESPACE = "http://schemas.android.com/apk/res-auto";
	public String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
	public boolean isExistAndroidNamespace = false;
	public boolean isAuto_NameSpaceExists = false;
	public String  RandomResAutoPrefix_Name = null;

	private Map<String, String> permissionInfoMap;
	private boolean isPermissionInfoLoaded = false;
	private String PERMISSION_TAG = "uses-permission";
	private boolean isExtractPermissionDescription = false;
	


	/**
     * Enables or disables ID-to-name conversion.
     * This allows translating hex IDs in XML to their corresponding resource names.
     *
     * @param enable Flag to enable or disable this feature.
     */
	public void setEnableID2Name(boolean enable) {
		enableId2Name = enable;
		if (enable) {
			try {
				// Load System resource file
				loadSystemResources();
			} catch (Exception e) {
				systemResourceFile = null;
				System.err.println("Failed to load system resources.");
			}
		}
	}


	/**
     * Enables or disables attribute value translation.
     * This feature interprets attribute values and converts them to human-readable formats.
     *
     * @param enable Flag to enable or disable this feature.
     */
	public void setAttrValueTranslation(boolean enable) {
		enableAttributeConversion = enable;
	}

	/**
     * Enables or disables permission description extraction from XML.
     *
     * @param enable Flag to enable or disable this feature.
     */
	public void setExtractPermissionDescription(boolean isExtract) {
		isExtractPermissionDescription = isExtract;
	}

	/**
     * Loads system resources from the bundled resources.arsc file.
     *
     * @throws Exception if the resource file cannot be loaded.
     */
    private void loadSystemResources() throws Exception {
        try (InputStream arscStream = AXMLPrinter.class.getResourceAsStream("/assets/resources.arsc")) {
            systemResourceFile.loadArscData(arscStream);
        }
    }

	/**
     * Reads a binary XML file and converts it into a human-readable XML string.
     *
     * @param path Path to the binary XML file.
     * @return The converted XML content as a string.
     * @throws Exception if an error occurs while reading the file.
     */
    public String readFromFile(String path) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        byte[] byteArray = new byte[fis.available()];
        fis.read(byteArray);
        fis.close();

        if (enableId2Name) {
            File resourceFile = new File(new File(path).getParentFile(), "resources.arsc");
            if (resourceFile.exists()) {
                try (InputStream arscStream = new FileInputStream(resourceFile)) {
                    customResourceFile.loadArscData(arscStream);
                    isCustomResourceFileExist = true;
                }
            }
        }

        // Convert the binary XML to readable XML
        return convertXml(byteArray);
    }
	
	// for MT Manager
	public void readProcessRes(String path) throws Exception {
		if (!path.endsWith(".xml")) {
			return;
		}
			File file = new File(path);
			String resourceFile = file.getParent() + "/resources.arsc";
			System.out.println(resourceFile);
			if (new File(resourceFile).exists()) {
				try {
					try (InputStream arscStream = new FileInputStream(resourceFile)) {
						customResourceFile.loadArscData(arscStream);
					}
					isCustomResourceFileExist = true;
				} catch (Exception e) {
					System.out.println(e);
					isCustomResourceFileExist = false;
				}
			}
	}


	/**
     * Converts a binary XML byte array into a readable XML string.
     *
     * @param byteArray The binary XML byte array.
     * @return The converted XML content as a string.
     */
	public String convertXml(byte[] byteArray) {
		System.out.println(COPYRIGHT);
		if (!enableId2Name) {
			try {
				loadSystemResources();
			} catch (Exception e) {
				systemResourceFile = null;
			}
		}
		try {
			// Initialize the XML parser with the byte array input
			AXmlResourceParser xmlParser = new AXmlResourceParser();
			xmlParser.open(new ByteArrayInputStream(byteArray));
			StringBuilder indentation = new StringBuilder();
			StringBuilder xmlContent = new StringBuilder();
			boolean isManuallyAddedAndroidNamespace = false;
			while (true) {
				int eventType = xmlParser.next();
				int attributeCount = xmlParser.getAttributeCount(); // count attributes
				if (eventType == XmlPullParser.END_DOCUMENT) {
					// End of document
					String result = xmlContent.toString();
					xmlParser.close();
					return result;
				}
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						// Append XML declaration at the start of the document
						xmlContent.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
						xmlContent.append("<!-- This is a modified version of AXMLPrinter2(By Google) library. Check out how many changes are made at https://github.com/developer-krushna/AXMLPrinter by (@developer-krushna) -->\n");
						break;

					case XmlPullParser.START_TAG:
						// Handle the start of a new XML tag
						if (xmlParser.getPrevious().type == XmlPullParser.START_TAG) {
							xmlContent.append(">\n");
						}

						String prefix = xmlParser.getName();

						//check if the user-permission prefix found (We know its available only in AndroidManifest.xml)
						if (isExtractPermissionDescription) {
							if (prefix.contains(PERMISSION_TAG)) {
								if (!isPermissionInfoLoaded) {
									//load permissionInfo one time only
									permissionInfoMap = loadPermissionsInfo();
									isPermissionInfoLoaded = true;
								}
								//extract permission description from corresponding permissionName
								if (attributeCount > 0) {
									for (int i = 0; i < attributeCount; i++) {
										String permissionName = xmlParser.getAttributeValue(i);
										String description = permissionInfoMap.get(permissionName);
										if (description != null) {
											// Print permission description
											xmlContent.append(indentation).append("<!-- ").append(description).append(" -->\n");
										}
									}
								}
							}
						}

						xmlContent.append(String.format("%s<%s%s", 
						                                indentation, 
														getMainNodeNamespacePrefix(xmlParser.getPrefix()), 
														prefix));
						indentation.append("    ");

						// Handle namespaces
						int depth = xmlParser.getDepth();
						int namespaceStart = xmlParser.getNamespaceCount(depth - 1);
						int namespaceEnd = xmlParser.getNamespaceCount(depth);
						
						//xmlContent.append(indentation).append("<!-- ").append(namespaceStart).append(" ").append(namespaceEnd).append(" -->");
						for (int i = namespaceStart; i < namespaceEnd; i++) {
							String namespaceFormat = (i == namespaceStart) ? "%sxmlns:%s=\"%s\"" : "\n%sxmlns:%s=\"%s\"";
							String nameSpacePrefix = xmlParser.getNamespacePrefix(i);
							String nameSpaceUri = xmlParser.getNamespaceUri(i);
							if(nameSpaceUri.equals(AUTO_NAMESPACE)){
								isAuto_NameSpaceExists = true;
							}
							if(nameSpaceUri.equals(AUTO_NAMESPACE) && !nameSpacePrefix.equals("app")){
								RandomResAutoPrefix_Name = nameSpacePrefix;
								nameSpacePrefix = "app";
							}
							
							xmlContent.append(String.format(namespaceFormat, 
															(i == namespaceStart) ? " " : indentation, 
															nameSpacePrefix, 
															nameSpaceUri));
							isExistAndroidNamespace = true; // make it true as it completed the above task
							
						}

						// if NamespaceUri failed to extract then add this manually
					    if (!isExistAndroidNamespace && !isManuallyAddedAndroidNamespace) {
							String namespaceFormat = "%sxmlns:%s=\"%s\"";
							// Search android namespace if exist then add
							if(containsSequence(byteArray, ANDROID_NAMESPACE.getBytes(StandardCharsets.UTF_8)) || prefix.equals("manifest")){
								xmlContent.append(String.format(namespaceFormat, " ", ANDROID_PREFIX, ANDROID_NAMESPACE));
								isManuallyAddedAndroidNamespace = true;
							}
							// Search auto namespace if exist then add
							if(containsSequence(byteArray, AUTO_NAMESPACE.getBytes(StandardCharsets.UTF_8))){
								xmlContent.append("\n");
								xmlContent.append(String.format(namespaceFormat, indentation, APP_PREFIX, AUTO_NAMESPACE));
								isAuto_NameSpaceExists = true;
							}
							isExistAndroidNamespace = false; // false because we add this mannualy
						}
							
						// Handle attributes
						if (attributeCount > 0) {
							if (attributeCount == 1) {
								xmlContent.append("");
								for (int i = 0; i < attributeCount; i++) {
									// Skip attributes with a dot (.)
									if (xmlParser.getAttributeName(i).contains(".")) {
										continue; // Skip this attribute if its name contains a dot
									}

									String attributeFormat = (i == attributeCount - 1) ? "%s%s%s=\"%s\"" : "%s%s%s=\"%s\"\n";
									String attributeName = getAttributeName(xmlParser, i);
									String attributeValue = getAttributeValue(xmlParser, i);
									// Final Addition of namespace , attribute along with its corresponding value
									int valueSize = attributeValue.codePointCount(0, attributeValue.length());
									if(valueSize <= 14 || prefix.equals(PERMISSION_TAG)){
										// Indention is not needed because it has 1 attribute only its main node
										xmlContent.append(String.format(attributeFormat, 
																		" ", 
																		getAttrNamespacePrefix(xmlParser, i, attributeName), 
																		attributeName.replaceAll(CUSTOM_ATTRIBUTE_TAG, "").replaceAll(SYSTEM_ATTRIBUTE_TAG, ""), 
																		attributeValue));
									} else {
										xmlContent.append('\n');
										xmlContent.append(String.format(attributeFormat, 
																		indentation, 
																		getAttrNamespacePrefix(xmlParser, i, attributeName), 
																		attributeName.replaceAll(CUSTOM_ATTRIBUTE_TAG, "").replaceAll(SYSTEM_ATTRIBUTE_TAG, ""), 
																		attributeValue));
									}
								}
							} else {
							    xmlContent.append('\n');
								for (int i = 0; i < attributeCount; i++) {
									// Skip attributes with a dot (.)
									if (xmlParser.getAttributeName(i).contains(".")) {
										continue; // Skip this attribute if its name contains a dot
									}

									String attributeFormat = (i == attributeCount - 1) ? "%s%s%s=\"%s\"" : "%s%s%s=\"%s\"\n";
									String attributeName = getAttributeName(xmlParser, i); //Attribute name
									// Final Addition of namespace , attribute along with its corresponding value
									// Indention is needed because it 2 or more attributes

									xmlContent.append(String.format(attributeFormat, 
									                                indentation, 
																	getAttrNamespacePrefix(xmlParser, i, attributeName), 
																	attributeName.replaceAll(CUSTOM_ATTRIBUTE_TAG, "").replaceAll(SYSTEM_ATTRIBUTE_TAG, ""), 
																	getAttributeValue(xmlParser, i)));

								}
							}
						}

						break;

					case XmlPullParser.END_TAG:
						// Handle the end of an XML tag
						indentation.setLength(indentation.length() - "    ".length());
						if (!isEndOfPrecededXmlTag(xmlParser, xmlParser.getPrevious())) {

							xmlContent.append(String.format("%s</%s%s>\n", 
															indentation, 
															getMainNodeNamespacePrefix(xmlParser.getPrefix()), 
															xmlParser.getName()));
						} else {
							xmlContent.append(" />\n");
						}
						break;

					case XmlPullParser.TEXT:
						// Handle text within an XML tag
						if (xmlParser.getPrevious().type == XmlPullParser.START_TAG) {
							xmlContent.append(">\n");
						}
						xmlContent.append(String.format("%s%s\n", 
						                                indentation, 
														xmlParser.getText()));
						break;
				}
			}
		} catch (Exception e) {
			// Handle exceptions and return the stack trace
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String exceptionDetails = sw.toString();
			return "----StackTrace----\n" + exceptionDetails;
		}
	}

	/**
     * Retrieves the value of an attribute based on its type.
     * This method processes attribute types like strings, references, dimensions, fractions, etc.,
     * and returns their human-readable representation.
     *
     * @param xmlParser The XML parser instance.
     * @param index     The index of the attribute to retrieve.
     * @return A formatted string representing the attribute value.
     */
	private String getAttributeValue(AXmlResourceParser xmlParser, int index) {

		String attributeName = getAttributeName(xmlParser, index).replaceAll(CUSTOM_ATTRIBUTE_TAG, "").replaceAll(SYSTEM_ATTRIBUTE_TAG, "");
		// String attributeName = getAttributeName(xmlParser, index);

		int attributeValueType = xmlParser.getAttributeValueType(index);

		int attributeValueData = xmlParser.getAttributeValueData(index);

		switch (attributeValueType) {
			case TypedValue.TYPE_STRING /* 3 */:
				// String value

				String stringValue = xmlParser.getAttributeValue(index);
				// Preserve newlines as \n for XML
				return stringValue.replace("\n", "\\n");

			case TypedValue.TYPE_ATTRIBUTE /* 2 */:
				// Resource ID
				if (enableId2Name) {
				    return "?" + extractResourecID(attributeValueData);
				} else {
				    return "?" + formatToHex(attributeValueData);
				}

			case TypedValue.TYPE_REFERENCE /* 1 */:
				// Reference
				if (enableId2Name) {
					return "@" + extractResourecID(attributeValueData);
				} else {
					return "@" + formatToHex(attributeValueData);
				}

			case TypedValue.TYPE_FLOAT /* 4 */:
				// Float value
				return Float.toString(Float.intBitsToFloat(attributeValueData));

			case TypedValue.TYPE_INT_HEX /* 17 */:
				// Hex integer value or flag values
				if (enableAttributeConversion) {
					String decodedValue = AttributesExtractor.getInstance().decode(attributeName, attributeValueData);
					if (decodedValue != null && !decodedValue.isEmpty()) {
						return decodedValue; // Return the decoded value if found
					} else {
						return "0x" + Integer.toHexString(attributeValueData);
					}
				} else {
					return "0x" + Integer.toHexString(attributeValueData);
				}

			case TypedValue.TYPE_INT_BOOLEAN /* 18 */:
				// Boolean value
				return attributeValueData != 0 ? "true" : "false";

			case TypedValue.TYPE_DIMENSION /* 5 */:
				// Dimension value
			    return formatDimension(attributeValueData);

			case TypedValue.TYPE_FRACTION /* 6 */:
				// Fraction value
				return formatFraction(attributeValueData);
				
			default:
				// Handle enum or flag values and other cases 
				if (enableAttributeConversion) {
					String decodedValue = AttributesExtractor.getInstance().decode(attributeName, attributeValueData);
					if (decodedValue != null) {
						return decodedValue; // Return the decoded value if found
					}
				}
				// For unhandled types or cases
				String result;
				if (attributeValueType >= TypedValue.TYPE_FIRST_COLOR_INT && attributeValueType <= TypedValue.TYPE_LAST_COLOR_INT) {
					// Condition 1: attributeValueType is a color type (0x1c-0x1f)
					result = String.format("#%08x", attributeValueData);
				} else if (attributeValueType >= 0x10 && attributeValueType <= TypedValue.TYPE_LAST_INT) {
					// Condition 2: attributeValueType is in the general integer range but not the color range(0x10-0x1f, but not 0x1c-0x1f)
					result = String.valueOf(attributeValueData);
				} else {
					// Condition 3: All other cases,
					result = String.format("<0x%X, type 0x%02X>", attributeValueData, attributeValueType);
				}

				return result;
		}
	}
	
	
	

	// Checks if the current XML tag is the end of the previous tag
	private  boolean isEndOfPrecededXmlTag(AXmlResourceParser xmlParser, AXmlResourceParser.PrecededXmlToken precededXmlToken) {
		return precededXmlToken.type == XmlPullParser.START_TAG &&
			xmlParser.getEventType() == XmlPullParser.END_TAG &&
			xmlParser.getName().equals(precededXmlToken.name) &&
			((precededXmlToken.namespace == null && xmlParser.getPrefix() == null) ||
			(precededXmlToken.namespace != null && xmlParser.getPrefix() != null && xmlParser.getPrefix().equals(precededXmlToken.namespace)));
	}

	// Retrieves the main node namespace prefix if it exists
	private  String getMainNodeNamespacePrefix(String prefix) {
		return (prefix == null || prefix.length() == 0) ? "" : prefix + ":";
	}

	// Retrieves the attribute namespace prefix if it exists
	private String getAttrNamespacePrefix(AXmlResourceParser xmlParser, int position, String attributeName) {
		String namespace = xmlParser.getAttributePrefix(position);
		int attributeNameResource = xmlParser.getAttributeNameResource(position);
		
		if (attributeName.contains(CUSTOM_ATTRIBUTE_TAG)) {
			// Check if auto namespace exists or not
			if(isAuto_NameSpaceExists){
				return APP_PREFIX + ":";
			}
			return "";
			// check if any unknown attributes are found and it will start from "id"
		} else if (isUnknownAttribute(attributeName)) {
		    return "";
		} else if (attributeName.contains(SYSTEM_ATTRIBUTE_TAG)) {
			return ANDROID_PREFIX + ":";
		} else if (namespace.isEmpty()) {
			if (xmlParser.isChunkResourceIDs || !isExistAndroidNamespace) {
				if (namespaceChecker.isAttributeExist(attributeName)) {
					return "";
				} else {
					if(attributeNameResource == 0){
					    return "";
					} 
					if(checkIfCustomAttribute(attributeNameResource)){
						return APP_PREFIX + ":";
					} else {
						return ANDROID_PREFIX + ":";
					}
				}
			}
			return "";
		}
		//check if the is res-auto nameSpaceUri's prefix name is random
		if(RandomResAutoPrefix_Name != null && namespace.equals(RandomResAutoPrefix_Name)){
			return "app" + ":";
		}
		return namespace + ":";
	}

	/**
     * Extracts the attribute name dynamically based on system or custom resources.
     *
     * @param xmlParser The XML parser instance.
     * @param index     The index of the attribute to retrieve.
     * @return The attribute name as a string.
     */
	public String getAttributeName(AXmlResourceParser xmlParser, int index) {
		String attributeName = xmlParser.getAttributeName(index);
		int attributeNameResource = xmlParser.getAttributeNameResource(index);

		//check if the attributes are encrypted with attribute hex id 
		if (xmlParser.isChunkResourceIDs || isUnknownAttribute(attributeName)) {
			try {
				String extractedName = getAttributeNameFromResources(attributeName.replace("id", ""));
				return extractedName != null ? extractedName.replaceAll("attr/", "") : getFallbackAttributeName(attributeName);
			} catch (Exception e) {
				return getFallbackAttributeName(attributeName);
			}
		} else {
			// Again ceck if custom res file is exist or not so that we can even extract more precise attribute data
			if(isCustomResourceFileExist){
				try {
					String extractedName = getAttributeNameFromCustomRes(Integer.toHexString(attributeNameResource), attributeName);
					return extractedName != null ? extractedName.replaceAll("attr/", "") : attributeName;
				} catch (Exception e) {
					return attributeName;
				}
			}
			return attributeName;
		}
	}
	
	
	private String getAttributeNameFromCustomRes(String attribute_hexId, String attributeName) throws Exception {
		String nameForHexId;
		if (this.isCustomResourceFileExist && (nameForHexId = customResourceFile.getNameForHexId(attribute_hexId)) != null) {
			if(!isResObfuscationCheckDone){
				String nextIdNameToCheckObfuscation = customResourceFile.getNameForHexId(generateNextHexId(attribute_hexId));
				if(nextIdNameToCheckObfuscation != null){
					if(nameForHexId.equals(nextIdNameToCheckObfuscation)){
						isResObfuscationCheckDone = true;
						isResObfuscated = true;
						return attributeName; //normal attribute
					} else{
						return CUSTOM_ATTRIBUTE_TAG + nameForHexId;
					}
				} else {
					return  attributeName;
				}
			}

			//finally check of res file is obfuscated or not
			if(!isResObfuscated){
				return CUSTOM_ATTRIBUTE_TAG + nameForHexId;
			} else {
				return attributeName;
			}
		}
		return attributeName;
	}
	

	// Get attribute name from either system resource file or custom resource file
	private String getAttributeNameFromResources(String attribute_hexId) throws Exception {
		String systemAttribute = systemResourceFile.getNameForHexId("0" + attribute_hexId);
		String extractedAttributeName = null;
		String nameForHexId;
		if (systemAttribute != null) {
			extractedAttributeName = SYSTEM_ATTRIBUTE_TAG + systemAttribute;
		}
        // Process custom resource file if exist and also check if the system resource file don't have target hex id
		if (this.isCustomResourceFileExist && extractedAttributeName == null && (nameForHexId = customResourceFile.getNameForHexId(attribute_hexId)) != null) {
			//check if res file is obfuscated or not
			if(!isResObfuscationCheckDone){
				String nextIdNameToCheckObfuscation = customResourceFile.getNameForHexId(generateNextHexId(attribute_hexId));
				if(nextIdNameToCheckObfuscation != null){
					if(nameForHexId.equals(nextIdNameToCheckObfuscation)){
						isResObfuscationCheckDone = true;
						isResObfuscated = true;
						return "id" + attribute_hexId;
					} else{
						return CUSTOM_ATTRIBUTE_TAG + nameForHexId;
					}
				} else {
					return "id" + attribute_hexId;
				}
			}

			//finally check of res file is obfuscated or not
			if(!isResObfuscated){
				return CUSTOM_ATTRIBUTE_TAG + nameForHexId;
			} else {
				return "id" + attribute_hexId;
			}

        }

		return extractedAttributeName;
	}
	

    //check the without namespace based specific attributes if matched
	private String getFallbackAttributeName(String attributeName) {
		if (namespaceChecker.isAttributeExist(attributeName)) {
			return attributeName;
		} else if (attributeName != null && attributeName.startsWith("id")) {
			return attributeName;
		} else {
			return "id" + attributeName;
		}
	}

	/**
     * Determines if an attribute name matches an unknown or obfuscated pattern.
     *
     * @param attributeName The name of the attribute to check.
     * @return True if the attribute is unknown or obfuscated, false otherwise.
     */
	public boolean isUnknownAttribute(String attributeName) {
        return attributeName.matches("^id\\d[a-z0-9]*$");
    }

	/**
     * Extracts the resource name based on its hexadecimal ID.
     * If the resource name cannot be found, the hexadecimal ID is returned.
     *
     * @param resourceId The resource ID to extract.
     * @return The resource name or its hexadecimal representation.
     */
	public String extractResourecID(int resourceId) {
		String resHexId = formatToHex(resourceId);
		String systemId2Name = null;
		String customResId2Name = null;
		try {
			if (enableId2Name) {
				// Load system resource file
				systemId2Name = systemResourceFile.getNameForHexId(resHexId);

				// If System don't have the id then lets move to custom resource file 
				if (isCustomResourceFileExist && systemId2Name == null) {
					customResId2Name = customResourceFile.getNameForHexId(resHexId);
				}
				// If id name is extracted from system resource then add "android:" before the attribute name
				if (systemId2Name != null) {
					return ANDROID_PREFIX + ":" + systemId2Name;
				}
				// Check if the custom id2name is not null .. and return the entry name without name space
				if (customResId2Name != null) {
					//check if res file is obfuscated or not
					if(!isResObfuscationCheckDone){
						String nextIdNameToCheckObfuscation = customResourceFile.getNameForHexId(generateNextHexId(resHexId));
						if(nextIdNameToCheckObfuscation != null){
							if(customResId2Name.equals(nextIdNameToCheckObfuscation)){
								isResObfuscationCheckDone = true;
								isResObfuscated = true;
								return resHexId;
							} else{
								return customResId2Name;
							}
						} else {
							return resHexId;
						}
					}
					
					//finally check of res file is obfuscated or not
					if(!isResObfuscated){
					  return customResId2Name;
					} else {
						return resHexId;
					}
				}
				return resHexId;
			} else {
				return resHexId;
			}
		} catch (Exception e) {
			return resHexId;
		}
	}
	
	
	//Load manifest permission description
	private Map<String, String> loadPermissionsInfo() throws Exception {
		Map<String, String> map = new HashMap<>();
		InputStream is = AXMLPrinter.class.getResourceAsStream("/assets/permissions_info_en.txt");
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String permission = null;
		String description = null;
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			// If the line is empty, we can skip it
			if (line.isEmpty()) {
				continue;
			}
			// match the permission and description with regex
			if (line.matches("^[a-zA-Z0-9._]+$")) {
				// If there's an existing permission and description, store it in the map
				if (permission != null && description != null) {
					map.put(permission, description);
				}
				// Now the new permission starts
				permission = line;
				description = null;
			} else {
				// If the line is a description, append it to the current description
				if (description != null) {
					description += " " + line;
				} else {
					description = line;
				}
			}
		}
		// Add the last permission entry to the map
		if (permission != null && description != null) {
			map.put(permission, description);
		}

		return map;
	}
	
	/**
     * Converts an integer to a hexadecimal string format.
     *
     * @param value The integer value to convert.
     * @return The formatted hexadecimal string.
     */
	public String formatToHex(int i) {
        return String.format("%08x", Integer.valueOf(i));
    }
	
	/* This method helped us to search and detect if a specific text is exists or not*/
	private  boolean containsSequence(byte[] source, byte[] sequence) {
		if (sequence.length == 0 || source.length == 0 || sequence.length > source.length) {
			return false;
		}
		for (int i = 0; i <= source.length - sequence.length; i++) {
			boolean found = true;
			for (int j = 0; j < sequence.length; j++) {
				if (source[i + j] != sequence[j]) {
					found = false;
					break;
				}
			}
			if (found) {
				return true;
			}
		}
		return false;
	}
	
	/* check if any custom attributes are used for xml ui, 
	  * Generally custom attribute dec ids are 10 digits unlike system which have 8 digits
	  * And also custom atributes dec ids are started with 2 unlike system which generally 1
	  * This can help us wheather it should use app: namespace or not
	*/
	public boolean checkIfCustomAttribute(int number) {
        String numberStr = String.valueOf(number);
        int digitCount = numberStr.length();
        char firstDigitChar = numberStr.charAt(0);
        int firstDigit = Character.getNumericValue(firstDigitChar);
        if (digitCount == 10 || firstDigit ==  2) {
            return true;
        } 
		return false;
    }
	/**
	 * Formats a dimension value (e.g., pixels, dp, sp).
	 *
	 * @param attributeValueData The raw attribute value data (as an int).
	 * @return A string representation of the dimension, including the unit.
	 */
    private String formatDimension(int attributeValueData) {
        // Convert the attribute data to a float value.
        float floatValue = TypedValue.complexToFloat(attributeValueData);

        // Extract the unit from the attribute value data using a bitwise AND to get the unit index (0-15)
        String unit = DIMENSION_UNIT_STRS[attributeValueData & 15];

        // Format the float value and append the unit to create the final output string.
        return formatFloat(floatValue) + unit;
    }


    /**
     * Formats a fraction value (e.g., percentage).
     *
     * @param attributeValueData The raw attribute value data (as an int).
     * @return A string representation of the fraction, including the unit.
     */
    private String formatFraction(int attributeValueData) {
        // Convert the attribute data to a float value, and then converts it to percentage value by multiplying with 100
        float floatValue = TypedValue.complexToFloat(attributeValueData) * 100.0f;

		// Extract the unit from the attribute value data using a bitwise AND to get the unit index (0-15).
        String unit = FRACTION_UNIT_STRS[attributeValueData & 15];

        // Format the float value and append the unit to create the final output string
        return formatFloat(floatValue) + unit;
    }

    /**
     * Formats a float value to a string representation.
     *
     * @param floatValue The float value to format.
     * @return A string representation of the float, either as an integer if possible or with one decimal place.
     */
    private String formatFloat(float floatValue) {
		// Check if the float value is equivalent to an integer value
		if (floatValue == (int) floatValue) {
            // If it's an integer, return its String value without decimal places
            return String.valueOf((int) floatValue);
        } else {
            // If it has a decimal part, format it to one decimal place
            return String.format(Locale.US, "%.1f", floatValue);
        }
    }

    /**
     * Extracts the text after the last slash in a given string.
     *
     * @param text The input string to process.
     * @return The text after the last slash, or the original text if no slash is found.
     */
    private String textAfterSlash(String text) {
        // Define the regex pattern to match any characters followed by a slash, and capture the text after the last slash
        Pattern pattern = Pattern.compile(".*/(.*)");
        // Create a Matcher object to perform the matching operation
        Matcher matcher = pattern.matcher(text);

        // Check if the regex pattern matches the given input text
        if (matcher.find()) {
            // If a match is found, extract the captured text which is the text after the last slash
            String textAfterSlash = matcher.group(1);
            return textAfterSlash;
        }
		// If no match is found return the original text
        return text;
    }
	/**
     * Generates the next sequential hexadecimal ID.
     *
     * @param inputHex The input hexadecimal string (without "0x" prefix).
     * @return The next sequential hexadecimal ID as a string. Returns inputHex if parsing fails.
     */
    public String generateNextHexId(String inputHexID) {
		try {
			// Parse the input hex string as a long (base 16)
            long hexValue = Long.parseLong(inputHexID, 16);

			// Increment the long value
            hexValue++;

            // Format the incremented value back to a hex string (8 digits)
            return formatToHex((int)hexValue);
        }
        catch(NumberFormatException e){
			// If there's a NumberFormatException (invalid input), return original value.
            return inputHexID;
        }
    }
	
}
