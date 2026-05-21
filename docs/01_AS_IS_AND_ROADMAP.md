# 01. 현행 분석 (AS-IS) 와 완성 로드맵

> 대상: `multi-msa-study` (마지막 커밋 2023-09-21).  
> 목적: 옛 학습 프로젝트의 현재 상태를 정리하고, "MSA 를 완벽하게 구축" 하기 위해 필요한 작업 목록을 우선순위와 함께 제시한다. 다음 문서 [`02_MODERN_MSA.md`](./02_MODERN_MSA.md) 와 [`03_COMPARISON.md`](./03_COMPARISON.md) 가 짝.

---

## 1. 한 줄 요약

> **2021~2022 년 강의·토이 프로젝트 형태의 Spring Cloud Netflix-Eureka 기반 MSA 골격.** 서비스 디스커버리·API Gateway·Config Server 같은 "이론의 핵심 박스" 는 잡혀 있으나, **운영성 (관측·배포·복원력·보안 강화)** 과 **현대 패턴 (Outbox/Saga/CQRS/Service Mesh)** 이 거의 비어있다. 학습 자산으로 가치는 충분하지만 그대로 운영·확장은 어렵다.

---

## 2. 현재 구성 (AS-IS)

### 2.1 모듈

| 모듈 | 역할 | 현재 상태 |
|------|------|-----------|
| `discoveryService` | Eureka Server (서비스 등록·디스커버리) | 동작 |
| `config-service` | Spring Cloud Config Server (Git 백엔드) | 동작 |
| `apigateway-service` | Spring Cloud Gateway + JWT(JJWT) `AuthorizationHeaderFilter` | 동작 |
| `user-service` | 사용자/인증, H2 in-memory | 동작 |
| `order-service` | 주문 | 동작 |
| `catalog-service` | 카탈로그 | 동작 |
| `first-service` / `second-service` | 라우팅·로드밸런싱 학습용 더미 | 동작 |
| `keystore/` | TLS·JWT 비대칭키 keystore | 정적 자원 |
| `bin/` | 구 Eureka 바이너리 | 미사용 |

### 2.2 스택·버전

| 항목 | 값 | 비고 |
|------|----|------|
| JDK | Java 17 | OK |
| Spring Boot | 2.7.15 | **EOL (Standard) 2023-11**. 3.x 로 마이그레이션 필요 |
| Spring Cloud | 2021.0.x (Jubilee) | EOL. 2023.x (Leyton) 또는 2024.x 로 |
| 빌드 | Maven | OK |
| Service Discovery | Netflix Eureka | 유지 가능. K8s 환경이면 K8s 자체 DNS 로 대체 |
| Config | Spring Cloud Config + Git + RabbitMQ Bus | 유효, 그러나 K8s 라면 ConfigMap/Secret 권장 |
| API Gateway | Spring Cloud Gateway | OK (현대 스택에서도 유효) |
| Auth | **JJWT 0.9.1** + 자체 발급 검증 | **취약**. JJWT 0.11+ 또는 **Spring Security Resource Server (Nimbus JOSE)** 로 교체 필요 |
| DB | H2 in-memory | 학습용 OK. 운영 시 Postgres/MySQL 필수 |
| MQ | RabbitMQ (Bus 용으로만) | 비즈니스 메시징 미구현 |

### 2.3 결정적으로 빠진 것 (빈칸)

- **컨테이너화/배포**: `Dockerfile` / `docker-compose.yml` / Kubernetes 매니페스트 / Helm 전부 없음
- **관측성**: Prometheus 메트릭, Grafana 대시보드, **OpenTelemetry 트레이싱**, 중앙 로그 (Loki/ELK) 없음. `application.yml` 의 Zipkin 설정은 주석 처리
- **복원력**: Resilience4j (Circuit Breaker/Retry/Bulkhead/RateLimiter) 없음, Spring Retry 없음
- **고급 분산 패턴**: Outbox / Saga (Choreography·Orchestration), CQRS, Event Sourcing 없음
- **API 명세**: SpringDoc/OpenAPI 미생성, Postman 컬렉션 없음
- **테스트**: Unit 외에 **Contract Test (Spring Cloud Contract / Pact)**, **TestContainers 통합 테스트** 없음
- **CI/CD**: `.github/workflows` 없음, GitOps 없음
- **보안 강화**: HTTPS 강제, mTLS, Vault/Sealed Secrets, OAuth2/OIDC 표준화 없음
- **데이터/메시지 일관성**: Kafka 미사용 → "주문 → 결제 → 재고" 같은 멀티-서비스 시나리오 검증 불가

---

## 3. 진단 — 어디서부터 손대야 하나

학습 자산은 보존하되, "실제 운영 가능한 MSA" 로 끌어올리기 위한 **5 단계 로드맵**.

### Phase 0 — 위생 (1 ~ 2 주)
> 새 코드를 얹기 전에 부패한 토대부터 교체.

1. **버전 업그레이드**
   - Spring Boot 3.3 + Spring Cloud 2024.0.x + Java 21 (LTS)
   - JJWT 0.12 또는 **Spring Security Resource Server** 채택
   - Lombok/MapStruct 등 의존성 최신화
2. **Repo 위생**
   - Multi-module Maven (parent POM) 또는 Gradle 멀티프로젝트로 통합
   - `.gitignore` 표준화, `keystore/*.jks` 는 git 제외 + Vault 위탁
   - README 골격 정비 (서비스 다이어그램, 로컬 실행, `make up`)
3. **컨테이너화 기본**
   - 각 서비스 `Dockerfile` (jib 또는 buildpacks)
   - 루트 `docker-compose.yml` 로 로컬 전체 기동 (Eureka·Config·Gateway·Postgres·RabbitMQ·Kafka·Zipkin)

