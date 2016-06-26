package net.onedaybeard.ecs.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class ComponentDependencyMatrixTest {

	private File output;

	@Before
	public void setup() {
		output = new File("hello.html");
		output.delete();
	}

	@After
	public void tearDown() {
		output.delete();
	}

	@Test
	public void ensureNoErrors() {
		ComponentDependencyMatrix cdm =	new ComponentDependencyMatrix(
			"test project",
			Arrays.asList(TestHelper.classRootPath().toURI()),
			output);

		cdm.process();

	}
}