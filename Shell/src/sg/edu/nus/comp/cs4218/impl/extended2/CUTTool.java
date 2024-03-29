package sg.edu.nus.comp.cs4218.impl.extended2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import sg.edu.nus.comp.cs4218.extended2.ICutTool;
import sg.edu.nus.comp.cs4218.impl.ATool;
import sg.edu.nus.comp.cs4218.impl.ArgList;
import sg.edu.nus.comp.cs4218.impl.FileUtils;
import sg.edu.nus.comp.cs4218.impl.PathUtils;
import sg.edu.nus.comp.cs4218.impl.ArgList.ArgType;
import sg.edu.nus.comp.cs4218.impl.ArgList.Option;
import sg.edu.nus.comp.cs4218.impl.RangeUtils;
import sg.edu.nus.comp.cs4218.impl.RangeUtils.Range;

public class CUTTool extends ATool implements ICutTool {
	
	private ArgList argList = new ArgList();
	private ArrayList<Range> rangeList;
	private String prevList;
	
	/**
	 * This function is used to cut the specified characters from the input.
	 * 
	 * @param list
	 * 		is an ArrayList of Range that specifies the cutting range.
	 * @param input
	 * 		is the origin input under cut.
	 * @return
	 * 		a String of cut result.
	 */
	private String cutSpecfiedCharacters(ArrayList<Range> list, String input) {
		StringBuilder finalString = new StringBuilder();
		ArrayList<Range> rangeList = RangeUtils.mergeRange(list); 
		
		for (Range range : rangeList) {
			finalString.append(input.substring(Math.min(range.left, input.length()) - 1,
											   Math.min(input.length(),
												        range.right)));
			
			if (range.right > input.length()) {
				break;
			}
		}
		
		return finalString.toString();
	}
	
	/**
	 * This function is used to cut specified fields delimited by delim from
	 * input.
	 * 
	 * Please be noted that the equivalent UNIX operation of this function is
	 * cut -f delim -c LIST INPUT
	 *   
	 * @param list
	 * 		is an ArrayList of Range that specifies the cutting range.
	 * @param delim
	 * 		is the delimiter of fields.
	 * @param input
	 * 		is the origin input under cut.
	 * @return
	 * 		a String of cut result delimited by delim.
	 */
	private String cutSpecifiedCharactersUseDelimiter(ArrayList<Range> list, String delim,
													  String input) {
		StringBuilder finalString = new StringBuilder();
		ArrayList<Range> rangeList = RangeUtils.mergeRange(list);
		
		String[] fields = input.split(delim);
		
		for (Range range : rangeList) {
			for (int i = Math.min(range.left, input.length()) - 1; i < Math.min(fields.length, range.right); i ++) {
				finalString.append(fields[i]);
				finalString.append(delim);
			}
			
			if (range.right > input.length()) {
				break;
			}
		}
		
		finalString.deleteCharAt(finalString.length() - 1);
		
		return finalString.toString();
	}

	/**
	 * Alternative constructor. Construct an executable Cut tool with a
	 * specified arguments.
	 * 
	 * @param arguments
	 * 		is the specified arguments of Cut tool.
	 */
	public CUTTool(String[] arguments) {
		super(arguments);
		
		argList.invalidOptionCheck = true;
		
		argList.registerAcceptableOption("c", ArgType.STRING, 
						"Use LIST as the list of characters to cut out.");
		argList.registerAcceptableOption("d", ArgType.STRING,
						"Use DELIM as the field-separator character"
						+ " instead of the TAB character.");
		argList.registerAcceptableOption("help",
						"Brief information about supported options.");
	}

	@Override
	/**
	 * This function is used to cut the specified characters from the input.
	 */
	public String cutSpecfiedCharacters(String list, String input) {
		// To increase calculation speed, we only parse the raw list once.
		// Therefore here we assume the list won't change through out the
		// execution of the tool. If this assumption does not hold any more in
		// later development, add !prevList.equals(list) as disjunction
		// condition.
		if (prevList == null) {
			rangeList = RangeUtils.parseRange(list);
			
			// if the start of the range is smaller than 1
			if (rangeList.get(0).left < 1) {
				setStatusCode(9);
				return "Values may not include zero.";
			}
			
			prevList = list;
		}
		
		return cutSpecfiedCharacters(rangeList, input);
	}

