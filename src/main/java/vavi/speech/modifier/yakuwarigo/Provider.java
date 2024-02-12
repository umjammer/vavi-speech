/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo;

import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import vavi.util.Debug;

import static vavi.speech.modifier.yakuwarigo.Feature.newPos;

import static vavi.speech.modifier.yakuwarigo.Rule.ContinuousConditionsConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.ConvertRule;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.ConversionResult;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.StringResult;


/**
 * Provider.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-04-23 nsano initial version <br>
 */
public interface Provider {

    /* gson: not serialize when false  */
    JsonSerializer<Boolean> booleanJsonSerializer = (in, type, context) ->
            in ? new JsonPrimitive(true) : null;

    /* gson: regex to string */
    JsonSerializer<Pattern> patternJsonSerializer = (in, type, context) ->
            context.serialize(in.pattern());

    /* gson: TODO why needed??? */
    JsonSerializer<ConvertCondition[]> convertConditionArrayJsonSerializer = (in, type, context) -> {
        JsonArray result = new JsonArray();
        Arrays.stream(in).forEach(c -> result.add(context.serialize(c, ConvertCondition.class)));
        return result;
    };

    /* gson: special value "Pos." + name */
    class FeatureConditionJsonSerDes implements JsonSerializer<Feature>, JsonDeserializer<Feature> {
        @Override
        public Feature deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsJsonObject().get("pos") != null)
                return newPos(json.getAsJsonObject().get("pos").getAsString());
            return new Feature.Feat() {{
                if (json.getAsJsonObject().get("elements") != null)
                    elements = context.deserialize(json.getAsJsonObject().get("elements"), String[].class);
            }};
        }

        @Override
        public JsonElement serialize(Feature src, Type typeOfSrc, JsonSerializationContext context) {
            if (src instanceof Feature.Pos) {
                JsonObject result = new JsonObject();
                result.add("pos", new JsonPrimitive("Pos." + ((Feature.Pos) src).name()));
                return result;
            } else {
                JsonObject result = new JsonObject();
                result.add("elements", context.serialize(src.elements(), String[].class));
                return result;
            }
        }
    }

    /* gson: ConvertCondition TODO really needed??? */
    class ConvertConditionJsonSerDes implements JsonSerializer<ConvertCondition>, JsonDeserializer<ConvertCondition> {

        @Override
        public ConvertCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new ConvertCondition() {{
                if (json.getAsJsonObject().get("feature") != null)
                    feature = context.deserialize(json.getAsJsonObject().get("feature"), Feature.class);
                if (json.getAsJsonObject().get("reading") != null)
                    reading = json.getAsJsonObject().get("reading").getAsString();
                if (json.getAsJsonObject().get("readingRe") != null)
                    readingRe = Pattern.compile(json.getAsJsonObject().get("readingRe").getAsString());
                if (json.getAsJsonObject().get("surface") != null)
                    surface = json.getAsJsonObject().get("surface").getAsString();
                if (json.getAsJsonObject().get("surfaceRe") != null)
                    surfaceRe = Pattern.compile(json.getAsJsonObject().get("surfaceRe").getAsString());
                if (json.getAsJsonObject().get("baseForm") != null)
                    baseForm = json.getAsJsonObject().get("baseForm").getAsString();
                if (json.getAsJsonObject().get("baseFormRe") != null)
                    baseFormRe = Pattern.compile(json.getAsJsonObject().get("baseFormRe").getAsString());
            }};
        }

        @Override
        public JsonElement serialize(ConvertCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            if (src.feature != null)
                result.add("feature", context.serialize(src.feature, Feature.class));
            if (src.reading != null)
                result.add("reading", new JsonPrimitive(src.reading));
            if (src.surface != null)
                result.add("surface", new JsonPrimitive(src.surface));
            if (src.baseForm != null)
                result.add("baseForm", new JsonPrimitive(src.baseForm));
            if (src.surfaceRe != null)
                result.add("surfaceRe", context.serialize(src.surfaceRe, Pattern.class));
            if (src.readingRe != null)
                result.add("readingRe", context.serialize(src.readingRe, Pattern.class));
            if (src.baseFormRe != null)
                result.add("baseFormRe", context.serialize(src.baseFormRe, Pattern.class));
            return result;
        }
    }

    /** json serdes */
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ConvertCondition[].class, convertConditionArrayJsonSerializer)
            .registerTypeAdapter(ConvertCondition.class, new ConvertConditionJsonSerDes())
            .registerTypeAdapter(Feature.class, new FeatureConditionJsonSerDes())
            .registerTypeAdapter(Pattern.class, patternJsonSerializer)
            .registerTypeAdapter(Boolean.class, booleanJsonSerializer)
            .create();

    /** load rule */
    default Rule getRule() {
        return gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("rule.json")), Rule.class);
    }

    /** provider name */
    String getName();

    /** runtime context */
    void setContext(YakuwarigoModifier context);

    /** factory */
    static Provider getProvider(String name) {
        ServiceLoader<Provider> providers = ServiceLoader.load(Provider.class);
        for (Provider provider : providers) {
            if (provider.getName().equals(name)) {
                return provider;
            }
        }
        throw new NoSuchElementException(name);
    }

    /** executes a method written in the json */
    default StringResult execExtraRule(String rule, Object... args) {
Debug.println(Level.FINE, "extraRule: " + rule);
        String[] parts = rule.split("#");
        String className = parts[0];
        String methodName = parts[1];
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, Integer.TYPE);
            StringResult r = (StringResult) method.invoke(this, args);
            return r;
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /** TODO more generic */
    StringResult convert(ContinuousConditionsConvertRule mc, int tokenPos);

    /** TODO more generic */
    ConversionResult convert(ConvertRule c, TokenData data, int p, String surface, boolean nounKeep);

    /** TODO more generic */
    String convert(TokenData data);
}
