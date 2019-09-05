package kimono.examples.directory;

import kimono.client.KCTenant;
import kimono.client.KimonoApiException;
import kimono.examples.datasource.DataSource;
import kimono.examples.datasource.RecordSet;
import kimono.examples.model.DirCourse;
import kimono.examples.model.DirGradingCategory;
import kimono.examples.model.DirOrg;
import kimono.examples.model.DirPerson;
import kimono.examples.model.DirTerm;

public interface Directory {
	
	KCTenant getTenant();
	
	String getName();
	
	void populate( DataSource source ) throws KimonoApiException;

	
	DirOrg getDistrict();

	RecordSet<DirOrg> getSchools();

	
	int getTotalStudentCount();
	
	int getTotalStaffCount();
	
	int getTotalCourseCount();
	
	int getTotalTermCount();
	
	int getTotalGradingCategoryCount();
	
	
	void updateDistrict( DirOrg org );

	void updateSchool( DirOrg org );

	void updateStudent( DirPerson person );
	
	void updateStaff( DirPerson person );
	
	void updateTerm( DirTerm term );
	
	void updateCourse( DirCourse course );	

	void updateGradingCategory( DirGradingCategory category );
	

	void deleteDistrict( String sourceId );

	void deleteSchool( String sourceId );

	void deleteStudent( String sourceId );
	
	void deleteStaff( String sourceId );
	
	void deleteTerm( String sourceId );
	
	void deleteCourse( String sourceId );	

	void deleteGradingCategory( String sourceId );
}
