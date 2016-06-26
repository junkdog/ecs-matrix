package net.onedaybeard.ecs.model.scan;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassData {
	private final byte[] bytes;
	public final Type type;

	public ClassData(Path path) {
		this.bytes = toBytes(path);
		this.type = Type.getObjectType(classReader().getClassName());
	}

	private static byte[] toBytes(Path f) {
		try {
			return Files.readAllBytes(f);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ClassReader classReader() {
		return new ClassReader(bytes);
	}

	public void accept(ClassVisitor cv) {
		new ClassReader(bytes).accept(cv, 0);
	}

	@Override
	public String toString() {
		return "ClassData[type=" + type + ", size=" + bytes.length + "]";
	}
}
