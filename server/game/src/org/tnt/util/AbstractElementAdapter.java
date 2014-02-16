package org.tnt.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
 
public class AbstractElementAdapter <E>implements JsonDeserializer<E> {
	
	private String packagePrefix;
	
	public AbstractElementAdapter(String packagePrefix)
	{
		this.packagePrefix = packagePrefix;
	}
	
    @Override
    public E deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException 
    {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonElement = jsonObject.get("type");
        
        Type realType;
        if(jsonElement == null)
        	realType = type;
        else
            try {
            	realType = Class.forName( packagePrefix + jsonElement.getAsString() );
            } catch (ClassNotFoundException cnfe) {
                throw new JsonParseException("Unknown element type: " + type, cnfe);
            }
 
        return context.deserialize(jsonObject, realType );
     }
}