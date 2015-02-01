package net.onedaybeard.ecs.model.scan;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.Type;

/**
 * Default ECS classes for each family of classes (systems,
 * components, managers and factories).
 */
class TypeConfiguration {
	private String resourcePrefix;
	protected Set<Type> components;
	protected Set<Type> managers;
	protected Set<Type> systems;
	protected Set<Type> factories;
	public Type componentMapper;

	String[] aspectRequire;
	String[] aspectRequireOne;
	String[] aspectExclude;

	public TypeConfiguration(String resourcePrefix) {
		components = new HashSet<Type>();
		managers = new HashSet<Type>();
		systems = new HashSet<Type>();
		factories = new HashSet<Type>();
		this.resourcePrefix = resourcePrefix;
		loadTypes();
		loadAspectMethods();
	}

	private void loadTypes() {
		Map<String, Set<Type>> keyToTypes = new HashMap<String, Set<Type>>();
		keyToTypes.put("COMPONENTS", components);
		keyToTypes.put("SYSTEMS", systems);
		keyToTypes.put("MANAGERS", managers);
		keyToTypes.put("FACTORIES", factories);
		keyToTypes.put("MAPPER", new HashSet<Type>());

		InputStream is = null;
		Set<Type> current = null;
		try {
			is = TypeConfiguration.class.getResourceAsStream(resourcePrefix + "/ecs-base-types.config");
			for (String s : IOUtils.readLines(is)) {
				if (s.isEmpty() || s.startsWith("#")) {
					continue;
				} else if (s.toUpperCase().equals(s)) {
					current = keyToTypes.get(s);
					assert current != null;
				} else {
					addType(s.trim(), current);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		componentMapper = keyToTypes.get("MAPPER").iterator().next();
	}

	private void loadAspectMethods() {
		Map<String, List<String>> methodNames = new HashMap<String, List<String>>();
		methodNames.put("ASPECT_REQUIRE", new ArrayList<String>());
		methodNames.put("ASPECT_REQUIRE_ONE", new ArrayList<String>());
		methodNames.put("ASPECT_EXCLUDE", new ArrayList<String>());

		InputStream is = null;
		List<String> current = null;
		try {
			is = TypeConfiguration.class.getResourceAsStream(resourcePrefix + "/aspect-methods.config");
			for (String s : IOUtils.readLines(is)) {
				if (s.isEmpty() || s.startsWith("#")) {
					continue;
				} else if (s.toUpperCase().equals(s)) {
					current = methodNames.get(s);
					assert current != null;
				} else {
					current.add(s.trim());
					Collections.sort(current);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String[] string = new String[0];
		aspectRequire = methodNames.get("ASPECT_REQUIRE").toArray(string);
		aspectRequireOne = methodNames.get("ASPECT_REQUIRE_ONE").toArray(string);
		aspectExclude = methodNames.get("ASPECT_EXCLUDE").toArray(string);
	}

	static Type type(Class<?> klazz) {
		return type(klazz.getName());
	}
	
	static Type type(String klazz) {
		klazz = klazz.replace('.', '/');
		return Type.getType("L" + klazz + ";");
	}
	
	static void addType(String qualifiedName, Set<Type> containerType) {
		containerType.add(type(qualifiedName));
	}
}