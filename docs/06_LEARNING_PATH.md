# 06. 다시 시작하는 MSA — 4주 학습 경로

> 짝 문서: [`07_GLOSSARY.md`](./07_GLOSSARY.md) (용어 사전 — 본 문서 보기 전 한 번 훑기)
> 그 외 [`02_MODERN_MSA.md`](./02_MODERN_MSA.md), [`04_OPEN_SOURCE_BUNDLES.md`](./04_OPEN_SOURCE_BUNDLES.md)

대상: MSA 를 옛날에 했지만 중간에 멈춰서 개념·도구가 가물가물한 사람.
목적: **이론 책 X, 손이 먼저** 의 4주 코스로 감 회복.

---

## 0. 큰 원칙 4가지

1. **이론 먼저 X, 동작하는 것 먼저 O** — 5분 만에 뜨는 데모부터.
2. **하나의 실습 프로젝트를 4주 내내 발전** — 매주 새 프로젝트 시작하면 누적 안 됨. *카페 주문 시스템* 같은 작은 도메인 1개.
3. **"왜 필요한가" 를 먼저 통증으로 체감** — 관측성을 처음부터 깔지 말 것. 디버깅 짜증 한 번 겪고 나서 깔기.
4. **옛 지식은 자산** — Spring Cloud 한 게 헛수고가 아님. K8s 가 *왜* Eureka·Hystrix·Config Server 를 대체했는지 비교하면서 배우면 2배 빠름.

---

## 1. Week 0 — 기억 회복 (3시간, 하루)

문서·영상으로만, 손은 안 댐.

- **30분**: Martin Fowler 의 "Microservices" 글 (한국어 번역본 있음) 다시 한 번 읽기
- **30분**: microservices.io 의 패턴 카탈로그 *제목만* 훑기 (Saga, Outbox, CQRS, API Gateway, Service Discovery)
- **30분**: 본 폴더 [`07_GLOSSARY.md`](./07_GLOSSARY.md) 용어 사전 1회 통독 — 안 외워도 됨
- **1시간**: YouTube — "Kubernetes in 100 seconds" + "K8s in 10 minutes" 2개로 K8s 어휘(Pod/Service/Deployment) 익숙해지기
- **30분**: 본인의 옛 `multi-msa-study` README/yaml 다시 열어보고 *"이건 K8s 에선 어떻게 대체될까?"* 질문만 던지기 (답은 안 찾음)

> **목표**: "MSA = 서비스 잘게 쪼개고, 누가 어디 있는지 찾고, 같이 망하지 않게 격리하는 것" 이라는 한 줄 감각만 회복.

---

## 2. Week 1 — 동작하는 데모 1개

도구 깔기 전에 **남이 만든 완성품** 으로 굴러가는 모양을 본다.

```bash
# 도구 3개만 설치
brew install docker k3d kubectl

# 클러스터 1개 만들기
k3d cluster create study

# Online Boutique (구글 데모) 띄우기
kubectl apply -f \
  https://raw.githubusercontent.com/GoogleCloudPlatform/microservices-demo/main/release/kubernetes-manifests.yaml

kubectl get pods -w
kubectl port-forward svc/frontend-external 8080:80
# http://localhost:8080 → 쇼핑몰 동작
```

확인할 것:
- `kubectl get svc` — Eureka 같은 게 없는데 어떻게 서로 부름?
- `kubectl logs deploy/frontend` — 로그가 어디서 나오나?
- Pod 1개 강제로 죽이기 → 자동으로 다시 뜨는지 (`kubectl delete pod ...`)

> **목표**: K8s 가 옛날의 Eureka + 헬스체크 + 재시작 을 다 해준다는 사실을 *눈으로* 본다.

---

## 3. Week 2 — 본인 서비스 2개 직접 만들기

남의 데모로는 못 외워짐. **본인 손**으로.

### 도메인: "카페 주문 시스템"
- `order-service` (Spring Boot 3) — 주문 생성·조회
- `menu-service` (Spring Boot 3) — 메뉴 목록
- DB: Postgres × 2 (서비스마다 1개)

### 단계
1. 두 서비스를 그냥 Spring Boot 로 만든다 (DB 는 H2 임시)
2. 각각 `Dockerfile` 작성 → `docker build` → 로컬 이미지
3. K8s 매니페스트(`Deployment` + `Service`) 손으로 작성. **Helm 안 씀, 일부러**
4. `order-service` 가 `menu-service` 를 호출 → URL 은 `http://menu-service` 로만. *DNS 의 마법 체험*
5. Postgres 를 K8s 에 띄우고 (Bitnami helm chart 1줄) 두 서비스 분리 연결

### 일부러 안 하는 것
- 관측성 X / Service Mesh X / Helm X
- (Week 3 통증 체험을 위해)

> **목표**: K8s yaml 을 손으로 한 번 써본다. Helm 부터 가면 templating 이 영원히 미스터리.

---

## 4. Week 3 — 통증 → 해결 (관측성 + 복원력 + Helm)

### 통증 1: "어떤 요청이 어디서 느린지 모르겠다"
→ **관측성** 도입.

