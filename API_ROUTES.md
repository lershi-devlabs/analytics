# API Routes Documentation

This document lists all available API endpoints in the application, grouped by controller, with example requests and responses.

---

## AuthController (`/api/auth`)

### POST `/api/auth/public/login`
Authenticate a user.

**Request Body:**
```json
{
  "username": "user1",
  "password": "password123"
}
```
**Response:**
```json
{
  "token": "<JWT_TOKEN>"
}
```

### POST `/api/auth/public/register`
Register a new user.

**Request Body:**
```json
{
  "username": "user1",
  "password": "password123",
  "email": "user1@example.com"
}
```
**Response:**
```
User registered successfully
```

---

## UrlMappingController (`/api/urls`)

### POST `/api/urls/shorten`
Create a short URL.

**Request Body:**
```json
{
  "originalUrl": "https://example.com"
}
```
**Response:**
```json
{
  "id": 1,
  "shortUrl": "abc123",
  "originalUrl": "https://example.com"
}
```

### GET `/api/urls/myurls`
Get all URLs for the authenticated user.

**Response:**
```json
[
  {
    "id": 1,
    "shortUrl": "abc123",
    "originalUrl": "https://example.com"
  }
]
```

### GET `/api/urls/analytics/{shortUrl}?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59`
Get analytics for a short URL between two dates.

**Response:**
```json
[
  {
    "clickDate": "2024-01-01T12:00:00",
    "ipAddress": "127.0.0.1",
    "userAgent": "Mozilla/5.0"
  }
]
```

### GET `/api/urls/totalClicks?startDate=2024-01-01&endDate=2024-01-31`
Get total clicks by date for the authenticated user.

**Response:**
```json
{
  "2024-01-01": 10,
  "2024-01-02": 5
}
```

---

## EventController (`/api/events`)

### POST `/api/events`
Record a generic event (not tied to a URL mapping).

**Request Body:**
```json
{
  "eventName": "Signup",
  "eventData": "testing signup"
}
```
**Response:**
```json
{
  "id": 1,
  "eventName": "Signup",
  "eventData": "testing signup"
}
```

---

## AnalyticsController (`/api/analytics`)

### GET `/api/analytics/top-pages?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59`
Get top pages by views.

**Response:**
```json
{
  "/home": 100,
  "/about": 50
}
```

### GET `/api/analytics/top-referrers?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59`
Get top referrers.

**Response:**
```json
{
  "google.com": 80,
  "twitter.com": 20
}
```

### GET `/api/analytics/device-breakdown?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59`
Get device breakdown.

**Response:**
```json
{
  "Desktop": {"Windows": 50, "Mac": 30},
  "Mobile": {"iOS": 10, "Android": 10}
}
```

### GET `/api/analytics/country-breakdown?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59`
Get country breakdown.

**Response:**
```json
{
  "US": {"Desktop": 40, "Mobile": 10},
  "IN": {"Desktop": 20, "Mobile": 10}
}
```

---

## RedirectController

### GET `/{shortUrl}`
Redirect to the original URL for a given short URL.

**Response:**
- HTTP 302 Redirects to the original URL if found.
- HTTP 404 if not found. 