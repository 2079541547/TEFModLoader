package mt.modder.hub.axml;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class NamespaceChecker {

    private Set<String> attributes;

    public NamespaceChecker() {
        attributes = new HashSet<>();
        loadAttributesFromFile();
    }

    private void loadAttributesFromFile() {
        try {
            // Open the file from the assets folder
            InputStream inputStream = NamespaceChecker.class.getResourceAsStream("/assets/no_nameSpace_attrs.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            // Read each line and add it to the set
            while ((line = reader.readLine()) != null) {
                attributes.add(line.trim());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNamespace(String attributeName) {
        if (attributes.contains(attributeName)) {
            return ""; // Empty namespace for specified attributes
        } else {
            return "android"; // Default namespace for others
        }
    }
	
	public boolean isAttributeExist(String str){
		if(attributes.contains(str)){
			return true;
		} else {
			return false;
		}
	}
}

