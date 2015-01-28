package net.onedaybeard.ecs.model.scan;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.Type;

/**
 * Default ECS classes for each family of classes (systems,
 * components, managers and factories).
 */
class TypeConfiguration {
	protected Set<Type> components;
	protected Set<Type> managers;
	protected Set<Type> systems;
	protected Set<Type> factories;

	public TypeConfiguration() {
		components = new HashSet<Type>();
		managers = new HashSet<Type>();
		systems = new HashSet<Type>();
		factories = new HashSet<Type>();
		loadTypes();
	}

	private void loadTypes() {
		Map<String, Set<Type>> keyToTypes = new HashMap<String, Set<Type>>();
		keyToTypes.put("COMPONENTS", components);
		keyToTypes.put("SYSTEMS", systems);
		keyToTypes.put("MANAGERS", managers);
		keyToTypes.put("FACTORIES", factories);

		InputStream is = null;
		Set<Type> current = null;
		try {
			is = TypeConfiguration.class.getResourceAsStream("/ecs-base-types.config");
			for (String s : IOUtils.readLines(is)) {
				if (s.isEmpty() || s.startsWith("#")) {
					continue;
				} else if (s.toUpperCase().equals(s)) {
					current = keyToTypes.get(s);
					assert current != null;
				} else {
					addType(s, current);
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