# HekReminder 코딩 관례

## 테스트

- 기능을 추가하거나 수정할 때 항상 검증 테스트를 함께 작성한다
- **도메인 엔티티** → 순수 단위 테스트 (JUnit만, Spring/JPA 컨텍스트 없이)
- **서비스 로직** → `@SpringBootTest` 통합 테스트 (`@Transactional`로 롤백), Mock 사용 금지
- **API 엔드포인트** → `@SpringBootTest` 통합 테스트


## Backend — 서비스 계층

- Service 인터페이스는 `service/ports/inp` 패키지에 위치 (`com.hekreminder.service.ports.inp.XxxService`)
- Service 구현체는 `service` 패키지에 위치, 네이밍은 `DefaultXxxService`
- Service 계층에서 비즈니스 로직 처리, Controller는 얇게 유지

