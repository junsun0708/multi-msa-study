# 02. 현대(2026 기준) MSA 구축 가이드

> 짝 문서: [`01_AS_IS_AND_ROADMAP.md`](./01_AS_IS_AND_ROADMAP.md), [`03_COMPARISON.md`](./03_COMPARISON.md)

본 문서는 "지금 새로 MSA 를 만든다면 어떻게 만드는가" 를 의도적으로 의견 있게 (opinionated) 정리한다. 기술은 보수적으로(검증된 것), 운영성과 관측성은 공격적으로(처음부터 내장) 가져간다.

---

## 1. 설계 원칙 (2026 합의 수준)

| 원칙 | 의미 |
|------|------|
| **플랫폼 우선** | 분산 시스템의 어려운 부분(트래픽 라우팅, 재시도, mTLS, 메트릭)은 **플랫폼(K8s + Mesh)** 에 위임. 애플리케이션 코드는 비즈니스 로직만. |
| **이벤트 우선, 호출 보조** | 동기 REST 호출은 가능하면 줄이고 **이벤트(Kafka)** 로 약결합. 결국 일관성은 Saga 로 보장. |
| **DB-per-service 절대** | 공유 DB 는 안티패턴. 서비스간 데이터 접근은 API/이벤트로만. |
| **트레이스 가능한 모든 경로** | 첫날부터 OTEL. trace 없는 호출은 운영 불가능. |
| **GitOps 가 단일 진실** | 클러스터 상태는 Git 에 선언, ArgoCD/Flux 가 동기화. kubectl apply 금지. |
| **Zero-trust** | 서비스간 mTLS, 모든 토큰은 짧은 TTL, secrets 는 Vault. |
| **계약 우선** | OpenAPI/AsyncAPI/Avro 스키마가 코드보다 먼저, Contract test 로 호환성 보장. |

---

## 2. 표준 컴포넌트 스택 (한국·글로벌 공통 기본기)

### 2.1 런타임/언어
- **Java**: Spring Boot 3.3+, Java 21 LTS, GraalVM Native (선택)
- **Kotlin**: Spring Boot 3 + Coroutines (Java 와 호환, 코드량 절감)
- **Go**: 단일 책임 마이크로 서비스 / Sidecar / 게이트웨이
- **Node/TypeScript**: BFF, 작은 API
- **Python (FastAPI)**: ML 서빙, 데이터 잡

> **하나의 언어에 묶일 필요 없음.** 서비스마다 적합한 것. 단 **공통 라이브러리**(로깅·트레이싱·에러 포맷) 는 언어별 표준 1개씩 둔다.

### 2.2 서비스 디스커버리
- **K8s 기본 DNS** 만으로 충분. Eureka/Consul 은 K8s 에서는 사실상 deprecated.
- **Service Mesh** (Istio/Linkerd) 사용 시 라우팅·로드밸런싱·재시도까지 위임.

### 2.3 API Gateway / Edge
- **Spring Cloud Gateway** (JVM 친화)
- **Kong / Traefik / Envoy Gateway** (언어 중립, K8s Gateway API 표준)
- **BFF 패턴**: 모바일·웹 별 BFF 두고 내부 API 합성

### 2.4 인증·인가
- **OAuth2 + OIDC**가 표준. 자체 JWT 발급 코드 작성 금지.
- **Keycloak** (셀프호스트) 또는 **Auth0/Cognito** (관리형)
- **Spring Security Resource Server** + Nimbus JOSE (자동 JWKS 회전)
- **Service-to-Service**: SPIFFE/SPIRE + mTLS (Istio 가 자동)

### 2.5 데이터
- **DB-per-service** — Postgres 가 기본기. 검색은 OpenSearch/Elastic, 캐시는 Redis, 그래프는 Neo4j.
- 마이그레이션: **Flyway** (또는 Liquibase). PR 단위 적용.
- **Outbox 패턴**: `transactional_outbox` 테이블 + Debezium CDC → Kafka

### 2.6 메시징/이벤트
- **Apache Kafka** (혹은 Redpanda, Confluent Cloud)
- 스키마: **Avro / Protobuf** + **Schema Registry** — JSON 은 작은 시스템에만
- **AsyncAPI** 로 이벤트 명세 문서화

