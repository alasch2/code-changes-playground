package io.sl.ex.antlr4.hello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;

import generated.JavaParser;
import generated.JavaParser.ArgumentsContext;
import generated.JavaParser.BlockContext;
import generated.JavaParser.BlockStatementContext;
import generated.JavaParser.ClassBodyDeclarationContext;
import generated.JavaParser.ClassCreatorRestContext;
import generated.JavaParser.ClassDeclarationContext;
import generated.JavaParser.ClassOrInterfaceModifierContext;
import generated.JavaParser.ClassOrInterfaceTypeContext;
import generated.JavaParser.CreatedNameContext;
import generated.JavaParser.CreatorContext;
import generated.JavaParser.ExpressionContext;
import generated.JavaParser.ExpressionListContext;
import generated.JavaParser.ImportDeclarationContext;
import generated.JavaParser.IntegerLiteralContext;
import generated.JavaParser.LambdaBodyContext;
import generated.JavaParser.LambdaExpressionContext;
import generated.JavaParser.LambdaParametersContext;
import generated.JavaParser.LiteralContext;
import generated.JavaParser.LocalVariableDeclarationContext;
import generated.JavaParser.MemberDeclarationContext;
import generated.JavaParser.MethodBodyContext;
import generated.JavaParser.MethodCallContext;
import generated.JavaParser.MethodDeclarationContext;
import generated.JavaParser.ModifierContext;
import generated.JavaParser.PackageDeclarationContext;
import generated.JavaParser.PrimaryContext;
import generated.JavaParser.QualifiedNameContext;
import generated.JavaParser.StatementContext;
import generated.JavaParser.VariableDeclaratorIdContext;
import generated.JavaParser.VariableDeclaratorsContext;
import generated.JavaParser.VariableInitializerContext;
import generated.JavaParserBaseListener;
import io.sl.ex.antlr4.hello.FileWalker.ListenerProvider;
import io.sl.ex.antlr4.hello.fileParser.GenericName;
import lombok.AllArgsConstructor;

public class JavaListenerExample extends JavaParserBaseListener {
	
    private JavaParser parser;
	private String packageName;
	private Stack<String> nameSpace = new Stack<>();
	private ModifiersCollector methodModifierFilter = new ModifiersCollector();
	
	int inClassCtr = 0;
	
	public JavaListenerExample(JavaParser parser) {
        this.parser = parser;
        System.out.println("rules:" + Arrays.asList(parser.getRuleNames()));
    }

//	@Override
//	public void enterEveryRule(ParserRuleContext ctx) {
////		if (isPrintableRule(ctx))
//		System.out.println("..... enterEveryRule - " + ctx.getClass() + "; index: " +  ctx.getRuleIndex());
//		methodModifierFilter.onEveryRule(ctx);
//	}

//	@Override
//	public void exitEveryRule(ParserRuleContext ctx) {
//		if (isPrintableRule(ctx))
//		System.out.println(".............exitEveryRule - " + ctx.getClass());// +  ctx.getText());
//	}

	@Override
	public void enterPackageDeclaration(PackageDeclarationContext ctx) {
		packageName = ctx.qualifiedName().getText();
		System.out.println("Package detected:" + packageName);
		nameSpace.add(packageName);
	}
	
	private boolean isPrintableRule(ParserRuleContext ctx) {
		return 
				!(ctx instanceof VariableDeclaratorsContext) &&
				!(ctx instanceof VariableDeclaratorIdContext) &&
				!(ctx instanceof VariableInitializerContext) &&
				!(ctx instanceof LocalVariableDeclarationContext) &&
				!(ctx instanceof PrimaryContext) &&
				!(ctx instanceof LiteralContext) &&
				!(ctx instanceof IntegerLiteralContext) &&
				!(ctx instanceof ArgumentsContext) &&
				!(ctx instanceof ClassCreatorRestContext) &&
				!(ctx instanceof CreatorContext) &&
				!(ctx instanceof CreatedNameContext) &&
				!(ctx instanceof QualifiedNameContext) &&
				!(ctx instanceof ImportDeclarationContext) &&
				!(ctx instanceof LambdaExpressionContext) &&
				!(ctx instanceof LambdaParametersContext) &&
				!(ctx instanceof LambdaBodyContext) &&
				!(ctx instanceof ExpressionListContext) &&
				!(ctx instanceof ExpressionContext) &&
				!(ctx instanceof MethodCallContext) &&
				!(ctx instanceof BlockContext) &&
				!(ctx instanceof BlockStatementContext) &&
				!(ctx instanceof StatementContext);
	}
	
//	@Override
//	public void enterClassBodyDeclaration(ClassBodyDeclarationContext ctx) {
//		System.out.println("enterClassBodyDeclaration:" + ctx.getText());
//		List<ModifierContext> modifiers = ctx.getRuleContexts(ModifierContext.class);
//		for (ModifierContext mdf: modifiers) {
//			List<MemberDeclarationContext> members = mdf.getRuleContexts(MemberDeclarationContext.class);
//			for (MemberDeclarationContext mmb : members) {
//				System.out.println("" + mmb.getText() + "index:" + mmb.getRuleIndex());
//			}
//		}
//	}
//
	@Override
	public void enterModifier(ModifierContext ctx) {
		System.out.println("enterModifier - parent:" + ctx.getParent().getClass());
		methodModifierFilter.onEnterModfierContext(ctx);
	}

