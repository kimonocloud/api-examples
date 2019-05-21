package kimono.examples.model;

import java.time.LocalDate;

/**
 * A term.
 * 
 * This interface abstracts the concept of a "term" or "period of time" so that
 * terms from disparate data sources (Kimono, OneRoster, Clever, etc.) can be
 * represented in our Directory. Generally you would not do this in a client
 * application because Kimono provides that abstraction for you, but in this
 * sample app, we want to demonstrate connecting to Kimono over different APIs.
 */
public interface DirTerm extends DirObject {

	String getName();
	
	void setName( String name );
	
	LocalDate getStartDate();
	
	void setStartDate( LocalDate date );
	
	LocalDate getEndDate();
	
	void setEndDate( LocalDate date );
}
