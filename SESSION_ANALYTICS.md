# Session Analytics API Documentation

This document describes the session analytics endpoints and the data model, to guide SDK development and integration.

---

## Session Model

A session represents a user's visit to your website. It tracks page views, bounce rate, session duration, and more.

**Fields:**
- `id` (UUID): Unique session identifier
- `ipAddress` (String): Visitor's IP address
- `userAgent` (String): Visitor's browser/device info
- `startTime` (LocalDateTime): When the session started
- `lastActivityTime` (LocalDateTime): Last activity timestamp
- `active` (boolean): Whether the session is currently active
- `referrer` (String): Referring URL (if any)
- `entryPage` (String): First page visited
- `exitPage` (String): Last page visited
- `pageViews` (Integer): Number of pages viewed in this session
- `bounceCount` (Integer): 1 if session is a bounce (only one page view), else 0
- `sessionDuration` (Long): Duration in seconds (set when session ends)
- `user` (User): Associated user (if logged in)

---

## API Endpoints

### 1. Start Session
**POST** `/api/sessions/start`

**Request Body:**
```json
{
  "ipAddress": "1.2.3.4",
  "userAgent": "Mozilla/5.0 ..."
}
```
**Response:**
```json
{
  "id": "...",
  "ipAddress": "1.2.3.4",
  "userAgent": "Mozilla/5.0 ...",
  "startTime": "2024-05-17T10:00:00",
  "lastActivityTime": "2024-05-17T10:00:00",
  "active": true,
  ...
}
```

---

### 2. Increment Page View
**POST** `/api/sessions/pageview`

**Request Body:**
```json
{
  "sessionId": "...",
  "pageUrl": "/about"
}
```
**Response:**
```json
{
  "id": "...",
  "pageViews": 2,
  "lastActivityTime": "2024-05-17T10:01:00",
  "exitPage": "/about",
  ...
}
```

---

### 3. End Session
**POST** `/api/sessions/end`

**Request Body:**
```json
{
  "sessionId": "...",
  "lastPageUrl": "/contact"
}
```
**Response:**
```json
{
  "id": "...",
  "active": false,
  "sessionDuration": 300,
  "bounceCount": 0,
  "exitPage": "/contact",
  ...
}
```

---

## How It Works (SDK Flow)

1. **On first page load:**
    - Call `/api/sessions/start` with IP and user agent.
    - Store returned `sessionId` in local storage or cookie.
2. **On every page view/route change:**
    - Call `/api/sessions/pageview` with `sessionId` and current page URL.
3. **On tab close or leave:**
    - Call `/api/sessions/end` with `sessionId` and last page URL.
4. **Backend automatically tracks:**
    - Page views, session duration, bounce rate, entry/exit pages, and timestamps.

---

## Example Session Table (after 3 visitors)

| id (UUID) | startTime           | lastActivityTime    | pageViews | bounceCount | sessionDuration | entryPage | exitPage | active |
|-----------|---------------------|---------------------|-----------|-------------|-----------------|-----------|----------|--------|
| ...       | 2024-05-17 10:00:00 | 2024-05-17 10:05:00 | 3         | 0           | 300             | /         | /about   | false  |
| ...       | 2024-05-17 10:01:00 | 2024-05-17 10:01:30 | 1         | 1           | 30              | /         | /        | false  |
| ...       | ...                 | ...                 | ...       | ...         | ...             | ...       | ...      | ...    |

---

**This file is your reference for how session analytics work in your backend and how to integrate with a frontend SDK.** 