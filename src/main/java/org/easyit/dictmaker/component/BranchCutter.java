package org.easyit.dictmaker.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.easyit.dictmaker.WordCard;

public class BranchCutter {

    public BranchCutter() {
    }

    public static List<WordCard> cut(List<WordCard> origin) {
        Map<String, List<WordCard>> nameMap = origin.stream().collect(Collectors.groupingBy(WordCard::getName));

        List<WordCards> cardsList = new ArrayList<>();
        Map<String, Integer> nameCountMap = new HashMap<>();
        for (final String s : nameMap.keySet()) {
            int count = nameMap.get(s).size();
            WordCards wordCards = new WordCards(s, nameMap.get(s), count);
            cardsList.add(wordCards);
            nameCountMap.put(s, count);
        }
        cardsList.sort(Comparator.comparingInt(WordCards::getCount).reversed());

        // 计算下阈值
        int size = cardsList.size();
        int neverThreshold1 = size / 100;
        int seldomThreshold1 = neverThreshold1 * 5;

        Set<String> never = new HashSet<>();
        Set<String> seldom = new HashSet<>();
        Set<String> common = new HashSet<>();

        for (int i = 0; i < size; i++) {
            if (i < neverThreshold1) {
                never.add(cardsList.get(i).getName());
            } else {
                if (i < seldomThreshold1) {
                    seldom.add(cardsList.get(i).getName());
                } else {
                    common.add(cardsList.get(i).getName());
                }
            }
        }

        Map<String, List<WordCard>> exampleMap = origin.stream().collect(Collectors.groupingBy(WordCard::getExample));

        // 开始剪枝
        // 1. 保证每个句子至少一个词
        for (final String s : exampleMap.keySet()) {
            List<WordCard> wordCards = exampleMap.get(s);
            WordCard wordCard = wordCards.stream()
                                         .min(Comparator.comparingInt(wc -> nameCountMap.get(wc.getName())))
                                         .get();
            wordCard.setSkip(false);
        }

        // 2. 保证每个词至少一遍
        for (final String s : nameMap.keySet()) {
            List<WordCard> wordCards = nameMap.get(s);
            wordCards.getFirst().setSkip(false);
        }

        return origin.stream().filter(wc -> !wc.isSkip()).toList();
    }

    public static class WordCards {
        private final String name;
        private final List<WordCard> wordCards;
        private final int count;

        public WordCards(final String name, final List<WordCard> wordCards, final int count) {
            this.name = name;
            this.wordCards = wordCards;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public List<WordCard> getWordCards() {
            return wordCards;
        }

        public int getCount() {
            return count;
        }
    }
}
