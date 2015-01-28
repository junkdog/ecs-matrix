package net.onedaybeard.ecs.model;

import java.io.File;

public class TestHelper {
	private TestHelper() {}

	public static File classRootPath() {
		return new File("target/test-classes").getAbsoluteFile();
	}
}
