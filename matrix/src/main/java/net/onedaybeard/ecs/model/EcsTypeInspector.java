package net.onedaybeard.ecs.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import net.onedaybeard.ecs.model.scan.ClassData;
import net.onedaybeard.ecs.model.scan.ClassStore;
import net.onedaybeard.ecs.model.scan.ConfigurationResolver;
import net.onedaybeard.ecs.model.scan.EcsScanner;
import net.onedaybeard.ecs.model.scan.EcsTypeData;

/**
 * Detailed introspection of all ECS-related types.
 */
public class EcsTypeInspector {
    private EcsMapping model = null;
    private final ConfigurationResolver initialTypeScan;

    public EcsTypeInspector(List<URI> files, String resourcePrefix) {
        this.initialTypeScan = new ConfigurationResolver(files, resourcePrefix);

        if (initialTypeScan.components.size() == 0
                && initialTypeScan.systems.size() == 0
                && initialTypeScan.managers.size() == 0
                && initialTypeScan.factories.size() == 0) {

            String error = "No ECS classes found on classpath. "
                    + "See https://github.com/junkdog/artemis-odb/wiki/Component-Dependency-Matrix for more info.";
            throw new RuntimeException(error);
        }
        // removes framework classes
        initialTypeScan.clearDefaultTypes();

        List<EcsTypeData> ecsTypes = findEcsTypes(initialTypeScan.store);
        if (ecsTypes.size() == 0)
            return;

        model = new EcsMapping(initialTypeScan, ecsTypes);
    }

    SortedMap<String, List<RowTypeMapping>> getTypeMap() {
        assert model != null;

        String common = findCommonPackage(model.typeMappings);
        SortedMap<String, List<RowTypeMapping>> map = new TreeMap<>();
        for (int i = 0, s = model.typeMappings.size(); s > i; i++) {
            RowTypeMapping system = model.typeMappings.get(i);
            String packageName = toPackageName(system.ecsType.getClassName());
            packageName = (packageName.length() > common.length())
                    ? packageName.substring(common.length())
                    : ".";
            if (!map.containsKey(packageName))
                map.put(packageName, new ArrayList<>());

            map.get(packageName).add(system);
        }

        return map;
    }

    MatrixData getMatrixData() {
        assert model != null;
        return model.matrixData;
    }

    boolean foundEcsClasses() {
        return model != null;
    }

    private List<EcsTypeData> findEcsTypes(ClassStore store) {
        List<EcsTypeData> types = new ArrayList<>();
        store.classes.stream()
                .filter(t -> isEcsType(t.type))
                .map(t -> ecsTypeData(t))
                .sorted()
                .forEach(types::add);

        return types;
    }

    private EcsTypeData ecsTypeData(ClassData classData) {
        return ecsTypeData(classData.classReader());
    }

    private EcsTypeData ecsTypeData(ClassReader cr) {
        EcsTypeData info = new EcsTypeData();
        info.current = Type.getObjectType(cr.getClassName());

        EcsScanner typeScanner = new EcsScanner(info, initialTypeScan);
        cr.accept(typeScanner, 0);

        return info;
    }

    private boolean isEcsType(Type objectType) {
        return initialTypeScan.managers.contains(objectType)
                || initialTypeScan.systems.contains(objectType)
                || initialTypeScan.factories.contains(objectType);
    }

    private static String findCommonPackage(List<RowTypeMapping> systems) {
        String prefix = toPackageName(systems.get(0).ecsType.getClassName());
        for (int i = 1, s = systems.size(); s > i; i++) {
            String p = toPackageName(systems.get(i).ecsType.getClassName());
            for (int j = 0, l = Math.min(prefix.length(), p.length()); l > j; j++) {
                if (prefix.charAt(j) != p.charAt(j)) {
                    prefix = prefix.substring(0, j);
                    break;
                }
            }
        }

        return prefix;
    }

    private static String toPackageName(String className) {
        return className.substring(0, className.lastIndexOf('.'));
    }
}
