# 07. 용어 사전 — "이건 이거고 이렇게 쓴다"

> 짝 문서: [`06_LEARNING_PATH.md`](./06_LEARNING_PATH.md) (학습 경로 — 이거 본 다음에 시작)

학습 들어가기 전에 한 번 훑고 가는 용어집.
**암기 X, "어디서 쓰이는지" 한 줄로 머리에 박아두는 게 목적**. 형식: `용어 — 한 줄 정의 — 어떻게/언제 쓰는지`.

---

## 1. 컨테이너 / 오케스트레이션

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **컨테이너 (Container)** | 앱 + OS 라이브러리를 하나의 격리된 박스로 패키징 | 운영체제 신경 X. "어디서든 똑같이 돈다" 가 핵심. `docker run` |
| **이미지 (Image)** | 컨테이너의 "설계도" | `Dockerfile` 로 만들고 레지스트리(GHCR/Docker Hub)에 push |
| **Dockerfile** | 이미지 만드는 레시피 | `FROM`, `COPY`, `RUN`, `CMD` 줄 단위로 작성 |
| **Kubernetes (K8s)** | 컨테이너 수백 개를 자동으로 띄우고/죽이고/네트워크 연결해주는 시스템 | 운영의 표준. 한 대 PC 위에 띄우는 K3s, 클라우드 EKS/GKE/AKS 등 |
| **Pod** | 컨테이너 1개(보통) 를 감싸는 K8s 의 최소 단위 | 직접 만들 일은 적음. Deployment 가 알아서 만듦 |
| **Deployment** | "이 Pod 를 N개 항상 떠 있게 해줘" 선언 | yaml 1장 = "내 앱 3개 떠 있어야 함". 가장 자주 쓰는 yaml |
| **ReplicaSet** | Deployment 가 내부적으로 만드는 Pod 무리 | 직접 만질 일 거의 없음 |
| **Service (K8s Service)** | Pod 들 앞에 붙는 "고정 주소 + 로드밸런서" | `http://order-service` 로만 호출하면 자동 분산 |
| **Ingress** | 클러스터 바깥 트래픽이 들어오는 입구 | 도메인·HTTPS·경로 라우팅. 보통 nginx-ingress 또는 Gateway API |
| **Namespace** | 클러스터 안의 폴더 | dev/stage/prod 분리하거나 팀별 격리에 사용 |
| **ConfigMap** | 설정 파일/환경변수를 K8s 안에 저장 | 코드와 설정 분리. 비밀이 아닌 것만 |
| **Secret** | 비밀번호/토큰 저장 (base64) | 진짜 비밀은 Vault/SealedSecrets 같이 써야 안전 |
| **HPA (Horizontal Pod Autoscaler)** | CPU·메모리 따라 Pod 수 자동 증감 | "트래픽 몰리면 알아서 늘려줘" |
| **PDB (Pod Disruption Budget)** | 동시에 몇 개까지 죽어도 되는지 한도 | 무중단 배포·노드 교체 안전망 |
| **DaemonSet** | 모든 노드에 1개씩 뜨는 Pod | 로그 수집기·모니터링 에이전트 |
| **StatefulSet** | 순서·이름·디스크가 영속인 Pod 묶음 | DB·Kafka 같이 *상태 있는* 워크로드 |
| **Job / CronJob** | 1회성 / 주기적 작업 | 마이그레이션·백업·배치 |
| **NetworkPolicy** | Pod 간 통신 방화벽 | "order-service 는 menu-service 만 호출 허용" |
| **kubectl** | K8s 조작 CLI | 99% 의 K8s 명령은 이걸로. `get / describe / logs / apply / port-forward` 5개만 알아도 됨 |

---

