package kimono.examples.render;

import java.io.IOException;

import kimono.examples.directory.Directory;

public class DirectoryRenderer extends MarkdownRenderer<Directory> {

	public DirectoryRenderer( MarkdownRenderer<?> template ) {
		super(template);
	}
	
	@Override
	public void render(Directory directory, Directory dir) throws IOException {
		Markdown md = new Markdown();
		md.h1(dir.getName());
		md.hr();
		
		directory.getSchools().getData().forEach(school->{
			try {
				md.h2(school.getName());
				if( school.getAddress() != null ) {
					md.p(school.getAddress());
				}
				md.h3("Attributes");
				
				md.definition("Source ID", school.getSourceId());
				md.definition("ID", school.getId());
				md.definition("Address", school.getAddress());
				md.definition("Phone", school.getPhone());
				md.definition("URL", school.getUrl());
				
				md.h3("Relationships");
				
				md.p(school.getStudents().getTotalAvailable()+" [students](students.html)");
				md.p(school.getStaff().getTotalAvailable()+" [staff](staff.html)");
				md.p(school.getTerms().getTotalAvailable()+" [terms](terms.html)");
				md.p(school.getCourses().getTotalAvailable()+" [courses](courses.html)");
				
				md.hr();

				new StudentsRenderer(this).render(directory,school);
				new StaffRenderer(this).render(directory,school);
				new TermsRenderer(this).render(directory,school);
				new CoursesRenderer(this).render(directory,school);
			} catch( IOException ioe ) {
				ioe.printStackTrace();
			}
		});
		
		write(md, directory, "index.html");
	}
}
