package kimono.examples.render;

import java.io.IOException;

import kimono.examples.datasource.Page;
import kimono.examples.directory.Directory;
import kimono.examples.model.DirOrg;

public class TermsRenderer extends MarkdownRenderer<DirOrg> {

	public TermsRenderer( MarkdownRenderer<?> template ) {
		super(template);
	}
	
	@Override
	public void render(Directory directory, DirOrg school) throws IOException {
		Markdown md = new Markdown();

		md.h1("Terms");
		Page pg = school.getTerms().getPage();
		md.p("Page "+pg.getNumber()+" listing "+pg.getSize()+" of "+school.getTerms().getTotalAvailable()+" terms");
		md.hr();
		
		school.getTerms().getData().forEach(term->{
			md.p(term.getName());
		});
		
		write(md, directory, "terms.html");
	}
}
