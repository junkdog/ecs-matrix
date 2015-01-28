package net.onedaybeard.ecs.model;

import net.onedaybeard.ecs.model.scan.ConfigurationResolver;
import net.onedaybeard.ecs.util.MatrixStringUtil;
import net.onedaybeard.ecs.model.scan.EcsTypeData;
import org.objectweb.asm.Type;

import java.util.*;

import static net.onedaybeard.ecs.util.MatrixStringUtil.shortName;

public class EcsMapping {
	final MatrixData matrixData;
	final List<RowTypeMapping> typeMappings;

	public EcsMapping(ConfigurationResolver resolver, List<EcsTypeData> artemisTypes) {
		SortedSet<Type> componentSet = findComponents(artemisTypes);

		// removes any artemis classes which aren't part of artemis
		resolver.clearDefaultTypes();

		typeMappings = new ArrayList<RowTypeMapping>();
		for (EcsTypeData system : artemisTypes) {
			RowTypeMapping mappedType = RowTypeMapping.from(
					system, resolver, getComponentIndices(componentSet)); // TODO: move to outside loop
			typeMappings.add(mappedType);
		}

		List<String> componentColumns = new ArrayList<String>();
		for (Type component : componentSet) {
			String name = component.getClassName();
			name = name.substring(name.lastIndexOf('.') + 1);
			componentColumns.add(name);
		}

		matrixData = new MatrixData(componentColumns, typeMappings);
		for (RowTypeMapping typeMapping : typeMappings) {
			typeMapping.setMatrixData(matrixData);
		}
	}


	private static SortedSet<Type> findComponents(List<EcsTypeData> artemisTypes) {
		SortedSet<Type> componentSet = new TreeSet<Type>(new ShortNameComparator());
		for (EcsTypeData artemis : artemisTypes) {
			componentSet.addAll(artemis.requires);
			componentSet.addAll(artemis.requiresOne);
			componentSet.addAll(artemis.optional);
			componentSet.addAll(artemis.exclude);
		}
		return componentSet;
	}

	private static Map<Type,Integer> getComponentIndices(SortedSet<Type> componentSet) {
		Map<Type, Integer> componentIndices = new HashMap<Type, Integer>();
		int index = 0;
		for (Type component : componentSet) {
			componentIndices.put(component, index++);
		}
		return componentIndices;
	}

	private static class ShortNameComparator implements Comparator<Type> {
		@Override
		public int compare(Type o1, Type o2) {
			return MatrixStringUtil.shortName(o1).compareTo(MatrixStringUtil.shortName(o2));
		}
	}
}
