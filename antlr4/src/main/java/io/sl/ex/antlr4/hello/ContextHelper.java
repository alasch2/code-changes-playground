package io.sl.ex.antlr4.hello;

import org.antlr.v4.runtime.ParserRuleContext;

public class ContextHelper {
	
	public static<T extends ParserRuleContext> String toStringContext(T ctx) {
		StringBuilder b = new StringBuilder();
		b.append(" start:").append(ctx.getStart().getLine());
		b.append(" stop:").append(ctx.getStop().getLine());
		int childsCount = ctx.getChildCount();
		if (childsCount > 0) {
			b.append(" childs (").append(childsCount).append(") : \n");
			for (int i=0; i<childsCount; i++) {
				b.append(ctx.getChild(i).toString()).append("\n");
			}
		}
		return b.toString();
	}

}
