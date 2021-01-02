package net.onedaybeard.ecs.model.scan;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class EcsScanner extends ClassVisitor {

    private final EcsTypeData config;
    private final ConfigurationResolver resolver;

    public EcsScanner(EcsTypeData config, ConfigurationResolver configurationResolver) {
        super(Opcodes.ASM7);
        this.config = config;
        this.resolver = configurationResolver;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (AnnotationAllVisitor.accepts(desc)) {
            return new AnnotationAllVisitor(config);
        } else if (AnnotationOneVisitor.accepts(desc)) {
            return new AnnotationOneVisitor(config);
        } else if (AnnotationExcludeVisitor.accepts(desc)) {
            return new AnnotationExcludeVisitor(config);
        } else if (BindVisitor.accepts(desc)) {
            return new BindVisitor(config);
        } else {
            return super.visitAnnotation(desc, visible);
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (!desc.endsWith(";"))
            return super.visitField(access, name, desc, signature, value);

        Type type = Type.getType(desc);
        if (resolver.systems.contains(type)) {
            config.systems.add(type);
        } else if (resolver.managers.contains(type)) {
            config.managers.add(type);
        } else if (resolver.componentMapper.equals(type)) {
            String componentDesc = signature.substring(signature.indexOf('<') + 1, signature.indexOf('>'));
            config.optional.add(Type.getType(componentDesc));
        }

        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<init>".equals(name)) {
            return new ConstructorScanner(config, resolver);
        } else {
            return new MethodScanner(config, resolver);
        }
    }

}