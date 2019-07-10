package kimono.examples.oneroster;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import kimono.examples.AbstractApi;
import kimono.examples.Fetcher;
import kimono.examples.Report;
import kimono.oneroster.v1p1.ApiClient;
import kimono.oneroster.v1p1.ApiException;
import kimono.oneroster.v1p1.api.RosteringApi;
import kimono.oneroster.v1p1.model.GUIDRef;
import kimono.oneroster.v1p1.model.ModelClass;
import kimono.oneroster.v1p1.model.Org;
import kimono.oneroster.v1p1.model.User;

/**
 * Fetches data from a OneRoster 1.1 API implementation 
 */
public class OneRoster extends AbstractApi {

	ApiClient actorAuthClient;
	RosteringApi rostering;
	
	public OneRoster( Properties props ) {
		super(props);
		
		setFetcher("orgs", getOrgs());
		setFetcher("leas", getDistricts());
		setFetcher("schools", getSchools());
		setFetcher("persons", getUsers());
		setFetcher("students", getStudents());
		setFetcher("teachers", getTeachers());
		setFetcher("terms", getAcademicSessions());
		setFetcher("courses", getCourses());
		setFetcher("sections", getSections());
		setFetcher("personorgmemberships", getPersonOrgMembership());
		setFetcher("personsectionmemberships", getPersonSectionMembership());
	}
	
	@Override
	public void authenticate( Properties props ) {
		if( actorAuthClient == null ) {
			// APIs that connect to a specific actor use Actor Authentication. Obtain 
			// the client ID and client secret from the Actor page in Dashboard or from 
			// the GET /interop/tenants API. Specify as properties "client" and "secret".
			Map<String,String> p = new HashMap<>();
			actorAuthClient = new ApiClient(props.getProperty("client"),props.getProperty("secret"),p);

			rostering = new RosteringApi(actorAuthClient);
		}
	}		

