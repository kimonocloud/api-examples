package kimono.examples.model;

import java.util.UUID;

public class GradingCategoryImpl extends AbstractDirObject implements DirGradingCategory {

	private String fName;

	public GradingCategoryImpl(UUID sourceId) {
		super(sourceId);
	}

	public GradingCategoryImpl(String sourceId) {
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
		DirGradingCategory src = (DirGradingCategory)record;
		fName = src.getName();
	}
}
