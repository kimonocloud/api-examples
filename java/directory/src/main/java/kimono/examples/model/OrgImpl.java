package kimono.examples.model;

import java.util.UUID;

import kimono.examples.datasource.RecordSet;

public class OrgImpl extends AbstractDirObject implements DirOrg {

	private String fId;
	private String fName;
	private String fAddress;
	private String fPhone;
	private String fUrl;
	
	/**
	 */
	private RecordSet<DirTerm> fTerms;

	/**
	 */
	private RecordSet<DirCourse> fCourses;

	/**
	 */
	private RecordSet<DirPerson> fStudents;

	/**
	 */
	private RecordSet<DirPerson> fStaff;

	/**
	 */
	private RecordSet<DirGradingCategory> fGradingCategories;
	
	
	public OrgImpl(UUID sourceId) {
		super(sourceId);
	}

	public OrgImpl(String sourceId) {
		super(sourceId);
	}
	
	@Override
	public void copyValuesFrom(DirObject record) {
		DirOrg src = (DirOrg)record;
		fId = src.getId();
		fName = src.getName();
		fAddress = src.getAddress();
		fPhone = src.getPhone();
		fUrl = src.getUrl();
	}

	@Override
	public String getId() {
		return fId;
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public String getAddress() {
		return fAddress;
	}

	@Override
	public String getPhone() {
		return fPhone;
	}

	@Override
	public String getUrl() {
		return fUrl;
	}

	@Override
	public void setId(String id) {
		fId = id;
	}

	@Override
	public void setName(String name) {
		fName = name;
	}

	@Override
	public void setAddress(String addr) {
		fAddress = addr;
	}

	@Override
	public void setPhone(String phone) {
		fPhone = phone;
	}

	@Override
	public void setUrl(String url) {
		fUrl = url;
	}

	@Override
	public RecordSet<DirPerson> getStudents() {
		return fStudents;
	}

	@Override
	public RecordSet<DirPerson> getStaff() {
		return fStaff;
	}

	@Override
	public RecordSet<DirCourse> getCourses() {
		return fCourses;
	}

	@Override
	public RecordSet<DirTerm> getTerms() {
		return fTerms;
	}

	@Override
	public RecordSet<DirGradingCategory> getGradingCategories() {
		return fGradingCategories;
	}

	@Override
	public synchronized void setStudents(RecordSet<DirPerson> students) {
		fStudents = students;
	}

	@Override
	public void setStaff(RecordSet<DirPerson> staff) {
		fStaff = staff;
	}

	@Override
	public void setTerms(RecordSet<DirTerm> terms) {
		fTerms = terms;
	}

	@Override
	public void setCourses(RecordSet<DirCourse> courses) {
		fCourses = courses;
	}

	@Override
	public void setGradingCategories(RecordSet<DirGradingCategory> categories) {
		fGradingCategories = categories;
	}
}
