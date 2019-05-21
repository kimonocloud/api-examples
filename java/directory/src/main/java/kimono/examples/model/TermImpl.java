package kimono.examples.model;

import java.time.LocalDate;
import java.util.UUID;

public class TermImpl extends AbstractDirObject implements DirTerm {

	private String fName;
	private LocalDate fStartDate;
	private LocalDate fEndDate;

	public TermImpl(UUID sourceId) {
		super(sourceId);
	}

	public TermImpl(String sourceId) {
		super(sourceId);
	}
	
	@Override
	public void copyValuesFrom(DirObject record) {
		DirTerm src = (DirTerm)record;
		fName = src.getName();
		fStartDate = src.getStartDate();
		fEndDate = src.getEndDate();
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
	public LocalDate getStartDate() {
		return fStartDate;
	}

	@Override
	public void setStartDate(LocalDate date) {
		fStartDate = date;
	}

	@Override
	public LocalDate getEndDate() {
		return fEndDate;
	}

	@Override
	public void setEndDate(LocalDate date) {
		fEndDate = date;
	}
}
