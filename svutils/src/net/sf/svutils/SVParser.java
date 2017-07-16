package net.sf.svutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;

import net.sf.sveditor.core.db.index.ISVDBIndex;
import net.sf.sveditor.core.db.index.SVDBFSFileSystemProvider;
import net.sf.sveditor.core.db.index.SVDBIndexUtil;
import net.sf.sveditor.core.db.index.argfile.SVDBArgFileIndex;
import net.sf.sveditor.core.db.index.argfile.SVDBArgFileIndexFactory;
import net.sf.sveditor.core.db.index.cache.ISVDBIndexCacheMgr;
import net.sf.sveditor.core.db.index.cache.file.SVDBFileIndexCacheMgr;
import net.sf.sveditor.core.db.index.cache.file.SVDBFileSystem;

public class SVParser {

	public static ISVDBIndex vlog(
			File			cache_dir,
			List<String> 	args) throws Exception {
		SVDBFileSystem cache_fs = new SVDBFileSystem(cache_dir, "1.0.0");
		try {
			cache_fs.init();
		} catch (IOException e) {
			throw e;
		}
		
		ISVDBIndexCacheMgr cache_mgr = new SVDBFileIndexCacheMgr();
		((SVDBFileIndexCacheMgr)cache_mgr).init(cache_fs);

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		
		for (String arg : args) {
			arg = SVDBIndexUtil.expandVars(arg, null, false);
			if (arg.indexOf(' ') != -1 || arg.indexOf('\t') != -1) {
				ps.println("\"" + arg + "\"");
			} else {
				ps.println(arg);
			}
		}
		ps.close();
		
		System.out.println("File: " + bos.toString());
	
		String project = "GLOBAL";
		
		final String ARGFILE_NAME = "/ABCDEFGH/IJKLMNOP";
		String base_location = ARGFILE_NAME;
		
		SVDBFSFileSystemProvider fs_provider = new SVDBFSFileSystemProvider() {

			@Override
			public boolean fileExists(String path) {
				System.out.println("fileExists: " + path);
				if (path.equals(ARGFILE_NAME)) {
					return true;
				} else {
					return super.fileExists(path);
				}
			}

			@Override
			public InputStream openStream(String path) {
				System.out.println("openStream: " + path);
				if (path.equals(ARGFILE_NAME)) {
					return new ByteArrayInputStream(bos.toByteArray());
				}
				// TODO Auto-generated method stub
				return super.openStream(path);
			}

			@Override
			public boolean isDir(String path) {
				if (path.equals(ARGFILE_NAME)) {
					return false;
				} else {
					return super.isDir(path);
				}
			}
			
		};

		SVDBArgFileIndex index = new SVDBArgFileIndex(
				project, base_location, fs_provider, 
				cache_mgr.createIndexCache(project, base_location),
				null);

//		index = f.createSVDBIndex(
//				project,
//				base_location,
//				cache_mgr.createIndexCache(project, base_location),
//				null);
		
		index.init(new NullProgressMonitor(), null);
		
		// Now build the index
		index.loadIndex(new NullProgressMonitor());
		
		return index;		
	}
}
