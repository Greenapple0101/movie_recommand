# 🎬 MovieHub API

> 콘텐츠 기업 맞춤 영화 추천 API 서비스

## 📋 프로젝트 개요

MovieHub API는 영화 데이터를 기반으로 검색·즐겨찾기·리뷰·추천·트렌드를 제공하는 백엔드 API 서비스입니다. 단순 CRUD 서비스를 넘어서 **개인화 경험·트렌드 반영·데이터 기반 인사이트**를 제공하는 차별화된 기능들을 구현했습니다.

## 🚀 주요 기능

### 기본 기능
- **영화 검색**: 제목, 장르, 평점, 상영시간 등 다양한 조건으로 영화 검색
- **즐겨찾기**: 사용자별 영화 즐겨찾기 관리
- **리뷰 시스템**: 평점 및 댓글 기반 리뷰 작성, 감정 분석
- **사용자 관리**: JWT 기반 인증 및 사용자 프로필 관리

### 차별화 기능
- **감정 기반 추천**: 사용자의 기분에 따른 영화 추천
- **취향 프로필 생성**: 사용자의 장르 선호도, 평점 패턴 분석
- **실시간 트렌드**: TMDb API 연동을 통한 인기 영화 트렌드
- **공동 리뷰**: 여러 사용자가 함께 작성하는 공동 감상평
- **감정 분석**: 리뷰 텍스트의 감정 분석 (긍정/부정/중립)

## 🛠 기술 스택

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: PostgreSQL 15, Redis 7
- **Security**: Spring Security, JWT
- **Documentation**: Swagger/OpenAPI 3
- **Containerization**: Docker, Docker Compose
- **Build Tool**: Maven

## 📁 프로젝트 구조

```
src/main/java/com/moviehub/
├── config/                 # 설정 클래스
├── controller/             # REST API 컨트롤러
├── dto/                   # 데이터 전송 객체
├── entity/                # JPA 엔티티
├── repository/            # 데이터 접근 계층
├── security/              # 보안 설정
├── service/               # 비즈니스 로직
└── MovieHubApplication.java
```

## 🚀 시작하기

### 1. 환경 요구사항

- Java 17+
- Docker & Docker Compose
- Maven 3.6+

### 2. 환경 변수 설정

```bash
# TMDb API 키 (https://www.themoviedb.org/settings/api)
export TMDB_API_KEY=your_tmdb_api_key_here

# JWT 시크릿 키
export JWT_SECRET=your_jwt_secret_key_here
```

### 3. Docker로 실행

```bash
# 프로젝트 클론
git clone <repository-url>
cd moviehub-api

# 환경 변수 설정
cp .env.example .env
# .env 파일에 TMDB_API_KEY와 JWT_SECRET 설정

# Docker Compose로 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f app
```

### 4. 로컬 개발 환경

```bash
# PostgreSQL과 Redis 실행
docker-compose up -d postgres redis

# 애플리케이션 실행
./mvnw spring-boot:run
```

## 📚 API 문서

애플리케이션 실행 후 다음 URL에서 Swagger UI에 접근할 수 있습니다:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API Docs**: http://localhost:8080/api/api-docs

## 🔐 인증

API는 JWT 기반 인증을 사용합니다.

### 사용자 등록
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "username": "username"
  }'
```

### 로그인
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 인증이 필요한 API 호출
```bash
curl -X GET http://localhost:8080/api/movies \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🎯 주요 API 엔드포인트

### 영화 관련
- `GET /api/movies` - 모든 영화 조회
- `GET /api/movies/search?query={query}` - 영화 검색
- `GET /api/movies/trending` - 트렌딩 영화
- `GET /api/movies/top-rated` - 높은 평점 영화
- `GET /api/movies/genre/{genre}` - 장르별 영화

### 리뷰 관련
- `POST /api/reviews` - 리뷰 작성
- `GET /api/reviews/movie/{movieId}` - 영화별 리뷰 조회
- `GET /api/reviews/user/{userId}` - 사용자별 리뷰 조회

### 즐겨찾기 관련
- `POST /api/favorites` - 즐겨찾기 추가
- `GET /api/favorites/user/{userId}` - 사용자 즐겨찾기 조회
- `DELETE /api/favorites/movie/{movieId}` - 즐겨찾기 제거

### 추천 관련
- `GET /api/recommendations/mood?mood={mood}` - 감정 기반 추천
- `GET /api/recommendations/content-based?movieId={movieId}` - 콘텐츠 기반 추천
- `GET /api/recommendations/social-based?userId={userId}` - 소셜 기반 추천

## 🗄 데이터베이스 스키마

### 주요 테이블
- **users**: 사용자 정보
- **movies**: 영화 정보
- **reviews**: 리뷰 및 평점
- **favorites**: 즐겨찾기
- **trends**: 트렌드 데이터
- **user_preferences**: 사용자 취향 분석
- **co_reviews**: 공동 리뷰
- **co_review_participants**: 공동 리뷰 참여자

## 🔧 개발 가이드

### 코드 스타일
- Java 17 문법 사용
- Lombok 활용
- Builder 패턴 적용
- RESTful API 설계 원칙 준수

### 테스트
```bash
# 단위 테스트 실행
./mvnw test

# 통합 테스트 실행
./mvnw verify
```

### 빌드
```bash
# JAR 파일 빌드
./mvnw clean package

# Docker 이미지 빌드
docker build -t moviehub-api .
```

## 📊 모니터링

### 헬스 체크
- **Health Check**: http://localhost:8080/api/actuator/health
- **Metrics**: http://localhost:8080/api/actuator/metrics

### 로그
```bash
# 애플리케이션 로그 확인
docker-compose logs -f app

# 데이터베이스 로그 확인
docker-compose logs -f postgres
```

## 🚀 배포

### AWS EC2 배포
```bash
# EC2 인스턴스에 Docker 설치
sudo yum update -y
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 애플리케이션 배포
git clone <repository-url>
cd moviehub-api
docker-compose up -d
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.

---

**MovieHub API** - 콘텐츠 기업을 위한 차별화된 영화 추천 서비스 🎬
