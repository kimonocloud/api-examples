package kimono.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import kimono.api.v2.broker.MessagesApi;
import kimono.api.v2.broker.model.Message;
import kimono.api.v2.interop.ApiException;
import kimono.api.v2.interopdata.IngestionsApi;
import kimono.api.v2.interopdata.RosteringApi;
import kimono.api.v2.interopdata.model.Ingestion;
import kimono.api.v2.interopdata.model.IngestionResponse;
import kimono.api.v2.interopdata.model.IngestionState;
import kimono.api.v2.interopdata.model.IngestionType;
import kimono.api.v2.interopdata.model.Org;
import kimono.api.v2.interopdata.model.OrgSysType;
import kimono.api.v2.interopdata.model.Person;
import kimono.api.v2.interopdata.model.PersonSysType;
import kimono.client.KimonoApiException;

/**
 * Demonstrates how to load data using the Ingestion API then read back that data
 * with the Interop Data API. Data is contained in JSON files named the same as
 * Kimono topics and located in the current working directory (e.g. students.json)
 */
public class Load {

	private static List<String> sTopics = Arrays.asList(
			"LEA", 
			"SCHOOL", 
			"STUDENT", 
			"STUDENT_SCHOOL_ENROLLMENT", // StudentSchoolMembership 
			"STAFF", // "Teacher"
			"STAFF_SCHOOL_ENROLLMENT" // TeacherSchoolMembership
	);
	
	/**
	 * Interop API client
	 */
	kimono.api.v2.interop.ApiClient interopClient;
	
	IngestionsApi ingestions; 
	
	/**
	 * Interop Data API client
	 */
	kimono.api.v2.interopdata.ApiClient dataClient;
	
	RosteringApi rostering;
	
	/**
	 * Broker API client
	 */
	kimono.api.v2.broker.ApiClient brokerClient;
	
	MessagesApi messages;
	
	
	/**
	 * Properties specified on the command line as {@code -property:value} 
	 */
	private Properties props = new Properties();
	
	public Load() {
		super();
	}
	
