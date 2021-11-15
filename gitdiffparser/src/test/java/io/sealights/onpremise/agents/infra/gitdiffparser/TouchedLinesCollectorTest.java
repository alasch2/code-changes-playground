package io.sealights.onpremise.agents.infra.gitdiffparser;

import static io.sealights.onpremise.agents.infra.gitdiffparser.TestConstants.MULTI_HUNK_DIFF_1;
import static io.sealights.onpremise.agents.infra.gitdiffparser.TestConstants.RENAMED_FILE;
import static io.sealights.onpremise.agents.infra.gitdiffparser.TestConstants.SIMPLE_DIFF;
import static io.sealights.onpremise.agents.infra.gitdiffparser.TestConstants.STRING_UTILS_DIFF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import io.sealights.onpremise.agents.infra.customparser.FileTouchedLines;
import io.sealights.onpremise.agents.infra.customparser.FileTouchedLines.RevisionRange;
import io.sealights.onpremise.agents.infra.customparser.CustomDiffParser;
import io.sealights.onpremise.agents.infra.customparser.TouchedLinesCollector;
import lombok.AllArgsConstructor;

public class TouchedLinesCollectorTest {

	private CustomDiffParser parser;
	private TouchedLinesCollector collector;
	
	@BeforeMethod
	void beforeMethod() {
		parser = new CustomDiffParser();
		collector = new TouchedLinesCollector();
	}

	@AllArgsConstructor
	static class RevisionRangePair {
		RevisionRange from;
		RevisionRange to;
	};

	@DataProvider
	public static Object[][] input() {
		return new Object[][] {
			{SIMPLE_DIFF, 
			"java-agent-git/src/main/java/io/sealights/onpremise/agents/infra/git/cli/commands/GitCliCommand.java",
			Arrays.asList(new RevisionRangePair(
					new RevisionRange(62), 
					new RevisionRange(62, range(65, 71)))
			)},
			{STRING_UTILS_DIFF,
			"java-agent-infra/src/main/java/io/sealights/onpremise/agents/infra/utils/StringUtils.java",
			Arrays.asList(new RevisionRangePair(
					new RevisionRange(132, range(137, 138)), 
					new RevisionRange(132, Arrays.asList(135, 136, 137, 140, 141)))
			)},
			{RENAMED_FILE,
			"java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/AgentCiInfoBuilderTest.java",
			Arrays.asList(new RevisionRangePair(
					new RevisionRange(1, range(1, 1)), 
					new RevisionRange(1, range(1, 1)))
			)},
			{MULTI_HUNK_DIFF_1,
			"java-agent-infra-service-proxies/src/test/java/io/sealights/onpremise/agents/infra/serviceproxy/buildsession/BuildSessionServiceProxyTest.java",
			Arrays.asList(
					new RevisionRangePair(
						new RevisionRange(1, range(3, 7)), 
						new RevisionRange(1)),
					new RevisionRangePair(
							new RevisionRange(33), 
							new RevisionRange(28, range(31, 31))),
					new RevisionRangePair(
							new RevisionRange(40), 
							new RevisionRange(36, range(39, 42)))
				
			)},
		};
	}
	
	@Test(dataProvider = "input")
	public void testCollectSingleHunk(String input, String fileName, List<RevisionRangePair> rangesPair ) {
		Map<String, FileTouchedLines> files = collector.collect(parser.parse(input.getBytes()));
		assertEquals(files.size(), 1);
		assertTrue(files.containsKey(fileName));
		FileTouchedLines touchedLines = files.get(fileName);
		assertEquals(touchedLines.getTouchedList().size(), rangesPair.size());
		for (int i = 0; i < rangesPair.size(); i++) {
			assertRevisionRange(touchedLines.getTouchedList().get(i).getOldRevision(), rangesPair.get(i).from);
			assertRevisionRange(touchedLines.getTouchedList().get(i).getNewRevision(), rangesPair.get(i).to);
		}
	}
	
	private void assertRevisionRange(RevisionRange left, RevisionRange right) {
		assertEquals(left.getStart(), right.getStart());
		assertEquals(left.getTouched(), right.getTouched());
	}
	
	private static List<Integer> range(int start, int end) {
		List<Integer> result = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			result.add(i);
		}
		return result;
	}
	
}

