package kimono.examples.datasource;

/**
 * Identifies page number and size for paging through a collection of resources.
 */
public class Page {

	private static int sPageSize = 25;
	
	/**
	 * Zero-based page number
	 */
	private int fNumber;
	
	/**
	 * Number of objects per page
	 */
	private int fSize;
	
	private Page( int number, int size) {
		fNumber = number;
		fSize = size;
	}
	
	public static void setDefaultPageSize( int size ) {
		sPageSize=size;
	}
	
	/**
	 * Construct a {@link Page} representing the first page
	 * @return 
	 */
	public static Page firstPage() {
		return new Page(0,sPageSize);
	}
	
	/**
	 * Construct a {@link Page} that represents the next page relative to {@code page}
	 * @return 
	 */
	public static Page nextPage( Page page ) {
		return new Page(page.getNumber()+1,page.getSize());
	}
	
	/**
	 * Construct a {@link Page} that represents the previous page relative to {@code page}
	 * @return 
	 */
	public static Page prevPage( Page page ) {
		if( page.isFirstPage() ) {
			return new Page(page.getNumber(),page.getSize());
		}
		return new Page(page.getNumber()-1,page.getSize());
	}
	
	public boolean isFirstPage() {
		return fNumber == 0;
	}
	
	public int getNumber() {
		return fNumber;
	}
	
	public int getSize() {
		return fSize;
	}
}
