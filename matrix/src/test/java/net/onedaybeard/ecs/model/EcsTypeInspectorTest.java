package net.onedaybeard.ecs.model;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EcsTypeInspectorTest {
	private static EcsTypeInspector inspector;
	private static MatrixData matrix;
    private static SortedMap<String, List<RowTypeMapping>> typeMap;

	@BeforeClass
	public static void init() {
		inspector = new EcsTypeInspector(Arrays.asList(TestHelper.classRootPath().toURI()), "");
		matrix = inspector.getMatrixData();
		assertNotNull(matrix);
		typeMap = inspector.getTypeMap();
		assertNotNull(typeMap);
	}

	@Test
	public void matrixColumnsTest() {
		assertTypes(
			Arrays.asList(
				"ExtPosition",
				"Position",
				"Velocity",
                "AllComponent",
                "OneComponent",
                "ExcludeComponent"),
			matrix.componentColumns);
	}

	@Test
	public void matrixManagerTest() {
		assertTypes(
			Arrays.asList("SomeManager"),
			matrix.managerColumns);
	}

	@Test
	public void matrixSystemTest() {
		assertTypes(
			Arrays.asList(
				"AnotherSystem",
				"ExtSomeSystem",
				"SomeSystem"),
			matrix.systemColumns);
	}

	private void assertTypes(Collection<String> names, List<String> columns) {
		String error = columns.toString();
		assertEquals(error, names.size(), columns.size());
		assertEquals(error, new HashSet<String>(names), new HashSet<String>(columns));
	}
}
