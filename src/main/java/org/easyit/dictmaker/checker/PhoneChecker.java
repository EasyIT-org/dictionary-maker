package org.easyit.dictmaker.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.easyit.dictmaker.WordCard;
import org.easyit.dictmaker.component.LocalDictCreator;

public class PhoneChecker {

    public static void main(String[] args) {
        Map<String, Map<String, WordCard>> unMerged = LocalDictCreator.createUnMerged();

        // 创建一个大的dict
        Map<String, List<WordCard>> bigDict = new ConcurrentHashMap<>();
        for (Map.Entry<String, Map<String, WordCard>> dictEntry : unMerged.entrySet()) {
            for (final Map.Entry<String, WordCard> entry : dictEntry.getValue().entrySet()) {
                List<WordCard> wordCards = bigDict.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
                entry.getValue().setDict(dictEntry.getKey());
                wordCards.add(entry.getValue());
            }
        }

        for (final Map.Entry<String, List<WordCard>> wordEntry : bigDict.entrySet()) {
            List<WordCard> cards = wordEntry.getValue();
            if (cards.size() != 1) {
                long ukCount = cards.stream().map(WordCard::getUkphone)
                                    .filter(s -> s != null && !s.isEmpty())
                                    .distinct().count();
                long usCount = cards.stream().map(WordCard::getUsphone)
                                    .filter(s -> s != null && !s.isEmpty())
                                    .distinct().count();
                if (ukCount > 1 || usCount > 1) {

                    List<String> list = cards.stream().map(WordCard::getUkphone)
                                             .filter(s -> s != null && !s.isEmpty())
                                             .distinct().toList();
                    List<String> list1 = cards.stream().map(WordCard::getUsphone)
                                              .filter(s -> s != null && !s.isEmpty())
                                              .distinct().toList();

                    System.out.println(wordEntry.getKey() + " UK" + list + " " + "--------------------------------");
                    System.out.println(wordEntry.getKey() + " US" + list1 + " " + "--------------------------------");
//                    cards.forEach(System.out::println);
                }
            }
        }
    }

}
