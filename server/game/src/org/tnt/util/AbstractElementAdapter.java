package org.tnt.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
 

/**
 * Gson adapter for loading implementations into interfaces.
 * 
 * When gson encounters field of type {@link E} it will use {@link #fieldName} string to
 * construct canonical class name and using the provided prefix and suffix and load the 
 * implementation with the resulting name.
 * 
 * @author Fima
 *
 * @param <E> interface type
 */
public class AbstractElementAdapter <E>implements JsonDeserializer<E> 
{
	/**
	 * Name of the field that will contain the loaded implementation
	 */
	private final String fieldName;
	private final String packagePrefix;
	private final String suffix;
	
	public AbstractElementAdapter(String fieldName, String packagePrefix, String suffix)
	{
		this.fieldName = fieldName;
		this.packagePrefix = packagePrefix;
		this.suffix = suffix;
	}
	
    @Override
    public E deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException 
    {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonElement = jsonObject.get( fieldName );
        
        Type realType;
        if(jsonElement == null)
        	realType = type;
        else
            try {
            	realType = Class.forName( constructClassName( jsonElement.getAsString() ));
            } catch (ClassNotFoundException cnfe) {
                throw new JsonParseException("Unknown element type: " + type, cnfe);
            }
 
        return context.deserialize(jsonObject, realType );
     }

	protected String constructClassName( String type )
	{
		return packagePrefix + type + suffix;	
	}
}