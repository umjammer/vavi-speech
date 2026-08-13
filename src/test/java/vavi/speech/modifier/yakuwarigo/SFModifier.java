/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * SFModifier.
 * <p>
 * Reads and writes private static fields, so that unit tests can swap the
 * randomizers and shufflers of the converters for fixed fixtures.
 * <p>
 * Writing needs the field to be non final at the point the JVM loaded it, which
 * {@link SFAgent} arranges by rewriting the class file on the way in. Up to
 * Java 17 this class instead cleared {@code ACC_FINAL} through the
 * {@code Field.modifiers} back door; JEP 416 closed that in Java 18.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-05-23 nsano initial version <br>
 *          0.01 2026-08-13 nsano rely on SFAgent instead of Field.modifiers <br>
 * @see SFAgent
 */
public class SFModifier {

    /** */
    private static void setSecurityLow(Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            throw new IllegalStateException(String.format("""
                    %s#%s is still final, so it cannot be replaced.
                    the -javaagent for %s is missing from the command line, or it was not asked for this field.
                    expected: -javaagent:target/sf-agent.jar=%s#%s""",
                    field.getDeclaringClass().getName(), field.getName(),
                    SFAgent.class.getName(),
                    field.getDeclaringClass().getName(), field.getName()));
        }
        field.setAccessible(true);
    }

    /** modify a static final field */
    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        setSecurityLow(field);
        field.set(null, newValue);
    }

    /** read a static final field */
    public static Object getFinalStatic(Field field) throws Exception {
        field.setAccessible(true);
        return field.get(null);
    }
}
