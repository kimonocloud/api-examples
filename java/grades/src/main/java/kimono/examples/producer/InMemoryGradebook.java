package kimono.examples.producer;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import kimono.api.v2.grades.model.Assignment;
import kimono.api.v2.grades.model.AssignmentScore;
import kimono.api.v2.grades.model.AssignmentSys;
import kimono.api.v2.grades.model.GradesCategoryRefType;
import kimono.api.v2.grades.model.GradesRefType;
import kimono.api.v2.grades.model.GradesRefTypeSys;
import kimono.api.v2.interopdata.model.GradingCategory;
import kimono.api.v2.interopdata.model.PersonMembershipRefType;
import kimono.api.v2.interopdata.model.PersonMembershipSys;
import kimono.api.v2.interopdata.model.Section;

public class InMemoryGradebook {
	private Random random = new Random();
	private int assignmentIdSequence = 0;
	private Map<UUID, List<Assignment>> assignments = new HashMap<>();

	public List<Assignment> gradesForSection(UUID sectionId) {
		return assignments.get(sectionId);
	}
	
	public List<Assignment> allGrades() {
		List<Assignment> allGrades = new ArrayList<>();
		assignments.values().forEach(allGrades::addAll);
		return allGrades;
	}

	public void generateGrades(List<Section> sections, Map<UUID, List<GradingCategory>> categories) {
		for(Section section : sections) {
			UUID sectionId = section.get$Sys().getId();
			assignments.put(sectionId, new ArrayList<>());
			if(categories.containsKey(sectionId)) {
				for(GradingCategory category : categories.get(sectionId)) {
					Assignment assignment = new Assignment();
					assignment.setName(category.getName() + " Assignment");
					assignment.set$Sys(createSysType(Integer.toString(assignmentIdSequence++)));
//					assignment.set$Category(createCategoryRefTypeByName("MyAppCategory"));
					assignment.set$Section(createRefType(sectionId));
					assignment.setAvailableAt(OffsetDateTime.now(ZoneOffset.UTC));
					assignment.setDescription("This is an assignment for the " + category.getName() + " category.");
					assignment.setDueAt(OffsetDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.WEEKS));
					assignment.setPointsPossible(BigDecimal.valueOf(Math.random() * 100.0));
					Map<String,String> ext = new HashMap<>();
					ext.put("an_ext_key", "an_ext_value");
					assignment.set$Ext(ext);
					generateScores(assignment, section);
					assignments.get(sectionId).add(assignment);
				}
			}
		}
	}
	
	private void generateScores(Assignment assignment, Section section) {
		UUID teacherId = section.get$Teachers().stream().findFirst().map(t -> t.get$Sys().getId()).orElse(null);
		List<AssignmentScore> scores = new ArrayList<>();
		for(PersonMembershipRefType student : section.get$Students().stream().map(PersonMembershipSys::get$Sys).collect(Collectors.toList())) {
			AssignmentScore score = new AssignmentScore();
			if(teacherId != null) {
				score.set$Grader(createRefType(teacherId));
			}
			score.set$Person(createRefType(student.getId()));
			score.setExcused(random.nextBoolean());
			score.setLate(random.nextBoolean());
			score.setGrade("A");
			score.setScore(BigDecimal.valueOf(Math.random() * assignment.getPointsPossible().doubleValue()));
			score.setSubmittedAt(OffsetDateTime.now(ZoneOffset.UTC).plus((int)(Math.random() * 72.0), ChronoUnit.HOURS));
			Map<String,String> ext = new HashMap<>();
			ext.put("time_on_task", "10 hours");
			score.set$Ext(ext);
			scores.add(score);
		}
		assignment.setScores(scores);
	}
	
	private GradesRefType createRefType(UUID kimonoId) {
		GradesRefType refType = new GradesRefType();
		GradesRefTypeSys sys = new GradesRefTypeSys();
		sys.setId(kimonoId);
		refType.set$Sys(sys);
		return refType;
	}
	
	private GradesCategoryRefType createCategoryRefType(UUID kimonoId) {
		GradesCategoryRefType refType = new GradesCategoryRefType();
		GradesRefTypeSys sys = new GradesRefTypeSys();
		sys.setId(kimonoId);
		refType.set$Sys(sys);
		return refType;
	}
	
	private GradesCategoryRefType createCategoryRefTypeByName(String name) {
		GradesCategoryRefType refType = new GradesCategoryRefType();
		refType.setName(name);
		return refType;
	}
	
	private AssignmentSys createSysType(String gradesId) {
		AssignmentSys sys = new AssignmentSys();
		Map<String,String> appIds = new HashMap<>();
		appIds.put("grades-producer", gradesId);
		sys.setAppId(appIds);
		return sys;
	}
}
