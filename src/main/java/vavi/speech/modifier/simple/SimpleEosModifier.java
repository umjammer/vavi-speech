/*
 * https://github.com/kssfilo/desumasu-converter/blob/master/desumasu-converter.coffee
 */

package vavi.speech.modifier.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import vavi.util.Debug;


/**
 * SimpleEosModifier.
 *
 * @author kssfilo@gmail.com
 */
public class SimpleEosModifier {

    /** */
    private List<String[]> dict = new ArrayList<>();

    /** */
    private static final String defaultModel = "normal";

    /** */
    public SimpleEosModifier() throws Exception {
        this(defaultModel);
    }

    /** */
    public SimpleEosModifier(String model) throws Exception {
        if (model == null) {
            throw new NullPointerException("model");
        }
Debug.println(Level.FINE, "model: " + model);
        Scanner s = new Scanner(SimpleEosModifier.class.getResourceAsStream("/modifier/" + model + ".txt"));
        while (s.hasNextLine()) {
            String l = s.nextLine();
            dict.add(l.split(","));
        }
        s.close();
    }

    private static final String seperator = "。、（）\\(\\)";

    boolean toJotai = true;
    boolean checkNe = true;
    boolean removeNe = false;

    public String convert2joutai(String input) {
        toJotai = true;
        return convert(input);
    }

    public String convert2keitai(String input) {
        toJotai = false;
        return convert(input);
    }

    private static boolean isX(String x) {
        return x.equals("ました") || x.equals("ります");
    }

    private String convert(String input) {
        int l = 0;
        int r = 1;
        if (!toJotai) {
            r = 0;
            l = 1;
        }
        String t = input;

        List<String[]> tempDict = new ArrayList<>(dict);
        tempDict.sort((a, b) -> toJotai ? b[0].length() - a[0].length() : b[1].length() - a[1].length());

        for (String[] i : tempDict) {
            if (!toJotai && isX(i[0])) continue;
            String left = String.format("(%s)([%s])", i[l], seperator);
            String right = String.format("%s$2", i[r]);
            t = t.replaceAll(left, right);
            if (checkNe) {
                String neLeft = String.format("(%sね)([%s])", i[l], seperator);
                String rightRep = removeNe ? "" : "ね";
                String neRight = String.format("%s%s$2", i[r], rightRep);
                t = t.replaceAll(neLeft, neRight);
            }
        }
        String output = t;
        return output;
    }
}