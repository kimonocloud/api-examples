package kimono.examples;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import kimono.api.v2.interop.ApiClient;
import kimono.api.v2.interop.Configuration;
import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.impl.TenantSupplier;
import kimono.client.impl.tasks.TaskPoller;
import kimono.client.tasks.KCTaskPoller;

/**
 * Processes events and responds based on a configuration file. The file is a JSON
 * document that describes how to respond to each Event action for each Topic. The 
 * first response with an expression that evaluates true is used to acknowledge
 * the Event.
 * 
 * The following example will return a success status with no message for the first 
 * 4 students; a warning status with a message for the 5th student added, and an 
 * error status and message for all subsequent students added. The expression is 
 * JavaScript that evaluates true or false. It may reference the Event data object 
 * "data" or the "count" object, which records the number of objects received for 
 * this topic for each event action:
 * 
 * 		count.total
 * 		count.add
 * 		count.update
 * 		count.delete
 * 
 * { "topics": [
 * 		{ "name": "RDM:Student",
 * 		  "events": [
 * 			{ "action": "add",
 * 			  "response": [
 * 				{ "expression": "count.add < 5",
 *				  "disposition": "success"
 *				},{
 *				  "expression": "count.add == 5",
 * 				  "disposition": "warning",
 *  			  "message": "No additional students will be accepted"
 *				},{
 *				  "expression": "count.add > 5",
 *				  "disposition": "error",
 *				  "message": "{data.name.first} {data.name.middle} {data.name.last} not added (already have maximum number of students)
 *				}
 *			  ]
 *			}
 *		  ]
 * 		}
 * 	  ]
 * }
 * 
 * 
 *
 */
public class EventsExample {

	private static final Logger LOGGER = Logger.getLogger(EventsExample.class.getName());

	ApiClient client;
	
	/**
	 * Polling interval in seconds
	 */
	private int interval = 15;
	
	/**
	 * Actor ID or null to poll data for all tenants
	 */
	private UUID actorId;
	
	/**
	 * API Key
	 */
	private String apiKey;
	
	public EventsExample() {
		super();
	}
	
	public EventsExample parse( String[] args ) {
		for( String arg : args ) {
			if( arg.startsWith("-apikey:") ) {
				this.apiKey = arg.substring(8);
			}
		}
		return this;
	}
	
	public void run() throws Exception {
		KCTaskPoller poller = new TaskPoller(new TenantSupplier());
		poller.setDefaultTaskHandler((tenant,task)->{
			return null;
		});

		poller.poll(interval,TimeUnit.SECONDS);
	}

	/**
	 * Create and return an {@link ApiClient} configured for API Key authentication,
	 * which is required by the Tenants API (and select other Kimono APIs that use
	 * Vendor Authentication rather than Actor Authentication, which requires the
	 * OAuth2 protocol). 
	 * 
	 * Actor Authentication is required to access the resources of an Actor (or 
	 * "tenant") and thus each request to actor-scoped APIs must be authenticated 
	 * using the credential supplied in a {@link TenantInfo}. API Key authentication 
	 * is used to call Kimono APIs that do not directly access user data, such as
	 * obtaining a list of {@link TenantInfo}.
	 * 
	 * @return
	 */
	protected ApiClient getApiKeyClient() {
		if (client == null) {
			client = Configuration.getDefaultApiClient();
			client.setUsername(apiKey);
		}
		return client;
	}
	
	public static void main(String[] args) {
		try {
			new EventsExample().parse(args).run();
		} catch( Exception ex ) {
			LOGGER.log(Level.SEVERE, "Unexpected error", ex);
		}
	}
}