```bash
helm install monitoring prometheus-community/kube-prometheus-stack -n monitoring --create-namespace
```
- 본인 앱에 `actuator/prometheus` 노출
- Grafana 들어가서 RED(Rate/Error/Duration) 대시보드 보기
- OpenTelemetry 추가 → Tempo 에서 trace 가시화

### 통증 2: "menu-service 죽으니까 order-service 가 30초 멈췄다"
→ **Resilience4j** 도입.
- order-service 의 menu-service 호출에 CircuitBreaker + 2초 Timeout
- 일부러 menu-service 죽이고 order-service 가 빠르게 fallback 하는지 확인

### 통증 3: "yaml 파일이 4환경 × 3서비스 = 12개라 관리 불가"
→ **Helm** 도입.
- Week 2 의 yaml 을 `helm create` 로 chart 화
- `values-dev.yaml`, `values-prod.yaml` 분리

> **목표**: 도구 도입 *전후의 체감 차이* 를 본인 몸으로 안다. 가장 강력한 학습.

---

## 5. Week 4 — 이벤트 + GitOps

### 이벤트
- Kafka (Strimzi 오퍼레이터로 K8s 에) 1개 띄움
- `order-service` 가 주문 생성 시 → `order.created` 토픽에 이벤트 발행
- 새 서비스 `notification-service` 추가 → 이벤트 받아 콘솔에 "메일 보냈어요"
- **여기서 처음으로 "동기 호출 → 비동기 이벤트" 의 결합도 차이 체감**

### GitOps
- chart 들을 git repo 에 push
- ArgoCD 설치 (`kubectl apply -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml`)
- ArgoCD `Application` 1개로 git ↔ 클러스터 연결
- 이제 **git push 만 하면 배포 끝**

> **목표**: "MSA = 서비스 + Kafka + GitOps" 의 3대 축이 머리에 박힘.

---

## 6. 4주 후 셀프 점검표

- [ ] K8s 의 Pod / Service / Deployment / ConfigMap / Secret 차이 설명 가능
- [ ] `kubectl logs / describe / port-forward` 손에 익음
- [ ] Helm chart 1개 직접 작성 가능
- [ ] Prometheus 메트릭 1개 추가하고 Grafana 패널 만들 줄 앎
- [ ] OpenTelemetry trace 가 왜 가치 있는지 한 문장으로 설명 가능
- [ ] CircuitBreaker / Retry / Timeout 차이 알고 코드에서 어디 두는지 앎
- [ ] Kafka producer/consumer 1개씩 직접 쓸 수 있음
- [ ] ArgoCD 가 무엇을 자동화해주는지 5초 안에 답 가능

여기까지 오면 옛 `multi-msa-study` 를 *현대 스택으로 재구축* 할 준비 완료.

---

## 7. 좋은 학습 자료 (한·영 섞어)

### 책 (1권만)
- **"마이크로서비스 패턴" — Chris Richardson** (한국어판 있음). 처음부터 끝까지 X, *목차 보고 그때그때 필요한 챕터만*.

### 영상
- 한국어: 인프런 "처음부터 시작하는 마이크로서비스 (이도원)" — 옛 `multi-msa-study` 의 출처일 가능성. *복습용*.
- 한국어: 인프런 "쿠버네티스 기초" / "따배쿠" — Week 1 전 K8s 어휘 보충
- 영어: TechWorld with Nana — K8s/Mesh 입문 인기
- 영어: Marcel Dempers (devopsdirective) — K8s/Helm/ArgoCD 짧고 좋음

### 문서
- microservices.io (Chris Richardson) — 패턴 사전. 사전처럼 그때그때 검색
- kubernetes.io/docs/concepts — 어휘 정의용
- learnk8s.io — 그림 좋은 K8s 글

### 손으로 만지는 무료 환경
- KillerCoda (브라우저에서 K8s 클러스터 인터랙티브)
- Online Boutique (Week 1)
- spring-petclinic-microservices (Spring 익숙한 사람용 비교)

---

## 8. 흔히 망하는 패턴 (이렇게는 하지 말 것)

| 망하는 길 | 왜 망함 |
|----------|---------|
| K8s 책 1권 정독부터 | 600 페이지 — 50쪽에서 멈춤 |
| Istio 부터 깔기 | 너무 복잡. K8s 도 모르는 상태에서 Mesh 는 미궁 |
| 처음부터 마이크로서비스 5개 만들기 | 1개도 안 끝남. 2개로 시작 |
| 모든 도구 한 번에 깔기 | 어디서 깨졌는지 모름. *통증 → 해결* 순서 위반 |
| 옛 Spring Cloud 그대로 K8s 위에 올리기 | Eureka 와 K8s Service 충돌. K8s 위에선 Eureka 빼는 게 정답 |

---

## 9. 한 줄 결론

> **이론 X, 4주 동안 "카페 주문 시스템" 1개를 단계별로 진화**.
> Week 1 (남의 데모) → Week 2 (내 서비스 2개) → Week 3 (통증 → 관측성·복원력) → Week 4 (Kafka + ArgoCD).
> 끝나면 자연스럽게 옛 `multi-msa-study` 가 어떻게 현대화되어야 하는지 *체감* 으로 보임.
