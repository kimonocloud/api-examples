package kimono.examples.clever;

import java.util.Properties;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.DataApi;
import io.swagger.client.auth.OAuth;
import io.swagger.client.model.District;
import io.swagger.client.model.DistrictResponse;
import io.swagger.client.model.DistrictsResponse;
import io.swagger.client.model.Name;
import io.swagger.client.model.School;
import io.swagger.client.model.SchoolResponse;
import io.swagger.client.model.SchoolsResponse;
import io.swagger.client.model.Student;
import io.swagger.client.model.StudentResponse;
import io.swagger.client.model.StudentsResponse;
import io.swagger.client.model.Teacher;
import io.swagger.client.model.TeacherResponse;
import io.swagger.client.model.TeachersResponse;
import kimono.examples.AbstractApi;
import kimono.examples.Fetcher;

/**
 * Fetches data from the Clever API 
 */
public class CleverData extends AbstractApi {

	ApiClient client;
	DataApi data;
	
	public CleverData( Properties props ) {
		super(props);
		setFetcher("districts", getDistricts());
		setFetcher("schools", getSchools());
		setFetcher("students", getStudents());
		setFetcher("teachers", getTeachers());
	}
	
	@Override
	public void authenticate( Properties props ) {
		if( client == null ) {
			client = Configuration.getDefaultApiClient();
			OAuth oauth = (OAuth) client.getAuthentication("oauth");
			oauth.setAccessToken("TEST_TOKEN");
			
			data = new DataApi();
		}
	}

	/**
	 * Get {@code clever:schools}
	 */
	public Fetcher getSchools() {
		return (report,props)->{
	        try {
	            SchoolsResponse schools = data.getSchools(props.getPageSize(), null, null);
	            for (SchoolResponse school : schools.getData()) {
	            	School s = school.getData();
	                report.line(s.getId()+": "+s.getName());
	            }
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
		};
	}

	/**
	 * Get {@code clever:districts}
	 */
	public Fetcher getDistricts() {
		return (report,props)->{
	        try {
	            DistrictsResponse districts = data.getDistricts();
	            for (DistrictResponse district : districts.getData()) {
	            	District d = district.getData();
	                report.line(d.getId()+": "+d.getName());
	            }
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
		};
	}
	
	/**
	 * Get {@code clever:students}
	 */
	public Fetcher getStudents() {
		return (report,props)->{
	        try {
	            StudentsResponse students = data.getStudents(props.getPageSize(), null, null);
	            for (StudentResponse student : students.getData()) {
	            	Student s = student.getData();
	            	Name n = s.getName();
	            	report.line(s.getId()+": "+n.getLast()+", "+n.getFirst()+" ("+s.getEmail()+")");
	            }
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
		};
	}
	
	/**
	 * Get {@code clever:teachers}
	 */
	public Fetcher getTeachers() {
		return (report,props)->{
	        try {
	            TeachersResponse teachers = data.getTeachers(props.getPageSize(), null, null);
	            for (TeacherResponse teacher : teachers.getData()) {
	            	Teacher t = teacher.getData();
	            	Name n = t.getName();
	            	report.line(t.getId()+": "+n.getLast()+", "+n.getFirst()+" ("+t.getEmail()+")");
	            }
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
		};
	}	
}
