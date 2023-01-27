[![GitHub Packages](https://github.com/umjammer/vavi-speech/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/umjammer?tab=packages&repo_name=vavi-speech)
[![Java CI with Maven](https://github.com/umjammer/vavi-speech/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/umjammer/vavi-speech/actions)
[![CodeQL](https://github.com/umjammer/vavi-speech/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-speech/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-8-b07219)

# vavi-speech

Text to Speech (JSAPI) for Java

| **Type** | **Description**                                                                                                                                                                                   | **Staus** | **SPI Status** | **Quality** | **Comment** |
|:---------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|:--------------:|:-----------:|:------------|
| [AquesTalk](https://www.a-quest.com/products/aquestalk.html) | JNI                                                                                                                                                                                               | ✅ |  ✅ | 😐 | 32bit only |
| AquesTalk | JNA                                                                                                                                                                                               | ✅ |  ✅ | 😐 | 32bit only |
| AquesTalk2 | JNI                                                                                                                                                                                               | - | - | | |
| AquesTalk10 | JNA                                                                                                                                                                                               | ✅ |  ✅ | 😐 | |
| Google Cloud Text To Speech | [Google Cloud Text To Speech](https://cloud.google.com/text-to-speech/docs/quickstart-client-libraries)                                                                                           | ✅ | ✅ | 👑 | |
| Rococoa | [Rococoa](https://github.com/iterate-ch/rococoa/blob/d5fdd3b884d5f044bc0b168aff66e5f52a014da8/rococoa/rococoa-contrib/src/test/java/org/rococoa/contrib/appkit/NSSpeechSynthesizerTest.java), JNA | ✅ | ✅ | 😃 | |
| FreeTTS | [FreeTTS](https://github.com/umjammer/FreeTTS/)                                                                                                                             | - | ✅ | 💩 | |

## Kanji -> Kana converter

| **Type** | **Description** | **Staus** | **Quality** | **Comment** |
|:---------|:----------------|:---------:|:-----------:|:------------|
| [Kromoji](https://github.com/atilika/kuromoji) |  | ✅ | 😐 | number, alphabet |
| [Sen](https://github.com/SenMorphologicalAnalyzer/sen) |  | ✅ | 😐 | number, alphabet |
| [Sudachi](https://github.com/WorksApplications/Sudachi) |  | ✅ | 😐 | 助詞(は,へ), number, alphabet |
| [YahooJapan Furigana](https://developer.yahoo.co.jp/webapi/jlp/furigana/v1/furigana.html) | Web | ✅ | 😐 | 助詞(は,へ) |
| [YahooJapan MA](https://developer.yahoo.co.jp/webapi/jlp/ma/v1/parse.html) | Web | - | | |
| [Goo Furigana](https://labs.goo.ne.jp/api/jp/hiragana-translation/) | Web | ✅ | 😐 | 助詞(は,へ) |
| [Rococoa](https://gist.github.com/doraTeX/3163b5aef70951ac8c541c4c77ac6293) |  | ✅ | 😃 | 数字+助数詞 |

selected by `aquestalk10.properties`

## Install

### meven

 * https://github.com/umjammer/vavi-speech/packages/1691244
 * this project uses gitlab package registry. add a personal access token to `~/.m2/settings.xml`
 * see https://docs.gitlab.com/ee/user/packages/maven_repository/index.html#authenticate-to-the-package-registry-with-maven

### AquesTalk10

 * place `AquesTalk10.framework` into `~/Library/Frameworks`
 * create symbolic link `AquesTalk10.framework/AquesTalk` as `AquesTalk10.framework/AquesTalk10`
 * write `aquesTalk10DevKey` into `local.properties`

### Google Cloud Text To Speech

 * [get token as json](https://cloud.google.com/text-to-speech/docs/quickstart-client-libraries)
 * set environment variable `"GOOGLE_APPLICATION_CREDENTIALS"` `your_json_path`

### Rococoa

 * locate `librococoa.dylib` into class path
   * if you use maven it's already done, you can find it at `target/test-classes`.
 * ~~rococoa doesn't work w/ current jna version 5.5.0, run w/ version 3.4.0~~

### Sen

```shell
$ git clone https://gitlab.com/umjammer/sen.git
$ mvn install
```
 * set `$SEN/src/main/home` as `sen.home` in `local.properties`
 * edit `aquestalk10.properties` like below

```shell
$ cat src/test/resources/aquestalk10.properties
phonemer=vavi.speech.phoneme.SenJaPhonemer
```

### Sudachi

```shell
$ cd vavi-speech
$ curl -o sudachi-dictionary-20210802-full.zip http://sudachi.s3-website-ap-northeast-1.amazonaws.com/sudachidict/sudachi-dictionary-20210802-full.zip
$ tar zxvf sudachi-dictionary-20210802-full.zip src/test/resources/sudachi/
```

* edit `aquestalk10.properties` like below

```shell
$ cat src/test/resources/aquestalk10.properties
phonemer=vavi.speech.phoneme.SudachiJaPhonemer
```

### goo to hiragana

 * register by github account
   * https://labs.goo.ne.jp/jp/apiregister/
 * set key to 'local.properties'
 * edit `aquestalk10.properties` like below

```shell
$ cat src/test/resources/aquestalk10.properties
phonemer=vavi.speech.phoneme.GooFuriganaJaPhonemer
```

### yahoo! japan

 * https://e.developer.yahoo.co.jp/dashboard/
 * set client id to 'local.properties'
 * edit `aquestalk10.properties` like below

```shell
$ cat src/test/resources/aquestalk10.properties
phonemer=vavi.speech.phoneme.YahooJapanJaPhonemer
```

### Kuromoji

 * default
 * edit `aquestalk10.properties` like below

```shell
$ cat src/test/resources/aquestalk10.properties
phonemer=vavi.speech.phoneme.KuromojiJaPhonemer
```

### a3rt

 * https://a3rt.recruit.co.jp/product/proofreadingAPI/registered/
 * set client id by email into 'local.properties'

## TODO

 * implement parameters
 * implement voices
 * [watson](https://www.ibm.com/watson/jp-ja/developercloud/text-to-speech.html)
 * https://github.com/festvox/festival
 * ~~voicevox~~ -> [vavi-speech2](https://github.com/umjammer/vavi-speech2)
 * jsapi source
 * ~~auto load aques framework (jna)~~
 * https://www.google.co.jp/ime/cgiapi.html
 * ~~text analytics + nicotalk character emotion~~ -> [vavi-speech2](https://github.com/umjammer/vavi-speech2) (wip)
 * tacotron2
 * 語尾方言変換
   * ~~[お嬢様言葉](https://github.com/jiro4989/ojosama)~~ done
     * remaining some unit tests
     * extract rules as json
   * ずんだもん

### Morphological Analyzer

 * https://mocobeta.github.io/janome/
 * https://developer.yahoo.co.jp/webapi/jlp/ma/v1/parse.html
 * https://github.com/neubig/kytea
 * https://github.com/google/sentencepiece
   * https://github.com/levyfan/sentencepiece-jni
   * https://github.com/keigohtr/apitore-sdk-java
 * https://github.com/oyahiroki/nlp4j
 * https://github.com/lucene-gosen/lucene-gosen
 * https://github.com/ikawaha/kagome

### NLP

 * https://emorynlp.github.io/nlp4j/
 * https://opennlp.apache.org/

### Alphabet -> Kana dictionary

 * https://fastapi.metacpan.org/source/MASH/Lingua-JA-Yomi-0.01/lib/Lingua/JA
 * https://github.com/KEINOS/google-ime-user-dictionary-ja-en

### Kanji -> Kana

 * chawan (lost in the internet)
 * ~~[open jtalk](https://github.com/r9y9/open_jtalk) njd*~~ -> [Gyutan](https://github.com/umjammer/Gyutan)
 * http://kakasi.namazu.org/index.html.ja
 * https://github.com/nicolas-raoul/kakasi-java (predecessor of jakaroma)
 * https://github.com/nicolas-raoul/jakaroma (engine is kuromoji)
 * ~~[mac cocoa](https://gist.github.com/doraTeX/3163b5aef70951ac8c541c4c77ac6293)~~ (done)
 * unidic

#### Romaji -> Kana

 * https://icu.unicode.org/home
 * https://github.com/andree-surya/moji4j

#### Proofreading

 * https://developer.yahoo.co.jp/webapi/jlp/kousei/v2/kousei.html
