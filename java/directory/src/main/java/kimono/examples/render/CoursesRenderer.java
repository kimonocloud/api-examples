package kimono.examples.render;

import java.io.IOException;

import kimono.examples.datasource.Page;
import kimono.examples.directory.Directory;
import kimono.examples.model.DirOrg;

public class CoursesRenderer extends MarkdownRenderer<DirOrg> {

	public CoursesRenderer( MarkdownRenderer<?> template ) {
		super(template);
	}
	
	@Override
	public void render(Directory directory, DirOrg school) throws IOException {
		Markdown md = new Markdown();

		md.h1("Courses");
		Page pg = school.getCourses().getPage();
		md.p("Page "+pg.getNumber()+" listing "+pg.getSize()+" of "+school.getCourses().getTotalAvailable()+" courses");
		md.hr();
		
		school.getCourses().getData().forEach(course->{
			md.p(course.getName());
		});
		
		write(md, directory, "courses.html");
	}
}
