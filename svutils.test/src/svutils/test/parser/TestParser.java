package svutils.test.parser;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;
import net.sf.sveditor.core.SVCorePlugin;
import net.sf.sveditor.core.db.index.ISVDBIndex;
import net.sf.sveditor.core.db.index.SVDBDeclCacheItem;
import net.sf.sveditor.core.db.search.SVDBAllTypeMatcher;
import net.sf.svutils.SVParser;

public class TestParser extends TestCase {
	
	private static void delete(File f) {
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				delete(ff);
			}
		}
		f.delete();
	}
	
	public void testParse() throws Exception {
		SVCorePlugin.testInit();
		SVCorePlugin.getDefault().enableDebug(true);
		File test_dir = new File(System.getProperty("user.dir"), "test_dir");
		delete(test_dir);
		
		File cache_dir = new File(test_dir, "db");
		File src = new File(test_dir, "src");
		
		assertTrue(cache_dir.mkdirs());
		assertTrue(src.mkdirs());
		
		PrintStream ps = new PrintStream(new File(src, "top.sv"));
		ps.println("module top;");
		ps.println("endmodule");
		ps.close();
		
		List<String> args = new ArrayList<String>();
		args.add(new File(src, "top.sv").getAbsolutePath());
		
		ISVDBIndex index = SVParser.vlog(cache_dir, args);
		
		Iterable<String> files = index.getFileList(new NullProgressMonitor());
		
		for (String f : files) {
			System.out.println("File: " + f);
		}
		
		List<SVDBDeclCacheItem> result = index.findGlobalScopeDecl(
				new NullProgressMonitor(), "", new SVDBAllTypeMatcher());
		
		for (SVDBDeclCacheItem c : result) {
			System.out.println("Cache: " + c.getName());
		}
	}

}
