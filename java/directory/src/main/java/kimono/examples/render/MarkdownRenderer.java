package kimono.examples.render;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

import kimono.examples.directory.Directory;

public abstract class MarkdownRenderer<T> implements Renderer<T> {

	private File fBaseDir = new File(".");
	private Parser fParser;
	private HtmlRenderer fRenderer;

	public MarkdownRenderer( MarkdownRenderer<?> template ) {
		super();
		fBaseDir = template.fBaseDir;
		fParser = template.fParser;
		fRenderer = template.fRenderer;
	}
	
	public MarkdownRenderer() {
		MutableDataSet options = new MutableDataSet();
		options.set(Parser.EXTENSIONS, 
				Arrays.asList(TablesExtension.create(),DefinitionExtension.create()));
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

		fParser = Parser.builder(options).build();
        fRenderer = HtmlRenderer.builder(options).build();
	}
	
	public void setBaseDir( File folder ) {
		fBaseDir = folder;
	}
	
	protected void write( Markdown md, String filename ) throws IOException {
		File f = new File(fBaseDir,filename);
		FileUtils.writeStringToFile(f, markdownToHtml(md.toString()), "UTF-8");
	}
	
	protected void write( Markdown md, Directory directory, String ... path ) throws IOException {
		String file = StringUtils.join(path,File.separator);
		File folder = new File(fBaseDir,directory.getTenant().getTenantInfo().getId().toString());
		folder.mkdirs();
		FileUtils.writeStringToFile(new File(folder,file), markdownToHtml(md.toString()), "UTF-8");
	}
	
//	public void render(Contents toc) {
//		new ContentsRenderer().render(toc);
//		
//		// Write each Directory
//		for (Directory dir : toc.getDirectories()) {
//			new DirectoryRenderer().render(dir,dir);
//		}
//	}

//	protected String renderDirectory(Directory dir, List<String> lines) {
//			lines.add("# " + dir.getName() + " #");
//			lines.add("---");
//			dir.getSchools().getData().forEach(school->renderSchool(out,school));
//			}
//			str = buf.toString();
//		}
//		return markdownToHtml(str);
//	}
//
//	protected String renderIndex(Contents toc) throws IOException {
//		String str;
//		try (StringWriter buf = new StringWriter()) {
//			try (PrintWriter out = new PrintWriter(buf)) {
//				out.println("# Directory");
//				out.println("---");
//				toc.getDirectories().forEach(dir->{
//					out.println("## ["+dir.getName()+"]("+dir.getTenant().getId()+"/index.html)");
//					if( dir.getDistrict() != null ) {
//						out.println(dir.getDistrict().getName());
//						out.println(dir.getDistrict().getAddress());
//					}
//					if( dir.getSchools().getTotalAvailable() > 0 ) {
//						out.print(dir.getSchools().getTotalAvailable()+" schools, ");
//						out.print(dir.getTotalStudentCount()+" students, ");
//						out.print(dir.getTotalStaffCount()+" staff, ");
//						out.print(dir.getTotalCourseCount()+" courses, ");
//						out.print(dir.getTotalTermCount()+" terms, ");
//						out.println(dir.getTotalGradingCategoryCount()+" grading categories");
//					}
//				});
//			}
//			str= buf.toString();
//		}
//		return markdownToHtml(str);
//	}

	protected String markdownToHtml(String markdown) {
        String md = fRenderer.render(fParser.parse(markdown));
		
		return "<html><head>"
		+ "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />"
		+ "<title>Directory</title>"
		+ "<style>"
		+ "body { padding: 25px; font-family: Arial, Helvetica, sans-serif; font-size: 12pt; } "
		+ "nav { font-size: 10pt; } " 
		+ "dt { font-weight: bold; margin-bottom: .5em; } "
		+ "</style></head><body>"
		+ md
		+ "</body></html>";
	}

}
