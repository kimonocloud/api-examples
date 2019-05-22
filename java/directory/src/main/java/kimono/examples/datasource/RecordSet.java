package kimono.examples.datasource;

import java.util.Collection;
import java.util.Collections;

import kimono.examples.model.DirObject;

/**
 * A page of objects of type {@code T}.
 *
 * @param <T> The type of {@link DirObject}
 */
public class RecordSet<T extends DirObject> {

	private Collection<T> fData;
	private Integer fCount;
	private Page fPage;

	/**
	 * Construct an empty record set
	 */
	public RecordSet() {
		this(Collections.emptyList(), Page.firstPage(), null);
	}

	/**
	 * Construct a RecordSet for a page of a collection
	 * @param data The data in this page
	 * @param page The page
	 * @param count The total number of records available
	 */
	public RecordSet(Collection<T> data, Page page, Integer count) {
		fData = data;
		fCount = count;
		fPage = page;
	}

	public Collection<T> getData() {
		return fData;
	}
	
	public boolean hasCount() {
		return fCount != null;
	}

	public int getTotalAvailable() {
		return fCount == null ? 0 : fCount;
	}
	
	public Page getPage() {
		return fPage;
	}
	
	@SuppressWarnings("unchecked")
	public T find( String sourceId ){
		return (T)fData.stream().filter(s->s.getSourceId().equals(sourceId));
	}
	
	public T update( T object ) {
		T rec = find(object.getSourceId());
		if( rec != null ) {
			rec.copyValuesFrom(object);
			return rec;
		}
		return null;
	}
	
	public T delete( String sourceId ) {
		T rec = find(sourceId);
		if( rec != null ) {
			rec.setDeleted(true);
			return rec;
		}
		return null;
	}
}
