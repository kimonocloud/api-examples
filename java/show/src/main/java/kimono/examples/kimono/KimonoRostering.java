package kimono.examples.kimono;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.api.v2.interopdata.ApiClient;
import kimono.api.v2.interopdata.ApiException;
import kimono.api.v2.interopdata.RosteringApi;
import kimono.api.v2.interopdata.model.Course;
import kimono.api.v2.interopdata.model.Org;
import kimono.api.v2.interopdata.model.Person;
import kimono.api.v2.interopdata.model.Section;
import kimono.api.v2.interopdata.model.Term;
import kimono.examples.AbstractApi;
import kimono.examples.Fetcher;

/**
 * Fetches data from Kimono Rostering APIs
 */
public class KimonoRostering extends AbstractApi {

	ApiClient client;
	RosteringApi rostering;

	public KimonoRostering( Properties props ) {
		super(props);

		// Rostering
		setFetcher("orgs", listOrgs());
		setFetcher("leas", listLeas());
		setFetcher("schools", listSchools());
		setFetcher("persons", listPersons());
		setFetcher("students", listStudents());
		setFetcher("teachers", listTeachers());
		setFetcher("terms", listTerms());
		setFetcher("courses", listCourses());
		setFetcher("sections", listSections());
	}
	
	@Override
	public void authenticate( Properties props ) {
		if( client == null ) {
			// APIs that connect to a specific actor use Actor Authentication. Obtain 
			// the client ID and client secret from the Actor page in Dashboard or from 
			// the GET /interop/tenants API. Specify as properties "client" and "secret".
			Map<String,String> p = new HashMap<>();
			client = new ApiClient(props.getProperty("client"),props.getProperty("secret"),p);
			client.setReadTimeout(30000);
			rostering = new RosteringApi(client);
		}
	}		

	/**
	 * {@code GET /orgs}
	 */
	public Fetcher listOrgs() {
		return (report, props) -> {
			try {
				rostering.listOrgs(1, getPageSize()).getData().forEach(org->
					report.writeEntity(org.get$Sys().getId(), formatOrg(org), dumpObject(org)));
			} catch (ApiException e) {
				report.error(e, e.getResponseBody(), e.getResponseHeaders());
			}
		};
	}

	/**
	 * Get {@code kimono:schools}. Lists all {@code Org}s published by the
	 * {@code School} topic; that is, objects where {@code $sys.org_type: "school"}
	 */
	public Fetcher listSchools() {
		return (report, props) -> {
			try {
				// === Clever porting note === In Kimono, a School is a topic
				// and a type of Org entity where the $sys.org_type == 'school'.
				// The specific attributes mapped to School may be different
				// from other Org topics like LEA.
				rostering.listSchools(1, getPageSize()).getData().forEach(org->
					report.writeEntity(org.get$Sys().getId(), formatOrg(org), dumpObject(org)));
			} catch (ApiException e) {
				log(e);
			}
		};
	}

	/**
	 * Get {@code kimono:leas}. Lists all {@code Org}s published by the
	 * {@code LEA} topic; that is, objects where {@code $sys.org_type: "lea"} 
	 */
	public Fetcher listLeas() {
		return (report, props) -> {
			try {
				// === Clever porting note === In Kimono, An LEA is a topic
				// and a type of Org entity where the $sys.org_type == 'lea'.
				// The specific attributes mapped to LEA may be different
				// from other Org topics like School.
				rostering.listLEAs(1, getPageSize()).getData().forEach(org->
					report.writeEntity(org.get$Sys().getId(), formatOrg(org), dumpObject(org)));
			} catch (ApiException e) {
				log(e);
			}
		};
	}

	/**
	 * Get {@code kimono:persons}
	 */
	public Fetcher listPersons() {
		return (report, props) -> {
			try {
				rostering.listPersons(1, getPageSize()).getData().forEach(person->
					report.writeEntity(person.get$Sys().getId(), formatPerson(person), dumpObject(person)));
			} catch (ApiException e) {
				log(e);
			}
		};
	}	