## 2. 패키징 / 배포 / GitOps

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **Helm** | K8s yaml 의 템플릿 엔진 + 패키지 매니저 | `helm install nginx bitnami/nginx`. yaml 12개를 chart 1개로 |
| **Chart** | Helm 의 패키지 단위 | `Chart.yaml`, `values.yaml`, `templates/` 폴더 |
| **values.yaml** | chart 에 주입할 변수 모음 | 환경별로 `values-dev.yaml`, `values-prod.yaml` |
| **Kustomize** | yaml 위에 patch 만 얹는 방식 | Helm 의 대안. K8s 내장. 단순함이 장점 |
| **GitOps** | "클러스터 상태 = Git 의 상태" 라는 원칙 | git push 만 하면 배포 자동. kubectl apply 직접 X |
| **ArgoCD** | GitOps 도구. Git 변경 감지 → 클러스터 동기화 | UI 가 좋아 한국에서 많이 씀 |
| **Flux** | 같은 GitOps 도구 (CNCF) | CLI/Operator 친화 |
| **CI (Continuous Integration)** | PR 마다 빌드·테스트 자동 | GitHub Actions, GitLab CI |
| **CD (Continuous Delivery/Deploy)** | 빌드된 결과를 자동으로 배포 | ArgoCD 가 CD 역할 |
| **이미지 레지스트리** | 컨테이너 이미지 저장소 | Docker Hub / GHCR / ECR / Harbor (셀프호스트) |
| **SBOM** | 이미지 안에 뭐가 들었는지 명세 | `syft` 로 생성. 보안 감사용 |
| **Trivy** | 이미지 취약점 스캐너 | CI 에 한 줄 추가해 PR 단위 검사 |

---

## 3. Service Discovery / 통신

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **Service Discovery** | "다른 서비스가 어디 있어?" 를 찾는 메커니즘 | 옛날엔 Eureka, 지금은 K8s DNS 가 자동 |
| **Eureka** | Netflix 의 서비스 레지스트리 | K8s 환경에선 거의 사용 안 함 (옛 프로젝트 호환용) |
| **Spring Cloud Gateway** | API 게이트웨이 (Spring 진영) | JWT 검증·라우팅·rate limit. 지금도 유효 |
| **Envoy / Envoy Gateway** | Lyft 가 만든 고성능 프록시. Mesh 의 표준 | Istio/Linkerd 내부에서 동작. 직접 쓰는 사람 적음 |
| **Kong** | 오픈소스 API 게이트웨이 | 플러그인 풍부. Spring 비의존 |
| **BFF (Backend For Frontend)** | 모바일·웹용 별도 게이트웨이 | "웹 BFF / 앱 BFF" 따로 두고 내부 API 합성 |
| **gRPC** | Protobuf 기반 고성능 RPC | 서비스간 통신. JSON REST 보다 빠름. K8s 친화 |
| **REST** | HTTP + JSON | 외부 공개 API 의 기본기 |
| **GraphQL** | 한 endpoint 에서 필요한 필드만 받아오는 쿼리 언어 | BFF 합성에 쓸 만함 |
| **mTLS (mutual TLS)** | 양쪽이 서로 인증서 검증 | 서비스간 보안의 표준. Mesh 가 자동 |

---

## 4. Service Mesh

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **Service Mesh** | 모든 서비스 사이에 sidecar 프록시를 깔아 트래픽·보안·관측을 자동화 | mTLS·재시도·timeout·canary 를 yaml 만으로 |
| **Sidecar** | Pod 안에 본 컨테이너 옆에 붙는 보조 컨테이너 | Mesh 가 자동 주입. 대표 예: Envoy |
| **Istio** | 가장 유명한 Service Mesh. 기능 많음 | 학습 곡선 가파름. 운영 인력 있을 때 |
| **Linkerd** | 가벼운 Service Mesh | mTLS 자동, 운영 단순. 학습용 추천 |
| **Cilium** | eBPF 기반 networking + Mesh | 최근 부상. 네트워크 단에서 통제 |
| **Traffic Split / Canary** | 새 버전에 5% 만 보내고 점진 증가 | Mesh yaml 한 줄로 |

