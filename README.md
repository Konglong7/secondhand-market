# 二手交易平台

一个基于 Spring Boot + Vue 3 的全栈二手交易平台，类似闲鱼的功能实现。

## 项目简介

本项目是一个完整的二手交易平台系统，包含用户管理、商品发布、在线交易、实时聊天、评价系统等核心功能。

### 主要功能

- **用户系统**：注册/登录、个人信息管理、实名认证、收货地址管理
- **商品管理**：商品发布/编辑、图片上传、分类浏览、搜索筛选、收藏功能
- **交易系统**：下单购买、模拟支付、订单管理、发货收货、退款流程
- **聊天功能**：WebSocket实时通讯、消息记录、在线状态
- **评价系统**：订单评价、评分、回复评价
- **后台管理**：用户管理、商品审核、订单管理、数据统计

## 技术栈

### 后端技术

- **框架**：Spring Boot 2.7.18
- **数据库**：MySQL 8.0
- **缓存**：Redis 7.0
- **ORM**：MyBatis Plus 3.5.5
- **安全**：Spring Security + JWT
- **实时通讯**：WebSocket
- **工具库**：Hutool、Lombok

### 前端技术

- **框架**：Vue 3 + Vite
- **UI组件**：Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **HTTP客户端**：Axios
- **实时通讯**：Socket.io-client

### 部署方案

- **容器化**：Docker + Docker Compose
- **反向代理**：Nginx

## 项目结构

```
二手交易平台/
├── backend/                 # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/secondhand/market/
│   │       │       ├── controller/    # 控制器层
│   │       │       ├── service/       # 业务逻辑层
│   │       │       ├── mapper/        # 数据访问层
│   │       │       ├── entity/        # 实体类
│   │       │       ├── dto/           # 数据传输对象
│   │       │       ├── vo/            # 视图对象
│   │       │       ├── config/        # 配置类
│   │       │       ├── filter/        # 过滤器
│   │       │       ├── handler/       # 处理器
│   │       │       └── utils/         # 工具类
│   │       └── resources/
│   │           └── application.yml    # 配置文件
│   └── pom.xml
│
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/            # API接口
│   │   ├── assets/         # 静态资源
│   │   ├── components/     # 公共组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   ├── utils/          # 工具函数
│   │   ├── views/          # 页面组件
│   │   ├── App.vue         # 根组件
│   │   └── main.js         # 入口文件
│   ├── package.json
│   └── vite.config.js
│
├── database/               # 数据库脚本
│   └── init.sql           # 初始化SQL
│
├── docker/                # Docker配置
│   ├── docker-compose.yml
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   └── nginx.conf
│
└── README.md
```

## 快速开始

### 环境要求

- JDK 11+
- Node.js 16+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.6+

### 方式一：本地开发环境

#### 1. 数据库初始化

```bash
# 创建数据库并导入初始数据
mysql -u root -p < database/init.sql
```

#### 2. 启动后端

```bash
cd backend

# 修改配置文件 src/main/resources/application.yml
# 配置数据库连接信息和Redis连接信息

# 编译并启动
mvn clean package -DskipTests
java -jar target/market-backend-1.0.0.jar

# 或使用Maven直接运行
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

#### 3. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

### 方式二：Docker部署（推荐）

```bash
cd docker

# 启动所有服务
docker compose up -d

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f
```

服务访问地址：
- 前端：http://localhost
- 后端API：http://localhost:8080/api
- MySQL：localhost:3306
- Redis：localhost:6379

## 默认账号

### 管理员账号
- 用户名：`admin`
- 密码：`admin123`

### 测试用户
需要自行注册

## API文档

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

### 主要接口

#### 用户相关
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/info` - 更新用户信息

#### 商品相关
- `POST /api/product` - 发布商品
- `GET /api/product/list` - 商品列表
- `GET /api/product/{id}` - 商品详情
- `PUT /api/product/{id}` - 编辑商品
- `DELETE /api/product/{id}` - 删除商品

