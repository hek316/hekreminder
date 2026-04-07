# HekReminder 코딩 관례

## 테스트

- 기능을 추가하거나 수정할 때 항상 검증 테스트를 함께 작성한다
- **도메인 엔티티** → 순수 단위 테스트 (JUnit만, Spring/JPA 컨텍스트 없이)

## Backend (Spring Boot)
- 엔티티를 API 응답에 직접 사용 금지 → Request/Response DTO 분리
- Service 계층에서 비즈니스 로직 처리, Controller는 얇게 유지
