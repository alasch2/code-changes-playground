package io.sl.ex.antlr4.hello;

import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import generated.JavaLexer;
import generated.JavaParser;
import generated.JavaParser.BlockContext;
import generated.JavaParserBaseListener;
import io.sl.ex.antlr4.hello.FileWalker.ListenerProvider;

public class SourceListener extends JavaParserBaseListener {
    private JavaParser parser;
    private String packageName;
    private List<String> imports;
    private Stack<Object> processStack = new Stack<>();
    private int declaredElements = 0;
    
    private boolean isProcessingBlock() {
        return !processStack.isEmpty() && processStack.peek() instanceof Boolean;
    }
    
    @Override
    public void enterBlock(BlockContext ctx) {
    	processStack.push(true);
    }

    @Override
    public void exitBlock(BlockContext ctx) {
        if (processStack.size() >= 2) {
            Object element = processStack.get(processStack.size() - 2);
            if (element instanceof StringBuilder) {
                // Process method/constructor source code
//                Utils.convertirTexto(ctx.children, (StringBuilder) element);
            }
        }
        processStack.pop();
    }
    
	@Override
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        System.out.println("enterCompilationUnit:" + ContextHelper.toStringContext(ctx));
        ctx.toStringTree();
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        System.out.println("enter class :" + ctx.IDENTIFIER().toString() + "," + ContextHelper.toStringContext(ctx));
        ctx.toStringTree();
//        ctx.getParent()
    }

    @Override
    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        System.out.println("end class :" + ctx.IDENTIFIER().toString());
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        System.out.println("enterMethodDeclaration:" + ctx.IDENTIFIER().toString() + "," + ContextHelper.toStringContext(ctx));
    }

//    @Override
//    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
//        System.out.println("exitMethodDeclaration:" + ctx.IDENTIFIER().toString());
//    }

    @Override
    public void enterMethodBody(JavaParser.MethodBodyContext ctx) {
        super.enterMethodBody(ctx);
    }

    public SourceListener(JavaParser parser) {
        this.parser = parser;
    }

	private static final String FILE_PATH2 = //"src/main/resources/io/sl/ex/samples/ReversePolishNotation.java";
			"C:\\Users\\AlaSchneider\\Dev\\SL.Java.Examples\\sl-example-multi-module\\sl-example-java8\\src\\main\\java\\io\\sl\\ex\\java8\\streams\\GroupingCollectors.java";
	private static SourceListenerProvider PROVIDER = new SourceListenerProvider();
	
	public static void main(String[] args) throws Exception {
		FileWalker.walkTree(PROVIDER, FILE_PATH2);
	}
	
	static class SourceListenerProvider implements ListenerProvider<SourceListener> {

		@Override
		public SourceListener createListener(JavaParser parser) {
			return new SourceListener(parser);
		}
		
	}
}
