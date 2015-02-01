package net.onedaybeard.ecs.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import net.onedaybeard.ecs.util.MatrixStringUtil;
import org.objectweb.asm.Opcodes;

import com.x5.template.Chunk;
import com.x5.template.Theme;

import static java.util.Arrays.asList;

public class ComponentDependencyMatrix implements Opcodes  {
	private final File root;
	private final File output;
	private final String projectName;

	public ComponentDependencyMatrix(String projectName, File root, File output) {
		this.projectName = projectName;
		this.root = root;
		this.output = output;
	}
	
	public String detectAndProcess() {
		EcsTypeInspector typeInspector;

		for (String ecs : asList("artemis", "ashley")) {
			typeInspector = new EcsTypeInspector(root, "/" + ecs);
			if (typeInspector.foundEcsClasses()) {
				process(typeInspector);
				return "Found ECS framework: " + ecs;
			}
		}

		return "Failed finding any ECS related classes.";
	}

	public void process() {
		process("");
	}

	public void process(String resourcePrefix) {
		EcsTypeInspector typeInspector = new EcsTypeInspector(root, resourcePrefix);
		process(typeInspector);
	}

	private void process(EcsTypeInspector typeInspector) {
		write(typeInspector.getTypeMap(), typeInspector.getMatrixData());
	}

	private void write(SortedMap<String, List<RowTypeMapping>> mappedSystems, MatrixData matrix) {
		Theme theme = new Theme();
		Chunk chunk = theme.makeChunk("matrix");
		
		List<RowTypeMapping> rows = new ArrayList<RowTypeMapping>();
		for (Entry<String,List<RowTypeMapping>> entry : mappedSystems.entrySet()) {
			rows.add(new RowTypeMapping(entry.getKey()));
			rows.addAll(entry.getValue());
		}
		
		chunk.set("longestName", MatrixStringUtil.findLongestClassName(mappedSystems).replaceAll(".", "_") + "______");
		
		chunk.set("rows", rows);

		chunk.set("headersComponents", matrix.componentColumns);
		chunk.set("componentCount", matrix.componentColumns.size());

		chunk.set("headersManagers", matrix.managerColumns);
		chunk.set("managerCount", matrix.managerColumns.size());

		chunk.set("headersSystems", matrix.systemColumns);
		chunk.set("systemCount", matrix.systemColumns.size());

		chunk.set("factoryCount", matrix.factoryColumns.size());
		chunk.set("headersFactories", matrix.factoryColumns);

		chunk.set("project", projectName);
		
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(output));
			chunk.render(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) try {
				out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
