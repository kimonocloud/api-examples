package kimono.examples.model;

/**
 * An object in the Directory.
 */
public interface DirObject {

	String getSourceId();

	boolean isDeleted();
	
	void setDeleted( boolean deleted );
	
	void copyValuesFrom( DirObject record );
}
