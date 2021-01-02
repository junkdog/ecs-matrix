package com.artemis.cli;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.artemis.cli.converter.FileOutputConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import net.onedaybeard.ecs.model.ComponentDependencyMatrix;

@Parameters(
		commandDescription="Generate the Component Dependency Matrix from existing classes")
public class MatrixCommand {
	static final String COMMAND = "matrix";

	@Parameter(
		names = {"-h", "--help"},
		description= "Displays this help message.",
		help = true)
	boolean help;

	@Parameter(
		names = {"-l", "--label"},
		description = "Project name, used as page title",
		required = false)
	private String projectName = "Unnamed project";
	
	@Parameter(
		names = {"-c", "--class-folder"},
		description = "Root class folder",
		converter = FileConverter.class,
		required = true)
	private final List<File> classRoot = new ArrayList<>();
	
	@Parameter(
		names = {"-o", "--output"},
		description = "Save to file, destination may be given as a folder path",
		converter = FileOutputConverter.class,
		required = false)
	private File output = new File("matrix.html");
	
	void execute() {
		ComponentDependencyMatrix cdm =
			new ComponentDependencyMatrix(projectName, convert(classRoot), output);
		System.out.println(cdm.detectAndProcess());
	}
	
	private List<URI> convert(List<File> files) {
	    return files.stream()
	            .map(File::toURI)
	            .collect(Collectors.toList());
	}
}
