package org.easyit.dictmaker.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.easyit.dictmaker.WordCard;

public class DictCreator {

    public static Map<String, WordCard> create(final String qwertyHome) {

        final String indexFilePath = qwertyHome + "/src/resources/dictionary.ts";
        final String publicHome = qwertyHome + "/public/";
        Map<String, List<Entry>> category = extractInfoFromFile(indexFilePath);
        List<String> targetDictPath = category.get("en").stream().map(Entry::getUrl).toList();
        Map<String, Map<String, WordCard>> enFileWord = processFolder(publicHome, targetDictPath);
        Map<String, WordCard> bigDict = mergeDict(enFileWord);
        return bigDict;
    }

    public static Map<String, Map<String, WordCard>> processFolder(String publicHome, List<String> folderPath) {
        Map<String, Map<String, WordCard>> resultMap = new HashMap<>();

        File[] jsonFiles = folderPath.stream().map(path -> publicHome + "/" + path).map(File::new).toArray(File[]::new);

        ObjectMapper mapper = new ObjectMapper();
        for (File file : jsonFiles) {
            try {
                List<WordCard> entries = mapper.readValue(
                    file, new TypeReference<List<WordCard>>() {
                    }
                );
                resultMap.put(
                    file.getName(), entries.stream().collect(Collectors.toMap(
                        WordCard::getName, Function.identity(), (existing, replacement) -> {
                            System.out.println("Duplicate key found: " + file.getName());
                            System.out.println("Existing entry: " + existing);
                            System.out.println("Replacement entry: " + replacement);
                            return existing;
                        }
                    ))
                );
            } catch (Exception e) {
                String errorMsg = "Error reading file: " + file.getName();
                throw new RuntimeException(errorMsg, e);
            }
        }
        return resultMap;
    }

    private static Map<String, WordCard> mergeDict(final Map<String, Map<String, WordCard>> enFileWord) {
        Map<String, WordCard> result = new HashMap<>();

        enFileWord.entrySet().stream().forEach(entry -> {
            result.putAll(entry.getValue());
        });

        return result;
    }

    public static Map<String, List<Entry>> extractInfoFromFile(String filePath) {
        try {
            String content = Files.readString(Path.of(filePath));
            List<String> categoryLines = filterLines(content, "languageCategory");
            List<String> urlLines = filterLines(content, "url");

            if (categoryLines.size() != urlLines.size()) {
                throw new Exception("categoryLines.size()!=urlLines.size()");
            }

            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < categoryLines.size(); i++) {
                String url = extraQuotes(urlLines.get(i));
                String category = extraQuotes(categoryLines.get(i));
                entries.add(new Entry(url, category));
            }
            return entries.stream().collect(Collectors.groupingBy(Entry::getCategory));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String extraQuotes(String urlLine) {
        try {
            int start = urlLine.indexOf("'");
            int end = urlLine.lastIndexOf("'");
            return urlLine.substring(start + 1, end);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> filterLines(String content, String pattern) {
        return Arrays.stream(content.split("\n"))  // 按行分割
                     .filter(line -> line.contains(pattern) && !line.contains("//")).collect(Collectors.toList());
    }

    public static class Entry {
        private final String url;
        private final String category;

        public Entry(final String url, final String category) {
            this.url = url;
            this.category = category;
        }

        public String getUrl() {
            return url;
        }

        public String getCategory() {
            return category;
        }
    }

}
