package kimono.examples.render;

public class Markdown {

	private StringBuilder fBuf = new StringBuilder();
	
	public void p( String line ) {
		p();
		fBuf.append(line);
		p();
	}
	
	public void p() {
		fBuf.append("\n");
	}
	
	public void hr() {
		p("---");
	}
	
	public void h1( String h1 ) {
		p("# "+h1);
	}
	
	public void h2( String h2 ) {
		p("## "+h2);
	}
	
	public void h3( String h3 ) {
		p("### "+h3);
	}

	protected void definition( String label, String value ) {
		if( value != null ) {
			p(label);
			p(": "+value);
			p();
		}
	}

	@Override
	public String toString() {
		return fBuf.toString();
	}
}
