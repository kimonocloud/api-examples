package kimono.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kimono.examples.clever.CleverData;
import kimono.examples.kimono.KimonoRostering;
import kimono.examples.oneroster.OneRoster;

/**
 * Demonstrates how to get data objects from these APIs:
 *
 * <ul>
 * 	<li>Kimono Interop Data API (kimono.examples.kimono.*)</li>
 *  <li>OneRoster API (kimono.examples.oneroster.*)</li>
 *  <li>Clever API (kimono.examples.clever.*)</li>
 * </ul>
 * 
 * Usage:
 * 
 * <pre>show topic1 .. topicN -api:[kimono|oneroster|clever] -option:value</pre>
 *
 * Authentication:
 * 
 * When connecting to a Kimono Integration or OneRoster Connector set 
 * the "-client:value" and "-secret:value" parameters. The Client ID and Secret 
 * are shown on the Actor page in Dashboard. They can be also be obtained via 
 * the GET /interop/tenants API.
 *
 * When connecting to Clever set the -token:value to the Clever OAuth token.
 *
 * Options:
 * 
 * 	-full:true Show the full data object
 * 	-page_size:n Change the default page size of 10
 * 
 * Examples:
 * 
 * Use the Kimono Interop Data API to show the first ten results from each topic:
 * <pre>show -api:kimono -client:<actor_client_id> -secret:<actor_client_secret>
 * 
 * Use the OneRoster API to show the first ten orgs, persons, and sections:
 * <pre>show orgs persons sections -api:oneroster -client:<actor_client_id> -secret:<actor_client_secret>
 *
 */
public class Show {

	/**
	 * Kimono-defined topics that can be specified on the command line. Each Api implementation
	 * translates these topics to API domains.
	 */
	private static List<String> sTopics = Arrays.asList(
			"orgs","schools","leas",
			"persons","students","teachers",
			"terms","courses","sections");

	/**
	 * The actions to perform when the program is run
	 */
	private List<Source> actions = new ArrayList<>();
	
	/**
	 * The supported API implementations
	 */
	private Map<String,Api> apis = new HashMap<>();
	
	/**
	 * Properties specified on the command line as {@code -property:value} 
	 */
	private Properties props = new Properties();
	
	public Show() {
		super();
	}
	
	public Show parse( String[] args ) {
		List<String> topics = new ArrayList<>();
		for( int i = 0; i < args.length; i++ ) {
			if( args[i].startsWith("-") ) {
				// "-property:value"
				int offs = args[i].indexOf(':');
				if( offs != -1 ) {
					props.setProperty(args[i].substring(1, offs),args[i].substring(offs+1));
				}
			} else {
				// "topic"
				topics.add(args[i]);
			}
		}

		String api = props.getProperty("api");
		if( api == null ) {
			throw new IllegalArgumentException("No -api:[kimono|oneroster|clever] specified");
		}
		
		for( String topic : ( topics.isEmpty() ? sTopics : topics ) ) {
			actions.add(new Source(getApi(api),topic));
		}
		
		return this;
	}
	
	public Api getApi( String name ) {
		return apis.computeIfAbsent(name, (n)->{
			if( "clever".equals(n) ) {
				return new CleverData(props);
			} else
			if( "oneroster".equals(n) ) {
				return new OneRoster(props);
			} else {
				return new KimonoRostering(props);
			}
		});
	}
	
	public void run() {
		Report report = new ConsoleReport();
		actions.forEach(action->action.api.fetch(action.resource,report));
	}
	
	public static void main(String[] args) {
		new Show().parse(args).run();
	}
}
