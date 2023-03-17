package com.justinblank.strings;

import com.justinblank.classloader.MyClassLoader;
import com.justinblank.strings.RegexAST.Node;
import org.objectweb.asm.Opcodes;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class DFACompiler {

    public static Pattern compile(String regex, String className) {
        return compile(regex, className, false);
    }

    static Pattern compile(String regex, String className, boolean debug) {
        if (debug) {
            System.out.println("Compiling " + className + "(" + regex + ")");
        }
        byte[] classBytes = compileToBytes(regex, className, debug ? new DebugOptions(true, true, true) : DebugOptions.none() );
        Class<?> matcherClass = MyClassLoader.getInstance().loadClass(className, classBytes);
        Class<? extends Pattern> c = createPatternClass(className, (Class<? extends Matcher>) matcherClass);
        try {
            return (Pattern) c.getDeclaredConstructors()[0].newInstance();
        } catch (Throwable t) {
            // TODO: determine good exceptions/result types
            throw new RuntimeException(t);
        }
    }

    public static byte[] compileToBytes(String regex, String className) {
        return compileToBytes(regex, className, new DebugOptions(false, false, false));
    }

    static byte[] compileToBytes(String regex, String className, DebugOptions debugOptions) {
        Node node = RegexParser.parse(regex);
        Factorization factors = node.bestFactors();
        factors.setMinLength(node.minLength());
        node.maxLength().ifPresent(factors::setMaxLength);
        DFA dfa = NFAToDFACompiler.compile(new NFA(RegexInstrBuilder.createNFA(node)));
        if (dfa.statesCount() > Short.MAX_VALUE / 2) {
            throw new IllegalArgumentException("Can't compile DFAs with more than " + (Short.MAX_VALUE / 2) + " states");
        }
        DFAClassBuilder builder = DFAClassBuilder.build(className, dfa, node, debugOptions);
        DFAClassCompiler compiler = new DFAClassCompiler(builder, debugOptions);
        byte[] classBytes = compiler.generateClassAsBytes();
        return classBytes;
    }

    private static Class<? extends Pattern> createPatternClass(String name, Class<? extends Matcher> m) {
        ClassBuilder builder = new ClassBuilder("Pattern" + name, "java/lang/Object", new String[]{"com/justinblank/strings/Pattern"});
        builder.emptyConstructor();
        var method = new Method("matcher", List.of("Ljava/lang/String;"), "Lcom/justinblank/strings/Matcher;", null);
        builder.addMethod(method);
        method
                .addBlock()
                .construct(name)
                .operate(DUP)
                .readVar(1, "Ljava/lang/String;")
                .call("<init>", name, "(Ljava/lang/String;)V", true)
                .addReturn(Opcodes.ARETURN);

        ClassCompiler compiler = new ClassCompiler(builder);
        return (Class<? extends Pattern>) MyClassLoader.getInstance().loadClass("Pattern" + name, compiler.writeClassAsBytes());
    }

}

