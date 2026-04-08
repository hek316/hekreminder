# Code Review Fix Checklist

> 생성일: 2026-04-08  
> 기준 점수: 68/100  
> 이슈: Critical 3 / Major 7 / Minor 8

---

## 🔴 Critical

- [x] **[C1] CORS origin 하드코딩 제거**
  - 파일: `controller/ReminderController.java:20`, `controller/ReminderListController.java:15`
  - `@CrossOrigin` 제거 → 전역 `WebMvcConfigurer` Bean으로 교체
  - origin을 환경변수 `CORS_ALLOWED_ORIGINS`로 주입

- [x] **[C2] IllegalArgumentException 예외 처리 분리 (400 vs 404)**
  - 파일: `controller/GlobalExceptionHandler.java:13`
  - `ReminderNotFoundException extends RuntimeException` 커스텀 예외 생성
  - "not found" → `404 NOT_FOUND`, 유효성 오류 → `400 BAD_REQUEST`

- [x] **[C3] Controller에서 Repository 직접 의존 제거**
  - 파일: `controller/ReminderController.java:26`
  - `getCounts()` 로직을 `ReminderService`로 이동
  - Controller는 Service만 의존하도록 수정

---

## 🟠 Major

- [x] **[M1] @Valid 실패 시 에러 핸들러 추가**
  - 파일: `controller/GlobalExceptionHandler.java`
  - `MethodArgumentNotValidException` 핸들러 추가
  - 응답 포맷: `{ field, message }` 배열 형태

- [ ] **[M2] H2 Console dev 프로파일로 분리**
  - 파일: `src/main/resources/application.properties`
  - `application-dev.properties` 생성 후 H2 관련 설정 이동
  - 운영 환경에서 H2 Console 자동 비활성화

- [ ] **[M3] 프론트엔드 API Base URL 환경변수화**
  - 파일: `frontend/lib/api.ts:3`
  - `http://localhost:8080` → `process.env.NEXT_PUBLIC_API_URL`
  - `frontend/.env.local` 파일 생성

- [ ] **[M4] API 에러 응답 body 파싱**
  - 파일: `frontend/lib/api.ts:5-12`
  - 에러 시 `res.json()`으로 서버 에러 메시지 파싱
  - 사용자에게 의미 있는 에러 메시지 전달

- [ ] **[M5] API 호출 전체에 try/catch 추가**
  - 파일: `frontend/app/reminders/page.tsx:30-47`
  - `handleAdd`, `handleToggleComplete`, `handleToggleFlag`, `handleDelete` 에러 처리
  - 실패 시 에러 토스트 또는 사용자 알림 표시

- [ ] **[M6] Sidebar 카운트 실시간 동기화**
  - 파일: `frontend/components/Sidebar.tsx`
  - 리마인더 생성/삭제/완료 토글 시 카운트 자동 갱신
  - Context 또는 React Query 도입 검토

- [ ] **[M7] 삭제 시 확인 다이얼로그 추가**
  - 파일: `frontend/components/ReminderItem.tsx:41`
  - 우클릭 즉시 삭제 → 확인 다이얼로그 후 삭제

---

## 🟡 Minor

- [ ] **[m1] ReminderList.update() color null 방어 로직 추가**
  - 파일: `domain/ReminderList.java:42`
  - `color != null ? color : "#007AFF"` 조건 추가

- [ ] **[m2] getRemindercounts 함수명 camelCase 수정**
  - 파일: `frontend/lib/api.ts:53`
  - `getRemindercounts` → `getReminderCounts`

- [ ] **[m3] BaseEntity 추출 (timestamp 중복 제거)**
  - 파일: `domain/Reminder.java`, `domain/ReminderList.java`
  - `@MappedSuperclass BaseEntity`에 `createdAt`, `updatedAt` 통합
  - `@PrePersist` / `@PreUpdate` 또는 `@CreationTimestamp` / `@UpdateTimestamp` 활용

- [ ] **[m4] URL 쿼리 파라미터 타입 가드 추가**
  - 파일: `frontend/app/reminders/page.tsx:25`
  - `as ReminderFilter` 캐스팅 → 유효한 값인지 검증 후 사용

- [ ] **[m5] ReminderListRequest color 필드 유효성 검증 추가**
  - 파일: `dto/ReminderListRequest.java`
  - `@Pattern(regexp = "^#[0-9A-Fa-f]{6}$")` 어노테이션 추가

- [ ] **[m6] findScheduledReminders 오늘 마감 건 중복 수정**
  - 파일: `repository/ReminderRepository.java:18`
  - `from`을 `LocalDateTime.now()` → 내일 시작(`LocalDate.now().plusDays(1).atStartOfDay()`)으로 변경

- [ ] **[m7] SmartListCard hover 스타일 Tailwind로 교체**
  - 파일: `frontend/components/SmartListCard.tsx:31`
  - `onMouseEnter`/`onMouseLeave` DOM 직접 수정 → Tailwind `hover:` 유틸리티 클래스 사용

- [ ] **[m8] Service 인터페이스 반환 타입 DTO 전환 검토**
  - 파일: `service/ports/inp/ReminderService.java`
  - 현재 엔티티 직접 반환 → 향후 DTO 반환으로 계층 경계 명확화
