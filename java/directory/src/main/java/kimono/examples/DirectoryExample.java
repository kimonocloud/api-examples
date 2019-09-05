package kimono.examples;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.KCTenant;
import kimono.client.KCTenantSupplier;
import kimono.client.impl.TenantSupplier;
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
 * Demonstrates how to use the Kimono Task APIs to create an HTML-based teacher
 * and student directory from the Data Event task stream sent from Kimono. A
 * directory is created in the ./directories folder for each tenant visible to
 * your API key.
 * 
 * Alternatively, use the {@code -populate} command line option to initially
 * populate the directory by querying the API for each topic.
 * 
 * INSTALLATION ============
 * 
 * From the Developer > Integrations area of Dashboard, install an Integration
 * Blueprint for this example. Use the blueprint from the blueprints/blind_sync
 * folder to implement Blind Sync, or from the blueprints/interactive folder to
 * implement Interactive Sync.
 * 
 * Install an instance of the Integration in a new Interop Cloud.
 * 
 * USAGE =====
 * 
 * When the {@code -populate} option is not used, there are two ways to trigger
 * Kimono to send Data Event tasks to your client:
 * 
 * (1) Go Live
 * 
 * When you switch from Setup to Live mode, Kimono generates SET events (Blind
 * Sync) or ADD and UPDATE events (Interactive Sync) for each data object in the
 * repository.
 * 
 * (2) Resend All Data
 * 
 * Once the Integration is in Live mode, click the Resend All Data button on the
 * Repository page to generate SET events (both Blind and Interactive Sync) for
 * each data object in the repository.
 * 
 * You can also use this example in conjunction with the Modify example to
 * modify a data object in a tenant's repository, resulting in a SET or UPDATE
 * data event. The Directory will be updated accordingly.
 * 
 * 
 */
public class DirectoryExample {

	private static final Logger LOGGER = Logger.getLogger(DirectoryExample.class.getName());

	enum Api {
		KIMONO, ONEROSTER, CLEVER
	}

	/**
	 * The name of the Integration. This must match the @Name attribute specified
	 * in the Integration Blueprint. This value is used to filter DirectoryExample
	 * tenants when iterating installed tenants. Without this filter, the client 
	 * would process data for all Integrations available to your API key.
	 */
	public static final String INTEGRATION_NAME = "DirectoryExample";

	/**
	 * The OAS wrapper ApiClient
	 */
	private kimono.api.v2.interop.ApiClient client;

	/**
	 * TaskLoop polling interval in seconds
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

	public DirectoryExample parse(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				String[] parts = StringUtils.split(args[i], ':');
				props.setProperty(parts[0].substring(1), parts.length == 1 ? "true" : parts[1]);
			}
		}

		api = Api.valueOf(props.getProperty("api", Api.KIMONO.name()).toUpperCase());

		return this;
	}

	/**
	 * Create a Table of Contents with a Directory for each {@code DirectoryExample} Integration 
	 * installed in Kimono. If the {@code -populate} option was specified, initially populates
	 * each Directory by querying the API for data objects. Otherwise, waits for Data Events
	 * to be received. 
	 */
	public void run() throws Exception {
		
		// Table of Contents
		Contents toc = new Contents();
		
		// When the -populate option is specified populate each Directory by querying the API
		boolean populate = Boolean.parseBoolean(props.getProperty("populate","false"));
		
		// Enumerate each tenant to create a Directory for each
		KCTenantSupplier supplier = new TenantSupplier().forIntegrations(INTEGRATION_NAME);
		while( supplier.hasNext() ) {
			KCTenant tenant = supplier.next();
			Directory dir = new InMemoryDirectory(tenant);
			toc.add(dir);
			if( populate ) {
				dir.populate(getDataSource(tenant));
			}
		}

		// Render the Table of Contents and the individual Directories
		File base = new File("directory");
		base.mkdirs();
		ContentsRenderer render = new ContentsRenderer();
		render.setBaseDir(base);
		render.render(null,toc);

		// Run the Task Loop until Ctrl+C is hit
		// startTaskLoop();
	}

	/**
	 * Get a {@link DataSource} implementation.
	 * @param tenant The tenant; used only with the Kimono API data source
	 * @return A data source instance
	 */
	protected DataSource getDataSource(KCTenant tenant) {
		switch (api) {
		case CLEVER:
			return new CleverDataSource();
		case ONEROSTER:
			return new OneRosterDataSource();
		default:
			return new KimonoDataSource(tenant);
		}
	}

	/**
	 * Start a Task Loop to periodically update the Directory as data changes in
	 * the repository
	 */
	protected void startTaskLoop() throws Exception {

		KCTaskPoller poller = new TaskPoller(new TenantSupplier());
		poller.setDefaultTaskHandler((tenant,task)->{
			return null;
		});

		poller.poll(interval, TimeUnit.SECONDS);
	}

	/**
	 * Create and return an {@link kimono.api.v2.interop.ApiClient} configured for
	 * API Key authentication.
	 * <p>
	 * 
	 * Clients use two forms of authentication depending on the type of data
	 * accessed: Acdtor Authentication and Vendor Authentication. Actor
	 * Authentication uses the OAuth2 protocol to authenticate with a specific Actor
	 * (or "tenant") given its credentials. Actor Authentication is required to
	 * access the resources of a specific Actor. Each request to actor-scoped APIs
	 * must be authenticated using the credentials supplied in a {@link TenantInfo}.
	 * In contrast, Vendor Authentication is used to access APIs scoped to the
	 * resources of a developer and which do not directly access user data, such as
	 * obtaining a list of {@link TenantInfo}. It requires your API Key.
	 * <p>
	 * 
	 * @return
	 */
	protected kimono.api.v2.interop.ApiClient getApiKeyClient() {
		if (client == null) {
			String apikey = props.getProperty("apikey");
			if (apikey == null) {
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
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Unexpected error", ex);
		}
	}
}
