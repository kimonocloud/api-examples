package kimono.examples.model;

import kimono.examples.datasource.RecordSet;

/**
 * An Organization such as a school or district.
 * 
 * This interface abstracts the concept of an Organization so that school and
 * district objects from disparate data sources (Kimono, OneRoster, Clever,
 * etc.) can be represented in our Directory. Generally you would not do this in
 * a client application because Kimono provides that abstraction for you, but in
 * this sample app, we want to demonstrate connecting to Kimono over different
 * APIs.
 */
public interface DirOrg extends DirObject {

	String getId();

	void setId(String id);

	String getName();

	void setName(String name);

	String getAddress();

	void setAddress(String addr);

	String getPhone();

	void setPhone(String phone);

	String getUrl();

	void setUrl(String url);
	
	void setStudents( RecordSet<DirPerson> students );
	
	void setStaff( RecordSet<DirPerson> staff );
	
	void setTerms( RecordSet<DirTerm> terms );
	
	void setCourses( RecordSet<DirCourse> courses );

	void setGradingCategories( RecordSet<DirGradingCategory> categories );
	
	RecordSet<DirPerson> getStudents();
	
	RecordSet<DirPerson> getStaff();
	
	RecordSet<DirCourse> getCourses();
	
	RecordSet<DirTerm> getTerms();
	
	RecordSet<DirGradingCategory> getGradingCategories();
}