	//
	@Override
	public void enterClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
		System.out.println("enterClassOrInterfaceModifier - parent:" + ctx.getParent().getClass());
		methodModifierFilter.onEnterClassOrInterfaceModifier(ctx);
	}
	
	///////////////////////
	// Class or Interface
	// classDeclaration | enumDeclaration | interfaceDeclaration |
	/////////////////////// annotationTypeDeclaration
	@Override
	public void enterClassDeclaration(ClassDeclarationContext ctx) {
		if (ctx.IDENTIFIER()==null) return;
//		System.out.println("enterClassDeclaration - " + ctx.toInfoString(parser));
//		System.out.println("enterClassDeclaration - tree:" + ctx.toStringTree(parser));
		String className = GenericName.build(ctx.IDENTIFIER().getText()).toString();
		TokenStream tokens = parser.getTokenStream();
		ctx.getRuleContexts(MemberDeclarationContext.class);
		if (nameSpace.empty()) {
			System.out.println("undefined package for class " + className);
			nameSpace.add(className);
		}
		else {
			String fullName = nameSpace.peek() + "." + className;
			System.out.println("-->entered name-space " + fullName);
			nameSpace.add(fullName);
		}
		inClassCtr++;
//		context.foundNewType(GenericName.build(ctx.IDENTIFIER().getText()));
//		// implements
//		if (ctx.typeList() != null) {
//			for (int i = 0; i < ctx.typeList().typeType().size(); i++) {
//				context.foundImplements(GenericName.build(ClassTypeContextHelper.getClassName(ctx.typeList().typeType().get(i))));
//			}
//		}
//		// extends relation
//		if (ctx.typeType() != null) {
//			context.foundExtends(GenericName.build(ClassTypeContextHelper.getClassName(ctx.typeType())));
//		}
//
//		if (ctx.typeParameters() != null) {
//			foundTypeParametersUse(ctx.typeParameters());
//		}
//		annotationProcessor.processAnnotationModifier(ctx, TypeDeclarationContext.class ,"classOrInterfaceModifier.annotation",context.lastContainer());
//		super.enterClassDeclaration(ctx);
	}
	
	@Override
	public void exitClassDeclaration(ClassDeclarationContext ctx) {
		String className = GenericName.build(ctx.IDENTIFIER().getText()).toString();
		if (nameSpace.peek().endsWith(className)) {
			String extracted = nameSpace.pop();
			System.out.println("exit nameSpace:" + extracted);
		}
		else {
			System.out.println("unexpected name-spase on exit of class " + className);
		}
		inClassCtr--;
	}

//	@Override
//	public void enterMethodBody(MethodBodyContext ctx) {
//		System.out.println("enterMethodBody" + ctx.getText());
//	}
//
	@Override
	public void enterMethodDeclaration(MethodDeclarationContext ctx) {
//		System.out.println("enterMethodDeclaration" + ctx.getText());
		System.out.println("enterMethodDeclaration - parent:" + ctx.getParent().getClass());
		TokenStream tokens = parser.getTokenStream();
		String type = "void";
		if (ctx.typeTypeOrVoid() != null) {
			type = tokens.getText(ctx.typeTypeOrVoid());
		}
		String modifiers = methodModifierFilter.getAndCleanModifiers();
		String args = tokens.getText(ctx.formalParameters());
		String fullMethodName = String.format("%s%s %s%s", modifiers, type, ctx.IDENTIFIER(), args);
		System.out.println("enterMethodDeclaration for:" + fullMethodName);
	}
//
//	@Override
//	public void enterClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
//		System.out.println("enterClassOrInterfaceModifier:" + ctx.getText()
//		+ ", parent:" + ctx.getParent().getRuleContext());
//	}

