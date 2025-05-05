# SmartRoute API Service

## Overview

This API provides endpoints to create, retrieve, update, and delete trips and their respective route steps using data from the Google Directions API. Deployed on Azure.

---

## Base URL

```
https://smartrouteapiservice.azurewebsites.net/
```

---

## Endpoints

### 1. Get all trips

**Request**

```
GET /api/trips
```

**Sample**

```bash
curl https://smartrouteapiservice.azurewebsites.net/api/trips
```

---

### 2. Get trip by code

**Request**

```
GET /api/trips/code/{code}
```

**Example**

```bash
curl https://smartrouteapiservice.azurewebsites.net/api/trips/code/002147
```

---

### 3. Create a trip

**Request**

```
POST /api/trips
Content-Type: application/json
```

**Body**

```json
{
  "locations": ["Rome, Italy", "Paris, France", "Moscow, Russia"],
  "name": "European Tour"
}
```

**Example**

```bash
curl -X POST https://smartrouteapiservice.azurewebsites.net/api/trips \
     -H "Content-Type: application/json" \
     -d '{ "locations": ["Rome, Italy", "Paris, France"], "name": "Weekend Getaway" }'
```

---

### 4. Update a trip

**Request**

```
PUT /api/trips/code/{code}
Content-Type: application/json
```

**Body**

```json
{
  "locations": ["London, UK", "Edinburgh, UK"],
  "name": "UK Highlands"
}
```

**Example**

```bash
curl -X PUT https://smartrouteapiservice.azurewebsites.net/api/trips/code/002147 \
     -H "Content-Type: application/json" \
     -d '{ "locations": ["London, UK", "Edinburgh, UK"], "name": "UK Highlands" }'
```

---

### 5. Delete a trip

**Request**

```
DELETE /api/trips/code/{code}
```

**Example**

```bash
curl -X DELETE https://smartrouteapiservice.azurewebsites.net/api/trips/code/002147
```

> If deleted, example requests to this trip code will no longer work.

---

### 6. Get all trip steps

**Request**

```
GET /api/tripsteps
```

**Example**

```bash
curl https://smartrouteapiservice.azurewebsites.net/api/tripsteps
```

---

### 7. Get trip steps by code

**Request**

```
GET /api/tripsteps/code/{code}
```

**Example**

```bash
curl https://smartrouteapiservice.azurewebsites.net/api/tripsteps/code/002147
```
