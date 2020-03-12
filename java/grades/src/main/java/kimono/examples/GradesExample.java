package kimono.examples;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import kimono.api.v2.grades.ApiException;
import kimono.api.v2.grades.GradesExchangeApi;
import kimono.api.v2.grades.JSON;
import kimono.api.v2.grades.model.ExchangeError;
import kimono.api.v2.grades.model.ExchangeError.CodeEnum;
import kimono.api.v2.grades.model.ExchangeQuery;
import kimono.api.v2.grades.model.ExchangeQuery.TypeEnum;
import kimono.api.v2.grades.model.ExchangeQueryConditions;
import kimono.api.v2.grades.model.ExchangeQueryConditions.ScopeByEnum;
import kimono.api.v2.grades.model.ExchangeQueryConditions.ScopeEnum;
import kimono.api.v2.interop.ApiClient;
import kimono.api.v2.interop.Configuration;
import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.KCTenant;
import kimono.client.impl.TenantSupplier;
import kimono.client.impl.tasks.TaskAck;
import kimono.client.impl.tasks.TaskPoller;
import kimono.client.tasks.KCTask;
import kimono.client.tasks.KCTaskPoller;
import kimono.client.tasks.KCTaskType;
import kimono.examples.producer.InMemoryProducer;

/**
 * 
 */
public class GradesExample {
	public static final Logger LOGGER = Logger.getLogger(GradesExample.class.getName());

	ApiClient client;

	InMemoryProducer gradesProducer = new InMemoryProducer();

	/**
	 * Polling interval in seconds
	 */
	private int interval = 15;

	/**
	 * API Key
	 */
	private String apiKey;

	public GradesExample() {
		super();
	}

	public GradesExample parse(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("-apikey:")) {
				this.apiKey = arg.substring(8);
			}
		}
		return this;
	}

	public void run() throws Exception {
		GradesJobManager jobHandler = new GradesJobManager(this::handleGradesJob);
		jobHandler.run(1, TimeUnit.SECONDS);

		KCTaskPoller poller = new TaskPoller(new TenantSupplier().forTenants(gradesProducer.getTenants()));
		poller.setTaskHandler(KCTaskType.GRADES_QUERY, (tenant, task) -> {
			LOGGER.log(Level.INFO, "Enqueing task: " + task.getId());
			jobHandler.addJob(tenant, task);
			return TaskAck.success();
		});

		poller.poll(interval, TimeUnit.SECONDS);
		jobHandler.stop();
	}

	private void handleGradesJob(KCTenant tenant, KCTask task) {
		LOGGER.log(Level.INFO, "Working on task: " + task.getId());
		gradesProducer.ensureRostered(tenant.getTenantInfo());
		UUID tenantId = tenant.getTenantInfo().getId();
		GradesExchangeApi gradesApi = gradesProducer.getExchangeApi(tenantId);
		UUID exchangeId = UUID.fromString(task.getAttributes().getString("exchange_id"));
		int maxLength = task.getAttributes().getInt("max_data_content_length");
		ExchangeQuery query = new JSON().deserialize(task.getAttributes().getJSONObject("query").toString(), ExchangeQuery.class);
		try {
			if (query.getType() == TypeEnum.ASSIGNMENT_SCORES) {
				handleAssignmentScoresQuery(tenantId, gradesApi, exchangeId, maxLength, query.getConditions());
			} else if (query.getType() == TypeEnum.SUMMARY) {
				handleSummaryQueryNotSupported(gradesApi, exchangeId);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Unexpected error", e);
		}
		LOGGER.log(Level.INFO, "Done with task: " + task.getId());
	}

	private void handleAssignmentScoresQuery(UUID tenantId, GradesExchangeApi gradesApi, UUID exchangeId, int maxLength, 
			List<ExchangeQueryConditions> conditions) {
		GradesPublisher publisher = new GradesPublisher(gradesApi, exchangeId, maxLength);
		for (ExchangeQueryConditions condition : conditions) {
			if (condition.getScopeBy() == ScopeByEnum.ALL) {
				gradesProducer.getAllGrades(tenantId).forEach(publisher::addAssignmentWithScores);
			} else if (condition.getScopeBy() == ScopeByEnum.ID) {
				UUID scopeId = condition.getScopeId().get$Sys().getId();
				if (condition.getScope() == ScopeEnum.SECTION) {
					gradesProducer.getGradesForSection(tenantId, scopeId).forEach(publisher::addAssignmentWithScores);
				} else if (condition.getScope() == ScopeEnum.COURSE) {
					gradesProducer.getGradesForCourse(tenantId, scopeId).forEach(publisher::addAssignmentWithScores);
				}
			}
		}
		publisher.done();
	}

	private void handleSummaryQueryNotSupported(GradesExchangeApi gradesApi, UUID exchangeId) throws ApiException {
		ExchangeError error = new ExchangeError();
		error.setCode(CodeEnum.QUERY_TYPE_NOT_SUPPORTED);
		error.setDetails("GradesExample does not support summary grades queries.");
		gradesApi.createExchangeError(exchangeId, error);
	}

	/**
	 * Create and return an {@link ApiClient} configured for API Key authentication,
	 * which is required by the Tenants API (and select other Kimono APIs that use
	 * Vendor Authentication rather than Actor Authentication, which requires the
	 * OAuth2 protocol).
	 * 
	 * Actor Authentication is required to access the resources of an Actor (or
	 * "tenant") and thus each request to actor-scoped APIs must be authenticated
	 * using the credential supplied in a {@link TenantInfo}. API Key authentication
	 * is used to call Kimono APIs that do not directly access user data, such as
	 * obtaining a list of {@link TenantInfo}.
	 * 
	 * @return
	 */
	protected ApiClient getApiKeyClient() {
		if (client == null) {
			client = Configuration.getDefaultApiClient();
			client.setUsername(apiKey);
		}
		return client;
	}

	public static void main(String[] args) {
		try {
			new GradesExample().parse(args).run();
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Unexpected error", ex);
		}
	}
}
