package kimono.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.squareup.okhttp.logging.HttpLoggingInterceptor.Logger;

public abstract class AbstractApi implements Api {

	Map<String,Fetcher> fetchers = new HashMap<>();

	private Properties props;
	
	public AbstractApi( Properties props ) {
		super();
		this.props=props;
	}

	/**
	 * Register a {@link Fetcher} to fetch resources of a given type
	 */
	protected void setFetcher( String resource, Fetcher impl ) {
		fetchers.put(resource, impl);
	}
	
	/**
	 * Get properties
	 */
	protected Properties getProperties() {
		return props;
	}
	
	/**
	 * Get the {@code page_size} property
	 * @return The number of objects to get for each topic. efaults to 10.
	 */
	protected int getPageSize() {
		return Integer.parseInt(props.getProperty("page_size", "10"));
	}
	
	/**
	 * Get the {@code full} property
	 * @return true to dump the full object, otherwise false. Defaults to false.
	 */
	protected boolean isFull() {
		return Boolean.parseBoolean(props.getProperty("full", "false"));
	}
	
	@Override
	public void fetch(String resource, Report report) {
		
		// Authenticate if not already
		authenticate(props);
		
		// Call the Fetcher for this resource type
		Fetcher fetcher = fetchers.get(resource);
		if( fetcher != null ) {
			report.startSection(resource);
			fetcher.fetch(report, new Props());
			report.endSection();
		} else {
			throw new IllegalArgumentException("Unknown resource: "+resource);
		}
	}
	
	protected void log( Exception ex ) {
		Logger.DEFAULT.log(ex.getMessage());
	}
}
