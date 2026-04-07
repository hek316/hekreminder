# 개발 계획: HekReminder

> 작성일: 2026-04-07  
> 참조 문서: spec.md

---

## 기술 스택

### Backend

| 항목 | 기술 | 버전 | 비고 |
|------|------|------|------|
| 언어 | Java | 25 | |
| 프레임워크 | Spring Boot | 4.0.4 | |
| ORM | Spring Data JPA + Hibernate | Boot 내장 | |
| DB (개발) | H2 | in-memory | 앱 재시작 시 초기화 |
| 빌드 | Gradle (Kotlin DSL) | 9.x | |
| 유틸 | Lombok | Boot 내장 | Getter/Setter/Constructor |
| 서버 포트 | 8080 | | |

### Frontend

| 항목 | 기술 | 버전 | 비고 |
|------|------|------|------|
| 언어 | TypeScript | 5.x | strict 모드 |
| 프레임워크 | Next.js App Router | latest | `/frontend` 디렉토리 |
| 스타일 | Tailwind CSS | v4 | |
| 상태 관리 | React 내장 (useState/useContext) | | Zustand는 Phase 3 이후 고려 |
| HTTP 클라이언트 | fetch API (Next.js 내장) | | |
| 폰트 | `-apple-system` 시스템 폰트 | | SF Pro 대응 |
| 서버 포트 | 3000 | | |

### 프로젝트 구조

```
hekreminder/                      ← Spring Boot (Backend)
├── src/main/java/com/hekreminder/
│   ├── domain/                   ← JPA 엔티티
│   ├── repository/               ← Spring Data JPA
│   ├── service/                  ← 비즈니스 로직
│   ├── controller/               ← REST API
│   └── dto/                      ← Request/Response DTO (Phase 1~)
├── src/main/resources/
│   └── application.properties
├── build.gradle.kts
├── spec.md
├── plan.md
└── frontend/                     ← Next.js (Frontend)
    ├── app/                      ← App Router 페이지
    ├── components/               ← UI 컴포넌트
    ├── lib/                      ← API 클라이언트, 유틸
    ├── types/                    ← TypeScript 타입 정의
    └── package.json
```

---

## 개발 Phase 전략

단순한 것부터 점진적으로 기능을 추가한다.
각 Phase는 독립적으로 동작 가능한 완성 상태를 목표로 한다.

---

## Phase 1 — 기본 CRUD (리마인더 단일 목록)

> **목표**: 리마인더를 추가·완료·삭제할 수 있는 가장 단순한 형태

### 1-1. Backend

**기존 코드 수정**

현재 `Reminder` 엔티티에 `description` 필드가 있는데 `notes`로 rename하고,
`flagged`, `priority` 필드를 추가한다.

```
Reminder
├── id           Long         (PK, auto)
├── title        String       not null
├── notes        String       nullable  ← description rename
├── dueDate      LocalDateTime nullable
├── completed    boolean      default false
├── priority     enum         NONE/LOW/MEDIUM/HIGH  [추가]
└── flagged      boolean      default false         [추가]
```

**DTO 도입**

`Reminder` 엔티티를 Request/Response에 직접 노출하지 않는다.

```
ReminderRequest  { title, notes, dueDate, priority, flagged }
ReminderResponse { id, title, notes, dueDate, completed, priority, flagged, createdAt }
```

**API**

```
GET    /api/reminders                → 전체 목록
GET    /api/reminders?filter=today   → 오늘 마감
GET    /api/reminders?filter=scheduled → 미래 마감
GET    /api/reminders?filter=flagged   → 깃발
GET    /api/reminders?filter=completed → 완료됨
POST   /api/reminders                → 생성
PUT    /api/reminders/{id}           → 수정
PATCH  /api/reminders/{id}/complete  → 완료 토글
PATCH  /api/reminders/{id}/flag      → 깃발 토글
DELETE /api/reminders/{id}           → 삭제
```

**CORS 설정**

`@CrossOrigin` 또는 `WebMvcConfigurer`로 `http://localhost:3000` 허용

### 1-2. Frontend

**Next.js 프로젝트 초기화**

```bash
cd hekreminder
npx create-next-app@latest frontend --typescript --tailwind --app --no-src-dir
```

**구현 컴포넌트**

```
app/
└── page.tsx                  ← 메인 페이지 (단일 화면)

components/
├── ReminderList.tsx           ← 리마인더 목록 렌더링
├── ReminderItem.tsx           ← 단일 리마인더 행 (체크박스 + 제목 + 날짜)
└── AddReminderInput.tsx       ← 하단 입력창 ("+ 새로운 리마인더")

lib/
└── api.ts                    ← fetch 래퍼 (baseURL: localhost:8080)

types/
└── reminder.ts               ← Reminder 타입 정의
```

