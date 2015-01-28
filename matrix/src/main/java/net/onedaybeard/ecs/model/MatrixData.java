package net.onedaybeard.ecs.model;

import java.util.*;

public class MatrixData {
	final List<String> componentColumns;
	final List<String> managerColumns;
	final List<String> systemColumns;
	final List<String> factoryColumns;
	final Map<String, Integer> managerIndexMap;
	final Map<String, Integer> systemIndexMap;
	final Map<String, Integer> factoryIndexMap;

	MatrixData(List<String> componentColumns, List<RowTypeMapping> typeMappings) {
		this.componentColumns = new ArrayList<String>(componentColumns);
		managerColumns = new ArrayList<String>();
		systemColumns = new ArrayList<String>();
		factoryColumns = new ArrayList<String>();
		managerIndexMap = new HashMap<String,Integer>();
		systemIndexMap = new HashMap<String,Integer>();
		factoryIndexMap = new HashMap<String,Integer>();
		extractArtemisTypes(typeMappings);
	}

	private void extractArtemisTypes(List<RowTypeMapping> typeMappings) {
		SortedSet<String> referencedManagers = new TreeSet<String>();
		SortedSet<String> referencedSystems = new TreeSet<String>();
		SortedSet<String> referencedFactories = new TreeSet<String>();

		for (RowTypeMapping mapping : typeMappings) {
			insert(referencedManagers, mapping.refManagers);
			insert(referencedSystems, mapping.refSystems);
			insert(referencedFactories, mapping.refFactories);
		}

		int nextColumnIndex = 0;
		for (String manager : referencedManagers) {
			managerIndexMap.put(manager, nextColumnIndex++);
		}

		nextColumnIndex = 0;
		for (String system : referencedSystems) {
			systemIndexMap.put(system, nextColumnIndex++);
		}

		nextColumnIndex = 0;
		for (String factory : referencedFactories) {
			factoryIndexMap.put(factory, nextColumnIndex++);
		}

		managerColumns.addAll(referencedManagers);
		systemColumns.addAll(referencedSystems);
		factoryColumns.addAll(referencedFactories);
	}

	private static void insert(SortedSet<String> artemisSet, String[] referenced) {
		for (String ref : referenced)
			artemisSet.add(ref);
	}
}