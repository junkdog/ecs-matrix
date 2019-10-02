package net.onedaybeard.ecs.model.scan;

import static org.objectweb.asm.Opcodes.ASM7;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public final class ConstructorScanner extends MethodVisitor {

	private final EcsTypeData config;
	private final ConfigurationResolver resolver;
	
	private final Set<Type> queuedComponents;
	
	public ConstructorScanner(EcsTypeData config, ConfigurationResolver resolver) {
		super(ASM7);
		this.config = config;
		this.resolver = resolver;
		queuedComponents = new HashSet<Type>();
	}
	
	@Override
	public void visitLdcInsn(Object cst) {
		if (cst instanceof Type && resolver.components.contains(cst)) {
			queuedComponents.add((Type)cst);
		}
		super.visitLdcInsn(cst);
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
		if (Arrays.binarySearch(resolver.aspectRequire, name) >= 0) {
			config.requires.addAll(queuedComponents);
			queuedComponents.clear();
		} else if (Arrays.binarySearch(resolver.aspectRequireOne, name) >= 0) {
			config.requiresOne.addAll(queuedComponents);
			queuedComponents.clear();
		} else if (Arrays.binarySearch(resolver.aspectExclude, name) >= 0) {
			config.exclude.addAll(queuedComponents);
			queuedComponents.clear();
		}
		
		super.visitMethodInsn(opcode, owner, name, desc, isInterface);
	}
}
