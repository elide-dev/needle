package com.justinblank.strings;

import com.justinblank.classcompiler.ClassCompiler;
import com.justinblank.classloader.MyClassLoader;
import com.justinblank.strings.RegexAST.Node;
import com.justinblank.strings.RegexAST.NodePrinter;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static com.justinblank.strings.SearchMethodTestUtil.find;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

// Tests belong here if they're coupled to the internal structure of the classes that the DFAClassBuilder produces
public class DFAClassBuilderTest {

    private static final AtomicInteger CLASS_COUNTER = new AtomicInteger(0);

    @Test
    public void testContainedIn() {
        var dfa = DFA.createDFA("abcd");
        var factorization = RegexParser.parse("abcd").bestFactors();
        var builder = new DFAClassBuilder("testContainedIn", dfa,
                dfa, dfa, dfa, factorization, DebugOptions.none());
        builder.addMethod(builder.createContainedInMethod(new FindMethodSpec(dfa, "", true, new CompilationPolicy())));
        builder.addStateMethods();

        var compiler = new ClassCompiler(builder);
        byte[] classBytes = compiler.generateClassAsBytes();

        MyClassLoader.getInstance().loadClass("testContainedIn", classBytes);
    }

    private Class<?> compileFromBuilder(DFAClassBuilder builder, String name) throws IOException {
        var compiler = new ClassCompiler(builder);
        byte[] classBytes = compiler.generateClassAsBytes();
        return MyClassLoader.getInstance().loadClass(name, classBytes);
    }

    @Test
    public void testIndexForwards() {
        try {
            var dfa = DFA.createDFA("abc");
            var node = RegexParser.parse("abc");
            var dfaReversed = NFAToDFACompiler.compile(new NFA(RegexInstrBuilder.createNFA(node.reversed())),
                    ConversionMode.BASIC);
            var builder = new DFAClassBuilder("indexForwards",
                    dfa, dfa, dfaReversed, dfa, node.bestFactors(), DebugOptions.none());
            builder.initMethods();
            Class<?> c = compileFromBuilder(builder, "indexForwards");
            Object o = c.getDeclaredConstructors()[0].newInstance("abca");
            assertEquals(3, o.getClass().getDeclaredMethod("indexForwards", int.class, int.class).invoke(o, 0, 0));
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

    @Test
    public void testIndexForwardsSingleChar() {
        try {
            var dfa = DFA.createDFA("a");
            var node = RegexParser.parse("a");
            var builder = new DFAClassBuilder("indexForwardsSingleChar",
                    dfa, dfa, dfa, dfa, node.bestFactors(), DebugOptions.none());
            builder. initMethods();
            Class<?> c = compileFromBuilder(builder, "indexForwardsSingleChar");
            Object o = c.getDeclaredConstructors()[0].newInstance("aba");
            assertEquals(1, o.getClass().getDeclaredMethod("indexForwards", int.class, int.class).invoke(o, 0, 0));
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

    @Test
    public void testBuildPossiblyEmptyRange() {
        // This triggers handling of an empty string prefix
        testCompilable("[B-i]{0,2}");
    }

    @Test
    public void testRangeWithOffset() {
        testCompilable("[X-k]{3,6}}");
    }

    @Test
    public void testStateMethodIsCompilable() throws Exception {
        var dfa = DFA.createDFA("a");
        var builder = new DFAClassBuilder("testStateMethod",
                dfa, dfa, dfa, dfa, null, DebugOptions.none());
        builder.addStateMethods();
        Class<?> c = compileFromBuilder(builder, "testStateMethod");
    }

    @Test
    public void generativeDFAMatchingTest() {
        Random random = new Random();
        for (int maxSize = 1; maxSize < 6; maxSize++) {
            int count = 20 * (int) Math.pow(2, 6 - maxSize);
            for (int i = 0; i < count; i++) {
                RegexGenerator regexGenerator = new RegexGenerator(random, maxSize);
                Node node = regexGenerator.generate();
                String regex = NodePrinter.print(node);
                try {
                    testCompilable(regex);
                } catch (Throwable t) {
                    System.out.println("failed to compile regex=" + regex);
                    throw t;
                }
            }
        }
    }

    private void testCompilable(String regex) {
        var p= DFACompiler.compile(regex, "DFAClassBuilderTest" + CLASS_COUNTER.incrementAndGet());
        // This ensures we load the matcher class, and thereby validate it
        assertNotNull(p.matcher(""));
    }
}
