package net.onedaybeard.ecs.model.scan;

import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class AbstractValueAnnotationVisitor extends AnnotationVisitor {

    private final Set<Type> targetSet;

    public AbstractValueAnnotationVisitor(Set<Type> targetSet) {
        super(Opcodes.ASM7);
        this.targetSet = targetSet;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        assert "value".equals(name);
        return new ValueVisitor(targetSet);
    }

    static boolean accepts(String desc) {
        return "Lcom/artemis/annotations/All;".equals(desc);
    }

    private class ValueVisitor extends AnnotationVisitor {
        private final Set<Type> targetSet;

        public ValueVisitor(Set<Type> targetSet) {
            super(Opcodes.ASM7);
            this.targetSet = targetSet;
        }

        @Override
        public void visit(String name, Object value) {
            targetSet.add((Type) value);
            super.visit(name, value);
        }
    }

}
