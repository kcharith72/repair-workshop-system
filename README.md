# Community Repair Workshop — Microservices System

A distributed Spring Boot microservices system for managing community repair appointments
and technician scheduling.

---

## Architecture

```
Client → API Gateway (8080) → Appointment Service (8081)
                             → Technician Service (8082)

Supporting Services:
  Config Server  : 8888
  Eureka Server  : 8761
  RabbitMQ UI    : 15672
  Zipkin UI      : 9411
```

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker Desktop

---

## Quick Start

### Step 1 — Start Infrastructure (Docker)

```bash
docker-compose up -d
```

Verify containers are running:
```bash
docker ps
```

Expected containers: `appointment-db`, `technician-db`, `rabbitmq`, `zipkin`

### Step 2 — Verify Config Repository

The config repository must exist at `~/Desktop/repair-workshop-config` with at least one git commit.

```bash
ls ~/Desktop/repair-workshop-config
git -C ~/Desktop/repair-workshop-config log --oneline
```

### Step 3 — Build All Modules

```bash
cd ~/Desktop/repair-workshop-system
mvn clean install -DskipTests
```

### Step 4 — Start Services IN ORDER

Open a separate terminal for each service:

**Terminal 1 — Config Server (start first, wait 10s)**
```bash
cd config-server
mvn spring-boot:run
# Verify: http://localhost:8888/appointment-service/dev
```

**Terminal 2 — Eureka Server (start second, wait 10s)**
```bash
cd eureka-server
mvn spring-boot:run
# Verify: http://localhost:8761
```

**Terminal 3 — API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```

**Terminal 4 — Technician Service**
```bash
cd technician-service
mvn spring-boot:run
```

**Terminal 5 — Appointment Service**
```bash
cd appointment-service
mvn spring-boot:run
```

---

## Service URLs

| Service | URL | Purpose |
|---------|-----|---------|
| API Gateway | http://localhost:8080 | All client requests go here |
| Eureka Dashboard | http://localhost:8761 | Service registry |
| Config Server | http://localhost:8888 | Configuration |
| RabbitMQ UI | http://localhost:15672 | Messaging (guest/guest) |
| Zipkin UI | http://localhost:9411 | Distributed tracing |

---

## Testing the System

### 1. Register users

```bash
# Register admin
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","role":"ADMIN"}'

# Register regular user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"user123","role":"USER"}'
```

### 2. Login and get token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Copy the token from the response.

### 3. Create a technician (ADMIN only)

```bash
curl -X POST http://localhost:8080/api/technicians \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Smith",
    "email": "alice@workshop.com",
    "specialisation": "ELECTRONICS",
    "status": "AVAILABLE"
  }'
```

### 4. Create an appointment (triggers Feign call to Technician Service)

```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "customerEmail": "john@email.com",
    "itemDescription": "Broken laptop screen needs replacement",
    "repairCategory": "ELECTRONICS",
    "scheduledDate": "2027-01-15"
  }'
```

### 5. Complete an appointment (triggers RabbitMQ event)

```bash
curl -X PATCH "http://localhost:8080/api/appointments/1/status?status=COMPLETED" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Check Technician Service logs — you will see the event consumed.
Check RabbitMQ UI — http://localhost:15672 (guest/guest)

---

## Environment Variables (Optional)

| Variable | Default | Description |
|----------|---------|-------------|
| JWT_SECRET | repairWorkshopSuperSecret... | JWT signing key |
| DB_USERNAME | appt_user / tech_user | Database username |
| DB_PASSWORD | appt_pass / tech_pass | Database password |

---

## Resilience4J Demo

To demonstrate circuit breaker fallback:

1. Start all services normally and create an appointment (gets CONFIRMED)
2. Stop Technician Service (Ctrl+C in its terminal)
3. Create another appointment — response will be PENDING (fallback triggered)
4. Restart Technician Service
5. Create another appointment — back to CONFIRMED

---

## Repository Commit Strategy

See `COMMITS.md` for the full commit history strategy.
