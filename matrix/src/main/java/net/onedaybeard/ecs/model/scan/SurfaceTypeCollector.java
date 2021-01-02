package net.onedaybeard.ecs.model.scan;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Collects any classes related to the ECS, categorized according
 * to function.
 */
public class SurfaceTypeCollector extends ClassVisitor {

	private TypeConfiguration mainTypes;
	private ConfigurationResolver resolver;

	public SurfaceTypeCollector(ConfigurationResolver resolver) {
		super(Opcodes.ASM7);
		this.resolver = resolver;
		this.mainTypes = resolver.typeConfiguration;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (superName != null) {
			Type superType = TypeConfiguration.type(superName);
			if (mainTypes.components.contains(superType)) {
				resolver.components.add(TypeConfiguration.type(name));
			} else if (mainTypes.managers.contains(superType)) {
				resolver.managers.add(TypeConfiguration.type(name));
			} else if (mainTypes.systems.contains(superType)) {
				resolver.systems.add(TypeConfiguration.type(name));
			} else if (!name.endsWith("Impl") && interfaces.length > 0) {
				for (String iface : interfaces) {
					if (mainTypes.factories.contains(TypeConfiguration.type(iface))) {
						resolver.factories.add(TypeConfiguration.type(name));
					}
				}
			}
		}
		
		super.visit(version, access, name, signature, superName, interfaces);
	}

}
