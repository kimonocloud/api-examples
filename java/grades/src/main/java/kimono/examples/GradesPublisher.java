package kimono.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import kimono.api.v2.grades.ApiException;
import kimono.api.v2.grades.GradesExchangeApi;
import kimono.api.v2.grades.JSON;
import kimono.api.v2.grades.model.Assignment;
import kimono.api.v2.grades.model.AssignmentScore;
import kimono.api.v2.grades.model.ExchangeDataRequest;

public class GradesPublisher {

	private final GradesExchangeApi api;
	private final UUID exchangeId;
	private final int maxLength;
	private ExchangeDataRequest currentRequest = null;
	private Assignment currentAssignment = null;
	private int currentLength = 0;

	public GradesPublisher(GradesExchangeApi api, UUID exchangeId, int maxLength) {
		this.api = api;
		this.exchangeId = exchangeId;
		this.maxLength = maxLength;
		buildNewRequest();
	}

	public void addAssignmentWithScores(Assignment assignment) {
		List<AssignmentScore> scores = assignment.getScores();
		assignment.setScores(new ArrayList<>());
		addAssignment(assignment);
		scores.forEach(this::addScore);
	}

	public void addAssignment(Assignment assignment) {
		checkLength(assignment);	

		currentAssignment = assignment;
		currentAssignment.setScores(new ArrayList<>());
		currentRequest.addAssignmentsItem(currentAssignment);
	}

	public void addScore(AssignmentScore score) {
		checkLength(score);
		if(currentRequest.getAssignments() == null || currentRequest.getAssignments().isEmpty()) {
			currentRequest.addAssignmentsItem(currentAssignment);
		}
		currentAssignment.addScoresItem(score);
	}

	public void done() {
		sendCurrentRequest(true);
	}

	private void checkLength(Object obj) {
		int newLength = lengthOfContent(obj);
		currentLength += newLength;
		if(currentLength > maxLength) {
			sendCurrentRequest(false);
			buildNewRequest();
			currentLength = newLength;
		}
	}
	
	private void buildNewRequest() {
		currentRequest = new ExchangeDataRequest();
		if(currentAssignment != null) {
			currentAssignment.setScores(new ArrayList<>());
		}
	}

	private void sendCurrentRequest(boolean isFinal) {
		try {
			currentRequest.setFinal(isFinal);
			api.createExchangeData(exchangeId, currentRequest);
			GradesExample.LOGGER.log(Level.INFO, "Sent Grades Request");
		} catch (ApiException e) {
			GradesExample.LOGGER.log(Level.SEVERE, "Unexpected error", e);
		}
	}
	
	private int lengthOfContent(Object obj) {
		return new JSON().serialize(obj).getBytes().length + 3; // 3 byte buffer for comma, {, }, [, and/or ].
	}
}