**UI 요구사항 (Phase 1)**

- 전체 배경 `#FFFFFF`, 폰트 `-apple-system`
- 리마인더 행: 원형 체크박스 (파란색) + 제목
- 완료 시: 체크박스 채워짐 + 취소선
- 하단 `+ 새로운 리마인더` 클릭 → 입력, Enter → 저장
- 마감일 있는 경우 우측에 날짜 표시, 기한 초과 시 빨간색

---

## Phase 2 — 사이드바 + 스마트 목록

> **목표**: Apple Reminder의 좌측 사이드바와 4개 스마트 목록 카드 구현

### 2-1. Backend

추가 API 없음. Phase 1의 `filter` 쿼리 파라미터 활용.

스마트 목록 카운트 API 추가:

```
GET /api/reminders/counts
→ { today: 3, scheduled: 7, all: 28, flagged: 2 }
```

### 2-2. Frontend

**레이아웃 분리**

```
app/
├── layout.tsx                ← 사이드바 + 메인 영역 분리
└── page.tsx                  ← redirect → /reminders

app/reminders/
└── page.tsx                  ← 선택된 뷰 렌더링 (filter 파라미터 기반)

components/
├── Sidebar.tsx               ← 전체 사이드바
├── SmartListCard.tsx         ← 2×2 카드 (오늘/예정/전체/깃발)
└── SmartListGrid.tsx         ← 2×2 그리드 컨테이너
```

**스마트 카드 스펙**

```
┌──────────┐ ┌──────────┐
│  3       │ │  7       │   ← 숫자: 28px bold 흰색
│          │ │          │
│ 📅 오늘  │ │ 📅 예정  │   ← 레이블: 13px 흰색
└──────────┘ └──────────┘   ← 배경: 파랑 / 빨강
┌──────────┐ ┌──────────┐
│  28      │ │  2       │
│          │ │          │
│ ≡ 전체   │ │ 🚩 깃발  │   ← 배경: 검정 / 주황
└──────────┘ └──────────┘
```

- 카드 클릭 → URL 쿼리 변경 → 메인 영역 필터링
- 선택된 카드: `scale(0.97)` 눌린 효과

---

## Phase 3 — 사용자 정의 목록 (ReminderList)

> **목표**: 목록 생성/수정/삭제, 목록별 리마인더 분리

### 3-1. Backend

**신규 엔티티**

```
ReminderList
├── id       Long    (PK)
├── name     String  not null
├── color    String  HEX (예: "#007AFF")
└── icon     String  이모지 (예: "🏠")
```

**Reminder ↔ ReminderList 연관**

```
Reminder
└── reminderList   @ManyToOne   nullable (null = 미분류)
```

**API**

```
GET    /api/lists              → 목록 전체 (각 목록의 미완료 수 포함)
POST   /api/lists              → 목록 생성
PUT    /api/lists/{id}         → 목록 수정 (이름/색상/아이콘)
DELETE /api/lists/{id}         → 목록 삭제
GET    /api/reminders?listId={id} → 목록별 리마인더 조회
```

**목록 삭제 정책**: 하위 리마인더를 함께 삭제 (Cascade)

### 3-2. Frontend

```
components/
├── ListSection.tsx            ← "나의 목록" 섹션
├── ListItem.tsx               ← 개별 목록 행 (색상 원 + 이름 + 수)
├── AddListButton.tsx          ← "+ 목록 추가" → 인라인 입력
├── ColorPicker.tsx            ← 12색 팔레트 선택기
└── IconPicker.tsx             ← 이모지 아이콘 선택기
```

**목록 생성 UX**

```
+ 목록 추가 클릭
→ 사이드바에 인라인 입력 필드 노출
→ 색상 기본값: #007AFF (파랑)
→ Enter: 저장, Escape: 취소
```

---

## Phase 4 — 상세 편집 패널

> **목표**: 리마인더 클릭 시 우측 슬라이드 패널에서 모든 필드 편집

### 4-1. Backend

추가 API 없음.

### 4-2. Frontend

```
components/
├── DetailPanel.tsx            ← 우측 슬라이드 패널 전체
├── DateTimePicker.tsx         ← 날짜/시각 선택기
├── PrioritySelector.tsx       ← 없음 / ! / !! / !!! 선택
└── FlagToggle.tsx             ← 깃발 토글 버튼
```

**패널 동작**

- 리마인더 행 클릭 → `translateX(0)` 슬라이드 인 (250ms ease-out)
- 패널 외부 클릭 → 자동 저장 후 슬라이드 아웃
- 레이아웃: 메인 영역이 패널만큼 좁아지는 방식 (push, overlay 아님)

**날짜 입력**

