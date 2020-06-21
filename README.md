[![](https://jitpack.io/v/umjammer/vavi-speech.svg)](https://jitpack.io/#umjammer/vavi-speech)

# vavi-speech

Text to Speech (JSAPI) for Java

| **Type** | **Description** | **Staus** | **SPI Status** | **Quality** | **Comment** |
|:---------|:----------------|:---------:|:--------------:|:-----------:|:------------|
| [AquesTalk](https://www.a-quest.com/products/aquestalk.html) | JNI | ✅ |  ✅ | 😐 | 32bit only |
| AquesTalk | JNA | ✅ |  ✅ | 😐 | 32bit only |
| AquesTalk2 | JNI | - | - | | |
| AquesTalk10 | JNA | ✅ |  ✅ | 😐 | |
| Google Cloud Text To Speech | [Google Cloud Text To Speech](https://cloud.google.com/text-to-speech/docs/quickstart-client-libraries) | ✅ | ✅ | 👑 | |
| Rococoa | [Rococoa](https://github.com/iterate-ch/rococoa/blob/d5fdd3b884d5f044bc0b168aff66e5f52a014da8/rococoa/rococoa-contrib/src/test/java/org/rococoa/contrib/appkit/NSSpeechSynthesizerTest.java), JNA | ✅ | ✅ | 😃 | |
| FreeTTS | [FreeTTS](https://freetts.sourceforge.io/) | - | ✅ | 💩 | |

## Kanji -> Kana converter

| **Type** | **Description** | **Staus** | **Quality** | **Comment** |
|:---------|:----------------|:---------:|:-----------:|:------------|
| [Kromoji](https://github.com/atilika/kuromoji) |  | ✅ | 😐 | number, alphabet |
| [Sen](https://github.com/SenMorphologicalAnalyzer/sen) |  | ✅ | 😐 | number, alphabet |
| [Sudachi](https://github.com/WorksApplications/Sudachi) |  | ✅ | 😐 | 助詞(は,へ), number, alphabet |
| [YahooJapan Furigana](https://developer.yahoo.co.jp/webapi/jlp/furigana/v1/furigana.html) | Web | ✅ | 😐 | 助詞(は,へ) |
| [YahooJapan MA](https://developer.yahoo.co.jp/webapi/jlp/ma/v1/parse.html) | Web | - | | |
| [Goo Furigana](https://labs.goo.ne.jp/api/jp/hiragana-translation/) | Web | ✅ | 😐 | 助詞(は,へ) |

## Install

### AquesTalk

 * place `AquesTalk10.framework` into `~/Library/Frameworks`
 * create symbolic link `AquesTalk10.framework/AquesTalk` as `AquesTalk10.framework/AquesTalk10`
 * write `aquesTalk10DevKey` into `local.properties`

### Google Cloud Text To Speech

 * [get token as json](https://cloud.google.com/text-to-speech/docs/quickstart-client-libraries)
 * set environment variable `"GOOGLE_APPLICATION_CREDENTIALS"` `your_json_path`

### Rococoa

 * locate `librococoa.dylib` into class path
 * rococoa doesn't work w/ current jna version 5.5.0, run w/ version 3.4.0

## TODO

 * implement parameters
 * implement voices
 * [watson](https://www.ibm.com/watson/jp-ja/developercloud/text-to-speech.html)
 * https://github.com/festvox/festival
 * jsapi source

 * ~~auto load aques framework (jna)~~

 * https://www.google.co.jp/ime/cgiapi.html

### Morphological Analyzer

 * https://mocobeta.github.io/janome/
 * https://developer.yahoo.co.jp/webapi/jlp/ma/v1/parse.html
 * https://github.com/neubig/kytea
 * https://github.com/google/sentencepiece
 * https://github.com/oyahiroki/nlp4j
 * https://github.com/lucene-gosen/lucene-gosen

### NLP

 * https://emorynlp.github.io/nlp4j/
 * https://opennlp.apache.org/

### Alphabet -> Kana dictionary

 * https://fastapi.metacpan.org/source/MASH/Lingua-JA-Yomi-0.01/lib/Lingua/JA
 * https://github.com/KEINOS/google-ime-user-dictionary-ja-en

### Kanji -> Kana

 * chawan (lost in the internet)
 * open jtalk njd*
