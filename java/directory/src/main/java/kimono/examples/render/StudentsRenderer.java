package kimono.examples.render;

import java.io.IOException;

import kimono.examples.datasource.Page;
import kimono.examples.directory.Directory;
import kimono.examples.model.DirOrg;

public class StudentsRenderer extends MarkdownRenderer<DirOrg> {

	public StudentsRenderer( MarkdownRenderer<?> template ) {
		super(template);
	}
	
	@Override
	public void render(Directory directory, DirOrg school) throws IOException {
		Markdown md = new Markdown();

		md.h1("Students");
		Page pg = school.getStudents().getPage();
		md.p("Page "+pg.getNumber()+" listing "+pg.getSize()+" of "+school.getStudents().getTotalAvailable()+" students");
		md.hr();
		
		school.getStudents().getData().forEach(student->{
			md.p(student.getName());
		});
		
		write(md, directory, "students.html");
	}
}
