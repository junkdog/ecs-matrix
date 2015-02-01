package com.artemis.cli;

import java.io.File;
import java.security.ProtectionDomain;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class CliApplication {
	public static void main(String[] args) {
		new CliApplication().parse(args);
	}
	
	private void parse(String[] args) {
		MatrixCommand matrix = new MatrixCommand();

		MatrixCommand cmd = new MatrixCommand();
		JCommander cli = new JCommander(cmd);
		cli.setProgramName("matrix-cli-<version>.jar");
		try {
			cli.parse(args);
			if (!cmd.help)
				cmd.execute();
			else
				cli.usage();
		} catch (ParameterException e) {
			cli.usage();
		}
	}
	
	static String getJarName() {
		ProtectionDomain domain = CliApplication.class.getProtectionDomain();
		String path = domain.getCodeSource().getLocation().getPath();
		
		return new File(path).getName();
	}
}
