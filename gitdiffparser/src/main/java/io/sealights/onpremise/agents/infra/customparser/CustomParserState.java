package io.sealights.onpremise.agents.infra.customparser;

import io.reflectoring.diffparser.unified.ParseWindow;

/**
 * Implements state machine for parsing a unified git diff; expects the following line indicators:
 * <p/>
 * <ul>'-' marks a line, existing only in the old revision (fromFile)</ul>
 * <ul>'+' marks a line, existing only in the new revision (toFile)</ul>
 * <ul>'=' marks a line, that is similar in both revisions</ul>
 * 
 * This is the enhanced version of {@link io.reflectoring.diffparser.unified.ParseState}
 * The original class does not support parsing of comments with inline characters '\r' or '\n'
 * The major difference is a new state PARTIAL_LINE. The state serves for handling lines with 
 * inline characters '\r' or '\n', which are not the end of the line.
 * 
 * @author AlaSchneider
 *
 */
public enum CustomParserState {

	/**
	 * This is the initial state of the parser.
	 */
	INITIAL {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			return nextHeaderOrFromFileState(window, INITIAL);
		}
	},

	NEXT_HEADER {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			return nextHeaderOrFromFileState(window, NEXT_HEADER);
		}
	},
	/**
	 * The parser is in this state if it is currently parsing a header line.
	 */
	HEADER {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			return nextHeaderOrFromFileState(window, HEADER);
		}
	},

	/**
	 * The parser is in this state if it is currently parsing the line containing the "from" file.
	 * <p/>
	 * Example line:<br/>
	 * {@code --- /path/to/file.txt}
	 */
	FROM_FILE {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			String line = window.getFocusLine();
			if (Patterns.matchesToFilePattern(line)) {
				logTransition(line, FROM_FILE, TO_FILE);
				return TO_FILE;
			} else {
				throw new IllegalStateException("A FROM_FILE line ('---') must be directly followed by a TO_FILE line ('+++')!");
			}
		}
	},
	
	/**
	 * The parser is in this state if it is currently parsing the line containing the "to" file.
	 * <p/>
	 * Example line:<br/>
	 * {@code +++ /path/to/file.txt}
	 */
	TO_FILE {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			String line = window.getFocusLine();
			if (Patterns.matchesHunkStartPattern(line)) {
				logTransition(line, TO_FILE, HUNK_START);
				return HUNK_START;
			} else {
				throw new IllegalStateException("A TO_FILE line ('+++') must be directly followed by a HUNK_START line ('@@')!");
			}
		}
	},
	

	/**
	 * The parser is in this state if it is currently parsing a line containing the header of a hunk.
	 * <p/>
	 * Example line:<br/>
	 * {@code @@ -1,5 +2,6 @@}
	 */
	HUNK_START {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			String line = window.getFocusLine();
			if (Patterns.matchesFromLinePattern(line)) {
				logTransition(line, HUNK_START, FROM_LINE);
				return FROM_LINE;
			} 
			else if (Patterns.matchesToLinePattern(line)) {
				logTransition(line, HUNK_START, TO_LINE);
				return TO_LINE;
			} 
			else {
				logTransition(line, HUNK_START, NEUTRAL_LINE);
				return NEUTRAL_LINE;
			}
		}
	},

	/**
	 * The parser is in this state if it is currently parsing a line containing a line that is in the first file,
	 * but not the second (a "from" line).
	 * <p/>
	 * Example line:<br/>
	 * {@code - only the dash at the start is important}
	 */
	FROM_LINE {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			return lineNextState(window, FROM_LINE);
		}
	},

	/**
	 * The parser is in this state if it is currently parsing a line containing a line that is in the second file,
	 * but not the first (a "to" line).
	 * <p/>
	 * Example line:<br/>
	 * {@code + only the plus at the start is important}
	 */
	TO_LINE {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			return lineNextState(window, TO_LINE);
		}
	},

	/**
	 * The parser is in this state if it is currently parsing a line that is contained in both files (a "neutral" line).
	 * <p/>
	 * Example line:<br/>
	 * {@code = only the equal at the start is important}
	 */
	NEUTRAL_LINE {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			return lineNextState(window, NEUTRAL_LINE);
		}
	},

	PARTTIAL_LINE {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			return PARTTIAL_LINE;
		}
	},

	/**
	 * The parser is in this state if it is currently parsing a line that is the delimiter between two Diffs. This line is always a new
	 * line.
	 */
	END {
		@Override
		public CustomParserState nextState(ParseWindow window) {
			String line = window.getFocusLine();
			logTransition(line, END, HEADER);
			return HEADER;
		}
	};
	

	private static CustomParserState nextHeaderOrFromFileState(ParseWindow window, CustomParserState fromState) {
		String line = window.getFocusLine();
		if (Patterns.matchesFromFilePattern(line)) {
			logTransition(line, fromState, FROM_FILE);
			return FROM_FILE;
		} 
		else {
			logTransition(line, fromState, HEADER);
			return HEADER;
		}
	}

	private static CustomParserState lineNextState(ParseWindow window, CustomParserState fromState) {
		String line = window.getFocusLine();
		if (Patterns.matchesFromLinePattern(line)) {
			logTransition(line, fromState, FROM_LINE);
			return FROM_LINE;
		}
		else if (Patterns.matchesToLinePattern(line)) {
			logTransition(line, fromState, TO_LINE);
			return TO_LINE;
		}
		else if (Patterns.matchesNeutralLinePattern(line)) {
			logTransition(line, fromState, NEUTRAL_LINE);
			return NEUTRAL_LINE;
		}
		else if (Patterns.matchesStartHeaderPattern(line)) {
			logTransition(line, fromState, NEXT_HEADER);
			return NEXT_HEADER;
		}
		else if (Patterns.matchesEndPattern(line, window)) {
			logTransition(line, fromState, END);
			return END;
		}
		else if (Patterns.matchesHunkStartPattern(line)) {
			logTransition(line, fromState, HUNK_START);
			return HUNK_START;
		}
		else {
			logTransition(line, fromState, PARTTIAL_LINE);
			return PARTTIAL_LINE;
		}
	}

//	protected static Logger logger = LoggerFactory.getLogger(ParserState.class);

	/**
	 * Returns the next state of the state machine depending on the current state and the content of a window of lines around the line
	 * that is currently being parsed.
	 *
	 * @param window the window around the line currently being parsed.
	 * @return the next state of the state machine.
	 */
	public abstract CustomParserState nextState(ParseWindow window);

	protected static void logTransition(String currentLine, CustomParserState fromState, CustomParserState toState) {
//		logger.debug(String.format("%12s -> %12s: %s", fromState, toState, currentLine));
	}

}