	public Load parse( String[] args ) {
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
	 * Use the Ingestion API to load a full data set into the Repository.
	 * @throws JsonSyntaxException 
	 * @throws JsonIOException 
	 * @throws ApiException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public Ingestion ingest() throws KimonoApiException, JsonIOException, JsonSyntaxException, FileNotFoundException {

		// Create the Ingestion
		Ingestion ingestion = createIngestion().getData();
		System.out.println("Created ingestion (session_token="+ingestion.getSessionToken()+"): "+ingestion.getId());
		
		// Send Load messages to Kimono
		for( String topic : sTopics ) {
			
			// Parse the ./data/topic.json file, expected to be an array of ObjectData elements
			File file = new File("./data/"+topic+".json");
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(new FileReader(file));
			JsonObject dataOrError = element.getAsJsonObject();
			
			// Create a Load message
			Message msg = createLoadMessage(ingestion,dataOrError.get("data"),dataOrError.get("error"),topic);
			
			try {
				// Send the message
				messages.sendMessage(msg);
			} catch (kimono.api.v2.broker.ApiException e) {
				throw new KimonoApiException("Error sending data packet: "+file.getAbsolutePath(),e); 
			}
		}
		
		return ingestion;
	}
	
	/**
	 * Wait for Ingestion to complete
	 * @throws ApiException 
	 * @throws kimono.api.v2.interopdata.ApiException 
	 */
	protected IngestionState waitForIngestion( Ingestion ingestion ) throws kimono.api.v2.interopdata.ApiException {
		
		// TODO: Use the Ingestion Report to accomplish this without polling

		for(;;) {
			IngestionResponse query = ingestions.findIngestion(ingestion.getId());
			IngestionState state = query.getData() == null ? null : query.getData().getState();
			
			if( state != null && ( state == IngestionState.SUCCESS || state == IngestionState.CANCELLED || state == IngestionState.TIMEOUT ) ) {
				return state;
			} else {
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
			}
		} 
	}
	
	protected Message createLoadMessage( Ingestion ingestion, JsonElement data, JsonElement error, String topic ) {
		
		// TODO: This should not be necessary, but because of a bug in Kimono we need to set the
		// topic header attribute.
		JsonObject header = new JsonObject();
		header.addProperty("topic", topic);

		JsonObject packet = new JsonObject();
		packet.addProperty("number", 1);
		packet.addProperty("final", true);

		JsonObject ing = new JsonObject();
		ing.addProperty("session_token", ingestion.getSessionToken());
		
		JsonObject msg = new JsonObject();
		msg.addProperty("message", "LOAD");
		msg.add("header",header);
		msg.add("ingestion", ing);
		msg.add("packet", packet);
		
		if( data != null ) {
			msg.add("data", data);
		}
		if( error != null ) {
			msg.add("error", error);
		}
		
		Message m = new Message();
		m.setData(msg);
		return m;
	}
	
	protected IngestionResponse createIngestion() throws KimonoApiException {
		try {
			// Create a Bulk Load Ingestion to load all topics:
			// - A Bulk Load is loaded at the direction of a client (in contrast to a Collection, which is
			//   a request for data from Kimono); it contains bulk data organized into one or more data
			//   packets. In this example we'll provide on file per topic.
			// - Let Kimono know which topics will be included in this Ingestion. The Ingestion will not
			//   complete until a final packet has been received for each topic.
			// - Kimono returns a "session token" that must be referenced in each Load message
			Ingestion load = new Ingestion();
			load.setTopics(sTopics);
			load.setType(IngestionType.BULK_LOAD);
			return ingestions.createIngestion(load);
		} catch( Exception ex ) {
			throw new KimonoApiException(ex);
		}
	}
	
	/**
	 * Print all data in the repository (exercising all API endpoints to do so)
	 * @throws kimono.api.v2.interopdata.ApiException 
	 */
	public void print() throws KimonoApiException {
		try {
			// Use each of the Interop Data APIs to fetch data
			
			// === Orgs ====
			
			List<Org> orgs = rostering.listOrgs(null, null).getData();
			orgs.forEach(this::print);
			assertSize("listOrgs",orgs,3);
			
			List<Org> leas = rostering.listLEAs(null, null).getData();
			leas.forEach(this::print);
			assertSize("listLEAs",leas,1);
			
			List<Org> schools = rostering.listSchools(null, null).getData();
			schools.forEach(this::print);
			assertSize("listSchools",schools,2);
			
			// === Persons ===
			
			List<Person> persons = rostering.listPersons(null, null).getData();
			persons.forEach(this::print);
			assertSize("listPersons",persons,4);
			
			List<Person> students = rostering.listStudents(null, null).getData();
			students.forEach(this::print);
			assertSize("listStudents",students,2);
			
			List<Person> teachers = rostering.listTeachers(null, null).getData();
			teachers.forEach(this::print);
			assertSize("listTeachers",teachers,2);
			
			
			// === Orgs relationships ===
			
			// Assert the student membership of the first school
			for( int school = 0; school < 2; school++ ) {
				List<PersonSysType> studentsRollup = schools.get(school).get$Students();
				if( studentsRollup != null ) {
					studentsRollup.forEach(this::print);
				}
				assertSize("listStudentsForSchool["+school+"]",studentsRollup,1);
	
				List<PersonSysType> teachersRollup = schools.get(school).get$Teachers();
				if( teachersRollup != null ) {
					teachersRollup.forEach(this::print);
				}
				assertSize("listTeachersForSchool["+school+"]",teachersRollup,1);
			}
		} catch( Exception ex ) {
			throw new KimonoApiException(ex);
		}
	}
	
	protected void print( Org org ) {
		System.out.println(org.get$Sys().getId()+" "+org.getName());
	}
	
	protected void print( OrgSysType org ) {
		System.out.println("Org["+org.getOrgType()+"] reference: "+org.getId());
	}
	
	protected void print( Person person ) {
		System.out.println(person.get$Sys().getId()+" "+person.getName().getFirst()+" "+person.getName().getLast());
	}
	
	protected void print( PersonSysType person ) {
		System.out.println("Person["+person.getPersonType()+"] reference: "+person.getId());
	}
	
	protected void assertSize( String operation, Collection<?> list, int expectedSize ) {
		int size = list == null ? 0 : list.size();
		if( size != expectedSize ) {
			throw new IllegalStateException(operation + " returned " + size + " objects (expected " + expectedSize + ")");
		}
	}
	
	protected void assertRef( PersonSysType personRef, Person person ) {
		if( !personRef.getId().equals(person.get$Sys().getId()) ||
			!personRef.getPersonType().equals(person.get$Sys().getPersonType()) ) {
			throw new IllegalStateException("Person reference ("+personRef.getId()+" of type "+personRef.getPersonType()+" does not reference expected Person ("+person.get$Sys().getId()+") of type "+person.get$Sys().getPersonType());
		}
	}
	
	protected void assertRef( OrgSysType orgRef, OrgSysType org ) {
		if( !orgRef.getId().equals(org.getId()) ||
			!orgRef.getOrgType().equals(org.getOrgType()) ) {
			throw new IllegalStateException("Org reference ("+orgRef.getId()+" of type "+orgRef.getOrgType()+" does not reference expected Org ("+org.getId()+") of type "+org.getOrgType());
		}
	}
	
	
	public void run() throws Exception {
		
		// Set up the Interop API client to manage Interop Cloud resources 
		interopClient = kimono.api.v2.interop.Configuration.getDefaultApiClient();
		
		interopClient.setAccessToken(props.getProperty("token"));
		interopClient.setUsername(props.getProperty("username"));
		interopClient.setPassword(props.getProperty("password"));
		interopClient.setUserAgent("Kimono API Example");
		ingestions = new IngestionsApi();
		
		// Set up the Interop Data API client to access data stored in repositories
		dataClient = kimono.api.v2.interopdata.Configuration.getDefaultApiClient();
		dataClient.setAccessToken(props.getProperty("token"));
		dataClient.setUsername(props.getProperty("username"));
		dataClient.setPassword(props.getProperty("password"));
		dataClient.setUserAgent("Kimono API Example");
		rostering = new RosteringApi();
		
		// Set up the Broker API client to send messages
		brokerClient = kimono.api.v2.broker.Configuration.getDefaultApiClient();
		brokerClient.setAccessToken(props.getProperty("token"));
		brokerClient.setUsername(props.getProperty("username"));
		brokerClient.setPassword(props.getProperty("password"));
		brokerClient.setUserAgent("Kimono API Example");
		messages = new MessagesApi();
		
		Ingestion i = ingest();
		if( i != null ) {
			IngestionState state = waitForIngestion(i);
			if( state == IngestionState.SUCCESS ) {
				print();
			} else {
				System.out.println("Ingestion did not complete successfully ("+state+")");
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			new Load().parse(args).run();
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}
}
