# multi-msa-study — 진단 & 현대화 문서

이 폴더는 `multi-msa-study` 프로젝트의 현재 상태를 진단하고, "MSA 를 완벽하게 구축" 하기 위한 로드맵, 그리고 2026 년 표준 MSA 구축 방식을 정리한 문서 묶음이다.

| 문서 | 내용 |
|------|------|
| [`01_AS_IS_AND_ROADMAP.md`](./01_AS_IS_AND_ROADMAP.md) | 현행 분석(어떤 모듈이 있고 무엇이 빠졌는지) + Phase 0 ~ 5 로드맵 + 완성 체크리스트 |
| [`02_MODERN_MSA.md`](./02_MODERN_MSA.md) | 2026 기준 MSA 표준 스택 가이드 (런타임/메시징/관측성/배포/Saga) + 안티패턴 + 학습 순서 |
| [`03_COMPARISON.md`](./03_COMPARISON.md) | 옛 방식(Spring Cloud Netflix) ↔ 현대 방식(K8s + Mesh + GitOps) 항목별 비교 |
| [`04_OPEN_SOURCE_BUNDLES.md`](./04_OPEN_SOURCE_BUNDLES.md) | "이미 묶여있는" 오픈소스 — 학습 데모(Online Boutique), 플랫폼(OpenShift/Rancher/KubeSphere), PaaS(Coolify/Dokploy), 관측성/IDP 묶음 |
| [`05_LICENSING_AND_COSTS.md`](./05_LICENSING_AND_COSTS.md) | 라이선스/비용 — 완전 무료 vs Open-core vs BUSL/SSPL vs 유료(OpenShift/Confluent Cloud 등) + 비용 시나리오 4가지 |
| [`06_LEARNING_PATH.md`](./06_LEARNING_PATH.md) | 다시 시작하는 MSA 4주 학습 코스 — Week 0(기억 회복) → Week 4(이벤트+GitOps), 셀프 점검표, 흔히 망하는 패턴 |
| [`07_GLOSSARY.md`](./07_GLOSSARY.md) | 용어 사전 — Pod/Helm/Saga/Kafka/OTEL 등 80+ 용어를 "이건 이거고 이렇게 쓴다" 한 줄 정의. 학습 시작 전 한 번 통독 권장 |

## 1 분 요약

- **진단**: 본 프로젝트는 2021~2023 Spring Cloud Netflix 시대 골격(Eureka, Cloud Gateway, Config, JWT). **컨테이너화·관측성·복원력·이벤트 일관성·CI/CD 거의 모두 부재**.
- **현대 표준**: K8s 가 Discovery 를 대신, Service Mesh 가 mTLS·재시도 자동화, OpenTelemetry 가 관측성, Kafka + Saga 가 이벤트 일관성, ArgoCD 가 GitOps.
- **권장 첫 액션**: Spring Boot 3.3 + Java 21 업그레이드 → Dockerfile/compose → Prometheus + OTEL 관측성 — 이 세 가지만 마쳐도 "운영 가능 후보" 진입.

## 권장 읽기 순서

### A. 학습자 (개념 다시 잡고 싶은 사람)
1. [`07_GLOSSARY.md`](./07_GLOSSARY.md) — 용어 한 번 통독 (안 외워도 됨, "이건 이거" 감각만)
2. [`06_LEARNING_PATH.md`](./06_LEARNING_PATH.md) — 4주 코스 따라 손으로 만지기
3. [`03_COMPARISON.md`](./03_COMPARISON.md) — 옛 / 현대 매핑으로 머리 정리
4. [`02_MODERN_MSA.md`](./02_MODERN_MSA.md) / [`04_OPEN_SOURCE_BUNDLES.md`](./04_OPEN_SOURCE_BUNDLES.md) — 깊이 들어갈 때 사전처럼

### B. 이 프로젝트를 현대화하려는 사람
1. [`01_AS_IS_AND_ROADMAP.md`](./01_AS_IS_AND_ROADMAP.md) §5 체크리스트로 갭 확인
2. [`03_COMPARISON.md`](./03_COMPARISON.md) §1 비교표를 PR 템플릿에 옮겨 매주 1~2 항목 처리
3. [`02_MODERN_MSA.md`](./02_MODERN_MSA.md) §6 학습 순서로 부족한 개념 보완
4. [`05_LICENSING_AND_COSTS.md`](./05_LICENSING_AND_COSTS.md) — 도구 선택 시 라이선스 확인
