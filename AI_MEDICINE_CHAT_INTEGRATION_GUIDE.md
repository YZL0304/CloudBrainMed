# AIMedicineChat 功能集成指南

## 集成完成概览

已成功将 AIMedicineChat 智能问诊功能集成到 CloudBrainMed 项目中。

## 📝 修改清单

### 后端修改 (ai-service)

#### 1. **pom.xml** - 添加依赖
- ✅ 添加 `spring-boot-starter-data-redis` - Redis 缓存支持
- ✅ 添加 `spring-ai-openai-spring-boot-starter` - Spring AI OpenAI 集成

#### 2. **Entity 层**
- ✅ `Medicine.java` - 药品实体，包含字段：
  - medicineId (主键)
  - name (药品名称)
  - spec (规格)
  - usage (用法用量)
  - indication (适应症)
  - attention (注意事项)
  - stock (库存)
  - price (价格)
  - createTime (创建时间)

#### 3. **Mapper 层**
- ✅ `MedicineMapper.java` - 药品数据库访问
  - extends BaseMapper<Medicine>
  - findByKeyword() - 按关键字查询药品

#### 4. **DTO 层**
- ✅ `MedicineQueryDto.java` - 患者查询请求对象
  - sessionId (会话标识)
  - question (医生提问)
  - medicineId (可选：指定药品ID)

#### 5. **VO 层**
- ✅ `MedicineAnswerVo.java` - AI 回答响应对象
  - answer (AI回答内容)
  - medicineName (药品名称)
  - usage (用法用量)
  - indication (适应症)
  - attention (注意事项)

#### 6. **Service 层**
- ✅ `AiMedicineService.java` - 服务接口
  - chatStream() - 流式返回 AI 回答
  - chat() - 非流式返回 AI 回答

- ✅ `AiMedicineServiceImpl.java` - 服务实现
  - 集成 Spring AI ChatClient
  - Redis 缓存聊天历史
  - 智能 prompt 工程化处理
  - SSE 流式输出支持

#### 7. **Controller 层**
- ✅ `AiMedicineController.java` - REST 控制器
  - POST `/api/ai/medicine/chat` - 流式问诊端点
  - 支持 Server-Sent Events (SSE) 协议

#### 8. **配置文件**
- ✅ `application.yml` - 应用配置
  ```yaml
  spring:
    redis:
      host: 127.0.0.1
      port: 6379
    ai:
      openai:
        api-key: ${OPENAI_API_KEY:sk-your-api-key}
        model: gpt-3.5-turbo
        base-url: https://api.openai.com/v1
  ```

### 前端修改 (vueFront)

#### 1. **页面组件**
- ✅ `src/pages/doctor/ai-medicine/Index.vue` - AI医药咨询页面
  - 聊天界面布局
  - 实时流式输出
  - 会话管理
  - 样式风格与系统统一

#### 2. **路由配置**
- ✅ `src/router/index.ts` - 添加路由
  ```typescript
  {
    path: '/doctor/ai-medicine',
    name: 'aiMedicine',
    component: () => import('@/pages/doctor/ai-medicine/Index.vue'),
  }
  ```

## 🔧 必需配置

### 环境变量设置 (已完成)

✅ **DeepSeek API 已配置**

在 `application.yml` 中已设置:
- **API Key**: `sk-86f5064268574a0ab487cb9a04fe5c5f`
- **API Base URL**: `https://api.deepseek.com`
- **Model**: `deepseek-chat`
- **Temperature**: `0.1` (稳定性优先)
- **Max Tokens**: `1024`

### 数据库初始化

需要在 PostgreSQL 中创建 `medicine` 表：

```sql
CREATE TABLE medicine (
    medicine_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    spec VARCHAR(255),
    usage TEXT,
    indication TEXT,
    attention TEXT,
    stock INTEGER,
    price DECIMAL(10, 2),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🚀 使用流程

### 1. 后端启动流程
```bash
# 进入后端目录
cd backEnd

# 编译并启动 ai-service
mvn clean install
./mvnw spring-boot:run -pl services/ai-service

# 或使用 docker
cd docker
docker-compose up ai-service
```

### 2. 前端访问
- 访问路径: `http://localhost:5173/doctor/ai-medicine`
- 或从医生端导航菜单访问

### 3. 使用功能
1. 输入病症名称或药品名称
2. 点击"发送查询"或按 Enter
3. AI 实时返回专业医药建议

## 📊 API 端点

### 药品查询流式接口
- **endpoint**: `POST /api/ai/medicine/chat`
- **请求体**:
  ```json
  {
    "sessionId": "session_1234567890",
    "question": "风寒感冒常用药?",
    "medicineId": null
  }
  ```
- **响应**: Server-Sent Events 流
  ```
  data:抗病毒口服液是一种中成药，具有清热解毒的功效...
  data:常用于感冒初期...
  ```

## 🔐 安全性考虑

1. **API Key 管理**
   - 使用环境变量而不是硬编码
   - 定期轮换 API Key
   - 限制 API Key 权限范围

2. **速率限制**
   - 建议在网关层添加流量控制
   - 单用户会话限制请求频率

3. **输入验证**
   - 前端已验证问题输入非空
   - 后端需添加更严格的参数验证

4. **会话隔离**
   - 使用 sessionId 隔离不同医生的对话
   - Redis 缓存设置 30 分钟过期时间

## ⚙️ 性能优化建议

1. **缓存策略**
   - 常见问题答案缓存
   - 药品数据定期预加载

2. **连接池配置**
   - 调整 Redis 连接池大小
   - 数据库连接池优化

3. **异步处理**
   - SSE 采用异步非阻塞模式
   - 支持多并发用户访问

## 📚 技术栈

- **后端框架**: Spring Boot 3.2.11 + Spring Cloud
- **API 智能**: Spring AI 1.0.0 + OpenAI GPT-3.5-turbo
- **数据库**: PostgreSQL
- **缓存**: Redis
- **ORM**: MyBatis Plus
- **前端框架**: Vue 3 + TypeScript + Vite

## 🐛 常见问题排查

### 问题1: 控制台报错 "ChatClient not found"
- **原因**: Spring AI 依赖未正确加载
- **解决**: 确保 OpenAI API Key 已设置，依赖已下载

### 问题2: Redis 连接失败
- **原因**: Redis 服务未启动或配置错误
- **解决**: 检查 Redis 服务状态，修正 application.yml 配置

### 问题3: SSE 连接超时
- **原因**: OpenAI API 响应缓慢或网络问题
- **解决**: 检查网络连接，调整超时时间（当前 300s）

### 问题4: 前端无法访问 API
- **原因**: 跨域问题或路由错误
- **解决**: 确保网关配置允许 `/api/ai/medicine/**` 路由

## 📞 支持信息

如果集成过程中遇到问题，请检查：
1. ✅ 所有依赖是否正确安装
2. ✅ 环境变量是否正确设置
3. ✅ 数据库表是否已创建
4. ✅ Redis 服务是否正常运行
5. ✅ OpenAI API 连接是否正常


