package net.onedaybeard.ecs.manager;

import com.artemis.Manager;
import net.onedaybeard.ecs.factory.FactoryA;
import net.onedaybeard.ecs.system.SomeSystem;

public class SomeManager extends Manager {
	private FactoryA factory;
	private SomeSystem someSystem;
}
