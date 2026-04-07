# HekReminder — 구현 태스크 목록

> 기준 문서: [plan.md](plan.md), [spec.md](spec.md)  
> 업데이트: 2026-04-07

---

## Phase 1 — 기본 CRUD (리마인더 단일 목록)

### Backend

- [ ] `Reminder` 엔티티 수정
  - [ ] `description` → `notes` 필드 rename
  - [ ] `priority` 필드 추가 (`enum`: NONE / LOW / MEDIUM / HIGH)
  - [ ] `flagged` 필드 추가 (`boolean`, default false)
  - [ ] `createdAt` 필드 추가 (`LocalDateTime`, 자동 생성)
- [ ] DTO 생성
  - [ ] `ReminderRequest` DTO (`title`, `notes`, `dueDate`, `priority`, `flagged`)
  - [ ] `ReminderResponse` DTO (`id`, `title`, `notes`, `dueDate`, `completed`, `priority`, `flagged`, `createdAt`)
- [ ] `ReminderRepository` — 필터 쿼리 메서드 추가
  - [ ] `filter=today` 쿼리 (오늘 마감, 미완료)
  - [ ] `filter=scheduled` 쿼리 (미래 마감, 미완료)
  - [ ] `filter=flagged` 쿼리 (flagged=true, 미완료)
  - [ ] `filter=completed` 쿼리
- [ ] `ReminderService` 구현
  - [ ] 전체 목록 조회 (filter 파라미터 처리)
  - [ ] 리마인더 생성
  - [ ] 리마인더 수정
  - [ ] 완료 토글
  - [ ] 깃발 토글
  - [ ] 리마인더 삭제
- [ ] `ReminderController` REST API 구현
  - [ ] `GET /api/reminders` (filter 쿼리 파라미터 포함)
  - [ ] `POST /api/reminders`
  - [ ] `PUT /api/reminders/{id}`
  - [ ] `PATCH /api/reminders/{id}/complete`
  - [ ] `PATCH /api/reminders/{id}/flag`
  - [ ] `DELETE /api/reminders/{id}`
- [ ] CORS 설정 (`http://localhost:3000` 허용)
- [ ] `@RestControllerAdvice` 글로벌 예외 처리

### Frontend

- [ ] Next.js 프로젝트 초기화 (`/frontend`, TypeScript + Tailwind + App Router)
- [ ] 타입 정의 (`types/reminder.ts`) — `Reminder` 인터페이스
- [ ] API 클라이언트 (`lib/api.ts`) — fetch 래퍼, baseURL: `localhost:8080`
  - [ ] `getReminders(filter?)` 함수
  - [ ] `createReminder(data)` 함수
  - [ ] `updateReminder(id, data)` 함수
  - [ ] `toggleComplete(id)` 함수
  - [ ] `toggleFlag(id)` 함수
  - [ ] `deleteReminder(id)` 함수
- [ ] `ReminderItem` 컴포넌트 — 단일 리마인더 행
  - [ ] 원형 체크박스 (파란 테두리 → 완료 시 채워짐)
  - [ ] 완료 시 취소선 표시
  - [ ] 마감일 우측 표시 (기한 초과 시 빨간색)
  - [ ] 깃발 아이콘 표시
- [ ] `ReminderList` 컴포넌트 — 목록 렌더링
- [ ] `AddReminderInput` 컴포넌트 — `+ 새로운 리마인더` 하단 입력창
  - [ ] Enter로 저장
  - [ ] Escape로 취소
- [ ] `app/page.tsx` — 메인 페이지 (단일 화면)

---

## Phase 2 — 사이드바 + 스마트 목록

### Backend

- [ ] `GET /api/reminders/counts` API 구현
  - [ ] `{ today, scheduled, all, flagged }` 응답

### Frontend

- [ ] `app/layout.tsx` — 사이드바 + 메인 영역 분리 레이아웃
- [ ] `app/reminders/page.tsx` — filter 파라미터 기반 뷰 렌더링
- [ ] `SmartListCard` 컴포넌트 — 개별 스마트 카드
  - [ ] 오늘 카드 (`#007AFF`)
  - [ ] 예정 카드 (`#FF3B30`)
  - [ ] 전체 카드 (`#000000`)
  - [ ] 깃발 카드 (`#FF9500`)
  - [ ] 우측 상단 흰색 숫자 (28px bold)
  - [ ] 호버 효과 (`brightness(1.08)`)
  - [ ] 선택 효과 (`scale(0.97)`)
