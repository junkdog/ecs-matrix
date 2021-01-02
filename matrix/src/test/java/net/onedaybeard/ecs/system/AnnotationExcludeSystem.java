package net.onedaybeard.ecs.system;

import com.artemis.annotations.Exclude;

import net.onedaybeard.ecs.component.ExcludeComponent;

@Exclude(ExcludeComponent.class)
public class AnnotationExcludeSystem extends EmptySystem {
}
