# 二手交易平台部署指南

## 快速启动（本地开发）

### 1. 启动数据库
```bash
cd docker
docker-compose up -d mysql redis
```

### 2. 启动后端
```bash
cd backend
mvn spring-boot:run
```

### 3. 启动前端
```bash
cd frontend
npm run dev
```

访问：http://localhost:5173

## Docker一键部署

```bash
cd docker
docker-compose up -d
```

访问：http://localhost

## 默认账号

**普通用户**
- 用户名：testuser
- 密码：123456

**管理员**
- 用户名：admin
- 密码：admin123
- 后台地址：http://localhost/admin/login

## 技术栈

**后端**
- Spring Boot 2.7.18
- MyBatis Plus 3.5.5
- MySQL 8.0
- Redis 7.0
- JWT认证
- WebSocket

**前端**
- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router
- Axios