package org.bootwebserver;

import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 
 * @author Flavio Ricci
 * 
 */
@SpringBootApplication
public class Bootstrap extends WebMvcConfigurationSupport {

	static protected void commandLineParser(String[] args) throws ParseException {
		Options options=new Options();
		options.addOption("r", "root", true, 
				"Web root (default: current directory)");
		options.addOption("p", "port", true, "Server port (default: 3000)");
		// parse the command line args
		CommandLineParser parser = new PosixParser();
		CommandLine cl = parser.parse(options, args, false);
		// set properties
		Properties as = System.getProperties();
		String webRoot = cl.getOptionValue("r");
		if (StringUtils.hasText(webRoot)) {
			as.put("web.root", webRoot);			
		}
		String portString = cl.getOptionValue("p", "3000");
		as.put("server.port", portString);		
	}


	public static void main(String[] args) throws ParseException {
		commandLineParser(args);
		SpringApplication.run(Bootstrap.class, args);
	}


	@Bean
	public WebRootHandler webRootHandler() {
		return new WebRootHandler(System.getProperty("web.root"));
	}

	@Bean
	public DirectoryContentHandler directoryContentHandler() {
		return new DirectoryContentHandler();
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new ResourceHttpMessageConverter());
	}

	/*	@Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(new ResourceHttpMessageConverter(), new ByteArrayHttpMessageConverter(), new StringHttpMessageConverter(), new FormHttpMessageConverter(), new SourceHttpMessageConverter<>());
    }*/

}
