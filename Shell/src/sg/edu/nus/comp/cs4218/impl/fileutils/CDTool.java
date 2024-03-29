package sg.edu.nus.comp.cs4218.impl.fileutils;

import java.io.File;

import sg.edu.nus.comp.cs4218.impl.ArgList;
import sg.edu.nus.comp.cs4218.fileutils.ICdTool;
import sg.edu.nus.comp.cs4218.impl.ATool;
import sg.edu.nus.comp.cs4218.impl.PathUtils;;

/**
 * cd - change current working directory
 *
 * cd [new working directory]
 * 
 * new working directory = '/', '~', '.', '..', "", path
 * 
 * '/' = root directory
 * '~' = home directory
 * '..' = parent directory
 * ""(empty string) = home directory
 * '.' = current directory
 * 
 * If the directory cannot be found, an error message will be returned and an
 * error code will be set.
 * 
 * Please be noted that our program only takes care of file system under MAC OS.
 */
public class CDTool extends ATool implements ICdTool {
	
	private ArgList argList = new ArgList();
	
	/**
	 * Alternative constructor. Construct an executable CD tool with a
	 * specified arguments.
	 * 
	 * @param arguments
	 * 		is the specified arguments of CD tool.
	 */
	public CDTool(String[] arguments) {
		super(arguments);
	}
	
	@Override
	public File changeDirectory(String newDirectory) {
		return new File(newDirectory);
	}

	@Override
	public String execute(File workingDir, String stdin) {
		String output;
		String regulatedStdin = stdin;
		
		// make sure stdin exists
		if (regulatedStdin == null) { regulatedStdin = ""; }
		
		//TODO: at this moment we assume no stdin. confirm with others
		// how to parse stdin!!!
		
		// parse arguments
		try {
			argList.parseArgs(this.args);
		} catch (IllegalArgumentException e) {
			setStatusCode(9);
			return e.getMessage();
		}
		
		// if no error occurs, the first argument should be the path we want
		// to change to
		String param;
		
		if (!argList.hasParams()) {
			param = "~";
		} else {
			param = argList.getParam(0);
		}
		
		File newDir = new File (PathUtils.pathResolver(workingDir, param));
		
		// if the new directory is null, the input file does not exist
		if (!newDir.exists()) {
			setStatusCode(9);
			output = "No such file or directory!";
		} else {
			// now we should check if the path is essentially a file
			if (newDir.isFile()) {
				setStatusCode(9);
				output = PathUtils.getLastElementOfPath(newDir) + " is a file!";
			} else {
				// everything is fine, we can change shell's default directory
				output = newDir.toString();
				setStatusCode(0);
			}
		}

		return output;
	}
}
