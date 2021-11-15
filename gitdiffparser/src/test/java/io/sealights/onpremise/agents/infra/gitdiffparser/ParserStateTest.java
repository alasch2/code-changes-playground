package io.sealights.onpremise.agents.infra.gitdiffparser;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.testng.Assert.assertEquals;

import io.reflectoring.diffparser.unified.ParseWindow;
import io.sealights.onpremise.agents.infra.customparser.CustomParserState;
import io.sealights.onpremise.agents.infra.customparser.Patterns;

public class ParserStateTest {
	private static final String SIMPLE_LINE = "       public void methodS() {\n";
	private static final String MULTI_TOKEN_LINE = "	 * Put here \n to confuse parser\n";
	
	@DataProvider
	public static Object[][] lines() {
		return new Object[][] {
			{Patterns.NEUTRAL_LINE + SIMPLE_LINE, CustomParserState.NEUTRAL_LINE},
			{Patterns.FROM_LINE + SIMPLE_LINE, CustomParserState.FROM_LINE},
			{Patterns.TO_LINE + SIMPLE_LINE, CustomParserState.TO_LINE},
			{SIMPLE_LINE, CustomParserState.PARTTIAL_LINE},
			{Patterns.NEUTRAL_LINE + MULTI_TOKEN_LINE, CustomParserState.NEUTRAL_LINE},
			{Patterns.FROM_LINE + MULTI_TOKEN_LINE, CustomParserState.FROM_LINE},
			{Patterns.TO_LINE + MULTI_TOKEN_LINE, CustomParserState.TO_LINE},
			{MULTI_TOKEN_LINE, CustomParserState.PARTTIAL_LINE},
		};
	}

	@Test(dataProvider="lines")
	public void testNextLineState(String line, CustomParserState expectedNextState) {
		ParseWindow window = mock(ParseWindow.class);
		when(window.getFocusLine()).thenReturn(line);
		assertEquals(CustomParserState.FROM_LINE.nextState(window), expectedNextState);
		assertEquals(CustomParserState.TO_LINE.nextState(window), expectedNextState);
		assertEquals(CustomParserState.NEUTRAL_LINE.nextState(window), expectedNextState);
	}
}
