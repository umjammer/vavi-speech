/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * SFModifier.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-05-23 nsano initial version <br>
 * @see "https://stackoverflow.com/a/56043252"
 * @see "https://stackoverflow.com/a/14724884"
 */
public class SFModifier {

    /** @see "https://stackoverflow.com/a/56043252" */
    private static final VarHandle MODIFIERS;

    /* @see "https://stackoverflow.com/a/56043252" */
    static {
        try {
            var lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            MODIFIERS = lookup.findVarHandle(Field.class, "modifiers", int.class);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see "https://stackoverflow.com/a/56043252"
     */
    static void setSecurityLow(Field field) throws Exception {
        // TODO this works Java 12 - 17
        MODIFIERS.set(field, field.getModifiers() & ~Modifier.FINAL);
        field.setAccessible(true);
    }

    /** modify a static final field */
    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        setSecurityLow(field);
        field.set(null, newValue);
    }

    public static Object getFinalStatic(Field field) throws Exception {
        setSecurityLow(field);
        return field.get(null);
    }
}
