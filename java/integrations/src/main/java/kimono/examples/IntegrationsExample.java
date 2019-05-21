package kimono.examples;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import kimono.api.v2.interop.ApiClient;
import kimono.api.v2.interop.ApiException;
import kimono.api.v2.interop.Configuration;
import kimono.api.v2.interop.IntegrationsApi;
import kimono.api.v2.interop.model.Integration;
import kimono.api.v2.interop.model.IntegrationVersion;
import kimono.api.v2.interop.model.IntegrationVersionsResponse;

/**
 * Demonstrates how to manage Integrations via REST APIs.
 */
public class IntegrationsExample {

	private static final Logger LOGGER = Logger.getLogger(IntegrationsExample.class.getName());
	
	ApiClient client;
	
	/**
	 * API Key
	 */
	private String fApiKey;
	
	public IntegrationsExample() {
		super();
	}
	
	public IntegrationsExample parse( String[] args ) {
		for( String arg : args ) {
			if( arg.startsWith("-apikey:") ) {
				fApiKey = arg.substring(8);
			}
		}
		return this;
	}
	
	/**
	 * Demonstrates how to install, update, and delete Integrations
	 * @throws Exception
	 */
	public void run() throws Exception {

		IntegrationsApi api = new IntegrationsApi(getApiKeyClient());
		
		// Find the Integration with the name "example"
		Integration ix = api.listIntegrations("example").getData().stream().findFirst().orElse(null);
		if( ix != null ) {
			System.out.println("Integration is already loaded");
			return;
		}
		
		// Create a new Integration with this name
		ix = new Integration();
		ix.setName("example");
		ix = api.createIntegration(ix);
		System.out.println("Created Integration with id: "+ix.getId());
		
		// Now load the Integration Blueprint. A given Integration can have 
		// several versions, each of which is defined by a Blueprint. During
		// development you can install any version of an Integration in your 
		// own Interop Cloud for development and testing. When you're ready 
		// to make it available to other customers, get in touch with Kimono
		// to publish it. We'll assign a product code to it at this time.
		
		// The API currently only supports sending a blueprint.xml file as
		// the body of the request.
		IntegrationVersion v1 = api.createIntegrationVersion(ix.getId(),readBlueprint("v1"));
		System.out.println("Created version of Integration: "+v1.getTitle()+" (version "+v1.getVersion()+")");
		listVersions(api,ix);
		
		// Upload v2
		IntegrationVersion v2 = api.createIntegrationVersion(ix.getId(),readBlueprint("v2"));
		System.out.println("Created version of Integration: "+v1.getTitle()+" (version "+v2.getVersion()+")");
		listVersions(api,ix);
		
		// Delete v1
		api.deleteIntegrationVersion(ix.getId(), v1.getVersion());
		System.out.println("Deleted version: "+v1.getVersion());
		listVersions(api,ix);
	}
	
	private void listVersions( IntegrationsApi api, Integration ix ) throws ApiException {
		IntegrationVersionsResponse rsp = api.listIntegrationVersions(ix.getId());
		System.out.println("There are now "+rsp.getData().size()+" versions:");
		rsp.getData().forEach(v->System.out.println("  "+v.getTitle()+" ("+v.getVersion()+")"));
	}
	
	private String readBlueprint( String version ) throws Exception {
		return new String(Files.readAllBytes(Paths.get(getClass().getResource("blueprint/"+version+"/blueprint.xml").toURI())));
	}

	/**
	 * Create and return an {@link ApiClient} configured for API Key authentication,
	 * which is required by the Integration APIs. 
	 * @return
	 */
	protected ApiClient getApiKeyClient() {
		if (client == null) {
			client = Configuration.getDefaultApiClient();
			client.setUsername(fApiKey);
		}
		return client;
	}
	
	public static void main(String[] args) {
		try {
			new IntegrationsExample().parse(args).run();
		} catch( Exception ex ) {
			LOGGER.log(Level.SEVERE, "Unexpected error", ex);
		}
	}
}
