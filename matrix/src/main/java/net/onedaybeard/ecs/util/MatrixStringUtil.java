package net.onedaybeard.ecs.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

import net.onedaybeard.ecs.model.RowTypeMapping;

public final class MatrixStringUtil {
	private MatrixStringUtil() {}
	
	public static String findLongestClassName(Map<String, List<RowTypeMapping>> mappings) {
		return findLongestString(mappings, new LongestClassName());
	}
	
	public static String findLongestManagerList(Map<String, List<RowTypeMapping>> mappings) {
		return findLongestString(mappings, new LongestManagers());
	}
	
	public static String findLongestSystemList(Map<String, List<RowTypeMapping>> mappings) {
		return findLongestString(mappings, new LongestSystems());
	}
	
	private static String findLongestString(Map<String, List<RowTypeMapping>> mappings, LongestMapper longestStrategy) {
		String longest = "";
		for (Entry<String, List<RowTypeMapping>> entry : mappings.entrySet()) {
			if (entry.getKey().length() > longest.length()) longest = entry.getKey();
			for (RowTypeMapping mapping : entry.getValue()) {
				longest = longestStrategy.getMaxLength(mapping, longest);
			}
		}
		return longest;
	}
	
	private static interface LongestMapper {
		String getMaxLength(RowTypeMapping mapping, String previousLongest);
	}
	
	private static class LongestClassName implements LongestMapper {
		@Override
		public String getMaxLength(RowTypeMapping mapping, String longest) {
			return (mapping.name.length() > longest.length())
				? mapping.name
				: longest;
		}
	}
	
	private static class LongestManagers implements LongestMapper {
		@Override
		public String getMaxLength(RowTypeMapping mapping, String longest) {
			return (Arrays.toString(mapping.refManagers).length() > longest.length())
				? Arrays.toString(mapping.refManagers)
				: longest;
		}
	}
	
	private static class LongestSystems implements LongestMapper {
		@Override
		public String getMaxLength(RowTypeMapping mapping, String longest) {
			return (Arrays.toString(mapping.refSystems).length() > longest.length())
				? Arrays.toString(mapping.refSystems)
				: longest;
		}
	}
	
	public static String shortName(String s) {
		String name = s;
		return name.substring(name.lastIndexOf('.') + 1);
	}

	public static String shortName(Type type) {
		String name = type.getClassName();
		return name.substring(name.lastIndexOf('.') + 1);
	}
}
