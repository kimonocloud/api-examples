package kimono.examples.datasource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.api.v2.interopdata.ApiClient;
import kimono.api.v2.interopdata.RosteringApi;
import kimono.api.v2.interopdata.model.Course;
import kimono.api.v2.interopdata.model.CoursesResponse;
import kimono.api.v2.interopdata.model.Org;
import kimono.api.v2.interopdata.model.OrgsResponse;
import kimono.api.v2.interopdata.model.PagedDataResponseTypePaging;
import kimono.api.v2.interopdata.model.Person;
import kimono.api.v2.interopdata.model.PersonsResponse;
import kimono.api.v2.interopdata.model.Term;
import kimono.api.v2.interopdata.model.TermsResponse;
import kimono.client.KimonoApiException;
import kimono.client.impl.DefaultInteropDataClientFactory;
import kimono.client.util.DataUtils;
import kimono.examples.model.CourseImpl;
import kimono.examples.model.DirCourse;
import kimono.examples.model.DirGradingCategory;
import kimono.examples.model.DirOrg;
import kimono.examples.model.DirPerson;
import kimono.examples.model.DirTerm;
import kimono.examples.model.OrgImpl;
import kimono.examples.model.PersonImpl;
import kimono.examples.model.TermImpl;

public class KimonoDataSource implements DataSource {

	private ApiClient fClient;
	private RosteringApi fRostering;

	public KimonoDataSource(TenantInfo tenant) {
		fClient = new DefaultInteropDataClientFactory().authenticate(tenant);
		fRostering = new RosteringApi(fClient);
	}

	@Override
	public DirOrg getDistrict() throws KimonoApiException {
		try {
			// Fetch a list of Kimono LEAs and return the first one
			List<Org> orgs = fRostering.listLEAs().getData();
			if( orgs != null ) {
				return transform(orgs.stream().findFirst().orElse(null));
			}
			return null;
		} catch (Exception ex) {
			throw new KimonoApiException(ex);
		}
	}

	@Override
	public RecordSet<DirOrg> getSchools(Page page) throws KimonoApiException {
		try {
			// Fetch a list of Kimono Schools
			OrgsResponse response = fRostering.listSchools();
			
			// Return transformed as DirOrg objects along with count of total objects available
			return new RecordSet<>(response.getData().stream().map(this::transform).collect(Collectors.toList()),
					page,getRecordCount(response.getPaging()));
		} catch (Exception ex) {
			throw new KimonoApiException(ex);
		}
	}

	@Override
	public RecordSet<DirPerson> getStudents(DirOrg school, Page page) throws KimonoApiException {
		try {
			// Fetch a list of Kimono students
			PersonsResponse response = fRostering.listStudentsForOrg(UUID.fromString(school.getSourceId()));
			
			// Return transformed as DirPerson objects along with count of total objects available
			return new RecordSet<>(response.getData().stream().map(this::transform).collect(Collectors.toList()),
					page,getRecordCount(response.getPaging()));
		} catch (Exception ex) {
			throw new KimonoApiException(ex);
		}
	}

	@Override
	public RecordSet<DirPerson> getStaff(DirOrg school, Page page) throws KimonoApiException {
		try {
			// Fetch a list of Kimono staff
			PersonsResponse response = fRostering.listTeachersForOrg(UUID.fromString(school.getSourceId()));
			
			// Return transformed as DirPerson objects along with count of total objects available
			return new RecordSet<>(response.getData().stream().map(this::transform).collect(Collectors.toList()),
					page,getRecordCount(response.getPaging()));
		} catch (Exception ex) {
			throw new KimonoApiException(ex);
		}
	}

	@Override
	public RecordSet<DirTerm> getTerms(DirOrg school, Page page) throws KimonoApiException {
		try {
			// Fetch a list of Kimono terms
			TermsResponse response = fRostering.listTermsForOrg(UUID.fromString(school.getSourceId()));
			
			// Return transformed as DirTerm objects along with count of total objects available
			return new RecordSet<>(response.getData().stream().map(this::transform).collect(Collectors.toList()),
					page,getRecordCount(response.getPaging()));
		} catch (Exception ex) {
			throw new KimonoApiException(ex);
		}
	}

	@Override
	public RecordSet<DirCourse> getCourses(DirOrg school, Page page) throws KimonoApiException {
		try {
			// Fetch a list of Kimono courses
			CoursesResponse response = fRostering.listCoursesForOrg(UUID.fromString(school.getSourceId()));
			
			// Return transformed as DirCourse objects along with count of total objects available
			return new RecordSet<>(response.getData().stream().map(this::transform).collect(Collectors.toList()),
					page,getRecordCount(response.getPaging()));
		} catch (Exception ex) {
			throw new KimonoApiException(ex);
		}
	}

	@Override
	public RecordSet<DirGradingCategory> getGradingCategories(DirOrg school, Page page) throws KimonoApiException {
		return null;
	}

	/**
	 * Transform a Kimono {@code Org} to a {@link DirOrg}
	 */
	protected DirOrg transform(Org org) {
		if( org == null ) {
			return null;
		}
		OrgImpl o = new OrgImpl(org.get$Sys().getId());
		o.setId(org.getLocalId());
		o.setName(org.getName());
		
		// This DirectoryExample recognizes three custom attributes for Org: $ext.phone, $ext.url,
		// and $ext.address. These are not Core Attributes defined by Kimono but this illustrates 
		// how to map custom attributes to an Integration. To ensure all data sources provided 
		// these elements, you could create custom Mapping Packages that are installed for 
		// OneRoster, SIF, etc.
		
		o.setPhone(DataUtils.asString(org.get$Ext(), "phone"));
		o.setUrl(DataUtils.asString(org.get$Ext(), "url"));
		o.setAddress(DataUtils.asString(org.get$Ext(), "address"));
		
		return o;
	}
	
	/**
	 * Transform a Kimono {@code Person} to a {@link DirPerson}
	 */
	protected DirPerson transform(Person person) {
		if( person == null ) {
			return null;
		}
		PersonImpl p = new PersonImpl(person.get$Sys().getId());
		p.setId(person.getLocalId());
		p.setName(DataUtils.combine(person.getName()));
		p.setEmail(person.getEmail());
		return p;
	}
	
	/**
	 * Transform a Kimono {@code Term} to a {@link DirTerm}
	 */
	protected DirTerm transform(Term term) {
		if( term == null ) {
			return null;
		}
		TermImpl t = new TermImpl(term.get$Sys().getId());
		t.setName(term.getName());
		return t;
	}
	
	/**
	 * Transform a Kimono {@code Course} to a {@link DirCourse}
	 */
	protected DirCourse transform(Course course) {
		if( course == null ) {
			return null;
		}
		CourseImpl c = new CourseImpl(course.get$Sys().getId());
		c.setName(course.getTitle());
		return c;
	}
	
	protected int getRecordCount( PagedDataResponseTypePaging paging ) {
		return 0;
	}
}
