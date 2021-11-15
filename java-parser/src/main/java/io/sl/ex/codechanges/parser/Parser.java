package io.sl.ex.codechanges.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class Parser {

	private static final String FILE_PATH1 = //"src/main/resources/io/sl/ex/samples/ReversePolishNotation.java";
			"C:/Users/AlaSchneider/Dev/code-changes-playground/java-parser/src/main/resources/io/sl/ex/samples/ReversePolishNotation.java";
	private static final String FILE_PATH2 = //"src/main/resources/io/sl/ex/samples/ReversePolishNotation.java";
			"C:\\Users\\AlaSchneider\\Dev\\SL.Java.Examples\\sl-example-multi-module\\sl-example-java8\\src\\main\\java\\io\\sl\\ex\\java8\\streams\\GroupingCollectors.java";
	private static final String INFRA_ROOT = "C:/Users/AlaSchneider/Dev/SL.OnPremise.Agents.Java.Infra/";
	private static final String INFRA_PATH = "C:/Users/AlaSchneider/Dev/SL.OnPremise.Agents.Java.Infra/java-agent-events/src/main/java";
	
	public static void main(String[] args) throws Exception {
		//CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(FILE_PATH));
		//TreeParse.parse();
		CompilationUnitVisitor cuv = new CompilationUnitVisitor();
		cuv.visit(Paths.get(FILE_PATH2));
	}

	static void exploreComplilationUnit(CompilationUnit cu) {
		System.out.println(String.format("Parsed cu:%s", cu));
	}

	static class TreeParse {
		static int filesCount = 0;
		static void parse() throws Exception {
//	        StaticJavaParser.getConfiguration().setAttributeComments(false);
//	        StaticJavaParser.getConfiguration().setLexicalPreservationEnabled(false);
			CompilationUnitVisitor cuVisitor = new CompilationUnitVisitor();
	        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/main/**/*.java");
	        final PathMatcher excludeMatcher1 = FileSystems.getDefault().getPathMatcher("glob:**/test/resources/**/*.java");
	        final PathMatcher excludeMatcher2 = FileSystems.getDefault().getPathMatcher("glob:**/target/**/*.java");
	        Files.walkFileTree(Paths.get(INFRA_ROOT), new SimpleFileVisitor<Path>() {
	            @Override
	            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
	                if (matcher.matches(path) && !excludeMatcher1.matches(path) && !excludeMatcher2.matches(path)) {
	                    try {
							if (cuVisitor.visit(path)) {
								filesCount++;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }
	                return FileVisitResult.CONTINUE;
	            }
	        });
	        System.out.println("filesCount=" + filesCount);
		}
	}

	static class CompilationUnitVisitor {
		boolean visit(Path path) throws Exception {
			System.out.println("Parsing " + path.toString() + "...");
			CompilationUnit cu = JavaParser.parse(new FileInputStream(path.toString()));
			VoidVisitor<Void> methodNameVisitor = new MethodNamePrinter();
			methodNameVisitor.visit(cu, null);
			return true;
		}
	}
	
    static class MethodNamePrinter extends VoidVisitorAdapter<Void> {
    	private int methodIndex = 0;
		private int lambdaIndex = 0;

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);
            //String a = new YamlPrinter(true).output(md);
            System.out.println(String.format("[methodIndex=%s], method: %s, begin: %s, end:%s, type params:%s, signature:%s", 
            		methodIndex++, md.getName(), md.getBeginLine(), md.getEndLine(), md.getType(), md.getDeclarationAsString(true, true, true)));
            //System.out.println("body: " + md.getBody());
        }
        
        @Override
        public void visit(LambdaExpr le, Void arg) {
            super.visit(le, arg);
//            Statement s = new Statement() {
//                @Override
//                public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
//                    return null;
//                }
//
//                @Override
//                public <A> void accept(VoidVisitor<A> v, A arg) {
//
//                }
//            };
//            le.setParameters(EMPTY_LIST);
//            le.setBody(s);
			getScope(le);
            System.out.println(String.format("[methodIndex=%s, lambdaIndex=%s], lambda: start line: %s, end line: %s, params:%s,%nbody:%s",
            		methodIndex++, lambdaIndex++, le.getBody().getBeginLine(), le.getBody().getEndLine(), le.getParameters(), le.toString()));
		}

		private void getScope(LambdaExpr le) {
			StringBuilder builder = new StringBuilder("lambda parents: ");
			// collect parents
        	ParentsVisitor pv = new ParentsVisitor(le);
        	Node nextParent;
			while (pv.hasNext()) {
				nextParent = pv.next();
				builder.append("->").append(nextParent.getClass().getSimpleName());
//				System.out.println("next parent class:" + nextParent.getClass());
//				if (nextParent instanceof MethodDeclaration) {
//					MethodDeclaration method = (MethodDeclaration)nextParent;
//					System.out.println("lambda expression inside method " + method.getName() + ", modifiers=" + method.getModifiers());
//					break;
//				}
				
			}
			System.out.println(builder.toString());
//			MethodDeclaration parentMethod = (MethodDeclaration) findParentType(le, MethodDeclaration.class);
//			if (parentMethod != null) {
//				System.out.println(String.format("found parent method %s", parentMethod.getName()));
//			}
//			else {
//				InitializerDeclaration staticBlock = (InitializerDeclaration) findParentType(le, InitializerDeclaration.class);
//				if (staticBlock != null) {
//					System.out.println(String.format("found static declaration"));
//				}
//			}
		}
		
		Node findParentType(LambdaExpr le, Class<?> parentType) {
        	ParentsVisitor pv = new ParentsVisitor(le);
        	Node nextParent;
			while (pv.hasNext()) {
				nextParent = pv.next();
				System.out.println("next parent class:" + nextParent.getClass());
				if (nextParent.getClass() == parentType) {
					return nextParent;
				}				
			}
			return null;
		}
		
		enum LambdaScope {
			StaticStatement,
			InstanseMethod,
			StaticMethod
		}
    }
    
    /**
     * Iterates over the parent of the node, then the parent's parent, then the parent's parent's parent, until running
     * out of parents.
     */
    public static class ParentsVisitor implements Iterator<Node> {

        private Node node;

        public ParentsVisitor(Node node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node.getParentNode() != null;
        }

        @Override
        public Node next() {
            node = node.getParentNode();
            return node;
        }
    }
    
//    @AllArgumentsC
//    static class NodeTypeFilter {
//    	private Class<?> relevantType;
//    	
//    	public boolean match(Class<?> parentType) {
//    		return relevantType = parentType;
//    	}
//    }
//
}
