package com.example.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static String safeToJson(Object obj) {
        if (obj == null) return "null";
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            Throwable root = getRootCause(e);
            return obj.getClass().getSimpleName() + " (serialization failed: " + root.getClass().getSimpleName() + " - " + root.getMessage() + ")";
        }
    }

    private static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    public static void createJsonAndSave(Object object) {
        String json = safeToJson(object);

        jsonToSave(object.getClass().getSimpleName(), json);
    }

    public static <T> void createJsonAndSave(List<T> objects) {
        String json = safeToJson(objects);

        jsonToSave(objects.getFirst().getClass().getSimpleName(), json);
    }

    private static void jsonToSave(String objectSimpleName, String contentToSave) {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = ldt.format(dtf);

        try {
            // Parse and pretty-print
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(contentToSave); // Validates and parses input
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            String prettyJson = writer.writeValueAsString(jsonNode);

            // Ensure directory exists
            File directory = new File("target/log");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save to file
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("target/log/output_" + objectSimpleName + "_" + time + ".json"))) {
                fileWriter.write(prettyJson);
            }
        } catch (IOException e) {
            System.err.println("Failed to format or save JSON: " + e.getMessage());
        }
    }

    public static void jsonToPrint(Object object) {
        String json = safeToJson(object);

        jsonToPrint(json);
    }

    public static <T> void jsonToPrint(List<T> objects) {
        String json = safeToJson(objects);

        jsonToPrint(json);
    }

    private static String jsonToPrint(String contentToSave) {
        String prettyJson = "";

        try {
            // Parse and pretty-print
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(contentToSave); // Validates and parses input
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            prettyJson = writer.writeValueAsString(jsonNode);


        } catch (IOException e) {
            System.err.println("Failed to format JSON: " + e.getMessage());
        }
        return prettyJson;
    }


}
