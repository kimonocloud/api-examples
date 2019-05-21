package kimono.examples.datasource;

import kimono.client.KimonoApiException;
import kimono.examples.model.DirCourse;
import kimono.examples.model.DirGradingCategory;
import kimono.examples.model.DirOrg;
import kimono.examples.model.DirPerson;
import kimono.examples.model.DirTerm;

public class OneRosterDataSource implements DataSource {

	@Override
	public DirOrg getDistrict() throws KimonoApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSet<DirOrg> getSchools(Page page) throws KimonoApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSet<DirPerson> getStudents(DirOrg school, Page page) throws KimonoApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSet<DirPerson> getStaff(DirOrg school, Page page) throws KimonoApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSet<DirTerm> getTerms(DirOrg school, Page page) throws KimonoApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSet<DirCourse> getCourses(DirOrg school, Page page) throws KimonoApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSet<DirGradingCategory> getGradingCategories(DirOrg school, Page page) throws KimonoApiException {
		// TODO Auto-generated method stub
		return null;
	}
}
