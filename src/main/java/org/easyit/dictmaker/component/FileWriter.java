package org.easyit.dictmaker.component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.easyit.dictmaker.WordCard;

public class FileWriter {

    public static void write2file(final String outputDir, final String outputName,
                                  final List<WordCard> result) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
            new FileOutputStream(outputDir + outputName), StandardCharsets.UTF_8)) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.configOverride(Map.class).setInclude(
                JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL));

            objectMapper = objectMapper.enable(SerializationFeature.INDENT_OUTPUT)  // 格式化 JSON
                                       .disable(SerializationFeature.WRITE_NULL_MAP_VALUES);  // 跳过 null
            objectMapper.writeValue(writer, result);

            System.out.println("Write count: " + result.size() + " to " + outputDir + outputName + " success");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
