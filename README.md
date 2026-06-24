# Rate Limiter Reverse Proxy

A Spring Boot-based reverse proxy that enforces per-client rate limits using a sliding window algorithm backed by Redis, and exposes real-time metrics to Prometheus and Grafana.

This repository currently contains **Phase 0 – Project Bootstrap**. Later phases will incrementally add the reverse proxy core, rate limiting logic, Redis integration, observability, and hardening.

---

## Architecture

The system has four external players:

- **Client** – sends HTTP requests to the proxy
- **Proxy** – this Spring Boot application; intercepts requests, enforces rate limits, and forwards allowed requests upstream
- **Redis** – shared state store for rate limit counters across multiple proxy instances
- **Upstream Service** – the actual backend the client wants to reach

Inside the proxy, responsibilities are split into three layers:

- **HTTP layer** – controllers and filters that handle incoming requests and return responses
- **Business logic layer** – the rate limiter interface and proxy forwarding service
- **Infrastructure layer** – Redis configuration, metrics configuration, and security

---

## Tech Stack

| Tool | Role |
|---|---|
| Spring Boot 4.1.0 | Application framework for the proxy |
| Spring Web MVC | HTTP request handling and routing |
| Spring Actuator | Health and metrics endpoints |
| Redis | Distributed rate limit state with atomic operations |
| Prometheus | Scrapes and stores metrics from the proxy |
| Grafana | Visualizes throughput, throttling, and latency dashboards |

---

## Project Structure

```
src/
└── main/
    ├── java/
    │   └── com/uddharsh/ratelimiterproxy/
    │       └── RateLimiterProxyApplication.java
    └── resources/
        └── application.properties
```

---

## Running Phase 0

**Prerequisites:** Java 21, Maven (or use the included Maven wrapper)

```bash
git clone https://github.com/UddharshV/rate-limiter-proxy.git
cd rate-limiter-proxy
./mvnw spring-boot:run
```

Verify the app is healthy:

```
GET http://localhost:8080/actuator/health
-> {"status":"UP"}
```

No proxy, rate limiting, or custom endpoints exist yet — those are introduced in Phase 1 and beyond.

---

## Development Workflow

All work happens in feature branches and merges to `main` via pull request:

- `feature/phase-0-setup`
- `feature/phase-1-reverse-proxy`
- `feature/phase-2-inmemory-limiter`
- `feature/phase-3-redis-limiter`
- `feature/phase-4-observability`
- `feature/phase-5-hardening`

`main` is branch-protected — direct pushes are disabled.

---

## Roadmap

1. **Phase 1 – Reverse Proxy** – Forward HTTP requests to an upstream service
2. **Phase 2 – In-Memory Rate Limiter** – Per-client sliding window rate limiting, single instance
3. **Phase 3 – Redis Rate Limiter** – Distributed rate limiting with atomic Redis operations
4. **Phase 4 – Observability** – Prometheus metrics and Grafana dashboards
5. **Phase 5 – Hardening** – Exception handling, security, CI, and final documentation