- [ ] `SmartListGrid` 컴포넌트 — 2×2 그리드 컨테이너
- [ ] `Sidebar` 컴포넌트 — 전체 사이드바 (`#F2F2F7` 배경, 260px 고정)
  - [ ] 스마트 카드 그리드 영역
  - [ ] "나의 목록" 섹션 (Phase 3 연동 예정)
  - [ ] 카드 클릭 → URL 쿼리 변경 → 메인 영역 필터링

---

## Phase 3 — 사용자 정의 목록 (ReminderList)

### Backend

- [ ] `ReminderList` 엔티티 생성 (`id`, `name`, `color`, `icon`)
- [ ] `Reminder` 엔티티에 `reminderList` 연관 추가 (`@ManyToOne`, nullable)
- [ ] `ReminderListRepository` 생성
- [ ] `ReminderListService` 구현
  - [ ] 전체 목록 조회 (미완료 리마인더 수 포함)
  - [ ] 목록 생성
  - [ ] 목록 수정 (이름/색상/아이콘)
  - [ ] 목록 삭제 (하위 리마인더 Cascade 삭제)
- [ ] `ReminderListController` REST API 구현
  - [ ] `GET /api/lists`
  - [ ] `POST /api/lists`
  - [ ] `PUT /api/lists/{id}`
  - [ ] `DELETE /api/lists/{id}`
- [ ] `GET /api/reminders?listId={id}` 지원 추가

### Frontend

- [ ] `types/list.ts` — `ReminderList` 인터페이스
- [ ] `lib/api.ts`에 목록 API 함수 추가
- [ ] `ListItem` 컴포넌트 — 개별 목록 행 (색상 원 + 이름 + 미완료 수)
- [ ] `ListSection` 컴포넌트 — "나의 목록" 섹션 헤더 + 목록 행들
  - [ ] 선택된 목록 행 하이라이트 (`#E5E5EA` 배경)
- [ ] `AddListButton` 컴포넌트 — `+ 목록 추가` → 인라인 입력
  - [ ] Enter: 저장 / Escape: 취소
- [ ] `ColorPicker` 컴포넌트 — Apple 12색 팔레트
- [ ] `IconPicker` 컴포넌트 — 이모지 아이콘 선택기
- [ ] 사이드바에 `ListSection` 통합
- [ ] 목록 선택 시 메인 영역 제목 색상 = 목록 색상

---

## Phase 4 — 상세 편집 패널

### Frontend

- [ ] `DetailPanel` 컴포넌트 — 우측 슬라이드 패널 (320px)
  - [ ] 슬라이드 인 애니메이션 (`translateX(0)`, 250ms ease-out)
  - [ ] 슬라이드 아웃 애니메이션 (200ms ease-in)
  - [ ] 패널 외부 클릭 → 자동 저장 후 닫힘
  - [ ] 메인 영역이 패널만큼 좁아지는 push 방식
  - [ ] 제목 인라인 편집 textarea
  - [ ] 메모 인라인 편집 textarea
- [ ] `DateTimePicker` 컴포넌트 — 날짜/시각 선택
  - [ ] 캘린더 팝오버 (날짜)
  - [ ] `<input type="time">` 또는 커스텀 picker (시각)
  - [ ] 날짜 제거 버튼
- [ ] `PrioritySelector` 컴포넌트 — 없음 / ! / !! / !!! 세그먼트 선택
- [ ] `FlagToggle` 컴포넌트 — 깃발 토글 버튼
- [ ] 리마인더 행 클릭 → 패널 오픈 연동

---

## Phase 5 — 서브태스크

### Backend

- [ ] `Subtask` 엔티티 생성 (`id`, `title`, `completed`, `reminder` @ManyToOne)
- [ ] `SubtaskRepository` 생성
- [ ] `SubtaskService` 구현
  - [ ] 서브태스크 생성
  - [ ] 서브태스크 제목 수정
  - [ ] 완료 토글
  - [ ] 서브태스크 삭제
