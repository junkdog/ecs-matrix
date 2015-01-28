package net.onedaybeard.ecs.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import net.onedaybeard.ecs.component.ExtPosition;
import net.onedaybeard.ecs.component.Position;
import net.onedaybeard.ecs.component.Velocity;

@Bind({Position.class, Velocity.class})
public interface FactoryA extends EntityFactory<FactoryA> {
	@Bind(ExtPosition.class) FactoryA extPos();
}
