# 03. 옛 방식 ↔ 현대 방식 비교 분석

> 짝 문서: [`01_AS_IS_AND_ROADMAP.md`](./01_AS_IS_AND_ROADMAP.md), [`02_MODERN_MSA.md`](./02_MODERN_MSA.md)

본 프로젝트(`multi-msa-study`, 2021~2023 Spring Cloud Netflix 시대) 와 2026 년 현재 표준 사이의 격차를 항목별로 한 화면에 정리한다.

---

## 1. 한눈에 보는 비교표

| 항목 | 옛 방식 (multi-msa-study) | 현대 (2026 표준) | 변화의 본질 |
|------|---------------------------|-------------------|-------------|
| **Service Discovery** | Netflix Eureka (앱 내장) | K8s DNS + Service Mesh | 플랫폼이 처리, 앱은 모름 |
| **Config 관리** | Spring Cloud Config + Git + RabbitMQ Bus | K8s ConfigMap/Secret + External Secrets + Vault | 동적 갱신은 Reloader/sidecar 로 |
| **API Gateway** | Spring Cloud Gateway (자체 JWT 필터) | Spring Cloud Gateway / Envoy / Kong + OAuth2 Resource Server | 인증은 표준 라이브러리에 위임 |
| **인증** | 자체 JJWT 0.9.x 발급/검증 | Keycloak/Auth0(OIDC) + Resource Server + JWKS 자동 회전 | 자체 구현 → 표준 위임 |
| **Service-to-Service 보안** | 평문 HTTP | mTLS (Mesh 자동) + SPIFFE/SPIRE | Zero-trust 가 기본 |
| **DB** | H2 in-memory, 1 모듈/1 DB 가이드만 존재 | Postgres × N (DB-per-service), Flyway 마이그레이션 | 강제 분리 |
| **분산 트랜잭션** | 없음 (또는 단일 DB 가정) | Outbox + Saga (Choreography or Temporal Orchestration) | "최종 일관성" 인정 |
| **메시징** | RabbitMQ (Bus 용도만) | **Kafka** + Schema Registry + AsyncAPI | 비즈니스 이벤트의 척추 |
| **복원력** | 없음 (Eureka 기본만) | Resilience4j + Mesh 정책 + 명시 타임아웃 | 모든 호출에 격벽 |
| **로깅** | 콘솔 텍스트 | JSON + traceId/spanId MDC + Loki/ELK | 검색·연관 가능 |
| **메트릭** | 없음 | Micrometer → Prometheus + Grafana | RED/USE 표준 대시보드 |
| **트레이싱** | Zipkin 설정 주석 처리 | OpenTelemetry → Tempo/Jaeger | 첫날부터 필수 |
| **배포** | 수동 jar 실행 | Docker → K8s Helm/Kustomize → ArgoCD GitOps | 선언적 + 감사 가능 |
| **CI** | 없음 | GitHub Actions, TestContainers, Trivy, SBOM | 모든 PR 그린 → 머지 |
| **Contract Test** | 없음 | Spring Cloud Contract / Pact | 프로듀서·컨슈머 호환 보장 |
| **API 문서** | 없음 | OpenAPI 3.1 (springdoc) + AsyncAPI | 코드와 동기화 |
| **Workflow / Saga 오케스트레이션** | 없음 | Temporal / Camunda 8 | 장기 트랜잭션·재시도·관측 |
| **비밀 관리** | keystore 파일 git 보관 | Vault / SealedSecrets / ESO | 코드와 분리 |
| **Multi-tenancy / 환경분리** | profile 만 (dev/prod) | 클러스터/네임스페이스 분리 + ApplicationSet | 환경별 격리 강함 |
| **Edge TLS** | keystore 수동 | cert-manager + Let's Encrypt 자동 | 자동 회전 |
| **로컬 개발 경험** | 각 서비스 IDE 에서 수동 기동 | `compose.yaml` / Tilt / Skaffold / DevSpace | "5분 안에 풀스택" |

---

## 2. 어떤 부분은 여전히 유효한가

옛 프로젝트의 모든 게 낡은 건 아니다. 살릴 가치가 있는 것:

