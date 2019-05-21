package kimono.examples.render;

import java.io.IOException;

import kimono.examples.directory.Directory;

@FunctionalInterface
public interface Renderer<T> {

	void render( Directory directory, T thing ) throws IOException;
}
