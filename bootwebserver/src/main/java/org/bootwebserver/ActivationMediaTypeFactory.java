package org.bootwebserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

/**
 * class to avoid a hard-coded JAF dependency
 * @author Flavio Ricci
 *
 */
public class ActivationMediaTypeFactory {
	
	private static final FileTypeMap fileTypeMap;

	static {
		fileTypeMap = loadFileTypeMapFromContextSupportModule();
	}

	private static FileTypeMap loadFileTypeMapFromContextSupportModule() {
		Resource mappingLocation = new ClassPathResource("org/bootwebserver/mime.types");
		if (mappingLocation.exists()) {
			InputStream inputStream = null;
			try {
				inputStream = mappingLocation.getInputStream();
				return new MimetypesFileTypeMap(inputStream);
			}
			catch (IOException ex) {
				// ignore
			}
			finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					}
					catch (IOException ex) {
						// ignore
					}
				}
			}
		}
		return FileTypeMap.getDefaultFileTypeMap();
	}

	public static MediaType getMediaType(Resource resource) {
		String filename = resource.getFilename();
		return getMediaType(filename);
	}
	
	public static MediaType getMediaType(Path targetPath) {
		String filename = targetPath.getFileName().toString();
		return getMediaType(filename);
	}
	
	public static MediaType getMediaType(String filename) {
		if (filename != null) {
			String mediaType = fileTypeMap.getContentType(filename);
			if (StringUtils.hasText(mediaType)) {
				return MediaType.parseMediaType(mediaType);				
			}
		}
		return null;
	}
}
