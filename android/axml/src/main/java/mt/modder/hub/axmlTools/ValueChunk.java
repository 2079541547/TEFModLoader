package mt.modder.hub.axmlTools;


import android.graphics.Color;
import android.os.Build;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a value chunk in a binary XML resource, containing the value of an attribute.
 * This chunk stores the data type and the actual data.
 */
public class ValueChunk extends Chunk<Chunk.EmptyHeader> {

    private final AttrChunk attributeChunk; // Reference to the attribute chunk associated with this value
    private String stringValue; // The string value if the type is string
    short valueSize = 8;        // Size of the value chunk in bytes.
    byte reserved = 0;         // Reserved byte (unused).
    byte valueType = -1;        // Data type of the value.
    int valueData = -1;        // Actual value data.
	
	private static final int RADIX_0 = 0;
	private static final int RADIX_1 = 1;
	private static final int RADIX_2 = 2;
	private static final int RADIX_3 = 3;
    private static final int MANTISSA_SHIFTS = 8;
    private static final int RADIX_SHIFT = 4;
	

	/**
     * Represents a pair of a matched position and value.
     * Used for parsing different types of values using regex.
     */
    static class ValuePositionPair {
        int position;     // The position of the match group
        String value;  // The actual value of the matched group.

		/**
		 * Constructor for ValuePositionPair.
		 * Extracts matched value and group position from matcher.
		 * @param matcher Matcher instance from which value and group index is to be retrieved.
		 */
        public ValuePositionPair(Matcher matcher) {
            int groupCount = matcher.groupCount();
			// Loop through each captured group to find a non-null group.
            for (int i = 1; i <= groupCount; ++i) {
                String matchedValue = matcher.group(i);
                if (matchedValue == null || matchedValue.isEmpty()) continue;
                this.position = i;
                this.value = matchedValue;
                return;
            }
			// if no match found
            this.position = -1;
            this.value = matcher.group();
        }
    }

	// Pattern to check for explicit types like !string!, !null!
    private final Pattern explicitTypePattern = Pattern.compile("^!(?:(string|str|null|)!)?(.*)");
    // Regex pattern for parsing different types of resource values
    private final Pattern typesPattern = Pattern.compile(("^(?:" +
														 "(@null)" + // Check for @null.
														 "|(@\\+?(?:\\w+:)?\\w+/\\w+|@(?:\\w+:)?[0-9a-zA-Z]+)" +  // check for references like @drawable/icon or @+id/text
														 "|(true|false)" +  // Check for true/false
														 "|([-+]?\\d+)" + //Check for integers.
														 "|(0x[0-9a-zA-Z]+)" + // Check for Hexadecimal values.
														 "|([-+]?\\d+(?:\\.\\d+)?)" +// check for floats.
														 "|([-+]?\\d+(?:\\.\\d+)?(?:dp|dip|in|px|sp|pt|mm))" + // check for dimension values.
														 "|([-+]?\\d+(?:\\.\\d+)?(?:%))" +  // Check for fraction/percent values.
														 "|(\\#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8}))" + // Check for color values.
														 "|(match_parent|wrap_content|fill_parent)" + // Check for layout param
														 ")$").replaceAll("\\s+", ""));


    /**
     * Constructor for ValueChunk.
     *
     * @param attributeChunk The parent AttrChunk.
     */
    public ValueChunk(AttrChunk attributeChunk) {
        super(attributeChunk);
        header.size = 8;
        this.attributeChunk = attributeChunk;
    }


    /**
     * Prepares the value chunk before writing by evaluating the attribute value.
     */
    @Override
    public void preWrite() {
        evaluate();
    }

    /**
     * Writes the value data to the given IntWriter.
     *
     * @param writer The IntWriter to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeEx(IntWriter writer) throws IOException {
		// Write the size of the value chunk.
        writer.write(valueSize);
        // Write the reserved byte.
        writer.write(reserved);
		// If the type is string, resolve the string index and store in valueData.
        if (valueType == 0x03) {
			valueData = stringIndex(null, stringValue);
        }
		// Write the type and data of the value chunk.
        writer.write(valueType);
        writer.write(valueData);
    }


    /**
     * Evaluates the value of the attribute based on its type and formats.
     *
     * @param value The value of the dimension string (e.g. 10dp, 12sp etc).
     * @return returns the complex int value of the dimension.
     * @throws RuntimeException Throws exception for invalid dimension format.
     */
    public int evaluateComplex(String value) {
		int unit;
        int radix;
        int base;
        String number;
		// Determine the unit type and get the number portion.
        if (value.endsWith("%")) {
			number = getNumber(value, 1);
            unit = TypedValue.COMPLEX_UNIT_FRACTION;
        } else if (value.endsWith("dp")) {
            unit = TypedValue.COMPLEX_UNIT_DIP;
			number = getNumber(value, 2);
        } else if (value.endsWith("dip")) {
            unit = TypedValue.COMPLEX_UNIT_DIP;
			number = getNumber(value, 3);
        } else if (value.endsWith("sp")) {
            unit = TypedValue.COMPLEX_UNIT_SP;
			number = getNumber(value, 2);
        } else if (value.endsWith("px")) {
            unit = TypedValue.COMPLEX_UNIT_PX;
			number = getNumber(value, 2);
        } else if (value.endsWith("pt")) {
            unit = TypedValue.COMPLEX_UNIT_PT;
			number = getNumber(value, 2);
        } else if (value.endsWith("in")) {
			number = getNumber(value, 2);
            unit = TypedValue.COMPLEX_UNIT_IN;
        } else if (value.endsWith("mm")) {
            unit = TypedValue.COMPLEX_UNIT_MM;
			number = getNumber(value, 2);
        } else {
            throw new RuntimeException("Invalid unit"); // more descriptive exception is recommended
        }
        try{
            double doubleValue = Double.parseDouble(number.trim()); // Handle leading and trailing spaces
			// Apply scaling based on value range
			if (doubleValue < 1 && doubleValue > -1) {
				base = (int) (doubleValue * (1 << 23)); // Scaling with 2^23
				radix = RADIX_3;
			}
            else if (doubleValue < 0x100 && doubleValue > -0x100) {
                base = (int) (doubleValue * (1 << 15)); // Scaling with 2^15
				radix = RADIX_2;
            }
			else if (doubleValue < 0x10000 && doubleValue > -0x10000) {
				base = (int) (doubleValue * (1 << 7)); // Scaling with 2^7
				radix = RADIX_1;
            } else {
				base = (int) doubleValue; // use integer part as a base.
				radix = RADIX_0; // No scaling.
			}
        }
		catch (NumberFormatException e) {
			// Use custom exception here instead of runtime exception to make it more descriptive.
			throw new RuntimeException("Invalid number format in complex value: " + value,e);
		}
		return (base << MANTISSA_SHIFTS) | (radix << RADIX_SHIFT) | unit;
    }
    /**
     * Extracts the number part from a string, removing the unit.
     *
     * @param value  The string containing the number and unit.
     * @param number The number of trailing characters to remove (unit length).
     * @return The number part of the input string.
     */
    private static String getNumber(String value, int number) {
        return value.substring(0, value.length() - number);
    }