- [ ] `SubtaskController` REST API 구현
  - [ ] `POST /api/reminders/{id}/subtasks`
  - [ ] `PUT /api/reminders/{id}/subtasks/{subId}`
  - [ ] `PATCH /api/reminders/{id}/subtasks/{subId}/complete`
  - [ ] `DELETE /api/reminders/{id}/subtasks/{subId}`
- [ ] `ReminderResponse`에 `subtasks` 배열 포함

### Frontend

- [ ] `types/subtask.ts` — `Subtask` 인터페이스
- [ ] `lib/api.ts`에 서브태스크 API 함수 추가
- [ ] `ReminderItem` 확장 — 서브태스크 있는 경우 `2/5` 진행률 표시
- [ ] 서브태스크 행 렌더링 — 32px 들여쓰기, 항상 펼쳐진 상태
- [ ] `DetailPanel` 확장 — 서브태스크 섹션 추가
  - [ ] `+ 서브태스크 추가` → 인라인 입력
  - [ ] 서브태스크 완료 토글
  - [ ] 서브태스크 삭제
- [ ] `Tab` 키 → 리마인더 입력에서 서브태스크 전환
- [ ] `Shift+Tab` 키 → 서브태스크에서 상위 항목 전환

---

## Phase 6 — UI 완성도 + 다크모드

### Frontend

- [ ] 완료된 항목 접힌 섹션 (`▶ 완료됨 (n)`)
  - [ ] 클릭 시 펼쳐짐/접힘 토글
  - [ ] 완료 항목 회색 처리
- [ ] 애니메이션 정리
  - [ ] 체크박스 완료: fill 300ms → 행 opacity+height 500ms fade-out
  - [ ] 리마인더 추가: `@keyframes slideDown` height 0 → auto
  - [ ] 리마인더 삭제: height collapse 200ms
  - [ ] 목록 전환: `opacity: 0 → 1` 150ms
  - [ ] 패널 슬라이드: `translateX(100%) → 0` 250ms
  - [ ] 목록 추가: 사이드바 fade-in 150ms
- [ ] 다크모드 구현
  - [ ] Tailwind `darkMode: 'class'` 설정
  - [ ] `prefers-color-scheme` 자동 감지
  - [ ] 사이드바 수동 토글 버튼 (☀️/🌙)
  - [ ] 다크모드 색상값 적용 (`#1C1C1E`, `#2C2C2E`, `#FFFFFF` 등)
- [ ] 키보드 단축키
  - [ ] `Ctrl+N` — 새 리마인더 추가
  - [ ] `Ctrl+Delete` — 선택 리마인더 삭제
  - [ ] `Space` — 선택된 리마인더 완료 토글
  - [ ] `Escape` — 편집 취소 / 패널 닫기
- [ ] 반응형 레이아웃
  - [ ] 사이드바: 모바일 기본 숨김, 햄버거 버튼 토글
  - [ ] 상세 패널: 모바일 full-screen overlay

---

## 공통 / 횡단 관심사

- [ ] `application.properties` — H2 콘솔 활성화, JPA ddl-auto 설정 확인
- [ ] 날짜 직렬화 — `LocalDateTime` ISO-8601 형식 JSON 변환 설정
- [ ] 우선순위 표시 스타일 — `!!!` 빨강 / `!!` 주황 / `!` 파랑
- [ ] 마감일 포맷 — `오늘`, `내일`, `목`, `3월 5일` 한국어 표시
- [ ] Phase 1 완료 후 브랜치 `phase/1-basic-crud` → main merge
- [ ] Phase 2 완료 후 브랜치 `phase/2-sidebar` → main merge
- [ ] Phase 3 완료 후 브랜치 `phase/3-custom-list` → main merge
- [ ] Phase 4 완료 후 브랜치 `phase/4-detail-panel` → main merge
- [ ] Phase 5 완료 후 브랜치 `phase/5-subtask` → main merge
- [ ] Phase 6 완료 후 브랜치 `phase/6-ui-polish` → main merge

---

## 미결 사항 (결정 필요)

- [ ] H2 → PostgreSQL 전환 시점 (Phase 3? 배포 시?)
- [ ] 서브태스크 depth 제한 (현재: 1단계 고정)
- [ ] 목록 삭제 시 리마인더 처리 (Cascade 삭제 vs 미분류 이동)
- [ ] 완료 항목 자동 숨김 여부 (현재: 접힌 섹션 유지 방식)
- [ ] 다크모드 MVP 포함 여부
