package io.sl.ex.antlr4.hello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

import generated.JavaLexer;
import generated.JavaParser;

public class ASTGenerator {
	private static final String FILE_PATH2 = //"src/main/resources/io/sl/ex/samples/ReversePolishNotation.java";
			"C:\\Users\\AlaSchneider\\Dev\\SL.Java.Examples\\sl-example-multi-module\\sl-example-java8\\src\\main\\java\\io\\sl\\ex\\java8\\streams\\GroupingCollectors.java";

    public static void main(String args[]) throws IOException {
		CharStream  input = CharStreams.fromFileName(FILE_PATH2);
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParserRuleContext ctx = parser.classDeclaration();
        ParseTree tree = parser.compilationUnit();
        printNode(tree, 0, Arrays.asList(parser.getRuleNames()));
    }
    
    private static void printNode(ParseTree node, int level, List<String> ruleNames) {
        String nodeText = Trees.getNodeText(node, ruleNames);

        // Print
        StringBuilder line = new StringBuilder();
        IntStream.range(0, level).forEach(i -> line.append(" "));
        line
                .append("Level ")
                .append(level)
                .append(" - ")
                .append(nodeText);
        System.out.println(line.toString());


        // Chilrdnre
        if (node instanceof ParserRuleContext) {
            ParserRuleContext parserRuleContext = (ParserRuleContext) node;
            if (parserRuleContext.children != null) {
                for (ParseTree child : parserRuleContext.children) {
                    printNode(child, level + 1, ruleNames);
                }
            }
        }
    }
}
