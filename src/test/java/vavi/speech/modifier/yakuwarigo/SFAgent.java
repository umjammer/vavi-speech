/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo;

import java.lang.classfile.AccessFlags;
import java.lang.classfile.ClassFile;
import java.lang.classfile.FieldElement;
import java.lang.classfile.FieldModel;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.AccessFlag;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * SFAgent.
 * <p>
 * A java agent that strips {@code ACC_FINAL} off selected {@code static final}
 * fields while the class is being loaded, so that unit tests can inject mocks
 * into them by plain reflection without the production code having to weaken
 * its own {@code final}s.
 * <p>
 * Java 18+ (JEP 416) reimplemented core reflection on method handles and put
 * {@code Field.modifiers} on the reflection filter list, which killed the old
 * "un-finalize a field at runtime" trick. Rewriting the class file on the way
 * in is the remaining supported route: the field is genuinely not final in the
 * class the JVM ends up loading, so {@link java.lang.reflect.Field#set} works
 * and the JIT will not constant-fold reads of it either.
 * <h2>usage</h2>
 * <pre>{@code
 * -javaagent:sf-agent.jar=vavi.speech.modifier.yakuwarigo.salome.EQMark#shuffler,foo.Bar
 * }</pre>
 * Each comma separated entry is either {@code fqcn#fieldName} for one field, or
 * a bare {@code fqcn} for every static field of that class. Add
 * {@code -Dvavi.speech.sfagent.verbose} to log what got rewritten.
 * <h2>caveat</h2>
 * Field modifiers are part of the class schema, so {@code retransformClasses}
 * cannot do this to a class that is already loaded -- the JVM answers
 * {@code UNSUPPORTED_REDEFINITION_SCHEMA_CHANGED}. The agent therefore has to be
 * on the command line via {@code -javaagent}, not attached later.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2026-08-13 nsano initial version <br>
 * @see "https://openjdk.org/jeps/416"
 * @see "https://openjdk.org/jeps/484"
 */
public final class SFAgent {

    private SFAgent() {
    }

    /** every static field of the class */
    private static final String WILDCARD = "*";

    /** */
    private static final boolean verbose = System.getProperty("vavi.speech.sfagent.verbose") != null;

    /** internal class name ("a/b/C") -> field names to de-finalize */
    private static final Map<String, Set<String>> targets = new HashMap<>();

    /** installed by {@code -javaagent}, before any target class is loaded */
    public static void premain(String args, Instrumentation inst) {
        install(args, inst);
    }

    /** installed by dynamic attach, too late for anything already loaded */
    public static void agentmain(String args, Instrumentation inst) {
        install(args, inst);

        Arrays.stream(inst.getAllLoadedClasses())
                .filter(c -> targets.containsKey(c.getName().replace('.', '/')))
                .forEach(c -> System.err.println("SFAgent: " + c.getName() + " is already loaded, its fields stay final"
                        + " (field modifiers are a schema change, so retransformClasses cannot help)"));
    }

    /** */
    private static void install(String args, Instrumentation inst) {
        parse(args);
        if (targets.isEmpty()) {
            System.err.println("SFAgent: no targets given, nothing to do");
            return;
        }

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain domain, byte[] classFileBuffer) {
                Set<String> fields = className == null ? null : targets.get(className);
                if (fields == null) {
                    return null;
                }
                try {
                    return deFinalize(className, classFileBuffer, fields);
                } catch (Throwable t) {
                    // a throw out of here is swallowed by the JVM and the class silently loads
                    // untransformed, so say something instead of failing far away from the cause
                    System.err.println("SFAgent: cannot transform " + className);
                    t.printStackTrace();
                    return null;
                }
            }
        }, false);
    }

    /** {@code fqcn#field,fqcn,...} */
    private static void parse(String args) {
        if (args == null || args.isBlank()) {
            return;
        }
        for (String spec : args.split(",")) {
            spec = spec.trim();
            if (spec.isEmpty()) {
                continue;
            }
            int p = spec.indexOf('#');
            String className = (p < 0 ? spec : spec.substring(0, p)).trim().replace('.', '/');
            String fieldName = p < 0 ? WILDCARD : spec.substring(p + 1).trim();
            targets.computeIfAbsent(className, k -> new HashSet<>()).add(fieldName);
        }
    }

    /** rewrites the class file with {@code ACC_FINAL} cleared on the wanted static fields */
    private static byte[] deFinalize(String className, byte[] bytes, Set<String> fields) {
        ClassFile classFile = ClassFile.of();
        return classFile.transformClass(classFile.parse(bytes), (builder, element) -> {
            if (element instanceof FieldModel field && wanted(field, fields)) {
                if (verbose) {
                    System.err.println("SFAgent: de-finalize " + className + "#" + field.fieldName().stringValue());
                }
                builder.withField(field.fieldName(), field.fieldType(), fieldBuilder -> {
                    for (FieldElement fieldElement : field) {
                        if (fieldElement instanceof AccessFlags flags) {
                            fieldBuilder.withFlags(flags.flagsMask() & ~ClassFile.ACC_FINAL);
                        } else {
                            // the ConstantValue attribute is carried over untouched: the JVM still
                            // honours it for a static field whether or not that field is final
                            fieldBuilder.with(fieldElement);
                        }
                    }
                });
            } else {
                builder.with(element);
            }
        });
    }

    /** */
    private static boolean wanted(FieldModel field, Set<String> fields) {
        return field.flags().has(AccessFlag.STATIC) &&
                field.flags().has(AccessFlag.FINAL) &&
                (fields.contains(WILDCARD) || fields.contains(field.fieldName().stringValue()));
    }
}
