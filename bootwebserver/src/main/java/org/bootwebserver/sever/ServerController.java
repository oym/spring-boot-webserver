package org.bootwebserver.sever;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.bootwebserver.ActivationMediaTypeFactory;
import org.bootwebserver.DirectoryContentHandler;
import org.bootwebserver.WebRootHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

/**
 * @author Flavio Ricci
 *
 */
@Controller
@RequestMapping("/")
public class ServerController {

	protected static final Logger logger = LoggerFactory.getLogger(ServerController.class);
	
	@Autowired WebRootHandler webRoot;
	@Autowired DirectoryContentHandler directoryContentHandler;
	
	
	protected static final ResponseEntity<Void> NOT_FOUND_ENTITY = new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	
	@ResponseBody
	@RequestMapping("**")
	public ResponseEntity<?> serveStatic(HttpServletRequest request) throws IOException {
		
		ResponseEntity<?> entity=NOT_FOUND_ENTITY;
		String remainder=extractPathFromPattern(request);

		if (webRoot.isDirectory(remainder)) {
			String directoryContent=directoryContentHandler.buildDirectoryContent(remainder);
			entity=ResponseEntity.ok()
		            .contentLength(directoryContent.length())
		            .contentType(MediaType.TEXT_HTML)
		            .body(new InputStreamResource(new ByteArrayInputStream(directoryContent.getBytes("UTF-8"))));
		}
		else {
			entity=serveEntity(webRoot.resolve(remainder));			
		}
		
		return entity;
	}
	
	protected ResponseEntity<?> serveEntity(Path targetPath) throws IOException {
		if (Files.exists(targetPath)) {
			MediaType mediaType= ActivationMediaTypeFactory.getMediaType(targetPath);
			if (logger.isDebugEnabled()) {
				logger.debug(mediaType + " " + targetPath.toAbsolutePath().normalize());
			}
			return ResponseEntity.ok()
		            .contentLength(Files.size(targetPath))
		            .contentType(mediaType)
		            .header("Content-Disposition", "inline;filename="+targetPath.getFileName().toString())
		            .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
		            .lastModified(Files.getLastModifiedTime(targetPath).toMillis())
		            .body(new InputStreamResource(Files.newInputStream(targetPath)));
		}
		
		return NOT_FOUND_ENTITY;
	}
	
	/**
	 * Extract path from a controller mapping. /controllerUrl/** => return matched **
	 * @param request incoming request.
	 * @return extracted path
	 */
	public static String extractPathFromPattern(final HttpServletRequest request){

		String path = (String) request.getAttribute(
	            HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	    String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

	    AntPathMatcher apm = new AntPathMatcher();
	    String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);

	    return finalPath;
	}
	
}
