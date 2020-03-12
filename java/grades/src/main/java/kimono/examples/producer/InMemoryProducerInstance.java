package kimono.examples.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import kimono.api.v2.grades.ApiClient;
import kimono.api.v2.grades.GradesExchangeApi;
import kimono.api.v2.grades.model.Assignment;
import kimono.api.v2.interop.model.TenantInfo;
import kimono.api.v2.interopdata.ApiException;
import kimono.api.v2.interopdata.GradesApi;
import kimono.api.v2.interopdata.RosteringApi;
import kimono.api.v2.interopdata.model.GradingCategory;
import kimono.api.v2.interopdata.model.GradingCategorysResponse;
import kimono.api.v2.interopdata.model.Section;
import kimono.api.v2.interopdata.model.SectionsResponse;
import kimono.client.KimonoApis;
import kimono.client.impl.Credentials;
import kimono.examples.GradesExample;

public class InMemoryProducerInstance {

	private List<Section> rosteredSections = new ArrayList<>();
	private Map<UUID, List<GradingCategory>> categories = new HashMap<>();
	private InMemoryGradebook gradebook = new InMemoryGradebook();
	private GradesExchangeApi exchangeApi;
	private RosteringApi rosteringApi;
	private GradesApi gradesApi;
	private boolean isRostered = false;

	public InMemoryProducerInstance(String gradesAppTenantId, String credential) {
		ApiClient client = new ApiClient();
		client.setUsername(gradesAppTenantId);
		client.setPassword(credential);
		exchangeApi = new GradesExchangeApi(client);
	}

	public void initTenant(TenantInfo tenantInfo) {
		kimono.api.v2.interopdata.ApiClient client = KimonoApis.getInteropDataClient(Credentials.forTenant(tenantInfo));
		rosteringApi = new RosteringApi(client);
		gradesApi = new GradesApi(client);
	}
	
	public List<Assignment> getGradesForSection(UUID sectionId) {
		return gradebook.gradesForSection(sectionId);
	}

	public List<Assignment> getGradesForCourse(UUID courseId) {
		List<Assignment> grades = new ArrayList<>();
		rosteredSections.stream().filter(s -> s.get$Course().get$Sys().getId().equals(courseId)).forEach(section -> 
			grades.addAll(getGradesForSection(section.get$Sys().getId()))
		);
		return grades;
	}

	public List<Assignment> getAllGrades() {
		return gradebook.allGrades();
	}

	public GradesExchangeApi getExchangeApi() {
		return exchangeApi;
	}

	public boolean doRostering() {
		if (isRostered)
			return true;
		try {
			doSections();
			doCategories();
			gradebook.generateGrades(rosteredSections, categories);
			isRostered = true;
		} catch (ApiException e) {
			GradesExample.LOGGER.log(Level.SEVERE, "Could not roster sections:", e);
			rosteredSections.clear();
		}
		return isRostered;
	}

	private void doSections() throws ApiException {
		int page = 0;
		SectionsResponse response;
		do {
			response = rosteringApi.listSections(page++, 1000);
			rosteredSections.addAll(response.getData());
		} while (!response.getData().isEmpty());
	}

	private void doCategories() throws ApiException {
		int page = 0;
		GradingCategorysResponse response;
		do {
			response = gradesApi.listGradingCategories(page++, 1000);
			for (GradingCategory category : response.getData()) {
				UUID sectionId = category.get$Section().get$Sys().getId();
				if (!categories.containsKey(sectionId)) {
					categories.put(sectionId, new ArrayList<>());
				}
				categories.get(sectionId).add(category);
			}
		} while (!response.getData().isEmpty());
	}
}
