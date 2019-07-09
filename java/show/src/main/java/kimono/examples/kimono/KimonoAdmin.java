package kimono.examples.kimono;

import java.util.Properties;

import kimono.api.v2.interop.ApiClient;
import kimono.api.v2.interop.ApiException;
import kimono.api.v2.interop.Configuration;
import kimono.api.v2.interop.TenantsApi;
import kimono.api.v2.interop.model.TenantInfo;
import kimono.examples.AbstractApi;
import kimono.examples.Fetcher;

/**
 * Fetches data from the Kimono API
 */
public class KimonoAdmin extends AbstractApi {

	ApiClient client;
	TenantsApi tenants;

	public KimonoAdmin( Properties props ) {
		super(props);
		setFetcher("tenants", listInteropTenants());
	}

	@Override
	public void authenticate( Properties props ) {
		if (client == null) {
			client = Configuration.getDefaultApiClient();
			client.setUsername(props.getProperty("username"));
			client.setPassword(props.getProperty("password"));
			if( props.containsKey("basePath") ) {
				client.setBasePath(props.getProperty("basePath"));
			}

			tenants = new TenantsApi();
		}
	}

	/**
	 * Get {@code kimono:tenants}. Lists {@code TenantInfo} representing all licensed
	 * and authorized instances of this developer's Integrations.
	 */
	public Fetcher listInteropTenants() {
		return (report, props) -> {
			try {
				// === Clever porting note === The /oauth/tokens endpoint is the nearest 
				// equivalent API in Clever
				for (TenantInfo tenant : tenants.listInteropTenants(null,null,null,null).getData()) {
					report.line(tenant.getId() + ": " + tenant.getName() + " ("+tenant.getAccount().getName()+")");
				}
			} catch (ApiException e) {
				log(e);
			}
		};
	}		
}
