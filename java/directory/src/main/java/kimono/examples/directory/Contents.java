package kimono.examples.directory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Contents {

	private List<Directory> fDirectories = new LinkedList<>();
	
	public void add( Directory dir ) {
		fDirectories.add(dir);
	}
	
	public Collection<Directory> getDirectories() {
		return Collections.unmodifiableList(fDirectories);
	}
}