### 2.7 분산 트랜잭션 / 일관성
- **Saga (Choreography)** — 이벤트 체인. 서비스간 결합 낮음.
- **Saga (Orchestration)** — 중앙 코디네이터(예: **Temporal**, **Camunda 8**, **Conductor**).
  복잡한 흐름·관측성·재시도 정책에서 우월.
- **2PC 는 사실상 사용 안 함.**

### 2.8 복원력
- **Resilience4j** (CircuitBreaker / Retry / Bulkhead / RateLimiter / TimeLimiter)
- **Service Mesh** 에서 트래픽 정책 (Istio VirtualService / Linkerd ServiceProfile)
- **타임아웃은 모든 호출에 명시** — 무한 대기 금지

### 2.9 컨테이너 / 오케스트레이션
- **Kubernetes** (EKS/GKE/AKS or 셀프호스트). K3s/Kind 로 로컬 학습.
- **Helm** + **Helmfile** 또는 **Kustomize**
- 보안: **Pod Security Admission** + **NetworkPolicy** + **OPA Gatekeeper** / **Kyverno**

### 2.10 GitOps / CD
- **ArgoCD** (이미지 자동 업데이트는 Argo Image Updater)
- **Flux** (CD + Helm Operator + Image Update)
- App-of-Apps 또는 ApplicationSet 으로 환경별 자동 생성

### 2.11 CI
- **GitHub Actions / GitLab CI**
- 표준 잡: lint → unit → integration(TestContainers) → contract → SBOM(syft) → image build(buildpacks/jib) → vulnerability scan(Trivy) → push → ArgoCD sync

### 2.12 관측성 ("3 pillars + 1")
| Pillar | 도구 |
|--------|------|
| Metrics | **Prometheus** + Grafana, RED/USE 대시보드, alertmanager |
| Logs | **Loki** (또는 OpenSearch/ELK), JSON 로그 + traceId |
| Traces | **OpenTelemetry SDK** → Tempo/Jaeger |
| Profiles | **Pyroscope** / **Parca** (지속 프로파일링) |

> Spring Boot 3 의 **Micrometer Tracing → OTEL** 자동 연동이 표준.

### 2.13 비밀/설정 관리
- **HashiCorp Vault** + Vault Agent / CSI driver
- 또는 **External Secrets Operator** + AWS Secrets Manager / Bitwarden
- **SealedSecrets** (간단한 GitOps 친화)

### 2.14 Service Mesh (선택이지만 강력 추천)
- **Istio** (기능 풍부, 학습 곡선)
- **Linkerd** (가볍고 mTLS 자동, 운영 간단)
- 효과: mTLS 자동, retry/timeout 정책 yaml, traffic split (canary), 측정 자동

### 2.15 워크플로우 / 비즈니스 프로세스
- **Temporal.io** (durable execution) — saga 오케스트레이션·장기 작업·재시도
- **Camunda 8** (Zeebe) — BPMN 기반

### 2.16 Edge / API 명세
- **OpenAPI 3.1** (springdoc) — 코드에서 자동 생성, GitHub Pages 로 호스팅
- **AsyncAPI** — Kafka 토픽 명세
- **GraphQL** (선택) — BFF 합성에 유효

---

## 3. 표준 디렉토리 / 모노레포 구조 (Java/Spring 기준 예시)

```
my-msa/
├── apps/
│   ├── gateway/                  # Spring Cloud Gateway 또는 Envoy
│   ├── auth/                     # OAuth2 Authorization Server (또는 Keycloak)
│   ├── user-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── catalog-service/
│   └── notification-service/
├── platform/
│   ├── kafka/                    # docker-compose 또는 K8s 매니페스트
│   ├── postgres/
│   ├── observability/            # otel-collector / loki / tempo / grafana
│   └── keycloak/
├── deploy/
│   ├── helm/                     # 서비스별 chart + umbrella chart
│   ├── kustomize/
│   └── argocd/                   # Application / ApplicationSet
├── libs/
│   ├── common-logging/           # 공통 logback / OTEL 부트스트랩
│   ├── common-errors/
│   └── proto/                    # Avro/Protobuf 스키마 단일 소스
├── docs/
│   ├── adr/                      # Architecture Decision Records
│   ├── openapi/
│   └── asyncapi/
├── .github/
│   └── workflows/                # ci.yml / image-update.yml
├── compose.yaml                  # 로컬 풀스택
├── Makefile                      # make up / make logs / make test
└── README.md
```

