package com.lionking.ddingchun.crawler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lionking.ddingchun.crawler.domain.Notice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GeminiTaggingClient {

    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com";

    private static final List<String> ALLOWED_CATEGORIES =
            List.of(
                    "NOTICE",
                    "EVENT",
                    "CONTEST",
                    "SCHOLARSHIP",
                    "CLUB",
                    "FESTIVAL",
                    "EXHIBITION",
                    "CAREER"
            );

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiTaggingClient(
            ObjectMapper objectMapper,
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-3.1-flash-lite}")
            String model
    ) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;

        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    /**
     * 여러 공지를 한 번의 Gemini 요청으로 분석한다.
     */
    public Map<Long, TaggingResult> tagNotices(
            List<Notice> notices
    ) {
        validateConfiguration();

        if (notices == null || notices.isEmpty()) {
            return Map.of();
        }

        String prompt = buildPrompt(notices);
        Map<String, Object> requestBody =
                buildRequestBody(prompt);

        try {
            JsonNode response = restClient.post()
                    .uri(
                            "/v1beta/models/{model}:generateContent",
                            model
                    )
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            GeminiBatchResponse parsedResponse =
                    parseResponse(response);

            if (parsedResponse.results() == null) {
                return Map.of();
            }

            return parsedResponse.results()
                    .stream()
                    .filter(result ->
                            result.noticeId() != null
                    )
                    .collect(
                            Collectors.toMap(
                                    TaggingResult::noticeId,
                                    Function.identity(),
                                    (first, second) -> second,
                                    LinkedHashMap::new
                            )
                    );

        } catch (RestClientResponseException exception) {
            String responseBody =
                    shorten(exception.getResponseBodyAsString(), 500);

            throw new IllegalStateException(
                    "Gemini API 호출에 실패했습니다. "
                            + "HTTP "
                            + exception.getStatusCode().value()
                            + ": "
                            + responseBody,
                    exception
            );

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Gemini 응답 JSON을 읽지 못했습니다.",
                    exception
            );
        }
    }

    private void validateConfiguration() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GEMINI_API_KEY가 설정되지 않았습니다."
            );
        }

        if (model == null || model.isBlank()) {
            throw new IllegalStateException(
                    "GEMINI_MODEL이 설정되지 않았습니다."
            );
        }
    }

    private String buildPrompt(
            List<Notice> notices
    ) {
        List<Map<String, Object>> noticeInputs =
                new ArrayList<>();

        for (Notice notice : notices) {
            Map<String, Object> input =
                    new LinkedHashMap<>();

            input.put("noticeId", notice.getId());
            input.put("source", safe(notice.getSource()));
            input.put("title", safe(notice.getTitle()));
            input.put("summary", safe(notice.getSummary()));
            input.put(
                    "existingCategory",
                    safe(notice.getCategory())
            );
            input.put(
                    "campus",
                    safe(notice.getCampus())
            );

            noticeInputs.add(input);
        }

        String noticeJson;

        try {
            noticeJson =
                    objectMapper.writeValueAsString(
                            noticeInputs
                    );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "공지 데이터를 JSON으로 변환하지 못했습니다.",
                    exception
            );
        }

        return """
                당신은 명지대학교 학교 공지를 분류하는 AI입니다.

                각 공지를 읽고 다음 규칙에 따라 분류하세요.

                category는 반드시 아래 값 중 하나만 선택하세요.

                NOTICE: 일반 공지
                EVENT: 교내외 행사, 특강, 세미나, 설명회
                CONTEST: 공모전, 경진대회, 아이디어 대회
                SCHOLARSHIP: 장학금, 학자금, 장학생 선발
                CLUB: 동아리, 학회, 학생회 모집
                FESTIVAL: 축제
                EXHIBITION: 전시, 공연
                CAREER: 취업, 채용, 인턴, 현장실습, 진로

                tags 규칙:
                - 공지의 핵심 관심사를 한국어로 작성하세요.
                - 1개 이상 5개 이하로 작성하세요.
                - 너무 일반적인 단어는 제외하세요.
                - 중복 태그는 만들지 마세요.
                - 예: 공모전, AI, 창업, 장학금, 인턴, 봉사

                각 입력 noticeId를 변경하지 말고 그대로 반환하세요.

                공지 데이터:
                """
                + noticeJson;
    }

    private Map<String, Object> buildRequestBody(
        String prompt
) {
    Map<String, Object> resultProperties =
            new LinkedHashMap<>();

    resultProperties.put(
            "noticeId",
            Map.of(
                    "type", "INTEGER",
                    "description", "입력으로 받은 공지 ID"
            )
    );

    resultProperties.put(
            "category",
            Map.of(
                    "type", "STRING",
                    "enum", ALLOWED_CATEGORIES,
                    "description", "학교 공지 카테고리"
            )
    );

    resultProperties.put(
            "tags",
            Map.of(
                    "type", "ARRAY",
                    "items",
                    Map.of(
                            "type", "STRING"
                    ),
                    "description", "공지의 핵심 관심 태그"
            )
    );

    Map<String, Object> singleResultSchema =
            new LinkedHashMap<>();

    singleResultSchema.put("type", "OBJECT");
    singleResultSchema.put(
            "properties",
            resultProperties
    );
    singleResultSchema.put(
            "required",
            List.of(
                    "noticeId",
                    "category",
                    "tags"
            )
    );

    Map<String, Object> responseSchema =
            new LinkedHashMap<>();

    responseSchema.put("type", "OBJECT");
    responseSchema.put(
            "properties",
            Map.of(
                    "results",
                    Map.of(
                            "type", "ARRAY",
                            "items", singleResultSchema
                    )
            )
    );
    responseSchema.put(
            "required",
            List.of("results")
    );

    Map<String, Object> generationConfig =
            new LinkedHashMap<>();

    generationConfig.put(
            "temperature",
            0.1
    );

    /*
     * generateContent API의 구조화 JSON 출력 설정
     */
    generationConfig.put(
            "responseMimeType",
            "application/json"
    );

    generationConfig.put(
            "responseSchema",
            responseSchema
    );

    return Map.of(
            "contents",
            List.of(
                    Map.of(
                            "role", "user",
                            "parts",
                            List.of(
                                    Map.of(
                                            "text",
                                            prompt
                                    )
                            )
                    )
            ),
            "generationConfig",
            generationConfig
    );
}

    private GeminiBatchResponse parseResponse(
            JsonNode response
    ) throws JsonProcessingException {

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini 응답이 비어 있습니다."
            );
        }

        JsonNode textNode = response
                .path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text");

        if (textNode.isMissingNode()
                || textNode.asText().isBlank()) {

            String blockReason = response
                    .path("promptFeedback")
                    .path("blockReason")
                    .asText("");

            throw new IllegalStateException(
                    blockReason.isBlank()
                            ? "Gemini 응답에서 결과를 찾지 못했습니다."
                            : "Gemini 요청이 차단되었습니다: "
                            + blockReason
            );
        }

        return objectMapper.readValue(
                textNode.asText(),
                GeminiBatchResponse.class
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String shorten(
            String value,
            int maxLength
    ) {
        if (value == null) {
            return "";
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }

    private record GeminiBatchResponse(
            List<TaggingResult> results
    ) {
    }

    public record TaggingResult(
            Long noticeId,
            String category,
            List<String> tags
    ) {
    }
}