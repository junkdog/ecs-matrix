package net.onedaybeard.ecs.model.scan;

import java.util.HashSet;
import java.util.Set;


import org.objectweb.asm.Type;

/**
 * Blob for an entity system or manager tracking references to
 * systems, managers and components.
 */
public class EcsTypeData implements Comparable<EcsTypeData> {
	public final Set<Type> requires = new HashSet<>();
	public final Set<Type> requiresOne = new HashSet<>();
	public final Set<Type> optional = new HashSet<>();
	public final Set<Type> exclude = new HashSet<>();
	public final Set<Type> systems = new HashSet<>();
	public final Set<Type> managers = new HashSet<>();
	public final Set<Type> factories = new HashSet<>();

	public Type current;
	
	public EcsTypeData() {}

	public void cleanSelfTypeReferences() {
		requires.remove(current);
		requiresOne.remove(current);
		optional.remove(current);
		exclude.remove(current);
		systems.remove(current);
		managers.remove(current);
		factories.remove(current);
	}

	public Type current() {
		return current;
	}

	@Override
	public String toString() {
		return "EcsTypeData[" +
			"current=" + current +
			", requires=" + requires +
			", requiresOne=" + requiresOne +
			", optional=" + optional +
			", exclude=" + exclude +
			", systems=" + systems +
			", managers=" + managers +
			", factories=" + factories +
			']';
	}

	@Override
	public int compareTo(EcsTypeData o) {
		return current.toString().compareTo(o.current.toString());
	}
}
