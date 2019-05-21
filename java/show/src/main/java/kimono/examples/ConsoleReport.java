package kimono.examples;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConsoleReport implements Report {

	@Override
	public void startSection(String title) {
		System.out.println("===== " + title + " =====");
	}

	@Override
	public void endSection() {
		System.out.println();
	}

	@Override
	public void line(String str) {
		System.out.println(str);
	}

	@Override
	public void writeEntity(UUID id, String label, Object object) {
		writeEntity(id.toString(),label,object);
	}

	@Override
	public void writeEntity(String id, String label, Object object) {
		StringBuilder b = new StringBuilder();
		b.append("(").append(id).append("): ").append(label);
		if( object != null ) {
			b.append("\r\n  ").append(object.toString());
		}
		line(b.toString());
	}

	@Override
	public void error(Exception ex, String responseBody, Map<String, List<String>> responseHeaders) {
		System.err.println(responseBody);
		ex.printStackTrace(System.err);
	}

}
