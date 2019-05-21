package kimono.examples.datasource;

import java.util.Collection;

import kimono.examples.model.DirObject;

/**
 * A page of objects of type {@code T}.
 *
 * @param <T> The type of {@link DirObject}
 */
public class RecordSet<T extends DirObject> {

	private Collection<T> fData;
	private int fCount;
	private Page fPage;
	
	public RecordSet(Collection<T> data, Page page, int count) {
		fData = data;
		fCount = count;
		fPage = page;
	}

	public Collection<T> getData() {
		return fData;
	}

	public int getTotalAvailable() {
		return fCount;
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
