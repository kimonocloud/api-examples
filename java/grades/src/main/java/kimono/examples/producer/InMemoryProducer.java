package kimono.examples.producer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kimono.api.v2.grades.GradesExchangeApi;
import kimono.api.v2.grades.model.Assignment;
import kimono.api.v2.interop.model.TenantInfo;

public class InMemoryProducer {

	Map<UUID, InMemoryProducerInstance> instances = new HashMap<>();

	public UUID[] getTenants() {
		return instances.keySet().stream().toArray(UUID[]::new);
	}

	public GradesExchangeApi getExchangeApi(UUID tenantId) {
		return instances.get(tenantId).getExchangeApi();
	}

	public synchronized void ensureRostered(TenantInfo tenantInfo) {
		InMemoryProducerInstance instance = instances.get(tenantInfo.getId());
		instance.initTenant(tenantInfo);
		instance.doRostering();
	}

	public void addInstance(String integrationTenantid, String gradesAppTenantId, String credential) {
		instances.put(UUID.fromString(integrationTenantid), new InMemoryProducerInstance(gradesAppTenantId, credential));
	}

	public List<Assignment> getGradesForSection(UUID tenantId, UUID sectionId) {
		return instances.get(tenantId).getGradesForSection(sectionId);
	}

	public List<Assignment> getGradesForCourse(UUID tenantId, UUID courseId) {
		return instances.get(tenantId).getGradesForCourse(courseId);
	}

	public List<Assignment> getAllGrades(UUID tenantId) {
		return instances.get(tenantId).getAllGrades();
	}
}
