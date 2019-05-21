package kimono.examples.model;

/**
 * A course.
 * 
 * This interface abstracts the concept of a Course so that course objects
 * from disparate data sources (Kimono, OneRoster, Clever, etc.) can be represented in 
 * our Directory. Generally you would not do this in a client application because
 * Kimono provides that abstraction for you, but in this sample app, we want to 
 * demonstrate connecting to Kimono over different APIs.
 */
public interface DirCourse extends DirObject {
	
	String getName();
	
	void setName( String name );
}
