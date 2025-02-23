package mt.modder.hub.axmlTools;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.Log;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultReferenceResolver implements ReferenceResolver {

    // Pattern to match references like @+[package:]/[type]/name or @name
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("^@\\+?(?:(\\w+):)?(?:(\\w+)/)?(\\w+)$");

    /**
     * Resolves a reference string to an integer ID.
     *
     * @param value The ValueChunk containing the context.
     * @param ref   The reference string to resolve. Expected format: @[package:][type]/name or @name
     *              where package and type are optional, and name is the resource name or a hexadecimal value.
     * @return The integer ID corresponding to the resolved reference.
     * @throws RuntimeException if the reference string is invalid.
     */
    @Override
    public int resolve(ValueChunk value, String ref) {
        Matcher matcher = REFERENCE_PATTERN.matcher(ref);
        if (!matcher.matches()) {
            throw new RuntimeException("Invalid reference format: " + ref);
        }

        String packageName = matcher.group(1); // Package name from the reference, can be null.
        String resourceType = matcher.group(2); // Resource type (e.g., "drawable", "string") from the reference, can be null.
        String resourceName = matcher.group(3); // Resource name or hexadecimal value from the reference.

        try {
            // Attempt to parse the name as a hexadecimal integer.
            // If successful, return the parsed hexadecimal value.
            return Integer.parseInt(Objects.requireNonNull(resourceName), 16);
        } catch (NumberFormatException e) {
            // If parsing as hex fails, it's not a hex value.
			Log.d("DefaultReferenceResolver", "Not a hexadecimal value, trying resource lookup: " + resourceName);
            // Continue with resource lookup.
        }

        // If not a hex value, attempt to resolve the reference using getIdentifier.
        int resolvedId = 0;
		try {
			//  Suppressing lint warning DiscouragedApi as getIdentifier method is only way to resolve resources at runtime
			@SuppressLint("DiscouragedApi")
				Resources resources = value.getContext().getResources();
			resolvedId = resources.getIdentifier(resourceName, resourceType, packageName);
		} catch (Exception e){
			Log.e("DefaultReferenceResolver", "Error resolving resource: " + ref, e);
		}
        return resolvedId;
    }
}
