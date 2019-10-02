package net.onedaybeard.ecs.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import net.onedaybeard.ecs.component.Position;
import net.onedaybeard.ecs.component.Velocity;
import com.artemis.systems.EntityProcessingSystem;

@Wire
public class AnotherSystem extends EntityProcessingSystem {

	private ExtSomeSystem someSystem;

	public AnotherSystem() {
		super(Aspect.all(Position.class).one( Velocity.class));
	}

	@Override
	protected void process(Entity e) {}
}
