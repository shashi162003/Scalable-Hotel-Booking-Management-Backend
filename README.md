# 🏨 Hotel Booking & Management System

> A scalable, production-ready REST API backend for hotel booking and management - built with Spring Boot and fully deployed on AWS.

---

## 🛠️ Tech Stack

<p>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security"/>
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate"/>
  <img src="https://img.shields.io/badge/Stripe-635BFF?style=for-the-badge&logo=stripe&logoColor=white" alt="Stripe"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger"/>
</p>

---

## ☁️ Deployment

<p>
  <img src="https://img.shields.io/badge/AWS_Elastic_Beanstalk-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white" alt="Elastic Beanstalk"/>
  <img src="https://img.shields.io/badge/AWS_RDS_(PostgreSQL)-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white" alt="RDS"/>
  <img src="https://img.shields.io/badge/AWS_S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white" alt="S3"/>
  <img src="https://img.shields.io/badge/AWS_CodePipeline-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white" alt="CodePipeline"/>
  <img src="https://img.shields.io/badge/AWS_CodeBuild-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white" alt="CodeBuild"/>
</p>

| Component | Service | Region |
|---|---|---|
| Application Server | AWS Elastic Beanstalk | `ap-south-1` |
| Relational Database | PostgreSQL on AWS RDS | `ap-south-1` |
| Build Artifact Storage | AWS S3 | `ap-south-1` |
| CI/CD Orchestration | AWS CodePipeline + CodeBuild | `ap-south-1` |

---

## 🔄 CI/CD Pipeline

Every push to the `main` branch triggers a fully automated build and deployment pipeline:

```
┌──────────────┐     ┌───────────────────┐     ┌────────────┐     ┌──────────────────────┐
│   Source      │────▶│    AWS CodeBuild   │────▶│   AWS S3   │────▶│  AWS Elastic         │
│   (GitHub)    │     │  (mvn clean pkg)  │     │ (artifact) │     │  Beanstalk (deploy)  │
└──────────────┘     └───────────────────┘     └────────────┘     └──────────────────────┘
```

1. **Source** — GitHub push to `main` triggers the pipeline automatically via CodePipeline
2. **Build** — CodeBuild runs `mvn clean package`, executes tests, and produces the deployable `.jar`
3. **Store** — The packaged artifact is stored in S3
4. **Deploy** — CodePipeline deploys the artifact to Elastic Beanstalk with zero manual steps

---

## 📖 Live API Documentation (Swagger UI)

