package net.onedaybeard.ecs.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.onedaybeard.ecs.model.scan.ConfigurationResolver;
import net.onedaybeard.ecs.util.MatrixStringUtil;
import net.onedaybeard.ecs.model.scan.EcsTypeData;
import org.objectweb.asm.Type;

public final class RowTypeMapping {

	public static enum EcsRowType {
		PACKAGE_NAME(null),
		SYSTEM("gear"),
		MANAGER("gears"),
		FACTORY("user-plus"),
		POJO("coffee"); // not used, yet

		public final String symbol;

		EcsRowType(String symbol) {
			this.symbol = symbol;
		}

	}

	public final EcsRowType rowType;
	public String symbol;

	public final Type ecsType;
	public final ComponentReference[] componentIndices;
	public final String name;
	public final String[] refSystems;
	public final String[] refManagers;
	public final String[] refFactories;

	// FIXMEmeh, dirty... fix sometime.
	public ComponentReference[] managerIndices;
	public ComponentReference[] systemIndices;
	public ComponentReference[] factoryIndices;

	public final boolean isPackage;

	public RowTypeMapping(String packageName) {
		name = packageName;
		ecsType = null;
		refSystems = null;
		refManagers = null;
		refFactories = null;
		componentIndices = null;
		symbol = null;

		rowType = EcsRowType.PACKAGE_NAME;
		isPackage = true;
	}

	private RowTypeMapping(EcsTypeData typeData, ConfigurationResolver resolver, ComponentReference[] componentIndices) {
		this.ecsType = typeData.current;
		this.componentIndices = componentIndices;
		
		name = MatrixStringUtil.shortName(this.ecsType);

		refManagers = createNameIndices(typeData.managers);
		refSystems = createNameIndices(typeData.systems);
		refFactories = createNameIndices(typeData.factories);

		if (resolver.systems.contains(this.ecsType)) {
			rowType = EcsRowType.SYSTEM;
		} else if (resolver.managers.contains(this.ecsType)) {
			rowType = EcsRowType.MANAGER;
		} else if (resolver.factories.contains(this.ecsType)) {
			rowType = EcsRowType.FACTORY;
		} else {
			throw new RuntimeException();
		}
		symbol = rowType.symbol;
		isPackage = false;
	}

	private static String[] createNameIndices(Set<Type> types) {
		String[] typeNames = new String[types.size()];
		int index = 0;
		for (Type type : types)
			typeNames[index++] = MatrixStringUtil.shortName(type);

		return typeNames;
	}

	private static void filterComponentMappings(EcsTypeData typeData) {
		typeData.optional.removeAll(typeData.requires);
		typeData.optional.removeAll(typeData.requiresOne);
		typeData.optional.removeAll(typeData.exclude);
	}

	public static RowTypeMapping from(EcsTypeData typeData, ConfigurationResolver resolver,
		Map<Type, Integer> componentIndices) {
		
		filterComponentMappings(typeData);
		
		ComponentReference[] components = new ComponentReference[componentIndices.size()];
		Arrays.fill(components, ComponentReference.NOT_REFERENCED);

		typeData.cleanSelfTypeReferences();
		mapComponents(typeData.requires, ComponentReference.REQUIRED, componentIndices, components);
		mapComponents(typeData.requiresOne, ComponentReference.ANY, componentIndices, components);
		mapComponents(typeData.optional, ComponentReference.OPTIONAL, componentIndices, components);
		mapComponents(typeData.exclude, ComponentReference.EXCLUDED, componentIndices, components);
		
		return new RowTypeMapping(typeData, resolver, components);
	}
	
	public void setMatrixData(MatrixData mapping) {
		managerIndices = typeIndices(mapping.managerIndexMap, refManagers);
		systemIndices = typeIndices(mapping.systemIndexMap, refSystems);
		factoryIndices = typeIndices(mapping.factoryIndexMap, refFactories);
	}
	
	private static ComponentReference[] typeIndices(Map<String, Integer> indexMapping, String[] referenced) {
		ComponentReference[] indices = new ComponentReference[indexMapping.size()];
		Arrays.fill(indices, ComponentReference.NOT_REFERENCED);
		for (String ref : referenced) {
			indices[indexMapping.get(ref)] = ComponentReference.OPTIONAL;
		}
		
		return indices;
	}
	
	public String getName() {
		return MatrixStringUtil.shortName(ecsType);
	}
	
	private static void mapComponents(Collection<Type> references, ComponentReference referenceType, Map<Type,Integer> componentIndices,
		ComponentReference[] components) {
		for (Type component : references)
			components[componentIndices.get(component)] = referenceType;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		sb.append('"').append(getName()).append('"');
		for (ComponentReference ref : componentIndices) {
			sb.append(", \"").append(ref.symbol).append('"');
		}
		sb.append(" ]");
		
		return sb.toString();
	}
}