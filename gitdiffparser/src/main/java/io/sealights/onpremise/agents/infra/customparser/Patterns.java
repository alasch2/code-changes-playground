package io.sealights.onpremise.agents.infra.customparser;

import java.util.regex.Pattern;

import io.reflectoring.diffparser.unified.ParseWindow;

public class Patterns {
	public static final String FROM_FILE = "---";
	public static final String TO_FILE = "+++";
	public static final String FROM_LINE = "-";
	public static final String TO_LINE = "+";
	public static final String NEUTRAL_LINE = "=";
	public static final String START_HEADER = "diff --git";
    public static final Pattern LINE_RANGE_PATTERN = Pattern.compile("^.*-([0-9]+)(?:,([0-9]+))? \\+([0-9]+)(?:,([0-9]+))?.*$");

	public static boolean matchesFromFilePattern(String line) {
		return line.startsWith(FROM_FILE);
	}

	public static boolean matchesToFilePattern(String line) {
		return line.startsWith(TO_FILE);
	}

	public static boolean matchesFromLinePattern(String line) {
		return line.startsWith(FROM_LINE);
	}

	public static boolean matchesToLinePattern(String line) {
		return line.startsWith(TO_LINE);
	}

	public static boolean matchesHunkStartPattern(String line) {
		return LINE_RANGE_PATTERN.matcher(line).matches();
	}

	public static boolean matchesNeutralLinePattern(String line) {
		return line.startsWith(NEUTRAL_LINE);
	}

	public static boolean matchesStartHeaderPattern(String line) {
		return line.startsWith(START_HEADER);
	}
	
	public static boolean matchesEndPattern(String line, ParseWindow window) {
		if ("".equals(line.trim())) {
			int i = 1;
			String futureLine;
			while ((futureLine = window.getFutureLine(i)) != null) {
				if (matchesStartHeaderPattern(futureLine)) {
					// We found the start of a new diff without another newline in between. That makes the current line the delimiter
					// between this diff and the next.
					return false;
				} 
				else if ("".equals(futureLine.trim())) {
					// We found another newline after the current newline without a start of a new diff in between. That makes the
					// current line just a newline within the current diff.
					return false;
				} 
				else {
					i++;
				}
			}
			// We reached the end of the stream.
			return true;
		}
		else { 
			return false;
		}

	}
}