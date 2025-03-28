package org.easyit.dictmaker.component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.easyit.dictmaker.WordCard;

import static org.easyit.dictmaker.component.QwertyDictCreator.mergeDict;
import static org.easyit.dictmaker.component.QwertyDictCreator.processFolder;

public class LocalDictCreator {

    public static Map<String, WordCard> create() {

        try {
            final String dictDir = "src/main/resources/dicts/";

            List<String> targetDictPath = Files.walk(Paths.get(dictDir))
                                               .filter(Files::isRegularFile)    // 过滤掉目录
                                               .map(path -> path.getFileName().toString()) // 提取文件名
                                               .collect(Collectors.toList());

            Map<String, Map<String, WordCard>> enFileWord = processFolder(dictDir, targetDictPath);
            Map<String, WordCard> bigDict = mergeDict(enFileWord);
            return bigDict;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        Map<String, WordCard> stringWordCardMap = LocalDictCreator.create();
        System.out.println(stringWordCardMap.size());
    }
}
