package com.sf.honeymorning.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiredArgsConstructor
@Component
@Slf4j
public class TtsUtil {

	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
	// 세팅
	private static final String ELEVEN_LABS_BASE_URL = "https://api.elevenlabs.io/v1";
	// 커스텀
	private final String voiceId = "XrExE9yKIg1WjnnlVkGX";
	private final String modelId = "eleven_turbo_v2_5";
	private final double stability = 0.5;
	private final double similarityBoost = 0.5;
	private final double style = 0.5;

	@Value("${file.directory.path.content}")
	private String contentPath;


	@Value("${file.directory.path.summary}")
	private String summaryPath;

	@Value("${file.directory.path.quiz}")
	private String quizPath;

	@Value("${api.key.elevenlabs}")
	private String elevenlabsKey;

	/**
	 * TTS API를 활용하여 주어진 텍스트를 음성 파일로 변환하고 저장합니다. 음성 파일 경로를 받은 뒤, 파일 경로를 각 DB에 저장해야 합니다.
	 *
	 * @param text     음성으로 변화할 텍스트입니다.
	 * @param fileType TTS로 만들 타입입니다. 파일 경로가 달라집니다. (ex. "summaryText", "content", "quiz").
	 * @return 음성 파일 경로를 반환합니다.
	 * @throws IOException 파일 저장 시 발생한 에러를 반환합니다.
	 */
	public String textToSpeech(String text, String fileType) throws IOException {
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
			.url(ELEVEN_LABS_BASE_URL + "/text-to-speech/" + voiceId)
			.addHeader("xi-api-key", elevenlabsKey)
			.addHeader("Content-Type", "application/json")
			.addHeader("optimize_streaming_latency", "0")
			.addHeader("output_format", "mp3_22050_32")
			.post(body)
			.build();

		try (Response response = HTTP_CLIENT.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("TTS API 요청의 예상치 못한 코드입니다: " + response);
			}

			return saveTts(response.body().bytes(), getFileDirectoryPath(fileType));
		}
	}

	// TTS 파일 저장
	private String saveTts(byte[] audioData, String fileDirectoryPath)
		throws IOException {
		String fileName = UUID.randomUUID().toString() + ".mp3";
		Path filePath = Paths.get(fileDirectoryPath, fileName);

		try {
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, audioData);
			log.info("디렉토리 저장이 완료되었습니다: " + filePath);
			return fileName;
		} catch (IOException e) {
			log.error("디렉토리 관련 파일 저장을 실패했습니다: " + e.getMessage(), e);
			throw new IOException("디렉토리 관련 파일 저장을 실패했습니다", e);
		}
	}

	// 파일 타입에 따른 디렉토리 경로 반환
	private String getFileDirectoryPath(String fileType) {
		switch (fileType.toLowerCase()) {
			case "content":
				return contentPath;
			case "summaryText":
				return summaryPath;
			case "quiz":
				return quizPath;
			default:
				throw new IllegalArgumentException("Invalid file type: " + fileType);
		}
	}
}