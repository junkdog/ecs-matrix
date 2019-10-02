package net.onedaybeard.ecs.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import net.onedaybeard.ecs.component.ExtPosition;
import net.onedaybeard.ecs.component.Position;
import net.onedaybeard.ecs.manager.SomeManager;
import com.artemis.systems.EntityProcessingSystem;

public class SomeSystem extends EntityProcessingSystem {
	private SomeManager someManager;
	private AnotherSystem anotherSystem;

	public SomeSystem() {
		super(Aspect.all(ExtPosition.class).exclude(Position.class));
	}

	@Override
	protected void process(Entity e) {}
}