---

## 4. 표준 서비스 한 개의 골격 (Spring Boot 3, Java 21)

```yaml
# application.yml
spring:
  application: { name: order-service }
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  flyway: { enabled: true }
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS}
    consumer: { group-id: order-service }
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${OIDC_ISSUER}     # Keycloak 등
management:
  endpoints.web.exposure.include: health,info,prometheus,metrics
  metrics.tags.application: ${spring.application.name}
  tracing.sampling.probability: 1.0
otel:
  exporter.otlp.endpoint: ${OTEL_ENDPOINT}
  service.name: ${spring.application.name}
resilience4j:
  circuitbreaker:
    instances:
      paymentClient: { failureRateThreshold: 50, slowCallDurationThreshold: 2s }
```

```dockerfile
# Dockerfile (cnb buildpacks 또는 jib 가 더 권장)
FROM eclipse-temurin:21-jre-alpine
COPY target/*.jar /app.jar
USER 10001
ENTRYPOINT ["java","-jar","/app.jar"]
```

```yaml
# k8s/deployment.yaml (요지)
apiVersion: apps/v1
kind: Deployment
metadata: { name: order-service }
spec:
  replicas: 2
  template:
    spec:
      containers:
        - name: app
          image: ghcr.io/org/order-service:1.4.2
          envFrom: [{ secretRef: { name: order-db } }]
          readinessProbe: { httpGet: { path: /actuator/health/readiness, port: 8080 } }
          livenessProbe:  { httpGet: { path: /actuator/health/liveness,  port: 8080 } }
          resources:
            requests: { cpu: 200m, memory: 512Mi }
            limits:   { cpu: 1,    memory: 1Gi }
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata: { name: order-service }
spec:
  scaleTargetRef: { kind: Deployment, name: order-service, apiVersion: apps/v1 }
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource: { name: cpu, target: { type: Utilization, averageUtilization: 60 } }
```

---

## 5. 흔한 안티패턴 (피해야 할 것)

1. **공유 DB** — "잠깐만, 두 서비스가 같은 테이블 쓰면 빠르잖아" → 결합·배포 잠금
2. **분산 모놀리스** — 서비스로 쪼갰지만 동기 호출 체인이 너무 길어 한 곳 장애가 전체 다운
3. **유산 RPC** (RMI, EJB, JAX-WS) — 디버깅·관측성 약함
4. **자체 작성 Service Discovery** — K8s/Mesh 가 한다
5. **자체 작성 OAuth/JWT 발급** — 보안 사고 99% 의 원인
6. **모든 서비스 로그를 stdout 만 → 수집 안 함** — 디버깅 불가
7. **재시도 + 비-멱등 API** — 중복 처리 폭탄
8. **2PC** — 운영 사례 거의 없음. Saga 로
9. **버저닝 없는 이벤트 스키마** — 호환성 깨지면 전사 마비
10. **kubectl apply 직접** — GitOps 없으면 환경 drift

---

## 6. 학습/실습 권장 순서 (실전 감각)

1. K3d/Kind 로 로컬 클러스터 → `kubectl get pods` 친숙해지기
2. 단일 Spring Boot 3 앱 컨테이너화 → K8s Deployment 까지
3. Kafka + Postgres compose 로컬 → Outbox 1개 구현
4. Service 2개 → 동기 REST 호출 → Tracing(Jaeger) 으로 trace 가시화
5. 호출 → 이벤트로 변환 → Saga choreography 1개
6. Helm chart 작성 → ArgoCD 로 dev 클러스터에 GitOps
7. Istio 또는 Linkerd 추가 → mTLS, traffic split (canary) 실험
8. Temporal 도입 → 복잡한 비즈니스 플로우 1개 (예: 주문→결제→환불)

---

## 7. 핵심 도서/레퍼런스

- *Building Microservices* — Sam Newman (2nd ed.)
- *Microservices Patterns* — Chris Richardson
- *Production-Ready Microservices* — Susan Fowler
- Martin Fowler microservices.io 사이트의 패턴 카탈로그
- CNCF Landscape (https://landscape.cncf.io)