	@Override
	/**
	 * This function is used to cut specified fields delimited by delim from
	 * input.
	 */
	public String cutSpecifiedCharactersUseDelimiter(String list, String delim,
			String input) {
		// To increase calculation speed, we only parse the raw list once.
		// Therefore here we assume the list won't change through out the
		// execution of the tool. If this assumption does not hold any more in
		// later development, add !prevList.equals(list) as disjunction
		// condition.
		if (prevList == null) {
			rangeList = RangeUtils.parseRange(list);
			
			// if the start of the range is smaller than 1
			if (rangeList.get(0).left < 1) {
				setStatusCode(9);
				return "Values may not include zero.";
			}
			
			prevList = list;
		}
		
		return cutSpecifiedCharactersUseDelimiter(rangeList, delim, input);
	}

	@Override
	public String getHelp() {
		StringBuilder help = new StringBuilder();

		help.append("Command Format - cut [OPTIONS] [FILE]\n");
		help.append("FILE - Name of the file, when no file is present (denoted by \"-\") use standard input\n");
		help.append("OPTIONS\n");

		for (Option opt : argList.getAcceptableOptions()) {
			help.append("  " + opt.toString() + "\n");
		}

		return help.toString();
	}

	@Override
	public String execute(File workingDir, String stdin) {
		// by default we assume execution is successful
		setStatusCode(0);
		
		// parse arguments
		try {
			argList.parseArgs(this.args);
		} catch (IllegalArgumentException e) {
			setStatusCode(9);
			return e.getMessage() + "\n" + getHelp();
		}
		
		// determine where the input comes from
		String input = (stdin == null? "" : stdin);
		String list = "";
		
		// command does not have options
		if (!argList.hasOptions()) {
			setStatusCode(9);
			return getHelp();
		} else
		// command has more than 1 option
		if (argList.getOptions().length > 1) {
			setStatusCode(9);
			return "Error: More than one option.\n" + getHelp();
		} else {
			try {
				if (argList.hasOption("help")) {
					return getHelp();
				} else
				if (argList.hasOption("c")) {
					list = argList.getOptionValue("c");
					
					if (argList.hasParams() && argList.getParams().length == 1) {
						if (!argList.getParam(0).equals("-")) {
							input = FileUtils.readFileContent(new File(PathUtils.
									pathResolver(workingDir, argList.getParam(0))));
						}
					} else {
						setStatusCode(9);
						return getHelp();
					}
				} else {
					if (argList.hasParams() && argList.getParams().length == 2) {
						list = argList.getParam(0);
						
						if (!argList.getParam(1).equals("-")) {
							input = FileUtils.readFileContent(new File(PathUtils.
									pathResolver(workingDir, argList.getParam(1))));
						}
					} else {
						setStatusCode(9);
						return getHelp();
					}
				}
			} catch (IOException e) {
				setStatusCode(1);
				return e.getMessage();
			} catch (RuntimeException e) {
				setStatusCode(2);
				return e.getMessage();
			}
		}

		// process input
		BufferedReader br = new BufferedReader(new StringReader(input));
		StringBuilder result = new StringBuilder();

		try {
			String line;
			while ((line = br.readLine()) != null) {
				String lineResult = argList.hasOption("d") ? cutSpecifiedCharactersUseDelimiter(list, 
								    argList.getOptionValue("d"), line) :
								    cutSpecfiedCharacters(list, line);
				
				result.append(lineResult);
				result.append("\n");
				
				// if something wrong when cutting the line, break the routine
				if (getStatusCode() != 0) {
					break;
				}
			}

			br.close();
		} catch (IOException e) {
			setStatusCode(2);
			result.append("Error: file reading exception.\n");
		} catch (IllegalArgumentException e) {
			setStatusCode(9);
			result.append(e.getMessage());
		}
		
		return result.toString();
	}
}
