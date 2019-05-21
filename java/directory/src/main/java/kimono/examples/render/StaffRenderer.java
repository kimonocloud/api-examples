package kimono.examples.render;

import java.io.IOException;

import kimono.examples.datasource.Page;
import kimono.examples.directory.Directory;
import kimono.examples.model.DirOrg;

public class StaffRenderer extends MarkdownRenderer<DirOrg> {

	public StaffRenderer( MarkdownRenderer<?> template ) {
		super(template);
	}
	
	@Override
	public void render(Directory directory, DirOrg school) throws IOException {
		Markdown md = new Markdown();

		md.h1("Staff");
		Page pg = school.getStaff().getPage();
		md.p("Page "+pg.getNumber()+" listing "+pg.getSize()+" of "+school.getStaff().getTotalAvailable()+" staff");
		md.hr();
		
		school.getStaff().getData().forEach(staff->{
			md.p(staff.getName());
		});
		
		write(md, directory, "staff.html");
	}
}