### Phase 1 — 관측성 (1 주)
> 무엇이 일어나는지 보이지 않으면 어떤 기능 추가도 신뢰 불가.

4. **Metrics**: `micrometer-registry-prometheus` + `/actuator/prometheus` 노출
5. **Tracing**: Spring Boot 3 + Micrometer Tracing + OpenTelemetry exporter → Tempo/Jaeger
6. **Logs**: Logback JSON encoder + `traceId/spanId` MDC → Loki 또는 OpenSearch
7. **대시보드**: Grafana (USE: rate/error/duration), 알람 룰 1세트

### Phase 2 — 복원력 / 보안 (1 ~ 2 주)
8. **Resilience4j** 적용: Gateway·서비스간 호출에 CircuitBreaker + Retry + TimeLimiter
9. **API Gateway 강화**:
   - JWT 검증을 **Spring Security Resource Server** 로 위임
   - Rate Limiting (Redis), CORS 표준 설정
10. **Vault 또는 SealedSecrets** 도입 — keystore/패스워드를 코드에서 분리
11. **mTLS** (서비스간) — 학습 단계라면 Istio·Linkerd 의 mTLS 활성으로 자동화

### Phase 3 — 데이터·메시지 일관성 (2 ~ 3 주)
> MSA 의 진짜 어려움이 시작되는 지점.

12. **Kafka 도입** (또는 Redpanda)
13. **Outbox 패턴** — `order-service` 가 트랜잭션과 메시지 발행을 원자적으로
14. **Saga (Choreography)** — 주문 → 결제 → 배송 흐름을 이벤트 체인으로 재구성, 보상 트랜잭션
15. 비즈니스 모델 보강 — `order-service` ↔ `catalog-service` ↔ `payment-service` (신규)
16. DB 분리 — Postgres × N (서비스당 1 DB, **shared DB 절대 금지**)

### Phase 4 — 배포 자동화 / GitOps (2 주)
17. **GitHub Actions** — build → test → image push → ArgoCD 트리거
18. **Helm chart** 또는 **Kustomize** — 서비스 1개당 1 chart, umbrella chart 로 환경 구성
19. **ArgoCD** 또는 **Flux** — GitOps. dev/stage/prod 분리
20. **K8s 매니페스트**: Deployment, Service, HPA, PDB, NetworkPolicy
21. **Ingress + TLS**: ingress-nginx 또는 Gateway API + cert-manager

### Phase 5 — 테스트·계약·문서 (지속)
22. **OpenAPI** 스펙 자동 생성 (springdoc-openapi-starter-webmvc-ui)
23. **TestContainers** 로 Postgres/Kafka 통합 테스트
24. **Spring Cloud Contract** (또는 Pact) — Producer/Consumer 계약 테스트
25. **k6** 또는 **Gatling** 부하 테스트, SLO/SLI 수립

---

## 4. 우선순위 매트릭스 (Eisenhower)

| 긴급↑ / 중요↑ | 긴급↑ / 중요↓ |
|----------------|----------------|
| **Phase 0** (버전·컨테이너), **Phase 1** (관측성) | docker-compose 단일 파일 정리 |
| **긴급↓ / 중요↑** | **긴급↓ / 중요↓** |
| **Phase 2~3** (복원력/Saga), Phase 4 (GitOps) | first/second-service 더미 정리 |

---

## 5. "완벽 구축" 의 정의 (체크리스트)

운영 가능한 MSA 의 **최소 합격선**. 모두 ✅ 가 되면 "완성" 으로 본다.

### 운영성
- [ ] 모든 서비스 컨테이너화 + K8s 배포
- [ ] HPA + PDB + ResourceRequests 설정
- [ ] 헬스체크 (liveness/readiness) 분리
- [ ] 로그 JSON + traceId, 중앙 수집
- [ ] 메트릭 + 알람 1세트 (latency·error rate·saturation)
- [ ] Distributed Tracing 1개 트랜잭션 끝-to-끝 가시성

### 신뢰성
- [ ] CircuitBreaker, Retry, Timeout 모든 서비스간 호출에 적용
- [ ] 무중단 배포 (rolling 또는 blue/green)
- [ ] DB 마이그레이션 (Flyway/Liquibase) + rollback 가능
- [ ] Outbox + Saga 시나리오 1개 이상 동작·테스트

### 보안
- [ ] OAuth2/OIDC 표준 (Keycloak 또는 Auth0)
- [ ] Secrets 코드 외부화 (Vault/SealedSecrets)
- [ ] mTLS 또는 Service Mesh
- [ ] Image vulnerability scan (Trivy) CI 통합

### 개발 경험
- [ ] OpenAPI 자동 생성 + 게시
- [ ] Contract test (Producer/Consumer)
- [ ] `make up` / `docker compose up` 으로 5분 내 로컬 부팅
- [ ] PR 단위 CI 그린, 머지 시 dev 자동 배포 (GitOps)

---

## 6. 다음 단계 (이번 주)

1. 이 문서 + `02_MODERN_MSA.md` + `03_COMPARISON.md` 검토 후 **Phase 0 의 작업 1~3** 만 우선 PR 로 시작
2. branch: `chore/upgrade-spring-3` — Spring Boot 3.3 + Cloud 2024 만 먼저 올림
3. Phase 1 의 관측성 스택은 별도 PR (`feat/observability-otel`) 로 — 한 번에 너무 많이 바꾸지 말 것

> 학습 가치 보존이 목적이라면 **`legacy/spring-cloud-2021`** 브랜치로 현행을 동결해두고, `master` 에 현대적 재구축본을 올리는 dual-track 도 추천.