---

## 5. 데이터 / 메시징

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **DB-per-service** | 서비스마다 자기 DB | MSA 의 절대 원칙. 공유 DB 는 안티패턴 |
| **Postgres / MySQL** | 관계형 DB | 일반적인 트랜잭션·정합성 워크로드 |
| **Redis** | 인메모리 KV 저장소 | 캐시·세션·rate limit·pub/sub |
| **MongoDB** | 도큐먼트 DB | 스키마 유연. 로그·콘텐츠 |
| **Elasticsearch / OpenSearch** | 검색 엔진 | 풀텍스트 검색·로그 인덱싱 |
| **Flyway / Liquibase** | DB 스키마 마이그레이션 | 앱 시작 시 자동 적용. PR 단위 변경 |
| **Kafka** | 분산 이벤트 스트리밍 플랫폼 | MSA 이벤트 통신의 척추. 서비스간 약결합 |
| **Topic** | Kafka 의 메시지 채널 | `order.created`, `payment.completed` 같이 도메인 단위 |
| **Producer / Consumer** | Kafka 메시지 발행자 / 구독자 | 서비스가 양쪽 모두일 수 있음 |
| **Schema Registry** | Kafka 메시지 스키마 버전 관리 | Avro/Protobuf 스키마 호환 보장 |
| **Avro / Protobuf** | 바이너리 직렬화 포맷 | Kafka 메시지 / gRPC 페이로드. JSON 보다 작고 빠름 |
| **CDC (Change Data Capture)** | DB 변경을 이벤트로 전환 | Debezium 이 표준. Outbox 패턴의 핵심 |
| **Debezium** | CDC 도구 | Postgres WAL → Kafka 토픽 자동 생성 |
| **RabbitMQ** | 메시지 큐 (AMQP) | 작업 큐·전통적 메시징. Kafka 와 다른 용도 |

---

## 6. 분산 패턴

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **Saga** | 여러 서비스에 걸친 트랜잭션을 작은 단계로 쪼개고 실패 시 보상 | 주문→결제→배송 같은 멀티 서비스 흐름 |
| **Choreography Saga** | 각 서비스가 이벤트 듣고 알아서 다음 동작 | 약결합. 흐름 추적 어려움 |
| **Orchestration Saga** | 중앙 코디네이터(Temporal 등) 가 흐름 관리 | 흐름 가시성↑. 복잡한 비즈니스에 |
| **Outbox 패턴** | DB 트랜잭션과 이벤트 발행을 원자적으로 | "DB 는 commit 됐는데 Kafka 발행 실패" 방지 |
| **Inbox 패턴** | 같은 메시지 중복 수신 방지 | consumer 측 중복 처리 가드 |
| **CQRS** | 읽기 모델과 쓰기 모델 분리 | 읽기 최적화. 복잡한 조회/검색 |
| **Event Sourcing** | 상태를 저장하지 않고 이벤트 시퀀스로 표현 | 감사 로그·시점 복원. 진입장벽 큼 |
| **Eventual Consistency (최종 일관성)** | 잠깐은 어긋나지만 결국 같아짐 | MSA 에서 ACID 트랜잭션 대신 받아들이는 현실 |
| **Idempotency (멱등성)** | 같은 요청 여러 번 = 한 번과 같은 결과 | 재시도 안전. 모든 write API 설계 시 고려 |
| **Circuit Breaker (회로차단기)** | 다운 서비스로의 호출을 차단해 fail-fast | Resilience4j. timeout 과 함께 |
| **Retry / Backoff** | 실패 시 지수적으로 대기하며 재시도 | 멱등 API 에만 안전 |
| **Bulkhead (격벽)** | 한 호출 그룹이 다른 그룹을 망치지 못하게 분리 | 스레드풀 격리 |
| **Rate Limiting** | 초당 호출 수 제한 | Gateway 또는 Mesh. Redis 토큰 버킷 흔함 |
| **Backpressure** | 빠른 producer 가 느린 consumer 를 짓누르지 않게 신호 보냄 | 스트리밍·메시징에서 |

