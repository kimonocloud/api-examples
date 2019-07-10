package kimono.examples;

import java.util.Collections;
import java.util.Properties;

import kimono.api.v2.interop.ApiClient;
import kimono.api.v2.interop.ApiException;
import kimono.api.v2.interop.TenantsApi;

/**
 * Demonstrates how to list tenant info.
 * 
 * Usage:
 * 
 * <pre>tenants -apikey:value</pre>
 *
 */
public class Tenants {

	/**
	 * Properties specified on the command line as {@code -property:value} 
	 */
	private Properties props = new Properties();
	
	private TenantsApi tenants;
	
	private ApiClient client;
	
	public Tenants() {
		super();
	}
	
	public Tenants parse( String[] args ) {
		for( int i = 0; i < args.length; i++ ) {
			if( args[i].startsWith("-") ) {
				// "-property:value"
				int offs = args[i].indexOf(':');
				if( offs != -1 ) {
					props.setProperty(args[i].substring(1, offs),args[i].substring(offs+1));
				}
			}
		}
		return this;
	}
	
	/**
	 * Set up the API Client 
	 */
	protected void authenticate() {
		String apiKey = props.getProperty("apikey",System.getenv("KIMONO_API_KEY"));
		client = new ApiClient();
		client.setReadTimeout(30000);
		client.setUsername(apiKey);
		client.setPassword(apiKey);
		
		tenants = new TenantsApi(client);
	}
	
	/**
	 * List all tenants visible to the authenticated API Key
	 */
	public void run() throws ApiException {
		authenticate();
		tenants.listInteropTenants(Collections.emptyList(), Collections.emptyList(), 1, 10).getData().forEach(tenant->{
			System.out.println(tenant);
		});
	}
	
	public static void main(String[] args) {
		try {
			new Tenants().parse(args).run();
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}
}
