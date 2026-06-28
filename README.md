# Rate Limiter Reverse Proxy

A Spring Boot-based reverse proxy that enforces per-client rate limits using a sliding window algorithm backed by Redis, and exposes real-time metrics to Prometheus and Grafana.

This repository currently contains **Phase 1 – Reverse Proxy**. Later phases will incrementally add rate limiting logic, Redis integration, observability, and hardening.

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
├── main/
│   ├── java/
│   │   └── com/uddharsh/ratelimiterproxy/
│   │       ├── RateLimiterProxyApplication.java
│   │       ├── config/
│   │       │   └── AppConfig.java              // RestClient bean wired to upstream.base-url
│   │       ├── upstream/
│   │       │   ├── UpstreamController.java      // GET /api/users, GET /api/orders
│   │       │   ├── UpstreamDataService.java     // static in-memory data for demo endpoints
│   │       │   └── models/
│   │       │       ├── User.java
│   │       │       └── Order.java
│   │       └── proxy/
│   │           ├── ProxyController.java         // /proxy/** entrypoint; delegates to ProxyService
│   │           └── ProxyService.java            // strips /proxy prefix, forwards via RestClient
│   └── resources/
│       └── application.properties              // upstream.base-url=http://localhost:8080
└── test/
    └── java/
        └── com/uddharsh/ratelimiterproxy/
            ├── RateLimiterProxyApplicationTests.java   // context loads
            └── proxy/
                └── ProxyServiceTest.java               // unit tests for ProxyService
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

---

## Phase 1 – Reverse Proxy (current state)

Phase 1 adds a basic HTTP reverse proxy in front of a demo upstream controller.

**Upstream endpoints (direct):**

- `GET /api/users` – returns a static list of users
- `GET /api/orders` – returns a static list of orders

**Proxy endpoints (forwarded):**

- `GET /proxy/api/users` – proxy strips the `/proxy` prefix, forwards to `/api/users` upstream, and relays the response
- `GET /proxy/api/orders` – same forwarding for orders

The forwarding logic in `ProxyService`:
1. Reads the HTTP method and request URI from the incoming `HttpServletRequest`
2. Strips the `/proxy` prefix (e.g. `/proxy/api/users` → `/api/users`)
3. Calls the upstream via `RestClient.method(HttpMethod).uri(upstreamPath).retrieve().toEntity(String.class)`
4. Returns the upstream `ResponseEntity` (status + body) directly to the client

### Running Phase 1

The upstream base URL is already configured to point to a different port on the local system:

```properties
upstream.base-url=http://localhost:9000
```

Start the app and try the proxy:

```bash
# Terminal 1 - proxy on 8080
./mvnw spring-boot:run

# Terminal 2 - upstream on 9000
./mvnw spring-boot:run -Dspring-boot.run.profiles=upstream

# Terminal 3 - test
curl http://localhost:8080/proxy/api/users
# -> [{"id":"1","name":"Alice"},{"id":"2","name":"Bob"}]
curl http://localhost:8080/proxy/api/orders
# -> [{"id":"1","description":"First Test Order","status":"In Stock"},{"id":"2","description":"Second Test Order","status":"Sold Out"}]
```

Each forwarded call is logged as:

```
GET -> /api/users
GET -> /api/orders
```

### Testing

`ProxyServiceTest` covers three behaviours using Mockito mocks for `RestClient` and `HttpServletRequest` — no Spring context required:

| Test | What it verifies |
|---|---|
| `forwardRequest_stripsProxyPrefixAndCallsUpstream` | `/proxy/api/users` is rewritten to `/api/users`; upstream body is relayed back with a 2xx status |
| `forwardRequest_usesDynamicHttpMethod` | A `POST` request is forwarded as `POST`, not hardcoded to `GET`; a 201 upstream response is passed through |
| `forwardRequest_relaysNon200StatusFromUpstream` | A 404 from the upstream is relayed to the client unchanged |

Run the tests:

```bash
./mvnw test
```

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

1. **Phase 1 – Reverse Proxy** ✅ Forward HTTP requests to an upstream service
2. **Phase 2 – In-Memory Rate Limiter** – Per-client sliding window rate limiting, single instance
3. **Phase 3 – Redis Rate Limiter** – Distributed rate limiting with atomic Redis operations
4. **Phase 4 – Observability** – Prometheus metrics and Grafana dashboards
5. **Phase 5 – Hardening** – Exception handling, security, CI, and final documentation