---

## 7. 인증 / 보안

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **OAuth 2.0** | "타사 앱이 내 리소스에 접근" 을 위한 인증 위임 표준 | 실제로는 OIDC 와 같이 씀 |
| **OIDC (OpenID Connect)** | OAuth 2.0 위에 "사용자 신원" 을 얹은 표준 | 로그인의 사실상 표준 |
| **JWT** | 클레임을 담은 서명된 토큰 (`header.payload.signature`) | OIDC 발급 토큰의 형식. 자체 발급 코드 작성 X |
| **JWKS** | JWT 검증용 공개키 모음 (JSON) | issuer 의 `/.well-known/jwks.json`. Resource Server 가 자동 가져감 |
| **Authorization Server** | 토큰 발급자 | Keycloak / Auth0 / Cognito |
| **Resource Server** | 토큰 검증해 API 보호 | Spring Security Resource Server |
| **Keycloak** | 오픈소스 OIDC/OAuth 서버 | 셀프호스트 표준. 한국에서도 많이 씀 |
| **Vault** | 비밀(시크릿) 관리 | 토큰·DB 비밀번호. 코드와 분리 |
| **SealedSecrets** | K8s Secret 을 암호화해서 git 에 보관 가능 | GitOps 친화 |
| **mTLS** | 위 §4 참조 | Mesh 에 위임 |
| **Zero Trust** | "안에서도 안 믿는다" 보안 모델 | 모든 호출에 인증, 모든 통신에 암호화 |

---

## 8. 관측성 (Observability) — "3 pillars"

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **Observability** | 시스템 안에서 무슨 일이 일어나는지 *밖에서* 알 수 있는 정도 | Metrics + Logs + Traces 셋이 짝 |
| **Metrics (메트릭)** | 시간에 따른 수치 (요청 수·지연·에러율) | Prometheus 가 표준 |
| **Prometheus** | 시계열 DB + 수집기 | `/metrics` 엔드포인트를 주기적 pull |
| **Grafana** | 시각화·대시보드·알람 | Prometheus/Loki/Tempo 데이터를 한 화면에 |
| **Logs (로그)** | 시점별 텍스트 기록 | JSON 포맷 + traceId 가 표준 |
| **Loki** | "로그용 Prometheus". 라벨 기반 인덱스 | Grafana 와 짝. 가볍고 저렴 |
| **ELK / EFK** | Elasticsearch + Logstash/Fluent + Kibana | 전통적 로그 스택. 무겁지만 검색 강력 |
| **Traces (트레이스)** | 한 요청이 여러 서비스 거치는 경로 기록 | trace 1개 안에 여러 span |
| **Span** | trace 안의 단일 작업 단위 | "DB 호출 50ms" 한 조각 |
| **OpenTelemetry (OTEL)** | 메트릭·로그·트레이스 수집의 표준 SDK + 프로토콜(OTLP) | 모든 언어 SDK 통일. 첫날부터 도입 |
| **Tempo / Jaeger / Zipkin** | trace 저장·조회 백엔드 | Jaeger 가 가장 보편 |
| **Pyroscope / Parca** | 지속적 프로파일링 (CPU 핫스팟) | "어느 함수가 CPU 먹나" 운영 중 추적 |
| **SLO / SLI / SLA** | 목표/측정치/약속 | "99.9% 가용성" 같은 거. SRE 용어 |
| **RED 메서드** | Rate / Errors / Duration | 요청형 서비스 대시보드 표준 |
| **USE 메서드** | Utilization / Saturation / Errors | 리소스(노드·디스크) 대시보드 표준 |

---

