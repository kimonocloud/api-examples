package kimono.examples.model;

import java.util.UUID;

/**
 * Interface of a Student in the Directory
 */
public class PersonImpl extends AbstractDirObject implements DirPerson {

	private String fId;
	private String fName;
	private String fEmail;
	private String fGrade;

	public PersonImpl(UUID sourceId) {
		super(sourceId);
	}
	
	public PersonImpl(String sourceId) {
		super(sourceId);
	}

	@Override
	public void copyValuesFrom(DirObject record) {
		DirPerson src = (DirPerson)record;
		fId = src.getId();
		fName = src.getName();
		fEmail = src.getEmail();
		fGrade = src.getGradeLevel();
	}

	@Override
	public String getId() {
		return fId;
	}
	
	@Override
	public void setId(String id) {
		fId = id;
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public String getGradeLevel() {
		return fGrade;
	}

	@Override
	public void setName(String name) {
		fName = name;
	}

	@Override
	public void setGradeLevel(String gradeLevel) {
		fGrade = gradeLevel;
	}

	@Override
	public String getEmail() {
		return fEmail;
	}

	@Override
	public void setEmail(String email) {
		fEmail = email;
	}
}
