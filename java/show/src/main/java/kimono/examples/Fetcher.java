package kimono.examples;

@FunctionalInterface
public interface Fetcher {

	void fetch( Report report, Props props );
}
