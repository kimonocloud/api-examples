package kimono.examples.model;

/**
 * A Person.
 * 
 * This interface abstracts the concept of a Person so that student and staff
 * objects from disparate data sources (Kimono, OneRoster, Clever, etc.) can be
 * represented in our Directory. Generally you would not do this in a client
 * application because Kimono provides that abstraction for you, but in this
 * sample app, we want to demonstrate connecting to Kimono over different APIs.
 */
public interface DirPerson extends DirObject {

	String getId();
	
	void setId( String id );

	String getName();
	
	void setName( String name );
	
	String getEmail();
	
	void setEmail( String email );

	String getGradeLevel();
	
	void setGradeLevel( String gradeLevel );
}
