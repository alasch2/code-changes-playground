package io.sl.ex.antlr4.hello;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import generated.JavaLexer;
import generated.JavaParser;
import generated.JavaParserBaseListener;

public class FileWalker {
	
	public static <T extends JavaParserBaseListener> void walkTree(ListenerProvider<T> listenerProvider, String fileName) throws IOException {
		JavaParser parser = createFileParser(fileName);
		ParseTree tree = parser.compilationUnit();
		ParseTreeWalker walker = new ParseTreeWalker();
		T listener = listenerProvider.createListener(parser);
		walker.walk(listener, tree);
	}
	
	public static JavaParser createFileParser(String fileName) throws IOException {
		CharStream  input = CharStreams.fromFileName(fileName);
		JavaLexer lexer = new JavaLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new JavaParser(tokens);
	}
	
	static interface ListenerProvider<T extends JavaParserBaseListener> {
		T createListener(JavaParser parser);
	}

}
