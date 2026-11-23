# SaintStream API

SaintStream film platforması üçün Spring Boot ilə yazılmış REST API. Auth hissəsi **JWT + Refresh Token** mexanizmasına əsaslanır: qeydiyyat, giriş, token yeniləmə, çıxış və qorunan endpointlər.

## Texnologiyalar

| Sahə | Texnologiya |
|---|---|
| Dil | Java 17 |
| Framework | Spring Boot 4 (Web MVC, Security, Data JPA) |
| Verilənlər bazası | MySQL 8 |
| Şifrə | BCrypt (Spring Security Crypto) |
| Token | JJWT 0.12 (jsonwebtoken) |
| Sənədləşmə | springdoc-openapi (Swagger UI) |
| Build | Gradle |
| Köməkçi | Lombok |

## Token mexanizmi (Frontend üçün)

Login və ya register-dən sonra API iki token qaytarır:

| | Access token | Refresh token |
|---|---|---|
| Nə üçündür? | qorunan endpointlərə sorğu göndərmək | yeni access token almaq |
| Ömrü | 15 dəqiqə | 7 gün |
| Harada göndərilir? | `Authorization: Bearer {access_token}` header-i | yalnız `/refresh-token` və `/logout` body-sində |

**Niyə iki token?** Access token qısa ömürlüdür: oğurlansa, yalnız 15 dəqiqə istifadə oluna bilər. İstifadəçinin hər 15 dəqiqədən bir login olmaması üçün isə uzun ömürlü refresh token var. O, bazada saxlanılır və server onu istənilən vaxt ləğv edə bilir.

**Frontend necə istifadə etməlidir:**

1. Login və ya register cavabından `access_token` və `refresh_token`-i saxla.
2. Hər qorunan sorğuya header əlavə et: `Authorization: Bearer {access_token}`.
3. `401 Token expired` gələrsə, `POST /api/auth/refresh-token` sorğusuna `refresh_token` göndər.
4. Cavabda **yeni cüt** gəlir. Hər ikisini köhnələrinin yerinə yaz, çünki köhnə refresh token artıq işləmir (rotation). Sonra uğursuz sorğunu təkrar göndər.
5. Refresh də 401 qaytarsa, istifadəçini login səhifəsinə yönləndir.
6. Logout-da `POST /api/auth/logout` göndər, sonra **hər iki token-i öz yaddaşından sil**. Access token server tərəfdən ləğv edilmir, vaxtı bitənə qədər etibarlı qalır.

## Quraşdırma

**Tələblər:** Java 17+, MySQL 8, Git.

1. Repo-nu klonla:
   ```bash
   git clone https://github.com/babayevmehrali2001/saintstream-api.git
   cd saintstream-api
   ```
2. MySQL-də baza yarat və aşağıdakı cədvəlləri qur (bax: "Verilənlər bazası qurulumu").
3. Ana qovluqda `.env` faylı yarat (bax: "Mühit dəyişənləri").
4. Serveri işə sal:
   ```bash
   ./gradlew bootRun
   ```
5. Swagger-i aç: `http://localhost:1111/docs`

## Mühit dəyişənləri

```
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=
PORT=
JWT_SECRET=
JWT_EXPIRES_IN=
REFRESH_TOKEN_EXPIRES_DAYS=
```

`.env` faylı `.gitignore`-dadır və GitHub-a göndərilmir.

## Verilənlər bazası qurulumu

```sql
CREATE DATABASE IF NOT EXISTS saintstream;
USE saintstream;

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(20)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME     NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE password_reset_tokens (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME     NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

## Endpointlər

| Method | URL | Auth tələbi | Təsvir |
|---|---|---|---|
| POST | `/api/auth/register` | Yox | Qeydiyyat, token-ləri qaytarır |
| POST | `/api/auth/login` | Yox | Giriş, token-ləri qaytarır |
| POST | `/api/auth/refresh-token` | Yox (body-də `refresh_token`) | Yeni token cütü |
| POST | `/api/auth/logout` | Yox (body-də `refresh_token`) | Refresh token-i ləğv edir |
| GET | `/api/auth/me` | **Bəli** (Bearer) | Cari istifadəçi |
| GET | `/api/home/hero` | Yox | Hero filmləri |
| GET | `/api/home/platforms` | Yox | Platformalar |
| GET | `/api/home/featured` | Yox | Featured filmlər |
| GET | `/api/home/sections?section=&limit=` | Yox | Ana səhifə bölmələri |
| POST | `/api/watchlist` | **Bəli** (Bearer) | Filmi siyahıya əlavə edir |
| DELETE | `/api/watchlist/{contentId}` | **Bəli** (Bearer) | Filmi siyahıdan silir |
| GET | `/api/watchlist` | **Bəli** (Bearer) | İstifadəçinin siyahısı |

Bütün cavablar eyni formatdadır:
```json
{ "success": true, "message": "OK", "data": { } }
```

## Swagger

Server işləyəndə: **`http://localhost:1111/docs`**

Bütün endpointlərin request schema-ları, uğurlu cavab və xəta nümunələri oradadır. Qorunan endpointləri test etmək üçün **Authorize 🔒** düyməsinə bas və `access_token`-i yapışdır.

## Müəllif

**Mehrali Babayev**
- GitHub: [babayevmehrali2001](https://github.com/babayevmehrali2001)
- LinkedIn: [LINKEDIN_LINKI](https://www.linkedin.com/in/LINKEDIN_LINKI)
