package net.onedaybeard.ecs.model.scan;

public class AnnotationAllVisitor extends AbstractValueAnnotationVisitor {

    public AnnotationAllVisitor(EcsTypeData config) {
        super(config.requires);
    }

    static boolean accepts(String desc) {
        return "Lcom/artemis/annotations/All;".equals(desc);
    }

}
