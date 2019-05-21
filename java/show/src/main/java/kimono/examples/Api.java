package kimono.examples;

import java.util.Properties;

public interface Api {
	
	void authenticate( Properties props );
	
	void fetch( String resource, Report report );
}
