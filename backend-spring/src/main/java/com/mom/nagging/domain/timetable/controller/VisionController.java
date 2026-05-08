package com.mom.nagging.domain.timetable.controller;

import com.mom.nagging.domain.timetable.dto.TimeItemResponse;
import com.mom.nagging.global.common.ApiResponse;
import com.mom.nagging.global.common.annotaion.LoginMember;
import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.timetable.domain.TimeData;
import com.mom.nagging.domain.timetable.dto.TimetableResponse;
import com.mom.nagging.domain.timetable.service.TimetableRegistrationService;
import com.mom.nagging.global.error.BusinessException;
import com.mom.nagging.global.error.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Vision API", description = "AI 기반 시간표 추출 및 분석")
@RestController
@RequestMapping("/api/v1/vision")
@RequiredArgsConstructor
public class VisionController {

    // 시간표 추출 및 저장 비즈니스를 처리하는 서비스 클래스를 주입받습니다.
    private final TimetableRegistrationService registrationService;

    @Operation(
            summary = "시간표 이미지 분석",
            description = "사용자가 업로드한 에브리타임 캡처본을 FastAPI 서버로 전달하여 텍스트 데이터를 추출합니다."
    )
    @PostMapping("/extract")
    public ResponseEntity<ApiResponse<TimetableResponse>> extractText(
            @Parameter(description = "분석할 시간표 이미지 파일")
            @RequestParam("file") MultipartFile file,
            // 커스텀 어노테이션을 통해 현재 인증된 회원 엔티티 정보를 가져옵니다.
            @LoginMember Member member
    ) throws IOException {

        // 전송된 파일 객체가 비어있을 경우 전역 예외 처리기에서 처리할 수 있도록 비즈니스 예외를 던집니다.
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_FILE);
        }

        // 이미지 파일의 바이트 배열과 회원 정보를 서비스로 넘겨 시간표를 추출하고 DB에 저장된 엔티티 리스트를 반환받습니다.
        List<TimeData> savedTimeData = registrationService.processAndSaveTimetable(file.getBytes(), member);

        // 엔티티 리스트를 순회하며 프론트엔드 응답용 DTO 리스트로 변환합니다.
        List<TimeItemResponse> itemDtoList = savedTimeData.stream()
                .map(TimeItemResponse::of)
                .collect(Collectors.toList());

        TimetableResponse finalResponse = new TimetableResponse(itemDtoList);

        // HTTP 상태 코드 200(OK)과 함께 공통 응답 포맷으로 데이터를 감싸서 클라이언트에 반환합니다.
        return ResponseEntity.ok(ApiResponse.success("성공! 시간표가 데이터베이스에 완벽하게 저장되었습니다.", finalResponse));
    }
}