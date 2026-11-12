# Resource Reservation Booking

Five Spring Boot services for booking shared resources such as rooms, desks, and vehicles.

Clients talk only to the API gateway. Auth issues a JWT. Resource owns the catalog. Reservation checks that catalog, then stores the booking in its own database. Eureka is the service registry.

```
client
  -> api-gateway :8080
       -> auth-service        :8081   database auth_db
       -> resource-service    :8082   database resource_db
       -> reservation-service :8083   database reservation_db
  discovery-service :8761
```

Reservation calls resource-service before it confirms a booking. A resource cannot be double-booked. Back-to-back slots are allowed. A booking cannot start in the past or last longer than 24 hours.

## Run everything

From this folder:

```powershell
docker compose up --build
```

Postgres is published on host port **5433** so it does not collide with a local Postgres on 5432. The apps reach it inside Compose as `postgres:5432`.

Wait until the gateway is up, then open the registry at http://localhost:8761.

Default admin, created on first auth startup:

- username: `admin`
- password: `Admin@123`

Change `JWT_SECRET` and the admin password before any shared or deployed use. Every service that checks tokens must use the same secret.

The resource service seeds three bookable items when its table is empty: Conference Room A, Focus Desk 1, and Company Van.

## Swagger

After the five services are running, open http://localhost:8080/swagger-ui/index.html

Use the dropdown at the top to switch between **Auth**, **Resources**, and **Reservations**.

1. Open **Auth** and run `POST /api/auth/login` with `admin` / `Admin@123`.
2. Copy the `token` value from the response.
3. Click **Authorize**, paste the token, and confirm.
4. Switch to **Resources** or **Reservations** and use **Try it out**.

## Try the API

Use PowerShell. All of these go through the gateway on port 8080.

```powershell
Invoke-RestMethod http://localhost:8080/api/resources

$admin = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/login -ContentType "application/json" -Body '{"username":"admin","password":"Admin@123"}'

Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/register -ContentType "application/json" -Body '{"username":"ada","password":"Secret@12"}'

$user = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/login -ContentType "application/json" -Body '{"username":"ada","password":"Secret@12"}'

Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/reservations -Headers @{Authorization="Bearer $($user.token)"} -ContentType "application/json" -Body '{"resourceId":1,"startTime":"2026-11-04T10:00:00","endTime":"2026-11-04T11:00:00","purpose":"Sprint planning"}'
```

Times are local date-times with no timezone. A second booking of resource 1 from 10:30 to 11:30 returns 409. Cancelling is `POST /api/reservations/{id}/cancel`.

## API

| Method | Path | Who |
| --- | --- | --- |
| POST | `/api/auth/register` | public, creates a USER |
| POST | `/api/auth/login` | public, returns a Bearer token |
| GET | `/api/auth/me` | signed in |
| GET | `/api/resources` | public |
| GET | `/api/resources/{id}` | public |
| POST, PUT, DELETE | `/api/resources` | ADMIN. DELETE deactivates the resource |
| POST | `/api/reservations` | signed in |
| GET | `/api/reservations/me` | signed in |
| GET | `/api/reservations/resource/{resourceId}` | signed in. Other people's purpose is hidden |
| GET | `/api/reservations/{id}` | owner or ADMIN |
| POST | `/api/reservations/{id}/cancel` | owner or ADMIN |
| GET | `/api/reservations` | ADMIN |

Passwords need 8 or more characters, with a letter, a number, and one of `@ $ ! % * ? &`.

## Database login

Put the Postgres username, password, and JWT secret in `.env` at the project root. Each service reads that file when it starts. You do not need to paste the password into the terminal.

```
DB_USERNAME=postgres
DB_PASSWORD=your-postgres-password
JWT_SECRET=change-this-to-a-long-secret-key-at-least-32-characters
JWT_EXPIRATION=3600000
```

Auth uses `auth_db`, resources use `resource_db`, and reservations use `reservation_db` on `localhost:5432`. A `.env` inside a service folder can set `DB_URL` for that service only. Real environment variables, including Docker Compose, override `.env`.

## Run from the IDE

Start discovery-service, then auth, resource, and reservation, then the gateway.

Each service has its own Maven project:

- `discovery-service`
- `Auth-service/Auth-service`
- `Resource-service/Resource-service`
- `Reservation-service/Reservation-service`
- `api-gateway/api-gateway`
