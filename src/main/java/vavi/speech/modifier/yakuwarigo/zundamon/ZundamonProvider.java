/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo.zundamon;

import vavi.speech.modifier.yakuwarigo.Provider;
import vavi.speech.modifier.yakuwarigo.TokenData;
import vavi.speech.modifier.yakuwarigo.YakuwarigoModifier;

import static vavi.speech.modifier.yakuwarigo.Rule.ContinuousConditionsConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.ConvertRule;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.ConversionResult;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.StringResult;


/**
 * ZundamonProvider.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-04-23 nsano initial version <br>
 */
public class ZundamonProvider implements Provider {

    YakuwarigoModifier context;

    @Override
    public String getName() {
        return "zundamon";
    }

    @Override
    public void setContext(YakuwarigoModifier context) {
        this.context = context;
    }

    @Override
    public StringResult convert(ContinuousConditionsConvertRule mc, int tokenPos) {
        int p = tokenPos + mc.conditions.length - 1;
        String result = mc.value;

        TokenData data = new TokenData(context.tokens[tokenPos]);
        String surface = data.surface;
        result = result.replaceAll("@1", surface);

        return new StringResult(result, p);
    }

    @Override
    public ConversionResult convert(ConvertRule c, TokenData data, int p, String surface, boolean nounKeep) {
        return new ConversionResult(surface, false, p, null);
    }

    @Override
    public String convert(TokenData data) {
        return data.surface;
    }
}
