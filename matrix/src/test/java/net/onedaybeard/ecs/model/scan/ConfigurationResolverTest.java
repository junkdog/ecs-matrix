package net.onedaybeard.ecs.model.scan;

import net.onedaybeard.ecs.component.ExtPosition;
import net.onedaybeard.ecs.component.OneComponent;
import net.onedaybeard.ecs.component.Position;
import net.onedaybeard.ecs.component.Velocity;
import net.onedaybeard.ecs.component.AllComponent;
import net.onedaybeard.ecs.component.ExcludeComponent;
import net.onedaybeard.ecs.manager.SomeManager;
import net.onedaybeard.ecs.model.TestHelper;
import net.onedaybeard.ecs.system.AnnotationAllSystem;
import net.onedaybeard.ecs.system.AnnotationExcludeSystem;
import net.onedaybeard.ecs.system.AnnotationOneSystem;
import net.onedaybeard.ecs.system.AnotherSystem;
import net.onedaybeard.ecs.system.EmptySystem;
import net.onedaybeard.ecs.system.SomeSystem;
import net.onedaybeard.ecs.system.ExtSomeSystem;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.Type;

import java.util.*;

import static net.onedaybeard.ecs.model.scan.TypeConfiguration.type;
import static org.junit.Assert.assertEquals;

public final class ConfigurationResolverTest {
    private static ConfigurationResolver resolver;

    @BeforeClass
    public static void setup() {
        resolver = new ConfigurationResolver(TestHelper.classRootPath(), "");
        resolver.clearDefaultTypes();
    }

    @Test
    public void systemIntrospectionTest() {
        assertTypes(
                types(EmptySystem.class, ExtSomeSystem.class, SomeSystem.class, AnotherSystem.class,
                        AnnotationAllSystem.class, AnnotationOneSystem.class, AnnotationExcludeSystem.class),
                resolver.systems);
    }

    @Test
    public void managerIntrospectionTest() {
        assertTypes(types(SomeManager.class), resolver.managers);
    }

    @Test
    public void componentIntrospectionTest() {
        assertTypes(types(ExtPosition.class, Position.class, Velocity.class, AllComponent.class, OneComponent.class,
                ExcludeComponent.class), resolver.components);
    }

    private static void assertTypes(Set<Type> expectedTypes, Set<Type> actualTypes) {
        String message = actualTypes.toString();
        assertEquals(message, expectedTypes.size(), actualTypes.size());
        assertEquals(message, expectedTypes, actualTypes);
    }

    private static Set<Type> types(Class<?>... klazzes) {
        Set<Type> expectedTypes = new HashSet<Type>();
        for (Class<?> klazz : klazzes)
            expectedTypes.add(type(klazz));

        return expectedTypes;
    }

}