| 살릴 것 | 이유 |
|---------|------|
| **Spring Cloud Gateway** | 2026 에도 표준. 그대로 사용 가능 (버전만 업) |
| **Eureka** | K8s 가 아니면 여전히 유효. Standalone VM 환경에서는 합리적 |
| **Spring Cloud Config** | K8s 아닌 환경에선 유효. 다만 Bus + Git 의존성은 무거움 |
| **모듈 분리 사고** | 서비스 경계 잡는 사고 자체는 동일 |
| **JWT 토큰 컨셉** | 표준 OIDC 로 가더라도 학습 자산 |

---

## 3. 변화의 핵심 흐름 3가지

### (a) "라이브러리에서 플랫폼으로"
- **과거**: 각 서비스가 라이브러리 의존성으로 Discovery, Circuit Breaker, Config 를 *내장* (Netflix OSS 시대)
- **현재**: 같은 일을 **K8s + Service Mesh + Operator** 가 *플랫폼 레벨*에서 수행. 애플리케이션 코드는 비즈니스만.
- **이유**: 다언어·다팀 환경에서 "Java 만의 라이브러리" 로 통일하기 어려워짐

### (b) "동기 → 비동기 + 이벤트"
- **과거**: REST 호출 체인 (A → B → C → D). 한 곳 장애가 전체 다운
- **현재**: Kafka 이벤트로 약결합, Saga 로 비즈니스 일관성. 호출 체인은 짧게.
- **이유**: 결합도 ↓, 확장성 ↑, 장애 격리

### (c) "수동 배포 → GitOps"
- **과거**: jar 파일 손으로 옮기거나, ssh 스크립트
- **현재**: Git 푸시 → CI 빌드 → 이미지 태그 변경 PR → ArgoCD 가 자동 동기. 클러스터 상태 = Git 이력.
- **이유**: 감사·롤백·재현성

---

## 4. 옛 방식이 남긴 학습 가치

> "낡았다 = 버려라" 는 아니다. 학습자에게는 다음 가치가 있다.

1. **개념의 시대적 흐름 이해**: Netflix OSS → CNCF Cloud-Native 로의 전환은 분산 시스템 사고의 진화 그 자체. 옛 프로젝트로 시대 변화를 체감 가능.
2. **무엇이 자동화되었는가** 를 직접 만져본 사람이 K8s/Mesh 가 무엇을 대신해주는지 *깊게* 이해한다. K8s 부터 시작한 사람은 "왜 자동인지" 모름.
3. **Spring Cloud Gateway, OAuth2** 등은 그대로 활용 가능. 골격은 살림.
4. **운영 미숙함의 통증** 을 작게 체험: 로그가 흩어져 디버깅 안 되고, 한 서비스 죽으면 전체 멈추는 느낌. 이걸 안 겪고 가면 "왜 관측성인가" 가 추상적임.

---

## 5. "지금 다시 만든다면" 의 1-페이지 리시피

만약 오늘 0 부터 같은 비즈니스(주문/카탈로그/유저)를 다시 짠다면:

```
저장소: monorepo (Nx, Turborepo 또는 Maven 멀티모듈)
언어: Java 21 + Spring Boot 3.3 (혹은 Kotlin)
클러스터: K3d 로 로컬, EKS/GKE 로 운영
서비스 메시: Linkerd (간단) 또는 Istio
인증: Keycloak (OIDC)
브로커: Kafka + Schema Registry (Avro)
DB: Postgres × 서비스, Flyway
관측성: OTEL → Prometheus/Grafana + Loki + Tempo
배포: Helm + ArgoCD
CI: GitHub Actions, TestContainers, Trivy, springdoc OpenAPI
Saga: Temporal (복잡한 흐름) + Outbox (간단한 알림)
```

---

## 6. 결론

- **multi-msa-study** = "MSA 의 첫번째 챕터". 박스(서비스, Discovery, Gateway) 는 잡혔지만 **운영성이 통째로 빠져 있음**.
- 현대 MSA = 같은 박스 위에 **플랫폼·관측성·이벤트·GitOps** 4 개 축이 추가된 형태.
- 학습 자산을 살리고 싶다면 [`01_AS_IS_AND_ROADMAP.md`](./01_AS_IS_AND_ROADMAP.md) 의 Phase 0~5 를 따라가면 같은 도메인을 현대 스택으로 *재구축* 가능.
- 시간이 없다면: **Phase 0(버전·컨테이너) + Phase 1(관측성)** 만 해도 "운영 가능 후보" 수준에 도달.
