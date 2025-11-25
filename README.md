# Weather App – Auth Service

This service handles authentication for the Weather App microservice architecture.  
It provides user registration, login with JWT cookies, logout, and user lookup for other microservices through both HTTP and RabbitMQ.
---

## Features

- User registration (username, password, email)
- Login returning JWT stored in HttpOnly cookie
- Logout that clears the authentication cookie
- JWT validation via a custom filter
- Role-based access (USER, ADMIN)
- RabbitMQ integration used by the Notification service:
    - Notification sends a `userId`
    - Auth Service retrieves the corresponding email
    - Auth Service returns the email through a response queue
- Internal HTTP endpoint as fallback (if RabbitMQ is not used)
- Stateless authentication

---

## Endpoints

### Auth
| Method | Path | Description |
|--------|------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login, sets JWT cookie |
| POST | `/auth/logout` | Clears the JWT cookie |

### Admin (requires ADMIN role)
| Method | Path | Description |
|--------|-------|-------------|
| GET | `/admin/users` | List all users |
| DELETE | `/admin/delete/{username}` | Delete a specific user |

### Internal (This endpoint is a fallback to be used by Notification service if RabbitMQ is not used)
| Method | Path | Description |
|--------|-------|-------------|
| GET | `/internal/users/{id}` | Returns the email of a user (used only if RabbitMQ is not used)|

---

## JWT Cookie (development settings)

- HttpOnly: true
- Secure: false
- SameSite: Lax
- Path: "/"
- Cookie name: `jwt`.

---

## JWT Cookie (production settings)

When frontend is served from a different domain (e.g. Vercel):

- HttpOnly: true
- Secure: true
- SameSite: None
- Path: "/"
- CORS must allow credentials
- Production frontend domain must be added to allowedOrigins

---

## Production Checklist

### Cookies
- [ ] Set `secure=true`
- [ ] Set `SameSite=None`
- [ ] Keep `HttpOnly=true`
- [ ] Use consistent cookie name: `jwt`

### CORS
- [ ] Add production frontend URL to `allowedOrigins`
- [ ] Keep `allowCredentials=true`

### JWT
- [ ] Review token expiration time

### Logging
- [ ] Use INFO in production
- [ ] Ensure no sensitive data is logged

---

## Notes

- This service is stateless: no session is stored on the server.
- All authentication is based on JWT inside an HttpOnly cookie.
- Notification Service can retrieve user emails through either RabbitMQ or the internal HTTP endpoint.