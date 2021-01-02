package net.onedaybeard.ecs.model.scan;

class BindVisitor extends AbstractValueAnnotationVisitor {

    public BindVisitor(EcsTypeData config) {
        super(config.requires);
    }

    static boolean accepts(String desc) {
        return "Lcom/artemis/annotations/Bind;".equals(desc);
    }

}