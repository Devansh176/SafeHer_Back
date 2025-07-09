
# 🛡️ SafeHer Backend - Women's Safety App

This is the **backend** for the SafeHer mobile application, designed to enhance women’s safety through emergency support, location sharing, and AI-assisted chatbot guidance.

---

## 📦 Features

- 🔒 Secure user authentication with Firebase
- 📤 Contact and location data stored in PostgreSQL
- 🧠 Intelligent chatbot (Spring Boot REST API) with route and safety guidance
- 🗺️ Geoapify API integration for safe route fetching and geocoding
- 📡 Exposes RESTful endpoints for frontend consumption

---

## 🧰 Tech Stack

| Layer        | Technology                  |
|--------------|------------------------------|
| Framework    | Spring Boot (Java)           |
| Database     | PostgreSQL                   |
| Authentication | Firebase Authentication   |
| APIs         | Geoapify Routing & Geocoding |
| Deployment   | Docker (planned)             |

---

## ⚙️ Setup Instructions

### 1. 🔁 Clone Repository

```bash
git clone https://github.com/your-username/safeher-backend.git
cd safeher-backend
```

### 2. 🔧 Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/safeHer
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# CORS and API Key Config
spring.mvc.cors.allowed-origins=*
spring.mvc.cors.allowed-methods=GET,POST

# Geoapify API
geoapify.api.key=YOUR_GEOAPIFY_API_KEY
```

### 3. 🏁 Run the Backend

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Or with Maven installed:

```bash
mvn spring-boot:run
```

---

## 📂 Project Structure

```
safeher-backend/
├── controller/          # REST API endpoints (ChatBot, Route)
├── service/             # Business logic (GeoRouteService)
├── model/               # Request/response DTOs
├── resources/
│   └── application.properties
└── SafeHerApplication.java
```

---

## 🧪 Endpoints

### `/api/chatbot`

Handles chatbot messages.

**POST** `/api/chatbot`

```json
{
  "message": "Find safe route from Sitabuldi to VNIT"
}
```

### `/api/route`

**GET** `/api/route?startLat=21.1458&startLng=79.0882&endLat=21.1234&endLng=79.0123`

---

## 📌 Future Enhancements

- Docker-based deployment and CI/CD
- OpenAI/GPT API integration for smarter chatbot
- Admin dashboard with real-time reports
- Live traffic + crime overlays on map

---

## 🤝 Contributing

Pull requests are welcome. Please create an issue first for major features or refactors.
