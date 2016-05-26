package net.onedaybeard.ecs.model.scan;

import java.io.*;
import java.util.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import net.onedaybeard.ecs.util.ClassFinder;

/**
 * Finds all ECS types by inspecting the class hierarchy
 * while matching against known parent classes.
 */
public final class ConfigurationResolver {
	public final String[] aspectRequire;
	public final String[] aspectRequireOne;
	public final String[] aspectExclude;

	public Set<Type> managers;
	public Set<Type> systems;
	public Set<Type> components;
	public Set<Type> factories;
	public Type componentMapper;
	private TypeConfiguration typeConfiguration;
	private final Map<Type,Set<Type>> parentChildrenMap;

	private final String resourcePrefix;
	
	public ConfigurationResolver(File rootFolder, String ecsResourcePrefix) {
		this.resourcePrefix = ecsResourcePrefix;
		if (!rootFolder.isDirectory())
			throw new RuntimeException("Expected folder - " + rootFolder);
		
		managers = new HashSet<Type>();
		systems = new HashSet<Type>();
		components = new HashSet<Type>();
		factories = new HashSet<Type>();
		
		typeConfiguration = new TypeConfiguration(resourcePrefix);
		systems.addAll(typeConfiguration.systems);
		managers.addAll(typeConfiguration.managers);
		components.addAll(typeConfiguration.components);
		factories.addAll(typeConfiguration.factories);
		componentMapper = typeConfiguration.componentMapper;
		aspectRequire = typeConfiguration.aspectRequire;
		aspectRequireOne = typeConfiguration.aspectRequireOne;
		aspectExclude = typeConfiguration.aspectExclude;

		parentChildrenMap = new HashMap<Type,Set<Type>>();
		
		List<File> classes = ClassFinder.find(rootFolder);
		for (File f : classes) {
			findExtendedEcsTypes(f); // for resolving children of children
		}
		
		resolveExtendedTypes(typeConfiguration, parentChildrenMap);
		
		for (File f : classes) {
			findEcsTypes(f);
		}
	}

	private static void resolveExtendedTypes(TypeConfiguration main, Map<Type,Set<Type>> found) {
		main.systems = recursiveResolution(main.systems, found);
		main.managers = recursiveResolution(main.managers, found);
		main.components = recursiveResolution(main.components, found);
		main.factories = recursiveResolution(main.factories, found);
	}

	private static Set<Type> recursiveResolution(Set<Type> types, Map<Type, Set<Type>> found) {
		Set<Type> destination = new HashSet<Type>();
		for (Type t : types) {
			recursiveResolution(t, found, destination);
		}
		
		return destination;
	}

	private static void recursiveResolution(Type t, Map<Type, Set<Type>> found, Set<Type> destination) {
		if (found.containsKey(t)) {
			destination.add(t);
			for (Type foundType : found.get(t)) {
				recursiveResolution(foundType, found, destination);
			}
		}
	}

	public EcsTypeData scan(ClassReader source) {
		EcsTypeData info = new EcsTypeData();
		
		EcsScanner typeScanner = new EcsScanner(info, this);
		source.accept(typeScanner, 0);
		return info;
	}
	
	public void clearDefaultTypes() {
		TypeConfiguration tc = new TypeConfiguration(resourcePrefix);
		systems.removeAll(tc.systems);
		managers.removeAll(tc.managers);
		components.removeAll(tc.components);
		factories.removeAll(tc.factories);


	}
	
	// TODO: merge with findArtemisType
	private void findExtendedEcsTypes(FileInputStream stream) {
		ClassReader cr;
		try {
			cr = new ClassReader(stream);
			ParentChainFinder artemisTypeFinder = new ParentChainFinder(parentChildrenMap);
			cr.accept(artemisTypeFinder, 0);
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

	private void findExtendedEcsTypes(File file) {
		try {
			findExtendedEcsTypes(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("not found: " + file);
		}
	}

	private void findEcsTypes(InputStream stream) {
		ClassReader cr;
		try {
			cr = new ClassReader(stream);
			SurfaceTypeCollector typeCollector = new SurfaceTypeCollector(this, typeConfiguration);
			cr.accept(typeCollector, 0);
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
	
	private void findEcsTypes(File file) {
		try {
			findEcsTypes(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("not found: " + file);
		}
	}
}
