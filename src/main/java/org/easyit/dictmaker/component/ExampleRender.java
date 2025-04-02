package org.easyit.dictmaker.component;

import java.util.List;
import org.easyit.dictmaker.WordCard;

public class ExampleRender {
    public static List<WordCard> render(final List<WordCard> subDict) {

        //        Map<String, List<WordCard>> exampleMap = subDict.stream().collect(groupingBy(WordCard::getExample));
        //
        //        Set<String> examples = exampleMap.keySet();
        //
        //        for (String example : examples) {
        //            String baseExample = getBaseExample(example, exampleMap.get(example));
        //            exampleMap.get(example).forEach(wordCard -> {
        //                String name = wordCard.getName();
        //                String newExample = baseExample.replaceFirst(toPreType(name), toTyping(name));
        //                wordCard.setExample(newExample);
        //            });
        //        }

        return subDict;
    }

    private static String getBaseExample(final String example, final List<WordCard> wordCards) {
        String result = example;
        for (final WordCard wordCard : wordCards) {
            result = result.replaceFirst(wordCard.getName(), toPreType(wordCard.getName()));
        }
        return result;
    }

    private static String toPreType(final String word) {
        return "<span class=\"example-pre-type\">" + word + "</span>";
    }

    private static String toTyping(final String word) {
        return "<span class=\"example-typing\">" + word + "</span>";
    }
}
