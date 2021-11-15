package io.sl.ex.antlr4.hello;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import generated.JavaLexer;
import generated.JavaParser;
import generated.JavaParser.CompilationUnitContext;
import generated.JavaParser.MemberDeclarationContext;
import generated.JavaParser.MethodBodyContext;
import generated.JavaParser.MethodDeclarationContext;
import generated.JavaParser.ModifierContext;
import generated.JavaParserBaseVisitor;

public class SourceVisitor extends JavaParserBaseVisitor<Void> {

	private JavaParser parser;
	private static final String FILE_PATH2 = //"src/main/resources/io/sl/ex/samples/ReversePolishNotation.java";
			"C:\\Users\\AlaSchneider\\Dev\\SL.Java.Examples\\sl-example-multi-module\\sl-example-java8\\src\\main\\java\\io\\sl\\ex\\java8\\streams\\GroupingCollectors.java";
	public static void main(String[] args) throws Exception {
		CharStream  input = CharStreams.fromFileName(FILE_PATH2);
		JavaLexer lexer = new JavaLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		JavaParser parser = new JavaParser(tokens);
		ParseTree tree = parser.compilationUnit();
		SourceVisitor visitor = new SourceVisitor(parser);
		visitor.visit(tree);
	}
	
	public SourceVisitor(JavaParser parser) {
		this.parser = parser;
	}
	
	@Override
	public Void visitCompilationUnit(CompilationUnitContext ctx) {
		System.out.println("visitCompilationUnit - tree " + ctx.toStringTree());
		return super.visitCompilationUnit(ctx);
	}
	
	@Override
	public Void visitModifier(ModifierContext ctx) {
		System.out.println("visitModifier:" + ctx.getText());
		return super.visitModifier(ctx);
	}

	
	@Override
	public Void visitMemberDeclaration(MemberDeclarationContext ctx) {
		return super.visitMemberDeclaration(ctx);
	}

	@Override
	public Void visitMethodDeclaration(MethodDeclarationContext ctx) {
		TokenStream tokens = parser.getTokenStream();
		String type = "void";
		if (ctx.typeTypeOrVoid() != null) {
			type = tokens.getText(ctx.typeTypeOrVoid());
		}
		String args = tokens.getText(ctx.formalParameters());
		String fullMethodName = String.format("%s %s%s", type, ctx.IDENTIFIER(), args);
		System.out.println("visitMethodDeclaration:" + fullMethodName);
		return super.visitMethodDeclaration(ctx);
	}
	
	
}
