package kimono.examples.model;

import java.util.UUID;

public abstract class AbstractDirObject implements DirObject {

	private String fSourceId;
	private boolean fDeleted;

	public AbstractDirObject(UUID sourceId) {
		this(sourceId == null ? null : sourceId.toString());
	}

	public AbstractDirObject(String sourceId) {
		fSourceId = sourceId;
	}

	@Override
	public String getSourceId() {
		return fSourceId;
	}
	
	@Override
	public boolean isDeleted() {
		return fDeleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		fDeleted = deleted;
	}
}
