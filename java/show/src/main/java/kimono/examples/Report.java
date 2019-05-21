package kimono.examples;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface of a data object report 
 */
public interface Report {

	/**
	 * Start a titled section 
	 * @param title The title
	 */
	void startSection( String title );
	
	/**
	 * End a titled section
	 */
	void endSection();
	
	/**
	 * Write a line to the current section
	 * @param str
	 */
	void line( String str );

	/**
	 * Write an entity to the current section
	 * @param id The string identifier of the entity
	 * @param label The label of the entity
	 * @param entity The entity
	 */
	void writeEntity( String id, String label, Object entity );

	/**
	 * Write an entity to the current section
	 * @param id The UUID of the entity
	 * @param label The label of the entity
	 * @param entity The entity
	 */
	void writeEntity( UUID id, String label, Object entity );
	
	/**
	 * Write an error to the current section of the report
	 * @param ex The exception
	 * @param responseBody A response body associated with the error
	 * @param responseHeaders Response headers associated with the error
	 */
	void error( Exception ex, String responseBody, Map<String,List<String>> responseHeaders );
}
