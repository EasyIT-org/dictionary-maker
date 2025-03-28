package org.easyit.dictmaker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.easyit.dictmaker.component.BranchCutter;
import org.easyit.dictmaker.component.DictCreator;
import org.easyit.dictmaker.component.ExampleRender;
import org.easyit.dictmaker.component.FileWriter;
import org.easyit.dictmaker.component.WordChanger;

public class Maker {

    public static void main(String[] args) {

        String qwertyHome = System.getProperty("user.home") + "/git/open/qwerty-learner/";
        //        String outputName = "A Tale of Two Cities.json";
        String textPath = System.getProperty(
            "user.home") + "/git/open/dict-maker/src/main/resources/novels/A Tale of Two Cities.txt";

        makeDict(qwertyHome, textPath);
    }

    private static List<WordCard> txt2dict(final String resourceLocation,
                                           final Map<String, WordCard> dict) {
        try {
            String content = Files.readString(Path.of(resourceLocation));

            content = content.replaceAll("\\n", " ");
            content = content.replaceAll("\\r", " ");
            String regex = "(?<=[\\.\\?\\!])";

            List<String> sentences = Arrays.stream(content.split(regex))
                                           .filter(s -> !s.isEmpty())
                                           .map(String::trim)
                                           .map(str -> str.replaceAll("\\s+", " "))
                                           .toList();

            List<ArrayList<WordCard>> ll = sentences.stream().map(sentence -> {
                List<String> words = Arrays.stream(sentence.replace("'s", "").split("[^a-zA-Z]"))
                                           .filter(s -> !s.isEmpty())
                                           //                                            .map(String::toLowerCase)
                                           .toList();
                ArrayList<WordCard> wordCards = new ArrayList<>();
                for (final String word : words) {
                    if (word.length() > 2) {
                        WordCard wordCard = WordChanger.tryToFind(word, dict);
                        if (wordCard != null) {
                            WordCard clone = wordCard.clone();
                            clone.setName(word);
                            clone.setExample(sentence);
                            wordCards.add(clone);
                        } else {
                            WordCard e = new WordCard(word);
                            e.setExample(sentence);
                            wordCards.add(e);
                        }
                    }
                }
                return wordCards;
            }).toList();

            List<WordCard> result = new ArrayList<>();
            for (final ArrayList<WordCard> entries : ll) {
                result.addAll(entries);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String makeDict(final String qwertyHome, String textPath) {
        int lastIndex = textPath.lastIndexOf(".");
        String outputName = textPath.substring(textPath.lastIndexOf("/") + 1, lastIndex) + ".json";
        // 构造一个字典全集
        Map<String, WordCard> dict = DictCreator.create(qwertyHome);
        // 将文本转换为字典子集
        List<WordCard> allDict = txt2dict(textPath, dict);
        // 过滤剪枝
        List<WordCard> subDict = BranchCutter.cut(allDict);
        // 渲染 Example
        List<WordCard> renderedDict = ExampleRender.render(subDict);
        // 写入文本
        String outputDir = qwertyHome + "/public/dicts/";
        FileWriter.write2file(outputDir, outputName, renderedDict);
        return outputDir + outputName + "(字数:" + renderedDict.size() + " )";
    }

}
