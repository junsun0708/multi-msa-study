# 05. 라이선스 / 비용 — 무엇이 진짜 무료인가

> 짝 문서: [`04_OPEN_SOURCE_BUNDLES.md`](./04_OPEN_SOURCE_BUNDLES.md)

대부분의 도구는 **소프트웨어 자체는 무료**지만, **상용 지원·관리형 서비스(SaaS)** 는 별도 과금. 일부는 *"오픈소스" 라고 마케팅하지만 사실은 부분 제한* 인 경우(BUSL/SSPL 등)가 있어 정확히 정리한다.

---

## 1. 완전 무료 (Apache 2.0 / MIT 등) — 셀프호스트 시 비용 0

| 도구 | 라이선스 | 유료 옵션 |
|------|----------|-----------|
| Online Boutique (microservices-demo) | Apache 2.0 | — |
| spring-petclinic-microservices | Apache 2.0 | — |
| eShop / eShopOnContainers | MIT | — |
| **Kubernetes** 본체 | Apache 2.0 | 클라우드 매니지드(EKS/GKE/AKS) 만 유료 |
| Helm | Apache 2.0 | — |
| ArgoCD / Flux | Apache 2.0 | — |
| Linkerd (CE/Edge) | Apache 2.0 | Buoyant Enterprise 별도 |
| Istio | Apache 2.0 | Solo.io / Tetrate 상용 지원 별도 |
| Envoy / Envoy Gateway | Apache 2.0 | — |
| Apache Kafka | Apache 2.0 | Confluent 매니지드 별도 |
| Strimzi (K8s Kafka 오퍼레이터) | Apache 2.0 | — |
| PostgreSQL | PostgreSQL License (≈MIT) | — |
| Keycloak | Apache 2.0 | Red Hat SSO(상용 지원) 별도 |
| Prometheus / Grafana / Loki / Tempo / Mimir | AGPL/Apache | Grafana Cloud(SaaS) 별도 |
| OpenTelemetry | Apache 2.0 | — |
| Jaeger | Apache 2.0 | — |
| kube-prometheus-stack | Apache 2.0 | — |
| Kompose | Apache 2.0 | — |
| Tilt / Skaffold / Devspace / k3d / kind / minikube | Apache 2.0 / MIT | — |
| Bitnami Helm Charts | Apache 2.0 | Bitnami Premium(VMware) 별도. **2024 말 카탈로그 변경** 이슈 있어 chart 별 확인 |
| Knative | Apache 2.0 | — |
| Crossplane | Apache 2.0 | Upbound 매니지드 |
| Backstage | Apache 2.0 | Spotify Plugins 일부 유료 |
| Sealed Secrets | Apache 2.0 | — |
| Forgejo / Gitea | MIT | — |

→ 학습·셀프호스트 범위에선 **0원**.

---

## 2. Open-core — 무료지만 일부 기능은 상용에만

| 도구 | 무료 (CE/OSS) | 유료 (EE/Cloud) |
|------|----------------|------------------|
| HashiCorp Vault | **2023 BUSL 변경**. 셀프호스트 무료, "Vault 자체를 SaaS 로 재판매 금지" | HCP Vault, Enterprise (replication, namespaces) |
| Temporal | Apache 2.0 (셀프호스트) | Temporal Cloud (사용량 과금) |
| Coolify | Apache 2.0 (셀프호스트) | Coolify Cloud (편의 구독) |
| Dokploy | MIT (현재 전부 무료) | 향후 SaaS 가능성 |
| Camunda 8 / Zeebe | Camunda License 2024 — 비상업/평가 무료, 자체 비즈니스 production 사용 제한 | Self-Managed/Cloud 유료 |
| Dapr | Apache 2.0 | Diagrid Cloud(매니지드) |
| **Linkerd Stable** | Edge release 만 무료, **Stable 은 Buoyant Enterprise 구독 필요** (2024 정책 변경) | Buoyant Enterprise |

→ 학습/PoC 는 모두 무료. 운영의 stable·SLA·멀티리전이 필요할 때 비용 발생.

---

## 3. "오픈소스" 라고 부르지만 사용 제한이 있는 라이선스

OSI 인증을 잃었거나 BSL/SSPL 같은 **source-available** 로 변경된 그룹.