    /**
     * Evaluates the attribute's raw value and sets its type and data.
     * This method parses different types of data using regular expressions.
     */
    public void evaluate() {
		Matcher explicitTypeMatcher = explicitTypePattern.matcher(attributeChunk.attributeRawValue);

		// Check if value has an explicit type
        if (explicitTypeMatcher.find()) {
			String typeString = explicitTypeMatcher.group(1);
            String value = explicitTypeMatcher.group(2);
			// If the type is string or null or undefined set the value type to string
            if (typeString == null || typeString.isEmpty() || typeString.equals("string") || typeString.equals("str")) {
                valueType = 0x03;
                stringValue = value;
                stringPool().addString(stringValue); // Add string to string pool.
            } else {
                throw new RuntimeException("Unsupported explicit type");
            }
        } else { // If value does not have explicit type, try to detect from its format
            Matcher typeMatcher = typesPattern.matcher(attributeChunk.attributeRawValue);

			// If value matches to one of the defined type.
            if (typeMatcher.find()) {
                ValuePositionPair valuePositionPair = new ValuePositionPair(typeMatcher);
				// Use value position to decide the type.
                switch (valuePositionPair.position) {
                    case 1: // Type null
                        valueType = 0x00;
                        valueData = 0;
                        break;
                    case 2: // Type Reference.
                        valueType = 0x01;
                        valueData = getReferenceResolver().resolve(this, valuePositionPair.value);
                        break;
                    case 3: // Type boolean
                        valueType = 0x12;
                        valueData = "true".equalsIgnoreCase(valuePositionPair.value) ? 1 : 0;
                        break;
                    case 4: // Type Integer
						valueType = 0x10;
						try {
							// Try to parse as a Long (for larger integers)
							valueData = (int)Long.parseLong(valuePositionPair.value); // Use Long.parseLong and then cast the long to int.
							//Check for overflow when casting to int.
							if((long)valueData != Long.parseLong(valuePositionPair.value)) {
								//If overflow occurs, consider it as string
								valueType = 0x03;
								stringValue = valuePositionPair.value;
								stringPool().addString(stringValue);
							}
						}
						catch (NumberFormatException e) {
							// If Long parsing fails (still could be a String) then consider it as string.
							valueType = 0x03;
							stringValue = valuePositionPair.value;
							stringPool().addString(stringValue);
						}
						break;
                    case 5: // Type Hexadecimal integer.
                        valueType = 0x11;
                        valueData = Integer.parseInt(valuePositionPair.value.substring(2), 16);
                        break;
                    case 6: // Type float.
                        valueType = 0x04;
                        valueData = Float.floatToIntBits(Float.parseFloat(valuePositionPair.value));
                        break;
                    case 7: // Type Dimension.
                        valueType = 0x05;
						valueData = evaluateComplex(valuePositionPair.value);
                        break;
                    case 8: // Type Fraction.
                        valueType = 0x06;
                        valueData = evaluateComplex(valuePositionPair.value);
                        break;
                    case 9: // Type Color.
                        valueType = 0x1c;
						valueData = Color.parseColor(valuePositionPair.value);
                        break;
                    case 10: // Layout parameter values
                        valueType = 0x10;
                        valueData = "wrap_content".equalsIgnoreCase(valuePositionPair.value) ? -2 : -1;
                        break;
                    default:  // type string if no type match.
                        valueType = 0x03;
                        stringValue = valuePositionPair.value;
                        stringPool().addString(stringValue);
                        break;
                }
            } else { // If no explicit type is specified and the value does not match any of the types then treat it as a string
                valueType = 0x03;
                stringValue = attributeChunk.attributeRawValue;
                stringPool().addString(stringValue); // Add the string to the string pool.
            }
        }
    }
}

