# 🎬 MovieHub API

> **AI 기반 영화 추천 서비스 백엔드 API**

MovieHub는 사용자의 취향과 감정 상태를 분석하여 맞춤형 영화를 추천하는 지능형 영화 추천 서비스입니다. TMDb API를 활용한 풍부한 영화 데이터와 다양한 추천 알고리즘을 통해 개인화된 영화 추천 경험을 제공합니다.

## 📋 목차

- [프로젝트 개요](#프로젝트-개요)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [아키텍처](#아키텍처)
- [설치 및 실행](#설치-및-실행)
- [API 문서](#api-문서)
- [사용 방법](#사용-방법)
- [데이터베이스 스키마](#데이터베이스-스키마)
- [추천 시스템](#추천-시스템)
- [보안](#보안)
- [배포](#배포)
- [기여하기](#기여하기)

## 🎯 프로젝트 개요

### 배경
현재 영화 추천 서비스들은 단순한 평점 기반 추천에 머물러 있어, 사용자의 개인적 취향과 감정 상태를 충분히 반영하지 못하고 있습니다. 또한 소셜 네트워크를 통한 협업 필터링의 한계로 인해 새로운 사용자나 장르에 대한 추천 정확도가 떨어지는 문제가 있습니다.

### 문제 정의
1. **개인화 부족**: 사용자의 감정 상태, 시간대, 상황을 고려하지 않은 추천
2. **콜드 스타트 문제**: 신규 사용자나 새로운 장르에 대한 추천 정확도 저하
3. **단조로운 추천**: 평점 기반의 단순한 추천으로 인한 다양성 부족
4. **실시간 트렌드 반영 부족**: 최신 영화 트렌드와 사용자 행동 패턴 미반영

### 솔루션
MovieHub는 다음과 같은 혁신적인 접근 방식을 통해 문제를 해결합니다:

- **다층 추천 시스템**: 콘텐츠 기반, 협업 필터링, 감정 기반 추천의 융합
- **실시간 감정 분석**: 리뷰 텍스트의 감정을 분석하여 상황별 추천
- **트렌드 기반 추천**: TMDb API를 통한 실시간 인기 영화 트렌드 반영
- **개인화된 사용자 프로필**: 장르 선호도, 평점 패턴, 감정 상태 학습

## ✨ 주요 기능

### 🔐 사용자 관리
- **회원가입/로그인**: JWT 기반 인증 시스템
- **사용자 프로필**: 개인화된 선호도 및 평점 패턴 관리
- **권한 관리**: 사용자 역할 기반 접근 제어

### 🎬 영화 관리
- **영화 데이터 동기화**: TMDb API를 통한 실시간 영화 정보 업데이트
- **영화 검색**: 제목, 장르, 출연진 기반 고급 검색
- **영화 상세 정보**: 포스터, 줄거리, 평점, 리뷰 등 종합 정보 제공

### ⭐ 리뷰 시스템
- **리뷰 작성/수정/삭제**: 사용자별 영화 리뷰 관리
- **감정 분석**: 리뷰 텍스트의 감정 상태 자동 분석
- **평점 시스템**: 1-5점 평점 및 상세 리뷰

### ❤️ 즐겨찾기
- **즐겨찾기 추가/제거**: 개인 영화 컬렉션 관리
- **즐겨찾기 기반 추천**: 선호 영화 패턴 분석

### 🧠 지능형 추천 시스템
- **콘텐츠 기반 추천**: 영화 메타데이터 기반 유사 영화 추천
- **협업 필터링**: 사용자 행동 패턴 기반 추천
- **감정 기반 추천**: 사용자 감정 상태에 맞는 영화 추천
- **트렌드 기반 추천**: 실시간 인기 영화 트렌드 반영

### 📊 트렌드 분석
- **인기 장르 분석**: 실시간 장르별 인기도 추적
- **트렌드 영화**: 시간대별 인기 영화 트렌드 제공
- **사용자 행동 분석**: 평점 패턴 및 선호도 변화 추적

## 🛠 기술 스택

### Backend
- **Java 17**: 메인 개발 언어
- **Spring Boot 3.2**: 웹 애플리케이션 프레임워크
- **Spring Security**: 인증 및 권한 관리
- **Spring Data JPA**: 데이터 접근 계층
- **Spring WebFlux**: 비동기 HTTP 클라이언트

### Database
- **PostgreSQL**: 메인 데이터베이스 (프로덕션)
- **H2 Database**: 개발/테스트용 인메모리 데이터베이스
- **Redis**: 캐싱 및 세션 관리 (선택사항)

### External APIs
- **TMDb API**: 영화 데이터 소스
- **감정 분석**: 텍스트 감정 분석 서비스

### Development Tools
- **Maven**: 빌드 도구
- **Docker & Docker Compose**: 컨테이너화
- **Swagger/OpenAPI 3**: API 문서화
- **Lombok**: 코드 간소화

### DevOps
- **GitHub Actions**: CI/CD 파이프라인
- **Docker**: 컨테이너 배포

## 🏗 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   API Gateway   │    │   External      │
│   (Web/Mobile)  │◄──►│   (Spring Boot) │◄──►│   APIs (TMDb)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   Business      │
                       │   Logic Layer   │
                       └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │   Data Access   │    │   Cache Layer   │
                       │   Layer (JPA)   │◄──►│   (Redis)       │
                       └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   Database      │
                       │   (PostgreSQL)  │
                       └─────────────────┘
```

### 계층 구조
1. **Controller Layer**: REST API 엔드포인트
2. **Service Layer**: 비즈니스 로직 처리
3. **Repository Layer**: 데이터 접근 계층
4. **Entity Layer**: 도메인 모델

## 🚀 설치 및 실행

### 사전 요구사항
- Java 17 이상
- Maven 3.6 이상
- Docker & Docker Compose (선택사항)

### 1. 저장소 클론
```bash
git clone https://github.com/Greenapple0101/movie_recommand.git
cd movie_recommand
```

### 2. 환경 설정

#### TMDb API 키 설정
`src/main/resources/application-local.yml` 파일에서 TMDb API 키를 설정하세요:

```yaml
external:
  tmdb:
    api-key: YOUR_TMDB_API_KEY
    base-url: https://api.themoviedb.org/3
    image-base-url: https://image.tmdb.org/t/p/w500
```

#### JWT 시크릿 키 설정
```yaml
jwt:
  secret: your-super-secret-jwt-key-here
  expiration: 86400000 # 24 hours
```

### 3. 로컬 실행 (H2 Database)

```bash
# Maven을 사용한 실행
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 또는 JAR 파일 빌드 후 실행
./mvnw clean package
java -jar target/moviehub-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

### 4. Docker를 사용한 실행

```bash
# Docker Compose로 전체 스택 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f
```

### 5. 접속 확인

- **웹 인터페이스**: http://localhost:8081/api/index.html
- **API 문서 (Swagger)**: http://localhost:8081/api/swagger-ui.html
- **H2 콘솔**: http://localhost:8081/api/h2-console
- **Health Check**: http://localhost:8081/api/actuator/health

## 📚 API 문서

### 인증 API
- `POST /api/auth/register` - 사용자 회원가입
- `POST /api/auth/login` - 사용자 로그인
- `POST /api/auth/refresh` - JWT 토큰 갱신

### 영화 API
- `GET /api/movies` - 영화 목록 조회 (페이징 지원)
- `GET /api/movies/{id}` - 특정 영화 상세 정보
- `GET /api/movies/search` - 영화 검색
- `GET /api/movies/popular` - 인기 영화 목록
- `GET /api/movies/trending` - 트렌딩 영화 목록

### 리뷰 API
- `POST /api/reviews` - 리뷰 작성
- `GET /api/reviews/movie/{movieId}` - 특정 영화의 리뷰 목록
- `GET /api/reviews/user/{userId}` - 특정 사용자의 리뷰 목록
- `PUT /api/reviews/{id}` - 리뷰 수정
- `DELETE /api/reviews/{id}` - 리뷰 삭제

### 즐겨찾기 API
- `POST /api/favorites` - 즐겨찾기 추가
- `DELETE /api/favorites/{id}` - 즐겨찾기 제거
- `GET /api/favorites/user/{userId}` - 사용자 즐겨찾기 목록

### 추천 API
- `GET /api/recommendations/content-based` - 콘텐츠 기반 추천
- `GET /api/recommendations/collaborative` - 협업 필터링 추천
- `GET /api/recommendations/mood-based` - 감정 기반 추천
- `GET /api/recommendations/trending` - 트렌드 기반 추천

### TMDb 동기화 API
- `POST /api/tmdb/sync/trending` - 트렌딩 영화 동기화
- `POST /api/tmdb/sync/popular` - 인기 영화 동기화
- `POST /api/tmdb/sync/search` - 검색 기반 영화 동기화

## 💻 사용 방법

### 1. 웹 인터페이스 사용

1. **회원가입**
   - 웹페이지에서 사용자명, 이메일, 비밀번호 입력
   - 회원가입 성공 시 JWT 토큰 자동 저장

2. **로그인**
   - 등록한 이메일과 비밀번호로 로그인
   - 로그인 성공 시 JWT 토큰 자동 저장

3. **영화 목록 보기**
   - "영화 목록 보기" 버튼 클릭
   - TMDb에서 동기화된 영화 목록 확인

4. **영화 검색**
   - 검색창에 영화 제목 입력
   - 실시간 검색 결과 확인

5. **리뷰 작성**
   - 영화 ID, 평점(1-5), 리뷰 내용 입력
   - 로그인 상태에서만 리뷰 작성 가능

### 2. API 직접 사용

#### 회원가입
```bash
curl -X POST "http://localhost:8081/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "testpass123"
  }'
```

#### 로그인
```bash
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "testpass123"
  }'
```

#### 영화 목록 조회
```bash
curl -X GET "http://localhost:8081/api/movies?page=0&size=20"
```

#### 리뷰 작성
```bash
curl -X POST "http://localhost:8081/api/reviews" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "movieId": "movie-uuid-here",
    "rating": 5,
    "content": "정말 재미있는 영화였어요!"
  }'
```

## 🗄 데이터베이스 스키마

### 주요 엔티티

#### User (사용자)
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Movie (영화)
```sql
CREATE TABLE movies (
    id UUID PRIMARY KEY,
    tmdb_id INTEGER UNIQUE,
    title VARCHAR(255) NOT NULL,
    original_title VARCHAR(255),
    overview TEXT,
    release_date DATE,
    poster_url VARCHAR(500),
    backdrop_url VARCHAR(500),
    vote_average DECIMAL(3,1),
    vote_count INTEGER,
    popularity DECIMAL(10,2),
    genres TEXT, -- JSON string
    runtime INTEGER,
    adult BOOLEAN DEFAULT FALSE,
    average_rating DECIMAL(3,1),
    review_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Review (리뷰)
```sql
CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    movie_id UUID REFERENCES movies(id),
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    content TEXT,
    sentiment VARCHAR(20),
    sentiment_score DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, movie_id)
);
```

#### Favorite (즐겨찾기)
```sql
CREATE TABLE favorites (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    movie_id UUID REFERENCES movies(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, movie_id)
);
```

## 🧠 추천 시스템

### 1. 콘텐츠 기반 추천
- **장르 유사성**: 사용자가 좋아하는 장르의 영화 추천
- **메타데이터 기반**: 감독, 출연진, 키워드 기반 유사 영화 찾기
- **평점 패턴**: 사용자의 평점 패턴과 유사한 영화 추천

### 2. 협업 필터링
- **사용자 기반**: 비슷한 취향의 사용자들이 좋아하는 영화 추천
- **아이템 기반**: 특정 영화를 좋아하는 사용자들이 좋아하는 다른 영화 추천
- **행렬 분해**: 사용자-아이템 평점 행렬을 이용한 잠재 요인 모델

### 3. 감정 기반 추천
- **리뷰 감정 분석**: 사용자 리뷰의 감정 상태 분석
- **상황별 추천**: 슬픔, 기쁨, 스트레스 등 감정 상태에 맞는 영화 추천
- **시간대 고려**: 시간대별 감정 상태 변화 반영

### 4. 트렌드 기반 추천
- **실시간 인기도**: TMDb API를 통한 실시간 인기 영화 반영
- **장르 트렌드**: 시간대별 인기 장르 분석
- **사용자 행동 트렌드**: 플랫폼 내 사용자 행동 패턴 분석

## 🔒 보안

### 인증 및 권한 관리
- **JWT 토큰**: Stateless 인증 방식
- **비밀번호 암호화**: BCrypt를 사용한 안전한 비밀번호 저장
- **역할 기반 접근 제어**: USER, ADMIN 역할 구분

### API 보안
- **CORS 설정**: 적절한 CORS 정책 적용
- **CSRF 보호**: CSRF 토큰을 통한 보호
- **입력 검증**: 모든 입력 데이터 검증 및 Sanitization

### 데이터 보호
- **개인정보 보호**: 사용자 개인정보 최소 수집 원칙
- **데이터 암호화**: 민감한 데이터 암호화 저장
- **접근 로그**: API 접근 로그 기록 및 모니터링

## 🚀 배포

### Docker 배포
```bash
# 이미지 빌드
docker build -t moviehub-api .

# 컨테이너 실행
docker run -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e TMDB_API_KEY=your_api_key \
  -e JWT_SECRET=your_jwt_secret \
  moviehub-api
```

### Docker Compose 배포
```bash
# 전체 스택 배포
docker-compose -f docker-compose.prod.yml up -d
```

### 환경 변수
```bash
# 필수 환경 변수
SPRING_PROFILES_ACTIVE=prod
TMDB_API_KEY=your_tmdb_api_key
JWT_SECRET=your_jwt_secret_key
DATABASE_URL=postgresql://user:pass@host:port/dbname
REDIS_URL=redis://host:port
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 개발 가이드라인
- **코드 스타일**: Google Java Style Guide 준수
- **커밋 메시지**: Conventional Commits 형식 사용
- **테스트**: 새로운 기능에 대한 단위 테스트 작성 필수
- **문서화**: API 변경사항에 대한 문서 업데이트

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 연락처

- **프로젝트 링크**: [https://github.com/Greenapple0101/movie_recommand](https://github.com/Greenapple0101/movie_recommand)
- **이메일**: yorange50@gmail.com

## 🙏 감사의 말

- [TMDb](https://www.themoviedb.org/) - 영화 데이터 제공
- [Spring Boot](https://spring.io/projects/spring-boot) - 웹 프레임워크
- [PostgreSQL](https://www.postgresql.org/) - 데이터베이스
- [Docker](https://www.docker.com/) - 컨테이너화

---

**MovieHub API**로 더 나은 영화 추천 경험을 만들어보세요! 🎬✨