## 9. 워크플로 / 비즈니스 프로세스

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **Temporal** | "durable execution" 워크플로 엔진 | 장기 트랜잭션·재시도·타임아웃을 코드로. Saga 오케스트레이션 |
| **Camunda 8 / Zeebe** | BPMN 기반 워크플로 엔진 | 비즈니스 분석가도 다이어그램으로 정의 |
| **BPMN** | 비즈니스 프로세스 표기 표준 | 그림으로 흐름 정의 |

---

## 10. 개발자 경험 / 플랫폼

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **IDP (Internal Developer Platform)** | 개발자가 셀프서비스로 인프라를 쓸 수 있는 내부 플랫폼 | Backstage + Crossplane 조합 흔함 |
| **Backstage** | Spotify 가 만든 개발자 포털 OSS | 서비스 카탈로그·문서·scaffold 템플릿 |
| **Crossplane** | 클라우드 인프라를 K8s API 로 선언 | Terraform 의 GitOps 친화 대안 |
| **Tilt / Skaffold / Devspace** | 로컬 K8s dev loop 자동화 | 코드 저장 → 자동 빌드·재배포 |
| **k3d / kind / minikube** | 노트북 위 로컬 K8s | 학습·개발용 |

---

## 11. 코드 / 명세 표준

| 용어 | 한 줄 정의 | 어떻게 쓰는지 |
|------|-----------|---------------|
| **OpenAPI 3** | REST API 명세 표준 (구 Swagger) | springdoc 으로 코드에서 자동 생성 |
| **AsyncAPI** | 이벤트·메시지 명세 표준 | Kafka 토픽 문서화 |
| **Contract Test** | producer ↔ consumer 호환 자동 검증 | Spring Cloud Contract / Pact |
| **TestContainers** | 테스트 시 실제 DB/Kafka 컨테이너 띄움 | 통합 테스트 표준 |
| **ADR (Architecture Decision Record)** | 설계 결정의 짧은 기록 | `docs/adr/0001-...md` 식. 후임자에게 *왜* 를 남김 |

---

## 12. 옛 ↔ 현대 매핑 (한 번에 보기)

| 옛 용어 (Spring Cloud Netflix) | 현대 대응 |
|--------------------------------|-----------|
| Eureka (Service Registry) | K8s DNS / Service Mesh |
| Zuul (Gateway 1세대) | Spring Cloud Gateway / Envoy / Kong |
| Hystrix (Circuit Breaker) | Resilience4j / Mesh 정책 |
| Ribbon (Client Load Balancer) | K8s Service / Mesh |
| Spring Cloud Config | K8s ConfigMap + ExternalSecrets/Vault |
| Spring Cloud Bus + RabbitMQ | Kafka + ConfigMap reloader |
| Sleuth + Zipkin | Micrometer Tracing + OpenTelemetry → Tempo/Jaeger |
| Archaius | ConfigMap (정적) / Spring Cloud Config (동적) |
| Turbine (Hystrix 대시보드) | Grafana + Prometheus |

---

## 13. 첫 학습 시작 전 "이 7개만" 머리에 박아두기

이거 7개만 한 줄씩 외우고 [`06_LEARNING_PATH.md`](./06_LEARNING_PATH.md) 로 넘어가도 충분.

1. **Pod / Deployment / Service** — 컨테이너를 K8s 에 띄우는 3종 세트
2. **Helm** — yaml 의 패키지 매니저, `values.yaml` 로 환경 분리
3. **ArgoCD** — git push 만 하면 클러스터 자동 동기화 (GitOps)
4. **Kafka 토픽 + producer/consumer** — 서비스간 이벤트 통신의 척추
5. **OpenTelemetry + Prometheus + Grafana** — 무엇이 일어나는지 보는 3종 세트
6. **Resilience4j (CircuitBreaker · Retry · Timeout)** — 한 서비스 죽음이 전체를 안 죽이게
7. **Keycloak (OIDC) + Spring Security Resource Server** — 자체 JWT 만들지 말 것

> 나머지는 4주 동안 *통증을 만났을 때* 사전에서 찾아 채우는 식으로 충분합니다.
