package kimono.examples.directory;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.KimonoApiException;
import kimono.client.util.TenantUtils;
import kimono.examples.datasource.DataSource;
import kimono.examples.datasource.Page;
import kimono.examples.datasource.RecordSet;
import kimono.examples.model.DirCourse;
import kimono.examples.model.DirGradingCategory;
import kimono.examples.model.DirOrg;
import kimono.examples.model.DirPerson;
import kimono.examples.model.DirTerm;

/**
 * An implementation of {@link Directory} that records a single page of records of
 * each kind in memory. In this example program there is no interaction with the
 * user so there's no way to page through data, but other examples build on this
 * one to add that capability.
 */
public class InMemoryDirectory implements Directory {

	/**
	 * The Kimono tenant represented by this directory
	 */
	private TenantInfo fTenant;

	/**
	 * The LEA ("district")
	 */
	DirOrg fLEA;

	/**
	 * Schools keyed by Source ID
	 */
	private RecordSet<DirOrg> fSchools;
	
	/**
	 * Constructor
	 * @param tenant The {@link TenantInfo} represented by this Directory
	 */
	public InMemoryDirectory(TenantInfo tenant) {
		fTenant = tenant;
	}
	
	/**
	 * Get the {@link TenantInfo} represented by this Directory
	 * @return The TenantInfo passed to the constructor
	 */
	@Override
	public TenantInfo getTenant() {
		return fTenant;
	}

	/**
	 * Refresh the Directory from a DataSource
	 */
	@Override
	public void populate(DataSource source) {
		Page firstPage = Page.firstPage();
		populateDistrict(source);
		populateSchools(source,firstPage);
		if( fSchools != null ) {
			populateCourses(source,firstPage);
			populateTerms(source,firstPage);
			populateStudents(source,firstPage);
			populateStaff(source,firstPage);
			populateGradingCategories(source,firstPage);
		}
	}

	protected void populateDistrict(DataSource source) {
		try {
			fLEA = source.getDistrict();
		} catch (KimonoApiException ex) {
			ex.printStackTrace();
		}
	}

	protected void populateSchools(DataSource source, Page page) {
		try {
			fSchools = source.getSchools(page);
		} catch (KimonoApiException ex) {
			ex.printStackTrace();
		}
	}

	protected void populateStudents(DataSource source, Page page) {
		try {
			for( DirOrg school : fSchools.getData() ) {
				school.setStudents(source.getStudents(school, page));
			}
		} catch (KimonoApiException ex) {
			ex.printStackTrace();
		}
	}

	protected void populateStaff(DataSource source, Page page) {
		try {
			for( DirOrg school : fSchools.getData() ) {
				school.setStaff(source.getStaff(school, page));
			}
		} catch (KimonoApiException ex) {
			ex.printStackTrace();
		}
	}

	protected void populateTerms(DataSource source, Page page) {
		try {
			for( DirOrg school : fSchools.getData() ) {
				school.setTerms(source.getTerms(school, page));
			}
		} catch (KimonoApiException ex) {
			ex.printStackTrace();
		}
	}

	protected void populateCourses(DataSource source, Page page) {
		try {
			for( DirOrg school : fSchools.getData() ) {
				school.setCourses(source.getCourses(school, page));
			}
		} catch (KimonoApiException ex) {
			ex.printStackTrace();
		}
	}

	protected void populateGradingCategories(DataSource source, Page page) {
		try {
			for( DirOrg school : fSchools.getData() ) {
				school.setGradingCategories(source.getGradingCategories(school, page));
			}
		} catch (KimonoApiException ex) {
			ex.printStackTrace();
		}
	}

	public DirOrg getLEA() {
		return fLEA;
	}

	@Override
	public RecordSet<DirOrg> getSchools() {
		return fSchools;
	}

	@Override
	public String getName() {
		if (fLEA != null) {
			if (fLEA.getName() != null) {
				return fLEA.getName();
			} else {
				return fLEA.getSourceId();
			}
		}
		return TenantUtils.describe(fTenant);
	}
	
	@Override
	public int getTotalStudentCount() {
		return fSchools.getData().stream().mapToInt(s->s.getStudents().getTotalAvailable()).sum();
	}
	
	@Override
	public int getTotalStaffCount() {
		return fSchools.getData().stream().mapToInt(s->s.getStaff().getTotalAvailable()).sum();
	}
	
	@Override
	public int getTotalCourseCount() {
		return fSchools.getData().stream().mapToInt(s->s.getCourses().getTotalAvailable()).sum();
	}
	
	@Override
	public int getTotalTermCount() {
		return fSchools.getData().stream().mapToInt(s->s.getTerms().getTotalAvailable()).sum();
	}
	
	@Override
	public int getTotalGradingCategoryCount() {
		return 0;//fSchools.getData().stream().mapToInt(s->s.getGradingCategories().getTotalAvailable()).sum();
	}

	@Override
	public void updateDistrict(DirOrg org) {
		if( fLEA != null && fLEA.getSourceId().equals(org.getSourceId()) ) {
			fLEA = org;
		}
	}

	@Override
	public void updateSchool(DirOrg org) {
		fSchools.update(org);
	}
	
	@Override
	public void updateStudent(DirPerson person) {
		fSchools.getData().forEach(s->s.getStudents().update(person));
	}

	@Override
	public void updateStaff(DirPerson person) {
		fSchools.getData().forEach(s->s.getStaff().update(person));
	}

	@Override
	public void updateTerm(DirTerm term) {
		fSchools.getData().forEach(s->s.getTerms().update(term));
	}

	@Override
	public void updateCourse(DirCourse course) {
		fSchools.getData().forEach(s->s.getCourses().update(course));
	}

	@Override
	public void updateGradingCategory(DirGradingCategory category) {
		fSchools.getData().forEach(s->s.getGradingCategories().update(category));
	}

	@Override
	public DirOrg getDistrict() {
		return fLEA;
	}

	@Override
	public void deleteDistrict(String sourceId) {
		if( fLEA != null && fLEA.getSourceId().equals(sourceId) ) {
			fLEA.setDeleted(true);
		}
	}
	
	@Override
	public void deleteSchool(String sourceId) {
		fSchools.delete(sourceId);
	}

	@Override
	public void deleteStudent(String sourceId) {
		fSchools.getData().forEach(s->s.getStudents().delete(sourceId));
	}

	@Override
	public void deleteStaff(String sourceId) {
		fSchools.getData().forEach(s->s.getStaff().delete(sourceId));
	}

	@Override
	public void deleteTerm(String sourceId) {
		fSchools.getData().forEach(s->s.getTerms().delete(sourceId));
	}

	@Override
	public void deleteCourse(String sourceId) {
		fSchools.getData().forEach(s->s.getCourses().delete(sourceId));
	}

	@Override
	public void deleteGradingCategory(String sourceId) {
		fSchools.getData().forEach(s->s.getGradingCategories().delete(sourceId));
	}
}