//		@Override
//		public void enterClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {
//			System.out.println("enterClassOrInterfaceType:" + ctx.getText());
//		}
//
//	@Override
//	public void enterClassType(ClassTypeContext ctx) {
//		System.out.println("enterClassType:" + ctx.getText());
//	}

	@Override
	public void enterMemberDeclaration(MemberDeclarationContext ctx) {
		System.out.println("enterMemberDeclaration:" +  ctx.getText() + ", parent:" + ctx.getParent().getClass());
	}
	

	private static final String FILE_PATH2 = //"src/main/resources/io/sl/ex/samples/ReversePolishNotation.java";
			"C:\\Users\\AlaSchneider\\Dev\\SL.Java.Examples\\sl-example-multi-module\\sl-example-java8\\src\\main\\java\\io\\sl\\ex\\java8\\streams\\GroupingCollectors.java";

	

	private static JavaListenerExampleProvider PROVIDER = new JavaListenerExampleProvider();
	
	public static void main(String[] args) throws Exception {
		FileWalker.walkTree(PROVIDER, FILE_PATH2);
	}
	
	static class JavaListenerExampleProvider implements ListenerProvider<JavaListenerExample> {

		@Override
		public JavaListenerExample createListener(JavaParser parser) {
			return new JavaListenerExample(parser);
		}
		
	}
	
	static class StateMachine {
		boolean isRelevant(ParserRuleContext previous, ParserRuleContext current) {
			MatchingRule previousRule = MatchingRule.toMatchingRule(previous);
			MatchingRule currentRule = MatchingRule.toMatchingRule(current);
			for (State s : states) {
				if (s.currentRule == currentRule && s.previousRule == previousRule) {
					return true;
				}
			}
			return false;
		}
		
		enum MatchingRule {
			Modifier, 
			ClassOrInterfaceModifier, 
			MemberDeclaration, 
			MethodDeclaration;
			
			private static MatchingRule toMatchingRule(ParserRuleContext ctx) {
				return valueOf(ctx.getClass().getSimpleName().replace("Context", ""));
			}
		}

		@AllArgsConstructor
		static class State {
			MatchingRule previousRule;
			MatchingRule currentRule;
			MatchingRule nextRule;
		}

		static List<State> states = Arrays.asList(
				new State(null, MatchingRule.Modifier, MatchingRule.ClassOrInterfaceModifier),
				new State(MatchingRule.Modifier, MatchingRule.ClassOrInterfaceModifier, MatchingRule.MemberDeclaration),
				new State(MatchingRule.ClassOrInterfaceModifier, MatchingRule.MemberDeclaration, MatchingRule.MethodDeclaration),
				new State(MatchingRule.MemberDeclaration, MatchingRule.MethodDeclaration, null)
				);
	}
	
	class ModifiersCollector {
		List<String> modifiers = new ArrayList<>();
		
		int enteredModfierContext = 0;
		int enteredClassOrInterfaceModifierContext = 0;		
		void onEveryRule(ParserRuleContext ctx) {
			if (!isEmpty() &&
				!(ctx instanceof ModifierContext) && 
				!(ctx instanceof ClassOrInterfaceModifierContext) &&
				!(ctx instanceof MemberDeclarationContext) &&
				!(ctx instanceof MethodDeclarationContext)
					) {
				reset();
			}
			else {
				if (!isEmpty())
				System.out.println("relevant ctx " + ctx.getClass());
			}
		}
		
//		void onEveryRule(ParserRuleContext ctx) {
//			if (!isEmpty() &&
//				!(ctx instanceof ModifierContext) && 
//				!(ctx instanceof ClassOrInterfaceModifierContext) &&
//				!(ctx instanceof MemberDeclarationContext) &&
//				!(ctx instanceof MethodDeclarationContext)
//					) {
//				reset();
//			}
//			else {
//				if (!isEmpty())
//				System.out.println("relevant ctx " + ctx.getClass());
//			}
//		}
//
		void onEnterModfierContext(ModifierContext ctx) {
			enteredModfierContext++;
		}
		
		void onEnterClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
			if (enteredModfierContext > 0) {
				enteredClassOrInterfaceModifierContext++;
				if (enteredModfierContext == enteredClassOrInterfaceModifierContext) {
					modifiers.add(ctx.getText());
					System.out.println("Added modifier " + ctx.getText());
				}
			}
		}
		
		String getAndCleanModifiers() {
			if (isEmpty()) {
				return "";
			}
			StringBuilder b = new StringBuilder();
			for (String m : modifiers) {
				b.append(m).append(" ");
			}
			reset();
			return b.toString();
			
		}
		
		boolean isEmpty() {
			return modifiers.isEmpty();
		}
		
		void reset() {
			modifiers.clear();
			System.out.println("Reset modifiers");
		}
	}
}
