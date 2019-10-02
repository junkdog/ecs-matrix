package net.onedaybeard.ecs.model.scan;

import static org.objectweb.asm.Opcodes.ASM7;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class MethodScanner extends MethodVisitor {

	private EcsTypeData config;
	private ConfigurationResolver resolver;

	public MethodScanner(EcsTypeData config, ConfigurationResolver resolver) {
		super(ASM7);
		this.config = config;
		this.resolver = resolver;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (BindVisitor.accepts(desc)) {
			return new BindVisitor(config);
		} else {
			return super.visitAnnotation(desc, visible);
		}
	}

	@Override
	public void visitLdcInsn(Object cst) {
		if (cst instanceof Type) {
			Type type = (Type)cst;
			if (resolver.components.contains(cst)) {
				config.optional.add(type);
			} else if (resolver.systems.contains(type)) {
				config.systems.add(type);
			} else if (resolver.managers.contains(type)) {
				config.managers.add(type);
			} else if (resolver.factories.contains(type)) {
				config.factories.add(type);
			}
		}
		super.visitLdcInsn(cst);
	}
}
