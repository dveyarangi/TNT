package org.tnt.config;

import java.io.IOException;
import java.io.InputStream;

import yarangi.files.FileLoader;

import com.google.gson.Gson;
import com.spinn3r.log5j.Logger;

/**
 * Server configuration provider.
 * 
 * @author Fima
 */
public class TNTConfig
{
	private static final String DEFAULT_CONFIG_FILE = "tnt.conf";
	
	private final Logger log = Logger.getLogger( this.getClass() );

	/**
	 * Loads server configuration.
	 * @return
	 */
	public TNTConfig load()
	{
		InputStream stream = null;
		try 
		{
			stream = TNTConfig.class.getClassLoader().getResourceAsStream( DEFAULT_CONFIG_FILE );
			
			Gson gson = new Gson();

			String jsonString = FileLoader.loadFile( stream );
			
			TNTConfig config = gson.fromJson( jsonString, TNTConfig.class );
			log.debug("Using configuration file [%s].", DEFAULT_CONFIG_FILE);
			
			return config;
		}
		catch(Exception e)
		{
			log.warn("Could not load configuration file [%s], using default configuration.", e, DEFAULT_CONFIG_FILE);
		}
		finally {
			try { if(stream != null)
			{
				stream.close();
			} }	
			catch( IOException e ) { log.error("Failed to close stream [%s].", e, DEFAULT_CONFIG_FILE); }
		}
		
		// returning default configuration:
		return new TNTConfig();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	
	private final NetworkConfig server = new NetworkConfig();
	
	public NetworkConfig getServerConfig() { return server; }
}	
