package kimono.examples.datasource;

import kimono.client.KimonoApiException;
import kimono.examples.model.DirCourse;
import kimono.examples.model.DirGradingCategory;
import kimono.examples.model.DirOrg;
import kimono.examples.model.DirPerson;
import kimono.examples.model.DirTerm;

/**
 * A source of rostering and grading category data.
 * <p>
 */
public interface DataSource {

	/**
	 * Get a single District to represent in the Directory. 
	 * @return
	 * @throws KimonoApiException
	 */
	DirOrg getDistrict() throws KimonoApiException;

	/**
	 * Get a page of schools
	 * @param district The parent district
	 * @param page The page
	 * @return
	 * @throws KimonoApiException
	 */
	RecordSet<DirOrg> getSchools(Page page) throws KimonoApiException;

	RecordSet<DirPerson> getStudents(DirOrg school, Page page) throws KimonoApiException;

	RecordSet<DirPerson> getStaff(DirOrg school, Page page) throws KimonoApiException;

	RecordSet<DirTerm> getTerms(DirOrg school, Page page) throws KimonoApiException;

	RecordSet<DirCourse> getCourses(DirOrg school, Page page) throws KimonoApiException;

	RecordSet<DirGradingCategory> getGradingCategories(DirOrg school, Page page) throws KimonoApiException;
}