	/**
	 * Get {@code kimono:students}. Lists all {@code Person}s published by the
	 * {@code Student} topic; that is, objects where {@code $sys.person_type: "student"}
	 */
	public Fetcher listStudents() {
		return (report, props) -> {
			try {
				// === Clever porting note === In Kimono, a Student is a topic
				// and a type of Person entity where the $sys.person_type == 'student'.
				// The specific attributes mapped to Student may be different
				// from other Person topics like Teacher.
				rostering.listStudents(1, getPageSize()).getData().forEach(person->
					report.writeEntity(person.get$Sys().getId(), formatPerson(person), dumpObject(person)));
			} catch (ApiException e) {
				log(e);
			}
		};
	}	

	/**
	 * Get {@code kimono:teachers}. Lists all {@code Person}s published by the
	 * {@code Teacher} topic; that is, objects where {@code $sys.person_type: "teacher"}
	 */
	public Fetcher listTeachers() {
		return (report, props) -> {
			try {
				// === Clever porting note === In Kimono, a Teacher is a topic
				// and a type of Person entity where the $sys.person_type == 'teacher'.
				// The specific attributes mapped to Teacher may be different
				// from other Person topics like Student.
				rostering.listTeachers(1, getPageSize()).getData().forEach(person->
					report.writeEntity(person.get$Sys().getId(), formatPerson(person), dumpObject(person)));
			} catch (ApiException e) {
				log(e);
			}
		};
	}
	
	/**
	 * Get {@code kimono:terms}
	 */
	public Fetcher listTerms() {
		return (report, props) -> {
			try {
				rostering.listTerms(1, getPageSize()).getData().forEach(term->
					report.writeEntity(term.get$Sys().getId(), formatTerm(term), dumpObject(term)));
			} catch (ApiException e) {
				log(e);
			}
		};
	}	
	
	/**
	 * Get {@code kimono:courses}
	 */
	public Fetcher listCourses() {
		return (report, props) -> {
			try {
				rostering.listCourses(1, getPageSize()).getData().forEach(course->
					report.writeEntity(course.get$Sys().getId(), formatCourse(course), dumpObject(course)));
			} catch (ApiException e) {
				log(e);
			}
		};
	}	
	
	/**
	 * Get {@code kimono:sections}
	 */
	public Fetcher listSections() {
		return (report, props) -> {
			try {
				rostering.listSections(1, getPageSize()).getData().forEach(section->
					report.writeEntity(section.get$Sys().getId(), formatSection(section), dumpObject(section)));
			} catch (ApiException e) {
				log(e);
			}
		};
	}
	
	/**
	 * Helper to return the {@code object} when the {@code -full:true} property is
	 * specified, otherwise null. When {@code -full:true} is enabled, the full object
	 * is dumped to the console in addition to the single line description.
	 */
	private <T extends Object> T dumpObject( T object ) {
		return isFull() ? object : null; 
	}

	public static String formatPerson( Person person ) {
		return person == null ? "null" : person.getName().getFirst()+" "+person.getName().getLast()+" ("+person.getEmail()+")";
	}
	
	public static String formatOrg( Org org ) {
		return org == null ? "null" : org.getName();
	}
	
	public static String formatTerm( Term term ) {
		return term == null ? "null" : term.getName()+" ("+term.getStartDate()+" to "+term.getEndDate()+")";
	}
	
	public static String formatCourse( Course course ) {
		return course == null ? "null" : course.getLocalId() + " " + course.getTitle();		
	}	
	
	public static String formatSection( Section section ) {
		return section == null ? "null" : section.getLocalId() + " " + section.getTitle();		
	}
	
	public static String formatTenant( TenantInfo tenant ) {
		return tenant == null ? "null" : tenant.getName();
	}
	
}
