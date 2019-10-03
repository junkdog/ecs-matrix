package net.onedaybeard.ecs.model.scan;

import org.objectweb.asm.Type;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;


public class ClassStore {
	public final List<ClassData> classes;

	private final ConfigurationResolver resolver;

	public ClassStore(ConfigurationResolver resolver, List<URI> uris) {
		this.resolver = resolver;

		classes = collectClasses(uris);
		resolveExtendedTypes(resolver.typeConfiguration, mapParentToChildren(classes));
		classes.forEach(this::findEcsTypes);
	}

	private List<ClassData> collectClasses(List<URI> uris) {
		List<ClassData> found = new ArrayList<>();

		for (URI uri : uris) {
			if (uri.getPath().endsWith(".jar")) {
				fileSystem(uri)
					.getRootDirectories()
					.forEach(root -> walk(root)
						.filter(f -> f.toString().endsWith(".class"))
						.map(ClassData::new)
						.forEach(found::add));
			} else {
				walk(Paths.get(uri))
					.filter(f -> f.toString().endsWith(".class"))
					.map(ClassData::new)
					.forEach(found::add);
			}
		}

		return found;
	}

	private void findEcsTypes(ClassData cd) {
		cd.accept(new SurfaceTypeCollector(resolver));
	}

	private static void resolveExtendedTypes(TypeConfiguration main, Map<Type,Set<Type>> parentToChildren) {
		main.systems = recursiveResolution(main.systems, parentToChildren);
		main.managers = recursiveResolution(main.managers, parentToChildren);
		main.components = recursiveResolution(main.components, parentToChildren);
		main.factories = recursiveResolution(main.factories, parentToChildren);
	}

	private static Set<Type> recursiveResolution(Set<Type> types, Map<Type, Set<Type>> parentToChildren) {
		Set<Type> destination = new HashSet<>();
		for (Type t : types) {
			recursiveResolution(t, parentToChildren, destination);
		}

		return destination;
	}

	private static void recursiveResolution(Type t, Map<Type, Set<Type>> parentToChildren, Set<Type> destination) {
		if (parentToChildren.containsKey(t)) {
			destination.add(t);
			for (Type foundType : parentToChildren.get(t)) {
				recursiveResolution(foundType, parentToChildren, destination);
			}
		}
	}

	private static Map<Type, Set<Type>> mapParentToChildren(List<ClassData> classes) {
		Map<Type,Set<Type>> parentToChild = new HashMap<>();
		classes.stream().forEach(t -> t.accept(new ParentChainFinder(parentToChild)));

		return parentToChild;
	}

	private static FileSystem fileSystem(URI file) {
		try {
			return FileSystems.newFileSystem(Paths.get(cleanedPath(file)), null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String cleanedPath(URI file) {
	    String path = file.getPath();
	    if (path.matches("^/[^:/]+:/.*")) {
	        return path.substring(1);
	    }
	    
	    return path;
	}

	private static Stream<Path> walk(Path path) {
		try {
			return Files.walk(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