🔗 **[http://airbnb-env.eba-tq7y6xxg.ap-south-1.elasticbeanstalk.com/api/v1/swagger-ui/index.html](http://airbnb-env.eba-tq7y6xxg.ap-south-1.elasticbeanstalk.com/api/v1/swagger-ui/index.html)**

## API Walkthrough Video

🔗 **[https://drive.google.com/file/d/1X6wRX-bmVE0L0NFz7qPuPhzGfbddjWD1/view?usp=sharing](https://drive.google.com/file/d/1X6wRX-bmVE0L0NFz7qPuPhzGfbddjWD1/view?usp=sharing)**

---

## 📋 Table of Contents

- [Features](#-features)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [API Reference](#-api-reference)
    - [Health Check](#health-check)
    - [Authentication](#authentication)
    - [Hotel Browse — Public](#hotel-browse--public)
    - [Bookings](#bookings)
    - [User](#user)
    - [Admin — Hotels](#admin--hotels)
    - [Admin — Rooms](#admin--rooms)
    - [Admin — Inventory](#admin--inventory)
    - [Webhooks](#webhooks)

---

## ✨ Features

- 🔐 **JWT Authentication** — Secure signup, login, and token refresh flow with Spring Security
- 🏨 **Hotel Management** — Full CRUD for hotels including amenities, photos, and contact info
- 🛏️ **Room Management** — Room types, base pricing, capacity, and photo management per hotel
- 📅 **Multi-Step Booking Engine** — Initialize → Add guests → Process payment → Confirm
- 💳 **Stripe Payments** — Payment processing with real-time webhook-based booking confirmation
- 📦 **Inventory Control** — Admin-level inventory management with surge pricing and date-range blackouts
- 📊 **Reporting** — Hotel-level booking reports filterable by date range
- 👤 **User Profiles** — Profile management and full booking history for guests
- 🔍 **Hotel Search** — Search hotels by city, date range, and room count with pagination

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/hotel/
│   │       ├── controllers/        # REST controllers (auth, booking, hotel, user, admin)
│   │       ├── services/           # Business logic layer
│   │       ├── repositories/       # JPA repositories
│   │       ├── entities/           # JPA entity models
│   │       ├── dto/                # Data Transfer Objects (request & response)
│   │       ├── security/           # JWT filter, Spring Security configuration
│   │       └── config/             # App-level configuration beans
│   └── resources/
│       └── application.properties
└── test/
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### Local Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/shashi162003/Scalable-Hotel-Booking-Management-Backend.git
   cd Scalable-Hotel-Booking-Management-Backend
   ```

2. **Set environment variables** (see [Environment Variables](#-environment-variables))

3. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

4. **Open Swagger UI**

   ```
   http://localhost:8080/api/v1/swagger-ui/index.html
   ```

---

## ⚙️ Environment Variables

| Variable | `application.properties` key | Description |
|---|---|---|
| `DB_HOST_URL` | `spring.datasource.url` | PostgreSQL host (e.g. `your-rds-instance.rds.amazonaws.com`) |
| `DB_NAME` | `spring.datasource.url` | Database name (e.g. `AirBnB`) |
| `DB_USERNAME` | `spring.datasource.username` | Database username |
| `DB_PASSWORD` | `spring.datasource.password` | Database password |
| `STRIPE_SECRET_KEY` | `stripe.secret.key` | Stripe secret API key |
| `STRIPE_WEBHOOK_SECRET` | `stripe.webhook.secret` | Stripe webhook signing secret (for verifying event payloads) |
| `FRONTEND_URL` | `frontend.url` | Allowed CORS origin URL |

> **Note:** `jwt.secretKey` is configured directly in `application.properties` and is not injected via an environment variable. Make sure to set a strong secret before deploying to production.

---

## 📡 API Reference

All endpoints are prefixed with `/api/v1`.

> 🔒 **[AUTH]** — Requires a valid `Bearer <token>` in the `Authorization` header
>
> 🛡️ **[ADMIN]** — Requires admin role privileges (implies AUTH)

---

### Health Check

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/` | — | Verify the server is up and running |

---

### Authentication

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/signup` | — | Register a new user account |
| `POST` | `/auth/login` | — | Login and receive access + refresh tokens |
| `POST` | `/auth/refresh` | — | Exchange a refresh token for a new access token |

#### `POST /auth/signup`

```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "name": "John Doe"
}
```

#### `POST /auth/login`

```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

---

### Hotel Browse — Public

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/hotels/search` | — | Search available hotels by city & dates |
| `GET` | `/hotels/{hotelId}/info` | — | Get public details of a specific hotel |

#### `GET /hotels/search` — Request Body

```json
{
  "city": "Mumbai",
  "startDate": "2025-03-01",
  "endDate": "2025-03-05",
  "roomsCount": 2,
  "page": 0,
  "size": 10
}
```

---

### Bookings

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/bookings/init` | 🔒 AUTH | Initialize a new booking |
| `POST` | `/bookings/{bookingId}/addGuests` | 🔒 AUTH | Attach guest details to a booking |
| `POST` | `/bookings/{bookingId}/payments` | 🔒 AUTH | Initiate Stripe payment for a booking |
| `POST` | `/bookings/{bookingId}/status` | 🔒 AUTH | Check or confirm booking status |
| `POST` | `/bookings/{bookingId}/cancel` | 🔒 AUTH | Cancel a booking |

#### Booking Flow

```
Step 1 → POST /bookings/init              Creates booking in PENDING state → returns bookingId
Step 2 → POST /bookings/{id}/addGuests    Attaches guest information to the booking
Step 3 → POST /bookings/{id}/payments     Creates Stripe PaymentIntent → returns clientSecret
Step 4 → [Client completes payment via Stripe]
Step 5 → POST /webhook/payment            Stripe notifies server → booking moves to CONFIRMED
```

#### `POST /bookings/init`

```json
{
  "hotelId": 1,
  "roomId": 3,
  "checkInDate": "2025-03-01",
  "checkOutDate": "2025-03-05",
  "roomsCount": 2
}
```

#### `POST /bookings/{bookingId}/addGuests`

```json
[
  {
    "name": "John Doe",
    "gender": "MALE",
    "age": 30
  },
  {
    "name": "Jane Doe",
    "gender": "FEMALE",
    "age": 28
  }
]
```

> `gender` accepts: `MALE` | `FEMALE` | `OTHER`

---

### User

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/users/profile` | 🔒 AUTH | Get the authenticated user's profile |
| `PATCH` | `/users/profile` | 🔒 AUTH | Update profile information |
| `GET` | `/users/myBookings` | 🔒 AUTH | List all bookings for the authenticated user |

#### `PATCH /users/profile`

```json
{
  "name": "John Doe",
  "dateOfBirth": "1995-06-15",
  "gender": "MALE"
}
```

> `gender` accepts: `MALE` | `FEMALE` | `OTHER`

---

### Admin — Hotels

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/admin/hotels` | 🛡️ ADMIN | List all hotels |
| `POST` | `/admin/hotels` | 🛡️ ADMIN | Create a new hotel |
| `GET` | `/admin/hotels/{hotelId}` | 🛡️ ADMIN | Get a specific hotel by ID |
| `PUT` | `/admin/hotels/{hotelId}` | 🛡️ ADMIN | Update hotel details |
| `DELETE` | `/admin/hotels/{hotelId}` | 🛡️ ADMIN | Delete a hotel |
| `PATCH` | `/admin/hotels/{hotelId}/activate` | 🛡️ ADMIN | Activate / publish a hotel listing |
| `GET` | `/admin/hotels/{hotelId}/bookings` | 🛡️ ADMIN | Get all bookings for a hotel |
| `GET` | `/admin/hotels/{hotelId}/reports` | 🛡️ ADMIN | Get booking reports (filterable by date range) |

#### `POST /admin/hotels` · `PUT /admin/hotels/{hotelId}`

```json
{
  "name": "The Grand Palace",
  "city": "Mumbai",
  "photos": ["https://cdn.example.com/photo1.jpg"],
  "amenities": ["WIFI", "POOL", "GYM", "PARKING"],
  "contactInfo": {
    "phone": "+91-9999999999",
    "email": "info@grandpalace.com",
    "address": "123 Marine Drive, Mumbai"
  },
  "active": true
}
```

#### `GET /admin/hotels/{hotelId}/reports` — Query Params

| Parameter | Type | Required | Description |
|---|---|---|---|
| `startDate` | `string` (date) | No | Filter results from this date |
| `endDate` | `string` (date) | No | Filter results until this date |

---

### Admin — Rooms

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/admin/hotels/{hotelId}/rooms` | 🛡️ ADMIN | List all rooms for a hotel |
| `POST` | `/admin/hotels/{hotelId}/rooms` | 🛡️ ADMIN | Add a new room to a hotel |
| `GET` | `/admin/hotels/{hotelId}/rooms/{roomId}` | 🛡️ ADMIN | Get a specific room |
| `PUT` | `/admin/hotels/{hotelId}/rooms/{roomId}` | 🛡️ ADMIN | Update room details |
| `DELETE` | `/admin/hotels/{hotelId}/rooms/{roomId}` | 🛡️ ADMIN | Delete a room |

#### `POST /admin/hotels/{hotelId}/rooms` · `PUT .../rooms/{roomId}`

```json
{
  "type": "DELUXE",
  "basePrice": 4999.00,
  "photos": ["https://cdn.example.com/room1.jpg"],
  "amenities": ["AC", "TV", "MINIBAR"],
  "totalCount": 10,
  "capacity": 2
}
```

---

### Admin — Inventory

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/admin/inventory/rooms/{roomId}` | 🛡️ ADMIN | View inventory for a room |
| `PATCH` | `/admin/inventory/rooms/{roomId}` | 🛡️ ADMIN | Update inventory pricing or availability |

#### `PATCH /admin/inventory/rooms/{roomId}`

```json
{
  "startDate": "2025-12-20",
  "endDate": "2025-12-31",
  "surgeFactor": 1.5,
  "closed": false
}
```

> `surgeFactor` multiplies the room's base price for the given date range (e.g., `1.5` = 50% price surge).
> Set `closed: true` to block room availability entirely for those dates.

---

### Webhooks

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/webhook/payment` | Stripe-Signature header | Handle Stripe payment events |

#### `POST /webhook/payment`

| Header | Description |
|---|---|
| `Stripe-Signature` | Stripe-generated HMAC signature for payload verification |

This endpoint is called directly by Stripe. On receiving a `payment_intent.succeeded` event, the server verifies the signature and automatically transitions the corresponding booking from `PENDING` to `CONFIRMED`.

---

## 📄 License

This project is licensed under the MIT License.
