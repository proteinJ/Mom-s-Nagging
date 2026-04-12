package com.mom.nagging.infra.ai.dto;

import java.util.List;

/**
 * Gemini API 통신 전용 DTO
 */
public class GeminiDto {

    // --- 1. Request 구조 (API 호출용) ---
    public record Request(
            List<Content> contents
    ) {
        // 단일 텍스트 프롬프트를 쉽게 생성하기 위한 생성자 위임
        public Request(String prompt) {
            this(List.of(new Content(List.of(new Part(prompt)))));
        }
    }

    // --- 2. Response 구조 (API 응답 파싱용) ---
    public record Response(
            List<Candidate> candidates
    ) {
        // 응답 데이터에서 최종 텍스트만 쏙 뽑아내는 편의 메서드
        public String extractText() {
            if (candidates == null || candidates.isEmpty()) return "";
            return candidates.get(0).content().parts().get(0).text();
        }
    }

    // --- 공통 내부 구조 (Google API 규격) ---
    public record Content(List<Part> parts) {}
    public record Part(String text) {}
    public record Candidate(Content content) {}
}