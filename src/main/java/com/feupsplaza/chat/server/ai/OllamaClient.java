package com.feupsplaza.chat.server.ai;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OllamaClient implements LLMClient {

    public String ask(String prompt) {
        try {
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();

            conection.setRequestMethod("POST");
            conection.setRequestProperty("Content-Type", "application/json");
            conection.setDoOutput(true);

            String json = """
                    {
                      "model": "llama3:latest",
                      "prompt": "%s",
                      "stream": false
                    }
                    """.formatted(escape(prompt)); // avoid breaking

            try (OutputStream os = conection.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            // check return 
            if (conection.getResponseCode() != 200) {
                return "AI error: Ollama returned status " + conection.getResponseCode();
            }

            String response;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conection.getInputStream(), StandardCharsets.UTF_8))) {
                response = br.readLine();
            }

            return extractResponse(response);

        } catch (Exception e) {
            return "AI error: could not contact Ollama";
        }
    }

    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");

    }

    private String extractResponse(String json) {
        String key = "\"response\":\"";
        int start = json.indexOf(key);

        if (start == -1) {
            return "AI error: invalid response";
        }

        start += key.length();
        int end = json.indexOf("\",", start);

        if (end == -1) {
            return "AI error: invalid response";
        }

        return json.substring(start, end).replace("\\n", " ").replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("|", "/")
                .trim();
    }
}