	/**
	 * {@code GET /academicsessions}
	 */
	public Fetcher getAcademicSessions() {
		return (report,props)->{
	        try {
	            rostering.getAllAcademicSessions().getAcademicSessions().forEach(term->
	                report.writeEntity(term.getSourcedId(), term.getTitle(), term));
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}

	/**
	 * {@code GET /schools}
	 */
	public Fetcher getSchools() {
		return (report,props)->{
	        try {
	            rostering.getAllSchools().getOrgs().forEach(org->
	            	report.writeEntity(org.getSourcedId(), formatOrg(org), org));
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}

	/**
	 * {@code GET /orgs}
	 */
	public Fetcher getOrgs() {
		return (report,props)->{
	        try {
	            rostering.getAllOrgs().getOrgs().forEach(org->
	                report.writeEntity(org.getSourcedId(), formatOrg(org), org));
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}	

	/**
	 * {@code GET /orgs} and filter the results by type "district"
	 */
	public Fetcher getDistricts() {
		return (report,props)->{
	        try {
	            rostering.getAllOrgs().getOrgs().stream().filter(o->o.getType().equals("district")).collect(Collectors.toList()).forEach(org->
	                report.writeEntity(org.getSourcedId(), formatOrg(org), org));
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}		

	/**
	 * {@code GET /students}
	 */
	public Fetcher getStudents() {
		return (report,props)->{
	        try {
	        	rostering.getAllStudents().getUsers().forEach(user->
        			report.writeEntity(user.getSourcedId(), formatUser(user), user));
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}	

	/**
	 * {@code GET /teachers}
	 */
	public Fetcher getTeachers() {
		return (report,props)->{
	        try {
	            rostering.getAllTeachers().getUsers().forEach(user->
            		report.writeEntity(user.getSourcedId(), formatUser(user), user));
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}

	/**
	 * {@code GET /users}
	 */
	public Fetcher getUsers() {
		return (report,props)->{
	        try {
	        	rostering.getAllUsers().getUsers().forEach(user->
        			report.writeEntity(user.getSourcedId(), formatUser(user), user));
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}
	
	/**
	 * {@code GET /courses}
	 */
	public Fetcher getCourses() {
		return (report,props)->{
	        try {
	        	rostering.getAllCourses().getCourses().forEach(course->
        			report.writeEntity(course.getSourcedId(), "(" + course.getCourseCode() + ") " + course.getTitle(), course));
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}	
	
	/**
	 * {@code GET /classes}
	 */
	public Fetcher getSections() {
		return (report,props)->{
	        try {
	        	rostering.getAllClasses().getClasses().forEach(section->
        			report.writeEntity(section.getSourcedId(), formatSection(section), section));
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}	

	/**
	 * {@code GET /enrollments}
	 */
	public Fetcher getPersonSectionMembership() {
		return (report,props)->{
	        try {
	        	rostering.getAllEnrollments().getEnrollments().forEach(enr->{
	        		
	        		// Get the referenced student, school, and class
	        		User user = getUser( enr.getUser() == null ? null : enr.getUser().getSourcedId(), report );
	        		String userStr = reference(formatUser(user),"user",enr.getUser());
	        		Org school = getSchool( enr.getSchool() == null ? null : enr.getSchool().getSourcedId(), report);
	        		String schoolStr = reference(formatOrg(school),"school",enr.getSchool());
	        		ModelClass section = getSection(enr.getClass() == null ? null : enr.getPropertyClass().getSourcedId(), report);
	        		String sectionStr = reference(formatSection(section),"class",enr.getPropertyClass());
	        		
        			report.writeEntity(enr.getSourcedId(), userStr + " enrolled in " + sectionStr + " in " + schoolStr + " (" + enr.getBeginDate() + " thru " + enr.getEndDate() +")", enr );
	        	});
	        } catch (ApiException e) {
	            report.error(e,e.getResponseBody(),e.getResponseHeaders());
	        }
		};
	}	

	public Fetcher getPersonOrgMembership() {
		return (report,props)->report.line("OneRoster does not have an independent entity that models person/org membership. See User.orgs.");
	}
	
	private String reference( String label, String type, GUIDRef ref ) {
		if( ref == null )
			return type+":null";
		if( label != null )
			return label;
		return ref.getSourcedId();
	}

	/**
	 * Get the specified User
	 * @param sourcedId The user's {@code sourcedId}
	 * @param report The Report to handle any error that occurs
	 * @return The User returned from the server or null if none found or if the {@code sourcedId} is null
	 */
	public User getUser( String sourcedId, Report report ) {
		try {
			return sourcedId == null ? null : rostering.getUser(sourcedId).getUser();
		} catch( ApiException e ) {
            report.error(e,e.getResponseBody(),e.getResponseHeaders());
            return null;
		}
	}		
	
	/**
	 * Get the specified School
	 * @param sourcedId The school's {@code sourcedId}
	 * @param report The Report to handle any error that occurs
	 * @return The Org returned from the server or null if none found or if the {@code sourcedId} is null
	 */
	public Org getSchool( String sourcedId, Report report ) {
		try {
			return sourcedId == null ? null : rostering.getSchool(sourcedId).getOrg();
		} catch( ApiException e ) {
            report.error(e,e.getResponseBody(),e.getResponseHeaders());
            return null;
		}
	}		
	
	/**
	 * Get the specified Class
	 * @param sourcedId The class's {@code sourcedId}
	 * @param report The Report to handle any error that occurs
	 * @return The Class returned from the server or null if none found or if the {@code sourcedId} is null
	 */
	public ModelClass getSection( String sourcedId, Report report ) {
		try {
			return sourcedId == null ? null : rostering.getClass(sourcedId).getPropertyClass();
		} catch( ApiException e ) {
            report.error(e,e.getResponseBody(),e.getResponseHeaders());
            return null;
		}
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
