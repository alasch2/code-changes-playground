package io.sealights.onpremise.agents.infra.gitdiffparser;

import static io.sealights.onpremise.agents.infra.gitdiffparser.TestConstants.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import io.reflectoring.diffparser.api.model.Range;
import io.sealights.onpremise.agents.infra.customparser.CustomDiffParser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

public class CustomDiffParserTest {

	private CustomDiffParser parser;
	
	@BeforeMethod
	void beforeMethod() {
		parser = new CustomDiffParser();
	}
	
	@DataProvider
	public static Object[][] singleDiffInput() {
		return new Object[][] {
			{SIMPLE_DIFF, 62, 62, 0, 7, 6},
			{STRING_UTILS_DIFF, 132, 132, 2, 5, 8},
			{ADDED_FILE, 0, 1, 0, 46, 0},
			{RENAMED_FILE, 1, 1, 1, 1, 3},
		};
	}
	
	@Test(dataProvider = "singleDiffInput")
	public void testParseSingleHunk(String input, int fromStart, int toStart, 
			int fromLinesSize, int toLinesSize, int bothLinesSize) {
		List<Diff> diffs = parser.parse(input.getBytes());
		assertEquals(diffs.size(), 1);
		Diff diff = diffs.get(0);
		assertEquals(diff.getHunks().size(), 1);
		Hunk hunk = diff.getHunks().get(0);
		assertEquals(hunk.getFromFileRange().getLineStart(), fromStart);
		assertEquals(hunk.getToFileRange().getLineStart(), toStart);
		assertEquals(countLinesOfType(Line.LineType.FROM, hunk.getLines()), fromLinesSize);
		assertEquals(countLinesOfType(Line.LineType.TO, hunk.getLines()), toLinesSize);
		assertEquals(countLinesOfType(Line.LineType.NEUTRAL, hunk.getLines()), bothLinesSize);
	}
	
	private int countLinesOfType(Line.LineType lineType, List<Line> lines) {
		int ctr = 0;
		for (Line line : lines) {
			if (line.getLineType() == lineType) ctr++;
		}
		return ctr;
	}
	
	@AllArgsConstructor
	static class RangePair {
		Range from;
		Range to;
	};
	
	@DataProvider
	public static Object[][] multiDiffInput() {
		return new Object[][] {
			{SIMPLE_DIFF + STRING_UTILS_DIFF + ADDED_FILE, 3, 1, 
				Arrays.asList(
						new RangePair(new Range(62, 6), new Range(62, 10)),
						new RangePair(new Range(132, 10), new Range(132, 13)),
						new RangePair(new Range(0, 0), new Range(1, 46))
						)},
			{MULTI_HUNK_DIFF_1, 1, 3, 
				Arrays.asList(
						new RangePair(new Range(1, 10), new Range(1, 5)),
						new RangePair(new Range(33, 6), new Range(28, 7)),
						new RangePair(new Range(40, 6), new Range(36, 10))
						)},
			{MULTI_HUNK_DIFF_2, 1, 2, 
							Arrays.asList(
									new RangePair(new Range(69, 2), new Range(69, 1)),
									new RangePair(new Range(72, 1), new Range(71, 1))
									)},
			{MULTI_HUNK_DIFF_2 + RENAMED_FILE_MULTI_HUNK, 2, 4, 
							Arrays.asList(
									new RangePair(new Range(69, 2), new Range(69, 1)),
									new RangePair(new Range(72, 1), new Range(71, 1)),
									new RangePair(new Range(1, 6), new Range(1, 1)),
									new RangePair(new Range(13, 1), new Range(8, 5))
									)},
		};
	}
	
	@Test(dataProvider = "multiDiffInput")
	public void testParseMultiDiffs(String input, int diffsSize, int hunksSize, List<RangePair> hunkRanges) {
		List<Diff> diffs = parser.parse(input.getBytes());
		assertEquals(diffs.size(), diffsSize);
		int huntRangesIndex = 0;
		for (Diff diff : diffs) {
			for (Hunk hunk : diff.getHunks()) {
				validateHunkRange(hunk, hunkRanges.get(huntRangesIndex++));
			}
		}
	}
	
	private void validateHunkRange(Hunk hunk, RangePair rangePair) {
		assertRange(hunk.getFromFileRange(), rangePair.from);
		assertRange(hunk.getToFileRange(), rangePair.to);
	}
	
	private void assertRange(Range left, Range right) {
		assertEquals(left.getLineStart(), right.getLineStart());
		assertEquals(left.getLineCount(), right.getLineCount());
	}
	
	private static final String DIFF_FILE_1 = "src/test/resources/diff.txt";
	private static final String DIFF_FILE_2 = "src/test/resources/diff-u0.txt";
	
	@DataProvider
	public static Object[][] diffFiles() {
		return new Object[][] {
			{DIFF_FILE_1, 20},
			{DIFF_FILE_2, 16}
		};
	}
	
	@SneakyThrows
	@Test(dataProvider = "diffFiles")
	public void testParseFile(String fileName,  int expectedFiles) {
		List<Diff> diffs = parser.parse(new File(fileName));
		assertEquals(diffs.size(), expectedFiles);
	}
	
}
