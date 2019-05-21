package kimono.examples.render;

import java.io.IOException;

import kimono.examples.directory.Contents;
import kimono.examples.directory.Directory;

public class ContentsRenderer extends MarkdownRenderer<Contents> {

	public ContentsRenderer() {
		super();
	}
	
	@Override
	public void render( Directory directory, Contents toc ) throws IOException {
		Markdown md = new Markdown();
		md.h1("Directory");
		md.hr();

		toc.getDirectories().forEach(dir->{
			md.h2("["+dir.getName()+"]("+dir.getTenant().getId()+"/index.html)");
			if( dir.getDistrict() != null ) {
				md.p(dir.getDistrict().getName());
				md.p(dir.getDistrict().getAddress());
			}
			if( dir.getSchools().getTotalAvailable() > 0 ) {
				md.p(
					dir.getSchools().getTotalAvailable()+" schools, " +
					dir.getTotalStudentCount()+" students, " + 
					dir.getTotalStaffCount()+" staff, " +
					dir.getTotalCourseCount()+" courses, " + 
					dir.getTotalTermCount()+" terms, " +
					dir.getTotalGradingCategoryCount()+" grading categories");
			}
		});
		
		write(md,"index.html");
		
		// Write each Directory
		for (Directory dir : toc.getDirectories()) {
			new DirectoryRenderer(this).render(dir,dir);
		}
	}
}
