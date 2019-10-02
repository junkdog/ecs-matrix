package net.onedaybeard.ecs.model.scan;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class BindVisitor extends AnnotationVisitor {
	private final EcsTypeData config;
	private final ConfigurationResolver resolver;

	public BindVisitor(EcsTypeData config, ConfigurationResolver resolver) {
		super(Opcodes.ASM7);
		this.config = config;
		this.resolver = resolver;
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		assert "value".equals(name);
		return new BindValueVisitor(config, resolver);
	}

	static boolean accepts(String desc) {
		return "Lcom/artemis/annotations/Bind;".equals(desc);
	}

	static private class BindValueVisitor extends AnnotationVisitor {
		private final EcsTypeData config;
		private final ConfigurationResolver resolver;

		public BindValueVisitor(EcsTypeData config, ConfigurationResolver resolver) {
			super(Opcodes.ASM7);
			this.config = config;
			this.resolver = resolver;
		}

		@Override
		public void visit(String name, Object value) {
			if (resolver.components.contains(value))
				config.requires.add((Type) value);
			super.visit(name, value);
		}
	}
}