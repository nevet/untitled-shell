/*
 * 
 * sort : sort lines of text file
 *
 * Command Format - sort [OPTIONS] [FILE]
 *	FILE - Name of the file
 *	OPTIONS
 *		-c : Check whether the given file is already sorted, if it is not all sorted, print a
 *           diagnostic containing the first line that is out of order
 *	    -help : Brief information about supported options
 */
package sg.edu.nus.comp.cs4218.impl.extended2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.extended2.ISortTool;
import sg.edu.nus.comp.cs4218.impl.ATool;
import sg.edu.nus.comp.cs4218.impl.ArgList;
import sg.edu.nus.comp.cs4218.impl.FileUtils;
import sg.edu.nus.comp.cs4218.impl.PathUtils;

public class SORTTool extends ATool implements ISortTool {

	private ArgList argList = new ArgList();

	public SORTTool(String[] arguments) {
		super(arguments);
		
		argList.invalidOptionCheck=true;
		
		argList.registerAcceptableOption("c",
				"Check whether the given file is already sorted, if it is not all sorted, " +
				"print adiagnostic containing the first line that is out of order");
		argList.registerAcceptableOption("help",
				"Brief information about supported options");
	
	}

	@Override
	public String sortFile(String input) {

				StringBuilder sortedString = new StringBuilder();
				String []stringToSort = input.split("\r\n");
				Vector <String> stringToSortVect = new Vector<String>(Arrays.asList(stringToSort));
				Collections.sort(stringToSortVect);
				for(int i = 0 ; i < stringToSortVect.size();i++){
					sortedString = sortedString.append(stringToSortVect.get(i)+"\n");
				}
				return new String(sortedString);


//		try{
//			File f = new File(input);
//			FileReader fr = new FileReader(f);
//			BufferedReader br = new BufferedReader(fr);
//			String readin = new String();
//			String tmp = br.readLine();
//			while(tmp != null){
//				System.out.println(tmp);
//				readin += tmp;
//			}
//
//			StringBuilder sortedString = new StringBuilder();
//			String []stringToSort = readin.split("\r\n");
//			Vector <String> stringToSortVect = new Vector<String>(Arrays.asList(stringToSort));
//			Collections.sort(stringToSortVect);
//			for(int i = 0 ; i < stringToSortVect.size();i++){
//				sortedString = sortedString.append(stringToSortVect.get(i)+"\n");
//			}
//
//			return new String(sortedString);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return null;
	}

	@Override
	public String checkIfSorted(String input) {
		StringBuilder disorderInfo = new StringBuilder();
		int disorderLineIndex = -1;  
		String []stringToCheck = input.split("\r\n");
		Vector <String> stringToCheckVect = new Vector<String>(Arrays.asList(stringToCheck));
		//System.out.println(stringToCheckVect.size());
		System.out.println(stringToCheckVect.get(0));

		for(int i = 0;i < stringToCheckVect.size()-1; i++){
			if(stringToCheckVect.get(i).compareToIgnoreCase(stringToCheckVect.get(i+1))>0 && 
					disorderLineIndex < 0){
				disorderLineIndex = i+1;
			}
		}

		if(disorderLineIndex >= 0){
			disorderInfo.append("sort: sortFile.txt:");
			disorderInfo.append(""+(disorderLineIndex+1)+" ");
			disorderInfo.append("disorder: ");
			disorderInfo.append(""+stringToCheckVect.get(disorderLineIndex)+"\n");
			return new String(disorderInfo);
		}
		else{
			return "";
		}


//		try{
//			File f = new File(input);
//			FileReader fr = new FileReader(f);
//			BufferedReader br = new BufferedReader(fr);
//			String readin = new String();
//			String tmp = br.readLine();
//			while(tmp != null){
//				readin += tmp;
//			}
//
//			StringBuilder disorderInfo = new StringBuilder();
//			int disorderLineIndex = -1;  
//			String []stringToCheck = readin.split("\r\n");
//			Vector <String> stringToCheckVect = new Vector<String>(Arrays.asList(stringToCheck));
//			//System.out.println(stringToCheckVect.size());
//			System.out.println(stringToCheckVect.get(0));
//
//			for(int i = 0;i < stringToCheckVect.size()-1; i++){
//				if(stringToCheckVect.get(i).compareToIgnoreCase(stringToCheckVect.get(i+1))>0 && 
//						disorderLineIndex < 0){
//					disorderLineIndex = i+1;
//				}
//			}
//
//			if(disorderLineIndex >= 0){
//				disorderInfo.append("sort: sortFile.txt:");
//				disorderInfo.append(""+(disorderLineIndex+1)+" ");
//				disorderInfo.append("disorder: ");
//				disorderInfo.append(""+stringToCheckVect.get(disorderLineIndex)+"\n");
//				return new String(disorderInfo);
//			}
//			else{
//				return "";
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return null;

	}

	@Override
	public String getHelp() {
		StringBuilder helpInfo = new StringBuilder();
		helpInfo = helpInfo.append("Command Format - sort [OPTIONS] [FILE]\r");
		helpInfo = helpInfo.append(" FILE - Name of the file\r");
		helpInfo = helpInfo.append(" OPTIONS\r");
		helpInfo = helpInfo.append("       -c : Check whether the given file is already sorted,\r" );
		helpInfo = helpInfo.append("            if it is not all sorted, print a diagnostic containing\r");
		helpInfo = helpInfo.append("            the first line that is out of order");
		helpInfo = helpInfo.append("       -help : Brief information about supported options");
		return new String(helpInfo);
	}

	@Override
	public String execute(File workingDir, String stdin) {
		try {
			argList.parseArgs(this.args);
		} catch (IllegalArgumentException e) {
			setStatusCode(9);
			return e.getMessage();
		}
		// help option?
		if (argList.hasOptions() && argList.getOption(0).equals("help")) {
			return getHelp();
		}
		// command does not have options and parameters
		try {
			System.out.println("execute here");
			String file = argList.getParam(0);
			String filePath = PathUtils.pathResolver(workingDir, file);
			String content = FileUtils.readFileContent(new File(filePath));
			System.out.println(content + "execute here");
			
			if(argList.hasOption("c")){
				return checkIfSorted(content);
			}
			else{
				return sortFile(content);
			}
		}catch(Exception e){
			setStatusCode(9);
			return e.getMessage();
		}
	}
}


