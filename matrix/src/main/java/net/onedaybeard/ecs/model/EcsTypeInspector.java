package net.onedaybeard.ecs.model;

import net.onedaybeard.ecs.model.scan.ConfigurationResolver;
import net.onedaybeard.ecs.model.scan.EcsTypeData;
import net.onedaybeard.ecs.util.ClassFinder;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Detailed introspection of all ECS-related types.
 */
public class EcsTypeInspector {
	private EcsMapping model = null;
	private final ConfigurationResolver initialTypeScan;

	public EcsTypeInspector(File root, String resourcePrefix) {
		this.initialTypeScan = new ConfigurationResolver(root, resourcePrefix);

		if (initialTypeScan.components.size() == 0
				&& initialTypeScan.systems.size() == 0
				&& initialTypeScan.managers.size() == 0
				&& initialTypeScan.factories.size() == 0) {

			String error = "No ECS classes found on classpath. "
					+ "See https://github.com/junkdog/artemis-odb/wiki/Component-Dependency-Matrix for more info.";
			throw new RuntimeException(error);
		}

		List<EcsTypeData> ecsTypes = findEcsTypes(root);
		if (ecsTypes.size() == 0)
			return;

		model = new EcsMapping(initialTypeScan, ecsTypes);
	}

	SortedMap<String,List<RowTypeMapping>> getTypeMap() {
		assert model != null;

		String common = findCommonPackage(model.typeMappings);
		SortedMap<String, List<RowTypeMapping>> map = new TreeMap<String, List<RowTypeMapping>>();
		for (int i = 0, s = model.typeMappings.size(); s > i; i++) {
			RowTypeMapping system = model.typeMappings.get(i);
			String packageName = toPackageName(system.ecsType.getClassName());
			packageName = (packageName.length() > common.length())
					? packageName.substring(common.length())
					: ".";
			if (!map.containsKey(packageName))
				map.put(packageName, new ArrayList<RowTypeMapping>());

			map.get(packageName).add(system);
		}

		return map;
	}

	MatrixData getMatrixData() {
		assert model != null;
		return model.matrixData;
	}

	boolean foundEcsClasses() {
		return model != null;
	}

	private List<EcsTypeData> findEcsTypes(File root) {
		List<EcsTypeData> systems = new ArrayList<EcsTypeData>();
		for (File f : ClassFinder.find(root))
			inspectType(f, systems);

		Collections.sort(systems, new TypeComparator());
		return systems;
	}

	private void inspectType(File file, List<EcsTypeData> destination) {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			ClassReader cr = new ClassReader(stream);
			Type objectType = Type.getObjectType(cr.getClassName());
			if (!isEcsType(objectType))
				return;

			EcsTypeData meta = initialTypeScan.scan(cr);
			meta.current = objectType;
			destination.add(meta);
		} catch (FileNotFoundException e) {
			System.err.println("not found: " + file);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isEcsType(Type objectType) {
		return initialTypeScan.managers.contains(objectType)
				|| initialTypeScan.systems.contains(objectType)
				|| initialTypeScan.factories.contains(objectType);
	}

	private static String findCommonPackage(List<RowTypeMapping> systems) {
		String prefix = toPackageName(systems.get(0).ecsType.getClassName());
		for (int i = 1, s = systems.size(); s > i; i++) {
			String p = toPackageName(systems.get(i).ecsType.getClassName());
			for (int j = 0, l = Math.min(prefix.length(), p.length()); l > j; j++) {
				if (prefix.charAt(j) != p.charAt(j)) {
					prefix = prefix.substring(0, j);
					break;
				}
			}
		}

		return prefix;
	}

	private static String toPackageName(String className) {
		return className.substring(0, className.lastIndexOf('.'));
	}

	private static class TypeComparator implements Comparator<EcsTypeData> {
		@Override
		public int compare(EcsTypeData o1, EcsTypeData o2) {
			return o1.current.toString().compareTo(o2.current.toString());
		}
	}
}
