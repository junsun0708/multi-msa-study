# 04. 다 묶여 있는 오픈소스 — 직접 조립하지 말 것

> 짝 문서: [`02_MODERN_MSA.md`](./02_MODERN_MSA.md), [`03_COMPARISON.md`](./03_COMPARISON.md)

직접 K8s + Helm + ArgoCD + Prometheus + Kafka 일일이 깔지 않아도, **이미 묶어놓은 오픈소스/플랫폼** 이 여러 갈래로 존재한다. 목적별 추천.

---

## 1. "MSA 학습/데모" — 곧장 돌아가는 완성 샘플

| 프로젝트 | 무엇 | 특징 |
|---------|------|------|
| **GoogleCloudPlatform/microservices-demo** ("Online Boutique") | 11개 폴리글랏 서비스 (Go/Java/Node/Python/C#) + gRPC + K8s 매니페스트 | 가장 유명. K8s/Istio/OTEL 데모 표준. `kubectl apply -f release/` 한 번으로 동작 |
| **dotnet-architecture/eShop** (구 eShopOnContainers) | .NET 풀MSA 쇼핑몰 + Saga + Event Bus + BFF + gRPC + Aspire | 마이크로소프트 공식 레퍼런스 |
| **spring-petclinic/spring-petclinic-microservices** | Spring Cloud (Eureka/Config/Gateway/Resilience4j/Zipkin) | 본 `multi-msa-study` 의 정석 버전. 비교용 |
| **piomin/sample-spring-microservices-new** | Spring Cloud + Kafka + Vault + ELK 데모 (Piotr Mińkowski) | 한국 학습 자료로 자주 인용 |

→ **학습 1순위**: `microservices-demo (Online Boutique)`. K8s + Mesh + OTEL 흐름이 그대로.

---

## 2. "MSA 플랫폼" — 클러스터 전체 패키지 (실 운영)

K8s 위에 빌드/CI/모니터링/Service Mesh/콘솔 등을 *PaaS 형태로 묶은 것*.

| 플랫폼 | 무엇 | 누가 쓰나 |
|--------|------|----------|
| **OpenShift / OKD** (Red Hat) | K8s + 빌드(S2I) + 모니터링 + 콘솔 + GitOps(ArgoCD) + Service Mesh(Istio) 통합 | 엔터프라이즈, 한국 금융권 점유율 높음 |
| **Rancher** (SUSE) | 멀티 K8s 클러스터 관리 + 모니터링/로깅 + Fleet GitOps | 중견 / 멀티클라우드 |
| **KubeSphere** (OSS, 청신소프트) | K8s + DevOps(Jenkins) + Istio + Observability + 멀티테넌시 | UI 친화, 한국 일부 도입 |
| **Crossplane + Backstage** | 인프라까지 K8s API 로 + 개발자 포털 | 플랫폼팀이 IDP(Internal Developer Platform) 만들 때 표준 |

→ 한국 운영 환경 기본은 보통 **OpenShift > Rancher** 순.

---

## 3. "한 명령으로 풀스택 부팅" — 로컬 / 소규모 운영

| 도구 | 무엇 |
|------|------|
| **Tilt** | 로컬 K8s + 코드변경 → 컨테이너 자동 리빌드. dev loop 표준 |
| **Skaffold** (Google) | 동급 dev loop |
| **Devspace** | 동급 |
| **k3d / kind / minikube** | 로컬 K8s 부팅 |
| **Coolify** | Docker 기반 셀프호스트 PaaS, git push → 자동 배포. **Heroku OSS 대체** |
| **Dokploy** | Coolify 와 비슷. 최근 떠오름 |
| **Dokku** | 한 대 VM 에서 git push 배포. 전통의 강자 |
| **CapRover** | Docker Swarm 기반, UI 친화 셀프호스트 PaaS |
| **Kompose** | docker-compose.yml → K8s yaml 자동 변환 |

→ "MSA 학습은 했고 사이드 프로젝트 운영" 단계라면 **Coolify / Dokploy** 가 핫함. Heroku 끊긴 사람들 다 이리로 옮겨감.

---

## 4. "관측성 / 메시징 묶음" — 부분 솔루션

| 묶음 | 무엇 |
|------|------|
| **Grafana LGTM Stack** | Loki(로그) + Grafana(대시) + Tempo(트레이스) + Mimir(메트릭) Helm 한 방 |
| **kube-prometheus-stack** | Prometheus + Alertmanager + Grafana + 노드/K8s 익스포터 한 차트 |
| **Strimzi** | Kafka 를 K8s 위에 자동 운영 (오퍼레이터) |
| **Bitnami Helm Charts** | Postgres/Kafka/Redis/MongoDB 등 OSS chart 묶음 |
| **Knative** | K8s 위 서버리스 (오토스케일 0→N) |
| **Dapr** | "MSA 빌딩블럭" 라이브러리. pub/sub·state·secrets·service invoke 를 언어 무관 sidecar 로 |

→ 관측성만 빠르게: **kube-prometheus-stack + Loki + Tempo (LGTM)** 한 줄 `helm install`.

---

## 5. 개발자 포털 / IDP — Spotify Backstage

서비스가 100 개 넘어가면 등장: "어느 서비스가 어디 있고 누가 주인이지?"
→ **Backstage** (Spotify OSS) 가 표준. 서비스 카탈로그·OpenAPI 모음·K8s 상태 통합·scaffold 템플릿(클릭 한 번에 새 서비스 골격 생성).

한국에선 **카카오 / 라인 / 토스** 등이 변형 도입.

---

## 6. 상황별 추천

### A. "MSA 가 어떻게 굴러가는지 K8s 로 체감" (학습)
1. `microservices-demo (Online Boutique)` 를 k3d 에 띄움 (10분)
2. Linkerd 설치 → mTLS·트래픽 분할 자동 시현
3. Grafana LGTM 스택 추가 → trace 가시화

### B. "옛 `multi-msa-study` 를 살리되 현대화"
1. `spring-petclinic-microservices` 구조 참고 → Spring Boot 3 + Spring Cloud 2024 로 업
2. **Kompose** 로 docker-compose → K8s 매니페스트 자동 변환
3. **kube-prometheus-stack + Loki** 로 관측성 먼저 확보

### C. "사이드 프로젝트 1~3 개 운영"
- K8s 오버킬. **Coolify** 또는 **Dokploy** 한 대 VPS 에 띄우고 git push 로 끝

### D. "실 운영 / 회사 도입"
- **OpenShift** (지원 계약) 또는 **Rancher** (OSS + 유료 지원 옵션)
- 위에 **ArgoCD + Backstage + Strimzi(Kafka) + Keycloak**

---

## 7. 한 줄 정리

> **학습용** = `Online Boutique` + Linkerd  
> **사이드 프로젝트 셀프호스트 PaaS** = `Coolify` 또는 `Dokploy`  
> **엔터프라이즈** = `OpenShift` / `Rancher`  
> **개발자 포털** = `Backstage`

직접 조립하지 말 것. 누군가 이미 묶어놨다.
