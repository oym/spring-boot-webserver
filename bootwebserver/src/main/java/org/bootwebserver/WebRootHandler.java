/**
 * 
 */
package org.bootwebserver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author Flavio Ricci
 *
 */
public class WebRootHandler {

	protected static final Logger logger = LoggerFactory.getLogger(WebRootHandler.class);
	
	private Path root;
	
	public WebRootHandler(String rootPath) {
		if (rootPath!=null) {
			Path tmpRoot=Paths.get(rootPath);
			if (Files.exists(tmpRoot) && Files.isDirectory(tmpRoot) && Files.isReadable(tmpRoot)) {
				root=tmpRoot.normalize().toAbsolutePath();
			}
		}
		if (root==null) {
			root=Paths.get(System.getProperty("user.dir")).normalize().toAbsolutePath();
		}
		
		logger.info("Web root:" + root);
	}
	
	public String breadcrumb(Path path) {
		String breadcrumb=root.relativize(path).toString().replace("\\", "/");
		return StringUtils.hasText(breadcrumb)?"/".concat(breadcrumb):"../";
	}
	
	public Path resolve(String remainder) {
		return root.resolve(remainder);
	}
	
	public boolean isRoot(Path path) {
		return root.equals(path);
	}
	
	public boolean isDirectory(String remainder) {
		return Files.isDirectory(resolve(remainder));
	}
	
	public boolean isFile(String remainder) {
		return Files.isRegularFile(resolve(remainder));
	}
	
}
