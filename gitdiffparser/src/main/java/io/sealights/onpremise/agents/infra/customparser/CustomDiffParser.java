package io.sealights.onpremise.agents.infra.customparser;

import static io.sealights.onpremise.agents.infra.customparser.Patterns.LINE_RANGE_PATTERN;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reflectoring.diffparser.api.DiffParser;

/**
 *    Copyright 2013-2015 Tom Hombergs (tom.hombergs@gmail.com | http://wickedsource.org)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import io.reflectoring.diffparser.api.model.Range;
import io.reflectoring.diffparser.unified.ResizingParseWindow;

/**
 * This is the enhanced version of {@link io.reflectoring.diffparser.api.UnifiedDiffParser}
 * The original class does not support parsing of comments with inline characters '\r' or '\n'
 * The major difference is using non-standard '=' indicator for unchanged lines, which assumes
 * that git diff output will be produced with a relevant option.
 * 
 * A parser that parses a unified diff from text into a {@link Diff} data structure.
 * <p/>
 * An example of a unified diff this parser can handle is the following:
 * <pre>
 * Modified: trunk/test1.txt
 * ===================================================================
 * --- /trunk/test1.txt	2013-10-23 19:41:56 UTC (rev 46)
 * +++ /trunk/test1.txt	2013-10-23 19:44:39 UTC (rev 47)
 * @@ -1,4 +1,3 @@
 * =test1
 * -test1
 * +test234
 * -test1
 * \ No newline at end of file
 * @@ -5,9 +6,10 @@
 * -test1
 * -test1
 * +test2
 * +test2
 * </pre>
 * Note that the TAB character and date after the file names are not being parsed but instead cut off.
 */
public class CustomDiffParser implements DiffParser {

    @Override
    public List<Diff> parse(InputStream in) {
        ResizingParseWindow window = new ResizingParseWindow(in);
        CustomParserState state = CustomParserState.INITIAL;
        List<Diff> parsedDiffs = new ArrayList<>();
        Diff currentDiff = new Diff();
        String currentLine;
        CustomParserState stateBefore = CustomParserState.INITIAL;
        while ((currentLine = window.slideForward()) != null) {
            if (state != CustomParserState.PARTTIAL_LINE ) {
            	stateBefore = state;
            }
            state = state.nextState(window);
            switch (state) {
                case INITIAL:
                    // nothing to do
                    break;
                case HEADER:
                    parseHeader(currentDiff, currentLine);
                    break;
                case FROM_FILE:
                    parseFromFile(currentDiff, currentLine);
                    break;
                case TO_FILE:
                    parseToFile(currentDiff, currentLine);
                    break;
                case HUNK_START:
                    parseHunkStart(currentDiff, currentLine);
                    break;
                case FROM_LINE:
                    parseFromLine(currentDiff, currentLine);
                    break;
                case TO_LINE:
                    parseToLine(currentDiff, currentLine);
                    break;
                case NEUTRAL_LINE:
                    parseNeutralLine(currentDiff, currentLine);
                    break;
                case PARTTIAL_LINE:
                	parsePartialLine(currentDiff, currentLine);
                	state = stateBefore;
                    break;
                case NEXT_HEADER:
                    parsedDiffs.add(currentDiff);
                    currentDiff = new Diff();
                    parseHeader(currentDiff, currentLine);
                    break;
                case END:
                    parsedDiffs.add(currentDiff);
                    currentDiff = new Diff();
                    break;
                default:
                    throw new IllegalStateException(String.format("Illegal parser state '%s", state));
            }
        }

        return parsedDiffs;
    }

    @Override
    public List<Diff> parse(byte[] bytes) {
        return parse(new ByteArrayInputStream(bytes));
    }

    @Override
    public List<Diff> parse(File file) throws IOException {
    	try (FileInputStream in = new FileInputStream(file)){
    		return parse(in);
    	}
    }

    private void parseNeutralLine(Diff currentDiff, String currentLine) {
        Line line = new Line(Line.LineType.NEUTRAL, currentLine.substring(1));
        currentDiff.getLatestHunk().getLines().add(line);
    }

    private void parseToLine(Diff currentDiff, String currentLine) {
        Line toLine = new Line(Line.LineType.TO, currentLine.substring(1));
        currentDiff.getLatestHunk().getLines().add(toLine);
    }

    private void parseFromLine(Diff currentDiff, String currentLine) {
        Line fromLine = new Line(Line.LineType.FROM, currentLine.substring(1));
        currentDiff.getLatestHunk().getLines().add(fromLine);
    }
    
    private void parsePartialLine(Diff currentDiff, String currentLine) {
    	int lastIndex = currentDiff.getLatestHunk().getLines().size() - 1;
    	Line lastLine = currentDiff.getLatestHunk().getLines().get(lastIndex);
    	String updatedContent = lastLine.getContent() + "\n" + currentLine;
    	currentDiff.getLatestHunk().getLines().remove(lastIndex);
    	currentDiff.getLatestHunk().getLines().add(new Line(lastLine.getLineType(), updatedContent));
    }

    private void parseHunkStart(Diff currentDiff, String currentLine) {
        Matcher matcher = LINE_RANGE_PATTERN.matcher(currentLine);
        if (matcher.matches()) {
            String range1Start = matcher.group(1);
            String range1Count = (matcher.group(2) != null) ? matcher.group(2) : "1";
            Range fromRange = new Range(Integer.valueOf(range1Start), Integer.valueOf(range1Count));

            String range2Start = matcher.group(3);
            String range2Count = (matcher.group(4) != null) ? matcher.group(4) : "1";
            Range toRange = new Range(Integer.valueOf(range2Start), Integer.valueOf(range2Count));

            Hunk hunk = new Hunk();
            hunk.setFromFileRange(fromRange);
            hunk.setToFileRange(toRange);
            currentDiff.getHunks().add(hunk);
        } else {
            throw new IllegalStateException(String.format("No line ranges found in the following hunk start line: '%s'. Expected something " +
                    "like '-1,5 +3,5'.", currentLine));
        }
    }

    private void parseToFile(Diff currentDiff, String currentLine) {
        currentDiff.setToFileName(cutAfterTab(currentLine.substring(4)));
    }

    private void parseFromFile(Diff currentDiff, String currentLine) {
        currentDiff.setFromFileName(cutAfterTab(currentLine.substring(4)));
    }

    /**
     * Cuts a TAB and all following characters from a String.
     */
    private String cutAfterTab(String line) {
        Pattern p = Pattern.compile("^(.*)\\t.*$");
        Matcher matcher = p.matcher(line);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return line;
        }
    }

    private void parseHeader(Diff currentDiff, String currentLine) {
        currentDiff.getHeaderLines().add(currentLine);
    }

}
