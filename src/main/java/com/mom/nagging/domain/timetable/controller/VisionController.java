package com.mom.nagging.controller;

import com.mom.nagging.global.common.ApiResponse;
import com.mom.nagging.global.common.annotaion.LoginMember;
import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.timetable.domain.Timetable;
import com.mom.nagging.domain.timetable.dto.TimetableResponse;
import com.mom.nagging.domain.timetable.service.TimetableRegistrationService;
import com.mom.nagging.global.error.exception.BusinessException;
import com.mom.nagging.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vision")
@RequiredArgsConstructor
public class VisionController {

    // 시간표 추출 및 저장 비즈니스를 처리하는 서비스 클래스를 주입받습니다.
    private final TimetableRegistrationService registrationService;

    // 클라이언트에서 시간표 이미지를 POST 방식으로 전송할 때 매핑되는 메서드입니다.
    @PostMapping("/extract")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> extractText(
            // HTTP 요청에서 'file' 파라미터로 넘어온 이미지 데이터를 받습니다.
            @RequestParam("file") MultipartFile file,
            // 커스텀 어노테이션을 통해 현재 인증된 회원 엔티티 정보를 가져옵니다.
            @LoginMember Member member
    ) throws IOException {

        // 전송된 파일 객체가 비어있을 경우 전역 예외 처리기에서 처리할 수 있도록 비즈니스 예외를 던집니다.
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_FILE);
        }

        // 이미지 파일의 바이트 배열과 회원 정보를 서비스로 넘겨 시간표를 추출하고 DB에 저장된 엔티티 리스트를 반환받습니다.
        List<Timetable> savedTimetables = registrationService.processAndSaveTimetable(file.getBytes(), member);

        // 엔티티 리스트를 순회하며 프론트엔드 응답용 DTO 리스트로 변환합니다.
        List<TimetableResponse> responseDtoList = savedTimetables.stream()
                .map(TimetableResponse::of)
                .collect(Collectors.toList());

        // HTTP 상태 코드 200(OK)과 함께 공통 응답 포맷으로 데이터를 감싸서 클라이언트에 반환합니다.
        return ResponseEntity.ok(ApiResponse.success("성공! 시간표가 데이터베이스에 완벽하게 저장되었습니다.", responseDtoList));
    }
}