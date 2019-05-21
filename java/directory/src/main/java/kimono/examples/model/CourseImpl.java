package kimono.examples.model;

import java.util.UUID;

public class CourseImpl extends AbstractDirObject implements DirCourse {

	private String fName;

	public CourseImpl(UUID sourceId) {
		super(sourceId);
	}

	public CourseImpl(String sourceId) {
		super(sourceId);
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public void setName(String name) {
		fName = name;
	}

	@Override
	public void copyValuesFrom(DirObject record) {
		DirCourse src = (DirCourse)record;
		fName = src.getName();
	}
}
