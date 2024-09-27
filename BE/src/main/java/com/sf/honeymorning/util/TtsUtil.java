package com.sf.honeymorning.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class TtsUtil {

    // TODO: 각 서비스마다 String을 받아서 file_path를 저장하기

    private final OkHttpClient client = new OkHttpClient();

    // 경로
    private static final String FILE_DIRECTORY_PATH_SUMMARY = "C:\\SSAFY\\HoneyMorning\\project_data\\summary";
    private static final String FILE_DIRECTORY_PATH_CONTENT = "C:\\SSAFY\\HoneyMorning\\project_data\\content";
    private static final String FILE_DIRECTORY_PATH_QUIZ = "C:\\SSAFY\\HoneyMorning\\project_data\\quiz";

    // 세팅
    private static final String API_KEY = "sk_bb66d759209977f666fb4d27eef34e0dca2e003415380b4f";
    private static final String ELEVENLABS_BASE_URL = "https://api.elevenlabs.io/v1";

    // 커스텀
    private final String voiceId = "XrExE9yKIg1WjnnlVkGX";
    private final String modelId = "eleven_turbo_v2_5";
    private final double stability = 0.5;
    private final double similarityBoost = 0.5;
    private final double style = 0.5;

    // Text -> Speech
    // fileType: summary, content, quiz
    public String textToSpeech(String text, Long briefId, String fileType) throws IOException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("text", text);
        jsonBody.put("model_id", modelId);
        jsonBody.put("voice_settings", new JSONObject()
                .put("stability", stability)
                .put("similarity_boost", similarityBoost)
                .put("style", style));

        RequestBody body = RequestBody.create(jsonBody.toString(),
                MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(ELEVENLABS_BASE_URL + "/text-to-speech/" + voiceId)
                .addHeader("xi-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("optimize_streaming_latency", "0")
                .addHeader("output_format", "mp3_22050_32")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return saveTts(response.body().bytes(), briefId, getFileDirectoryPath(fileType));
        }
    }

    // TTS 파일 저장
    private String saveTts(byte[] audioData, Long briefId, String fileDirectoryPath)
            throws IOException {
        String fileName = briefId + "_" + UUID.randomUUID().toString() + ".mp3";
        Path filePath = Paths.get(fileDirectoryPath, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, audioData);

        return fileName; // 반환 내용
    }

    // 파일 타입에 따른 디렉토리 경로 반환
    private String getFileDirectoryPath(String fileType) {
        switch (fileType.toLowerCase()) {
            case "summary":
                return FILE_DIRECTORY_PATH_SUMMARY;
            case "content":
                return FILE_DIRECTORY_PATH_CONTENT;
            case "quiz":
                return FILE_DIRECTORY_PATH_QUIZ;
            default:
                throw new IllegalArgumentException("Invalid file type: " + fileType);
        }
    }
}