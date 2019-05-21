package kimono.examples;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.impl.DefaultInteropDataClientFactory;
import kimono.client.impl.DefaultTenantInfoSupplier;
import kimono.client.impl.tasks.TaskHandler;
import kimono.client.impl.tasks.TaskPoller;
import kimono.client.tasks.KCTaskHandler;
import kimono.client.tasks.KCTaskPoller;
import kimono.examples.datasource.CleverDataSource;
import kimono.examples.datasource.DataSource;
import kimono.examples.datasource.KimonoDataSource;
import kimono.examples.datasource.OneRosterDataSource;
import kimono.examples.directory.Contents;
import kimono.examples.directory.Directory;
import kimono.examples.directory.InMemoryDirectory;
import kimono.examples.render.ContentsRenderer;

/**
 * Demonstrates how to use the Kimono APIs to create an HTML-based directory.
 * This example is built on the Client Kit for Java.
 */
public class DirectoryExample {

	private static final Logger LOGGER = Logger.getLogger(DirectoryExample.class.getName());
	
	enum Api { KIMONO, ONEROSTER, CLEVER }
	
	kimono.api.v2.interop.ApiClient client;
	
	/**
	 * Event polling interval in seconds
	 */
	private int interval = 15;
	
	/**
	 * Properties specified on the command line as {@code -property:value} 
	 */
	private Properties props = new Properties();
	
	/**
	 * The API to use to source data
	 */
	private Api api = Api.KIMONO;
	
	public DirectoryExample() {
		super();
	}
	
	public DirectoryExample parse( String[] args ) {
		for( int i = 0; i < args.length; i++ ) {
			if( args[i].startsWith("-") ) {
				String[] parts = StringUtils.split(args[i],':'); 
				props.setProperty(parts[0].substring(1),parts[1]);
			}
		}

		api = Api.valueOf(props.getProperty("api",Api.KIMONO.name()).toUpperCase());

		return this;
	}
	
	public void run() throws Exception {
		
		// Table of Contents
		Contents toc = new Contents();
		
		// Enumerate each tenant to create a Directory for each
		for( TenantInfo tenant : new DefaultTenantInfoSupplier(getApiKeyClient()).get() ) {
			Directory dir = new InMemoryDirectory(tenant);
			dir.populate(getDataSource(tenant));
			toc.add(dir);
		}

		// Render the toc and directories
		File base = new File("directory");
		base.mkdirs();
		ContentsRenderer render = new ContentsRenderer();
		render.setBaseDir(base);
		render.render(null,toc);
		
		//startEventLoop();
	}
	
	protected DataSource getDataSource( TenantInfo tenant ) {
		switch( api ) {
		case CLEVER:
			return new CleverDataSource();
		case ONEROSTER:
			return new OneRosterDataSource();
		default:
			return new KimonoDataSource(tenant);
		}
	}

	/**
	 * Set up the Task Loop to periodically update the Directory as data changes in the repository
	 */
	protected void startTaskLoop() throws Exception {
		
		KCTaskPoller poller = new TaskPoller();
		poller.initialize(new DefaultTenantInfoSupplier(getApiKeyClient()), new DefaultInteropDataClientFactory());
		poller.setDefaultTaskHandler(event->{
			KCTaskHandler handler = new TaskHandler();
			return handler.handle(event);
		});

		poller.poll(interval,TimeUnit.SECONDS);
	}

	/**
	 * Create and return an {@link kimono.api.v2.interop.ApiClient} configured for API Key authentication.<p>
	 * 
	 * Clients use two forms of authentication depending on the type of data accessed:
	 * Acdtor Authentication and Vendor Authentication. Actor Authentication uses the 
	 * OAuth2 protocol to authenticate with a specific Actor (or "tenant") given its 
	 * credentials. Actor Authentication is required to access the resources of a
	 * specific Actor. Each request to actor-scoped APIs must be authenticated using 
	 * the credentials supplied in a {@link TenantInfo}. In contrast, Vendor Authentication
	 * is used to access APIs scoped to the resources of a developer and which do not 
	 * directly access user data, such as obtaining a list of {@link TenantInfo}. It
	 * requires your API Key.<p>
	 * 
	 * @return
	 */
	protected kimono.api.v2.interop.ApiClient getApiKeyClient() {
 		if (client == null) {
			String apikey = props.getProperty("apikey");
			if( apikey == null ) {
				throw new IllegalArgumentException("-apikey:value is required");
			}
			client = kimono.api.v2.interop.Configuration.getDefaultApiClient();
			client.setUsername(apikey);
		}
		return client;
	}
	
	public static void main(String[] args) {
		try {
			new DirectoryExample().parse(args).run();
		} catch( Exception ex ) {
			LOGGER.log(Level.SEVERE, "Unexpected error", ex);
		}
	}
}