| 도구 | 실제 라이선스 | 의미 |
|------|---------------|------|
| HashiCorp Vault / Consul / Terraform | BUSL 1.1 (2023~) | 셀프호스트 OK. **HashiCorp 와 경쟁하는 SaaS 제공 금지** |
| Redis 7.4+ | AGPLv3 또는 RSALv2/SSPL 듀얼 (2024 변경) | 자체 사용 OK. SaaS 재판매 시 라이선스 비용 |
| MongoDB | SSPL | 동일. 매니지드 재판매 시 모든 인프라 코드 공개 의무 |
| Elasticsearch / Kibana 8.x+ | AGPL 또는 SSPL/Elastic 듀얼 | 동일 |
| Grafana Loki / Tempo 일부 | AGPLv3 | 셀프호스트 무료, SaaS 재판매에 부담 |

→ **본인이 직접 쓰는 한 모두 무료**. "이걸로 SaaS 만들어 팔겠다" 면 변호사 검토 필수.

---

## 4. 유료 / 셀프호스트도 라이선스 필요

| 도구 | 비용 |
|------|------|
| **Red Hat OpenShift** | 노드/소켓 구독제 (수천만 ~ 수억원/년) |
| OpenShift Local (CRC) | 개발자 PC 학습용 무료 (Red Hat Developer 가입) |
| **OKD** | OpenShift 의 OSS 버전 — **무료** (Red Hat 지원 없음) |
| Rancher | 코어는 Apache 2.0 무료, **SUSE Rancher Prime** 유료 지원 |
| KubeSphere | Apache 2.0 무료, KubeSphere Enterprise 유료 |
| GitHub Actions | 퍼블릭 저장소 무료, 프라이빗은 분당 과금(월 무료 한도) |
| GitLab | CE 무료, EE 유료 |

→ OpenShift 만 빼면 모두 셀프호스트 0원. OpenShift 도 OKD 로 무료 사용 가능 (지원만 없음).

---

## 5. 클라우드 매니지드 (시간당 과금)

오픈소스를 직접 운영하기 싫으면 클라우드가 해줌. 무료 아님.

| 서비스 | 매니지드 대상 |
|--------|---------------|
| EKS / GKE / AKS | Kubernetes |
| Confluent Cloud / MSK / Aiven | Kafka |
| RDS / Aurora / Cloud SQL | Postgres |
| Grafana Cloud | LGTM |
| Auth0 / Cognito | OIDC (Keycloak 대체) |
| Temporal Cloud | Temporal |
| HCP Vault | Vault |

→ "운영 인력이 없으면" 비용 < 인건비 라 합리적. 학습·소규모는 셀프호스트가 답.

---

## 6. 비용 시나리오

### A. 완전 무료 학습 (개인 노트북)
- k3d + Online Boutique + Linkerd Edge + kube-prometheus-stack + Loki
- **0원**

### B. VPS 1대 (월 2~3만원)
- Hetzner / Vultr VPS + Coolify 또는 Dokploy + 본인 앱
- **VPS 비용만**, 소프트웨어 0원

### C. 작은 회사, K8s 자가 운영
- Kubespray 셀프 K8s + Rancher + ArgoCD + Strimzi + Keycloak + Vault OSS
- **하드웨어/IDC 비용만**, 라이선스 0원
- 운영 인력 1명 인건비가 사실상의 비용

### D. 엔터프라이즈
- OpenShift 또는 Rancher Prime + Red Hat SSO + Confluent + Grafana Cloud + Backstage
- 노드 수에 따라 **수천만 ~ 수억원/년**

---

## 7. 한 줄 정리

> **학습·개인·소규모는 거의 다 0원.**
> 돈은 (1) 클라우드 매니지드 사용료, (2) 엔터프라이즈 SLA·기술지원, (3) 일부 도구의 stable/Enterprise 기능 — 이 세 군데서만 발생.
> 대표적 유료 라인: **OpenShift / Confluent Cloud / Temporal Cloud / Grafana Cloud / Auth0**.
> 라이선스 변경 트렌드는 *"셀프호스트는 무료, 우리와 경쟁하는 SaaS 만 막는다"* — 일반 사용자에게 영향 없음.
