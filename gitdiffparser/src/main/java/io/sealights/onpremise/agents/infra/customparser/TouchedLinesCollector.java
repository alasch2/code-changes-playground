package io.sealights.onpremise.agents.infra.customparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import io.sealights.onpremise.agents.infra.customparser.FileTouchedLines.TouchedLines;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Implements collecting of touched lines from parsed git-diff output
 * 
 * @author AlaSchneider
 *
 */
public class TouchedLinesCollector {
	private static final String NOT_EXIST = "/dev/null";
	private Map<String, FileTouchedLines> files = new HashMap<>();

	public Map<String, FileTouchedLines> collect(List<Diff> diffs) {
//		System.out.println("parsed:\n" + DiffPrinter.toString(diffs));
		for (Diff diff : diffs) {
//			System.out.println("Handling\n" + DiffPrinter.toString(diff));
			if (isNotRelevantDiff(diff)) {
				continue;
			}
			
			files.put(diff.getToFileName().substring(2), toFileTouchedLines(diff));
		}
//		System.out.println("collected touched:\n" + modifiedFiles);	
		return files;
	}
	
	protected boolean isNotRelevantDiff(Diff diff) {
		if (NOT_EXIST.equals(diff.getToFileName())) {
			return true;
		}

		if (NOT_EXIST.equals(diff.getFromFileName())) {
			return true;
		}

		return false;		
	}
	
	protected FileTouchedLines toFileTouchedLines(Diff diff) {
		FileTouchedLines fileTouchedLines = new FileTouchedLines();
		for (Hunk hunk : diff.getHunks()) {
			TouchedLines touchedLines = new TouchedLines(
					hunk.getFromFileRange().getLineStart(),
					hunk.getToFileRange().getLineStart());
			Cursor cursor = new Cursor(hunk);
			for (Line line : hunk.getLines()) {
				analyzeLine(line, cursor, touchedLines);
			}
			fileTouchedLines.addTouchedLines(touchedLines);
		}
		System.out.println("collected fileTouchedLines:\n" + fileTouchedLines);
		return fileTouchedLines;
	}

	public void analyzeLine(Line line, Cursor cursor, TouchedLines touchedLines) {
			switch (line.getLineType()) {
			case FROM:
				touchedLines.addOldTouchedLine(cursor.getFromLine());
				cursor.incFrom();
				break;
			case TO:
				touchedLines.addNewTouchedLine(cursor.getToLine());
				cursor.incTo();
				break;
			case NEUTRAL:
				cursor.incAll();
				break;
			}
	}

	@Data
	@AllArgsConstructor
	static class Cursor {
		private int fromLine;
		private int toLine;
		
		public Cursor(Hunk hunk) {
			fromLine = hunk.getFromFileRange().getLineStart();
			toLine = hunk.getToFileRange().getLineStart();
		}
		
		public void incFrom() {
			fromLine++;
		}
		
		public void incTo() {
			toLine++;
		}
		
		public void incAll() {
			incFrom();
			incTo();
		}
		
		@Override
		public String toString() {
			return String.format("fromLine=%s, toLine=%s", fromLine, toLine);
		}
	}
}
