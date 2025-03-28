package org.easyit.dictmaker.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easyit.dictmaker.WordCard;

public class WordChanger {

    private static final Map<String, String> IRREGULAR_VERBS = new HashMap<>();

    static {
        // 动词变形映射（过去式/过去分词 -> 原型）
        // A-C
        putEntries(
            "awoke", "awake",
            "was", "be",
            "were", "be",
            "been", "be",
            "became", "become",
            "began", "begin",
            "bent", "bend",
            "bit", "bite",
            "bled", "bleed",
            "blew", "blow",
            "broke", "break",
            "brought", "bring",
            "built", "build",
            "bought", "buy",
            "caught", "catch",
            "chose", "choose",
            "came", "come",
            "cost", "cost",
            "cut", "cut"
        );

        // D-F
        putEntries(
            "did", "do",
            "drew", "draw",
            "drank", "drink",
            "drove", "drive",
            "ate", "eat",
            "fell", "fall",
            "fed", "feed",
            "felt", "feel",
            "fought", "fight",
            "found", "find",
            "flew", "fly",
            "forgot", "forget"
        );

        // G-R
        putEntries(
            "got", "get",
            "gave", "give",
            "went", "go",
            "grew", "grow",
            "hung", "hang",
            "had", "have",
            "heard", "hear",
            "hid", "hide",
            "hit", "hit",
            "held", "hold",
            "hurt", "hurt",
            "kept", "keep",
            "knew", "know",
            "laid", "lay",
            "led", "lead",
            "left", "leave",
            "lent", "lend",
            "let", "let",
            "lost", "lose",
            "made", "make",
            "meant", "mean",
            "met", "meet",
            "paid", "pay",
            "put", "put",
            "read", "read",
            "rode", "ride",
            "rang", "ring",
            "rose", "rise",
            "ran", "run"
        );

        // S-W
        putEntries(
            "said", "say",
            "saw", "see",
            "sold", "sell",
            "sent", "send",
            "shook", "shake",
            "shot", "shoot",
            "showed", "show",
            "shut", "shut",
            "sang", "sing",
            "sank", "sink",
            "sat", "sit",
            "slept", "sleep",
            "spoke", "speak",
            "spent", "spend",
            "stood", "stand",
            "stole", "steal",
            "stuck", "stick",
            "struck", "strike",
            "swore", "swear",
            "swam", "swim",
            "took", "take",
            "taught", "teach",
            "tore", "tear",
            "told", "tell",
            "thought", "think",
            "threw", "throw",
            "understood", "understand",
            "woke", "wake",
            "wore", "wear",
            "won", "win",
            "wrote", "write",
            "forgiven", "forgive"
        );

        // 处理过去分词（如加-en的情况）
        IRREGULAR_VERBS.put("spoken", "speak");
        IRREGULAR_VERBS.put("written", "write");
        IRREGULAR_VERBS.put("driven", "drive");
        IRREGULAR_VERBS.put("better", "good");
        IRREGULAR_VERBS.put("worse", "bad");
        IRREGULAR_VERBS.put("elder", "old");

        putEntries(
            "children", "child",
            "teeth", "tooth",
            "mice", "mouse",
            "geese", "goose"
        );

    }

    public static WordCard tryToFind(final String word,
                                     final Map<String, WordCard> dict) {

        String lowerCase = word.toLowerCase();
        if (dict.containsKey(lowerCase)) {
            return dict.get(lowerCase);
        }

        // 新增：处理不规则动词（必须放在常规规则之后）
        if (IRREGULAR_VERBS.containsKey(lowerCase)) {
            String base = IRREGULAR_VERBS.get(lowerCase);
            return dict.getOrDefault(base, null);
        }

        // 处理后缀
        List<String> suffixes = Arrays.asList("ed", "ing", "ly", "s", "es", "ies", "er", "est", "ful", "ings");

        for (final String suffix : suffixes) {
            if (lowerCase.endsWith(suffix)) {
                String base = lowerCase.substring(0, lowerCase.length() - suffix.length());
                if (dict.containsKey(base)) {
                    return dict.get(base);
                }

                base = base + "e";
                if (dict.containsKey(base)) {
                    return dict.get(base);
                }

                int endIndex = lowerCase.length() - suffix.length() - 1;
                if (endIndex > 2) {
                    base = lowerCase.substring(0, endIndex);
                    if (dict.containsKey(base)) {
                        return dict.get(base);
                    }
                }
            }
        }

        suffixes = Arrays.asList("ies", "ing", "ier", "iest");
        for (final String suffix : suffixes) {
            if (lowerCase.startsWith(suffix)) {
                String base = lowerCase.substring(0, lowerCase.length() - suffix.length()) + "y";
                if (dict.containsKey(base)) {
                    return dict.get(base);
                }
            }
        }

        //处理前缀
        List<String> prefixes = Arrays.asList("un", "re", "im");

        for (final String prefix : prefixes) {
            if (lowerCase.startsWith(prefix)) {
                String base = lowerCase.substring(prefix.length());
                if (dict.containsKey(base)) {
                    return dict.get(base);
                }
            }
        }

        return null;
    }

    // 辅助方法简化批量插入
    private static void putEntries(String... pairs) {
        for (int i = 0; i < pairs.length; i += 2) {
            IRREGULAR_VERBS.put(pairs[i], pairs[i + 1]);
        }
    }

}
