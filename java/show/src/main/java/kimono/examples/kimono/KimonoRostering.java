package kimono.examples.kimono;

import java.util.Properties;

import kimono.api.v2.interopdata.ApiClient;
import kimono.api.v2.interopdata.ApiException;
import kimono.api.v2.interopdata.Configuration;
import kimono.api.v2.interopdata.RosteringApi;
import kimono.api.v2.interopdata.TasksApi;
import kimono.api.v2.interopdata.model.Course;
import kimono.api.v2.interopdata.model.NameType;
import kimono.api.v2.interopdata.model.Org;
import kimono.api.v2.interopdata.model.OrgsResponse;
import kimono.api.v2.interopdata.model.Person;
import kimono.api.v2.interopdata.model.Section;
import kimono.api.v2.interopdata.model.Term;
import kimono.examples.AbstractApi;
import kimono.examples.Fetcher;
import kimono.oneroster.v1p1.model.ModelClass;
import kimono.oneroster.v1p1.model.User;

/**
 * Fetches data from Kimono Rostering APIs
 */
public class KimonoRostering extends AbstractApi {

	ApiClient client;
	RosteringApi rostering;
	TasksApi tasks;

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
		setFetcher("personorgmemberships", listPersonOrgMemberships());
		setFetcher("personsectionmemberships", listPersonSectionMemberships());
	}
	
	@Override
	public void authenticate( Properties props ) {
		if( client == null ) {
			client = Configuration.getDefaultApiClient();
			client.setAccessToken(props.getProperty("token"));
			client.setUsername(props.getProperty("username"));
			client.setPassword(props.getProperty("password"));
			client.setUserAgent("Kimono API Example");

			rostering = new RosteringApi();
			tasks = new TasksApi();
		}
	}		

	/**
	 * {@code GET /orgs}
	 */
	public Fetcher listOrgs() {
		return (report, props) -> {
			try {
				rostering.listOrgs(null, null).getData().forEach(org->
					report.writeEntity(org.get$Sys().getId(), formatOrg(org), org));
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
				for (Org org : rostering.listSchools(null, null).getData()) {
					report.line(org.get$Sys().getId() + ": " + org.getName());
				}
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
				OrgsResponse rsp = rostering.listLEAs(null, null);
				if( rsp.getData() != null ) {
					for (Org org : rsp.getData() ) {
						report.line(org.get$Sys().getId() + ": " + org.getName());
					}
				}
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
				for (Person person : rostering.listPersons(null, null).getData()) {
					report.line(person.get$Sys().getId() + ": " + person.getName());
				}
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
				for (Person person : rostering.listStudents(null, null).getData()) {
					NameType name = person.getName();
					report.line(person.get$Sys().getId() + ": " + name.getLast() + ", " + name.getFirst() +
							" (" + person.getEmail() + ")");
				}
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
				for (Person person : rostering.listTeachers(null, null).getData()) {
					NameType name = person.getName();
					report.line(person.get$Sys().getId() + ": " + name.getLast() + ", " + name.getFirst() +
							" (" + person.getEmail() + ")");
				}
			} catch (ApiException e) {
				log(e);
			}
		};
	}
	
	/**
	 * Get {@code kimono:sessions}
	 */
	public Fetcher listTerms() {
		return (report, props) -> {
			try {
				for (Term session : rostering.listTerms(null, null).getData()) {
					report.line(session.get$Sys().getId() + ": " + session.getName() + " ("+session.getStartDate()+" to "+session.getEndDate()+")");
				}
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
				for (Course course : rostering.listCourses(null, null).getData()) {
					report.line(course.get$Sys().getId() + ": " + course.getLocalId() + " " + course.getTitle());
				}
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
				for (Section section : rostering.listSections(null, null).getData()) {
					report.line(section.get$Sys().getId() + ": " + section.getLocalId() + " " + section.getTitle());
				}
			} catch (ApiException e) {
				log(e);
			}
		};
	}
	
	/**
	 * Get {@code kimono:sections}
	 */
	public Fetcher listPersonOrgMemberships() {
		return (report, props) -> {
			try {
				for (Section section : rostering.listSections(null, null).getData()) {
					report.line(section.get$Sys().getId() + ": " + section.getLocalId() + " " + section.getTitle());
				}
			} catch (ApiException e) {
				log(e);
			}
		};
	}
	
	/**
	 * Get {@code kimono:sections}
	 */
	public Fetcher listPersonSectionMemberships() {
		return (report, props) -> {
			try {
				for (Section section : rostering.listSections(null, null).getData()) {
					report.line(section.get$Sys().getId() + ": " + section.getLocalId() + " " + section.getTitle());
				}
			} catch (ApiException e) {
				log(e);
			}
		};
	}	
	
	public static String formatUser( User user ) {
		return user == null ? "null" : user.getGivenName()+" "+user.getFamilyName()+" ("+user.getEmail()+")";
	}
	
	public static String formatOrg( Org org ) {
		return org == null ? "null" : org.getName();
	}
	
	public static String formatSection( ModelClass section ) {
		return section == null ? "null" : section.getTitle();		
	}	
	
}