- 날짜 행 클릭 → 캘린더 팝오버
- 시각 행 클릭 → `<input type="time">` 또는 커스텀 picker
- 날짜 제거 버튼 포함

---

## Phase 5 — 서브태스크

> **목표**: 리마인더 하위에 서브태스크 추가/완료/삭제

### 5-1. Backend

**신규 엔티티**

```
Subtask
├── id         Long    (PK)
├── title      String  not null
├── completed  boolean default false
└── reminder   @ManyToOne  Reminder
```

**API**

```
POST   /api/reminders/{id}/subtasks              → 생성
PUT    /api/reminders/{id}/subtasks/{subId}      → 제목 수정
PATCH  /api/reminders/{id}/subtasks/{subId}/complete → 완료 토글
DELETE /api/reminders/{id}/subtasks/{subId}      → 삭제
```

`GET /api/reminders/{id}` 응답에 `subtasks` 배열 포함

### 5-2. Frontend

**리마인더 행 확장**

- 서브태스크 있는 경우 리마인더 우측에 `2/5` (완료/전체) 표시
- 행 아래 들여쓰기 형태로 서브태스크 행 렌더링

**상세 패널 확장**

- 패널 하단에 "서브태스크" 섹션 추가
- `+ 서브태스크 추가` 클릭 → 인라인 입력
- `Tab` 키로 리마인더 입력 → 서브태스크 전환

---

## Phase 6 — UI 완성도 + 다크모드

> **목표**: Apple Reminder와 시각적으로 최대한 동일하게 마무리

### 6-1. 완료 항목 섹션

- 목록 하단 `▶ 완료됨 (3)` 접힌 섹션
- 클릭 시 펼쳐짐, 완료 항목 회색 처리

### 6-2. 애니메이션 정리

| 동작 | 구현 |
|------|------|
| 체크박스 완료 | CSS transition: fill 300ms → 행 opacity+height 500ms |
| 리마인더 추가 | `@keyframes slideDown` height 0 → auto |
| 리마인더 삭제 | height collapse 200ms |
| 목록 전환 | 메인 영역 `opacity: 0 → 1` 150ms |
| 패널 슬라이드 | `transform: translateX(100%) → 0` 250ms |

### 6-3. 다크모드

- `next-themes` 라이브러리 또는 Tailwind `darkMode: 'class'`
- 시스템 설정 자동 감지 (`prefers-color-scheme`)
- 사이드바 상단 버튼으로 수동 토글 (☀️/🌙)

### 6-4. 반응형

- 사이드바: 모바일에서 기본 숨김, 햄버거 버튼으로 토글
- 상세 패널: 모바일에서 full-screen overlay로 전환

---

## Phase 요약 및 체크리스트

| Phase | 목표 | Backend | Frontend |
|-------|------|---------|----------|
| **1** | 기본 CRUD | 엔티티 수정 + DTO + filter API | Next.js 초기화 + 단일 리스트 UI |
| **2** | 스마트 목록 | counts API | 사이드바 + 스마트 카드 |
| **3** | 커스텀 목록 | ReminderList 엔티티 + API | 목록 CRUD + 색상/아이콘 선택 |
| **4** | 상세 패널 | — | 슬라이드 패널 + 날짜/우선순위 편집 |
| **5** | 서브태스크 | Subtask 엔티티 + API | 서브태스크 UI + Tab 전환 |
| **6** | 완성도 | — | 애니메이션 + 다크모드 + 반응형 |

---

## 공통 개발 규칙

### Backend
- 엔티티를 API 응답에 직접 사용 금지 → DTO 분리
- Service 계층에서 비즈니스 로직 처리, Controller는 얇게 유지
- 예외 처리: `@RestControllerAdvice`로 일관된 에러 응답
- 날짜 타입: `LocalDateTime` 사용, JSON 직렬화 `ISO-8601` 형식

### Frontend
- 모든 API 호출은 `lib/api.ts` 집중
- 컴포넌트는 표시 책임만, 데이터 fetch는 page 또는 커스텀 훅
- TypeScript strict 모드 — `any` 사용 금지
- Tailwind 유틸리티 클래스 우선, 커스텀 CSS는 최소화

### Git
- Phase 단위로 브랜치: `phase/1-basic-crud`, `phase/2-sidebar` 등
- 각 Phase 완료 시 main에 merge

---

## 미결 사항

- [ ] H2 → PostgreSQL 전환 시점 (Phase 3? 배포 시?)
- [ ] 서브태스크 depth 제한 (현재: 1단계)
- [ ] 목록 삭제 시 리마인더 처리 (현재: cascade 삭제)
- [ ] 완료 항목 자동 숨김 여부 (현재: 접힌 섹션으로 유지)
