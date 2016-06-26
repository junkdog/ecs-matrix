package net.onedaybeard.ecs.model.scan;

import java.io.*;
import java.net.URI;
import java.util.*;

import org.objectweb.asm.Type;

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
	TypeConfiguration typeConfiguration;

	private final String resourcePrefix;

	public final ClassStore store;
	
	public ConfigurationResolver(File folderOrJar, String ecsResourcePrefix) {
		this(Arrays.asList(folderOrJar.toURI()), ecsResourcePrefix);
	}

	public ConfigurationResolver(List<URI> folderOrJars, String ecsResourcePrefix) {
		this.resourcePrefix = ecsResourcePrefix;

		managers = new HashSet<>();
		systems = new HashSet<>();
		components = new HashSet<>();
		factories = new HashSet<>();

		typeConfiguration = new TypeConfiguration(resourcePrefix);
		systems.addAll(typeConfiguration.systems);
		managers.addAll(typeConfiguration.managers);
		components.addAll(typeConfiguration.components);
		factories.addAll(typeConfiguration.factories);
		componentMapper = typeConfiguration.componentMapper;
		aspectRequire = typeConfiguration.aspectRequire;
		aspectRequireOne = typeConfiguration.aspectRequireOne;
		aspectExclude = typeConfiguration.aspectExclude;

		store = new ClassStore(this, folderOrJars);
	}

	public void clearDefaultTypes() {
		TypeConfiguration tc = new TypeConfiguration(resourcePrefix);
		systems.removeAll(tc.systems);
		managers.removeAll(tc.managers);
		components.removeAll(tc.components);
		factories.removeAll(tc.factories);
	}
}