#### 订单相关
- `POST /api/order` - 创建订单
- `GET /api/order/buyer` - 买家订单列表
- `GET /api/order/seller` - 卖家订单列表
- `PUT /api/order/{id}/ship` - 发货
- `PUT /api/order/{id}/receive` - 确认收货

#### 其他接口
详见各Controller类的注释说明

## 核心功能说明

### 1. 用户认证

使用JWT Token进行身份认证，Token有效期为7天。

```java
// 请求头格式
Authorization: Bearer {token}
```

### 2. 文件上传

支持单文件和批量文件上传，文件存储在 `./uploads/` 目录。

```javascript
// 前端上传示例
const formData = new FormData()
formData.append('file', file)
await uploadFile(formData)
```

### 3. WebSocket聊天

实时聊天功能基于WebSocket实现。

```javascript
// 连接地址
ws://localhost:8080/api/ws/{userId}
```

### 4. 模拟支付

支持支付宝、微信、余额三种支付方式的模拟。

## 开发说明

### 后端开发规范

1. **分层架构**：Controller → Service → Mapper
2. **统一返回**：使用 `Result` 类封装响应
3. **异常处理**：使用 `@ControllerAdvice` 全局异常处理
4. **参数校验**：使用 `@Valid` 注解进行参数验证
5. **日志记录**：关键操作记录日志

### 前端开发规范

1. **组件化开发**：合理拆分组件
2. **状态管理**：使用Pinia管理全局状态
3. **路由守卫**：实现登录拦截
4. **错误处理**：统一的错误提示
5. **代码规范**：遵循Vue 3风格指南

## 数据库设计

### 核心表

1. `user` - 用户表
2. `user_address` - 收货地址表
3. `category` - 商品分类表
4. `product` - 商品表
5. `product_image` - 商品图片表
6. `order` - 订单表
7. `payment` - 支付记录表
8. `favorite` - 收藏表
9. `review` - 评价表
10. `message` - 聊天消息表
11. `notification` - 通知表
12. `admin_user` - 管理员表

详细表结构见 `database/init.sql`

## 常见问题

### 1. 后端启动失败

- 检查MySQL和Redis是否正常运行
- 检查配置文件中的数据库连接信息
- 确认端口8080未被占用

### 2. 前端无法访问后端

- 检查后端是否正常启动
- 检查Vite代理配置是否正确
- 查看浏览器控制台错误信息

### 3. 文件上传失败

- 检查 `./uploads/` 目录是否存在且有写权限
- 检查文件大小是否超过限制（默认10MB）

### 4. WebSocket连接失败

- 检查后端WebSocket配置
- 确认防火墙未阻止WebSocket连接

## 性能优化

1. **数据库优化**
   - 合理使用索引
   - 分页查询避免全表扫描
   - 使用Redis缓存热点数据

2. **接口优化**
   - 接口响应时间控制在500ms以内
   - 使用异步处理耗时操作
   - 实现接口限流防刷

3. **前端优化**
   - 图片懒加载
   - 路由懒加载
   - 组件按需引入

## 安全措施

1. **密码安全**：使用BCrypt加密存储
2. **JWT认证**：Token过期自动刷新
3. **SQL注入防护**：使用MyBatis Plus预编译
4. **XSS防护**：前端输入过滤
5. **接口限流**：防止恶意刷接口
6. **敏感信息脱敏**：日志中隐藏敏感数据

## 后续扩展

- [ ] 小程序版本
- [ ] App版本
- [ ] 直播功能
- [ ] 拍卖功能
- [ ] 积分系统
- [ ] 优惠券系统
- [ ] 物流跟踪
- [ ] 在线客服

## 参考资料

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Vue 3官方文档](https://cn.vuejs.org/)
- [Element Plus文档](https://element-plus.org/)
- [MyBatis Plus文档](https://baomidou.com/)

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎提Issue。

---

**开发时间**：2026年3月  
**版本**：v1.0.0
