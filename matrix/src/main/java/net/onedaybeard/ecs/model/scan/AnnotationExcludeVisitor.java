package net.onedaybeard.ecs.model.scan;

public class AnnotationExcludeVisitor extends AbstractValueAnnotationVisitor {

    public AnnotationExcludeVisitor(EcsTypeData config) {
        super(config.exclude);
    }

    static boolean accepts(String desc) {
        return "Lcom/artemis/annotations/Exclude;".equals(desc);
    }

}
