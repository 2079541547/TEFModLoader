package mt.modder.hub.axmlTools;

/**
 • Defines constants for the different types and units of a typed value, which is typically used
 • for storing resources in Android.
 */
public class TypedValue {

    /**
     * Represents a null CharSequence.
     * This constant is included for compatibility with existing Android code but it not used in resource files
     */
    public static final CharSequence string = null;

    /**
     * Mask for extracting the unit of a complex value.
     * This can be used with values of type {@link #TYPE_DIMENSION} and {@link #TYPE_FRACTION}.
     */
    public static final int COMPLEX_UNIT_MASK = 15;


    /**
     * Type constant for attribute values.
     * Used to indicate that the value is an attribute reference, where the actual value should be
     * looked up from attributes set on an XML tag.
     */
    public static final int TYPE_ATTRIBUTE = 2;

    /**
     * Type constant for boolean integer values.
     * Used for representing boolean values such as `true` or `false`.
     */
    public static final int TYPE_INT_BOOLEAN = 18;

    /**
     * Type constant for dimension values (e.g., pixels, density-independent pixels).
     * Used for representing sizes, margins, paddings etc.
     */
    public static final int TYPE_DIMENSION = 5;

    /**
     * The first type constant for color integers.
     * Represents the beginning of the range of color integer type values.
     */
    public static final int TYPE_FIRST_COLOR_INT = 28;


    /**
     * The first type constant for integer values.
     * Represents the beginning of the range of integer type values.
     */
    public static final int TYPE_FIRST_INT = 16;


    /**
     * Type constant for floating-point values.
     * Used for representing decimal values.
     */
    public static final int TYPE_FLOAT = 4;

    /**
     * Type constant for fraction values (e.g., percentages).
     * Used for representing relative values.
     */
    public static final int TYPE_FRACTION = 6;


    /**
     * Type constant for hexadecimal integer values.
     * Used for representing numbers in base 16
     */
    public static final int TYPE_INT_HEX = 17;

    /**
     * The last type constant for color integers.
     * Represents the end of the range of color integer type values.
     */
    public static final int TYPE_LAST_COLOR_INT = 31;

    /**
     * The last type constant for integer values.
     * Represents the end of the range of integer type values.
     */
    public static final int TYPE_LAST_INT = 31;

    /**
     * Type constant for resource reference values.
     * Used to refer to another resource by its ID (e.g., `@drawable/icon`).
     */
    public static final int TYPE_REFERENCE = 1;


    /**
     * Type constant for string values.
     * Used for representing textual data.
     */
    public static final int TYPE_STRING = 3;
	
	public static final int COMPLEX_MANTISSA_MASK = 16777215;

    public static final int COMPLEX_MANTISSA_SHIFT = 8;

    public static final int COMPLEX_RADIX_0p23 = 3;

    public static final int COMPLEX_RADIX_16p7 = 1;

    public static final int COMPLEX_RADIX_23p0 = 0;

    public static final int COMPLEX_RADIX_8p15 = 2;

    public static final int COMPLEX_RADIX_MASK = 3;

    public static final int COMPLEX_RADIX_SHIFT = 4;

    public static final int COMPLEX_UNIT_DIP = 1;

    public static final int COMPLEX_UNIT_FRACTION = 0;

    public static final int COMPLEX_UNIT_FRACTION_PARENT = 1;

    public static final int COMPLEX_UNIT_IN = 4;

    public static final int COMPLEX_UNIT_MM = 5;

    public static final int COMPLEX_UNIT_PT = 3;

    public static final int COMPLEX_UNIT_PX = 0;

    public static final int COMPLEX_UNIT_SHIFT = 0;

    public static final int COMPLEX_UNIT_SP = 2;
	
}
