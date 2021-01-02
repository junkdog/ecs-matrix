package net.onedaybeard.ecs.model.scan;

public class AnnotationOneVisitor extends AbstractValueAnnotationVisitor {

    public AnnotationOneVisitor(EcsTypeData config) {
        super(config.requiresOne);
    }

    static boolean accepts(String desc) {
        return "Lcom/artemis/annotations/One;".equals(desc);
    }

}
