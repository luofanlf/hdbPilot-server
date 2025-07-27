# FlashJudge Server

## 项目简介
FlashJudge Server 是一个基于 Spring Boot 的后端服务项目，使用 MyBatis-Plus 作为 ORM 框架，提供用户管理、认证授权等功能。

## 技术栈
- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL
- **ORM**: MyBatis-Plus
- **构建工具**: Maven
- **Java版本**: JDK 21

## 项目结构

```
src/main/java/com/iss/hdbPilot/
├── annotation/          # 自定义注解
│   └── AuthCheck.java   # 权限检查注解
├── aop/                 # 切面编程
│   └── AuthInterceptor.java  # 认证拦截器
├── common/              # 公共类
│   ├── BaseResponse.java      # 统一响应格式
│   ├── MyMetaObjectHandler.java  # MyBatis-Plus 元数据处理器
│   └── ResultUtils.java       # 结果工具类
├── config/              # 配置类
│   ├── CorsConfig.java        # 跨域配置
│   └── MybatisPlusConfig.java # MyBatis-Plus 配置
├── controller/          # 控制器层
│   └── UserController.java    # 用户控制器
├── exception/           # 异常处理
│   └── GlobalExceptionHandler.java  # 全局异常处理器
├── mapper/              # 数据访问层
│   └── UserMapper.java        # 用户数据访问接口
├── model/               # 数据模型
│   ├── dto/            # 数据传输对象
│   │   ├── PageRequest.java        # 分页请求
│   │   ├── UserLoginRequest.java   # 用户登录请求
│   │   └── UserRegisterRequest.java # 用户注册请求
│   ├── entity/         # 实体类
│   │   └── User.java           # 用户实体
│   ├── enums/          # 枚举类
│   └── vo/             # 视图对象
│       └── UserVO.java         # 用户视图对象
├── service/             # 业务逻辑层
│   ├── impl/           # 业务实现类
│   │   └── UserServiceImpl.java    # 用户服务实现
│   └── UserService.java            # 用户服务接口
└── hbdPilotApplication.java         # 主启动类
```

## 开发规范

### 1. 统一响应格式 (BaseResponse)

所有API接口必须返回统一的响应格式：

```java
@Data
public class BaseResponse<T> implements Serializable {
    private int code;      // 响应码：0-成功，其他-失败
    private T data;        // 响应数据
    private String message; // 响应消息
}
```

**响应示例：**
```json
// 成功响应
{
    "code": 0,
    "data": {...},
    "message": "ok"
}

// 失败响应
{
    "code": -1,
    "data": null,
    "message": "错误信息"
}
```

**使用 ResultUtils 构建响应：**
```java
// 成功响应
return ResultUtils.success(data);

// 失败响应
return ResultUtils.error("错误信息");
return ResultUtils.error(500, "服务器内部错误");
```

### 2. 异常处理规范

#### 2.1 全局异常处理器
项目使用 `@RestControllerAdvice` 进行全局异常处理：

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(e.getMessage());
    }
}
```

#### 2.2 异常抛出规范
- **业务异常**: 直接抛出 `RuntimeException`，全局异常处理器会自动捕获
- **参数校验**: 在 Controller 层进行基础校验，抛出明确的异常信息
- **数据库异常**: 在 Service 层处理，转换为业务异常

**示例：**
```java
// Controller 层参数校验
if (loginRequest == null) {
    throw new RuntimeException("Request body is null");
}

// Service 层业务校验
if (user == null) {
    throw new RuntimeException("用户不存在");
}
```

### 3. DTO (Data Transfer Object) 规范

DTO 用于接收前端请求参数，位于 `model/dto` 包下。

#### 3.1 命名规范
- 请求DTO: `XxxRequest.java`
- 分页请求: `PageRequest.java`

#### 3.2 定义规范
```java
@Data
public class UserLoginRequest {
    private String username;  // 使用驼峰命名
    private String password;
    
    // 可添加校验注解
    // @NotBlank(message = "用户名不能为空")
    // private String username;
}
```

#### 3.3 分页请求标准
```java
@Data
public class PageRequest {
    private int pageNum = 1;   // 当前页码，默认第1页
    private int pageSize = 10; // 每页条数，默认10条
}
```

### 4. VO (View Object) 规范

VO 用于返回给前端的数据，位于 `model/vo` 包下。

#### 4.1 命名规范
- 视图对象: `XxxVO.java`

#### 4.2 定义规范
```java
@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String bio;
    
    // 注意：VO不应包含敏感信息（如密码、密钥等）
}
```

#### 4.3 转换规范
实体类应提供 `toVO()` 方法进行转换：

```java
// 在 Entity 中定义转换方法
public UserVO toVO() {
    UserVO userVO = new UserVO();
    userVO.setId(this.id);
    userVO.setUsername(this.username);
    userVO.setEmail(this.email);
    userVO.setNickname(this.nickname);
    userVO.setBio(this.bio);
    return userVO;
}

// 在 Controller 中使用
@GetMapping("/current")
public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request) {
    UserVO userVO = userService.getCurrentUser(request).toVO();
    return ResultUtils.success(userVO);
}
```

### 5. Entity 实体类规范

#### 5.1 注解使用
```java
@Data
@TableName("users")  // 指定数据库表名
public class User {
    
    @TableId(value = "id", type = IdType.AUTO)  // 主键自增
    private Long id;
    
    @TableField("password_hash")  // 字段映射
    private String passwordHash;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
```

#### 5.2 命名规范
- 类名使用 PascalCase
- 属性名使用 camelCase
- 数据库字段使用 snake_case

### 6. Controller 层规范

#### 6.1 注解使用
```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public BaseResponse<Long> login(@RequestBody UserLoginRequest request) {
        // 参数校验
        if (request == null) {
            throw new RuntimeException("Request body is null");
        }
        // 业务处理
        return ResultUtils.success(userService.login(request));
    }
}
```

#### 6.2 URL 设计规范
- 使用 RESTful 风格
- 统一使用 `/api` 前缀
- 资源名使用复数形式

```
GET    /api/users          # 获取用户列表
GET    /api/users/{id}     # 获取指定用户
POST   /api/users          # 创建用户
PUT    /api/users/{id}     # 更新用户
DELETE /api/users/{id}     # 删除用户
```

### 7. Service 层规范

#### 7.1 接口定义
```java
public interface UserService {
    Long login(String username, String password, HttpServletRequest request);
    Long register(String username, String password, String confirmPassword);
    User getCurrentUser(HttpServletRequest request);
}
```

#### 7.2 实现类
```java
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Long login(String username, String password, HttpServletRequest request) {
        // 业务逻辑实现
    }
}
```

### 8. Mapper 层规范

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis-Plus 提供基础CRUD方法
    // 可添加自定义查询方法
}
```

## 启动配置

### 环境要求
- JDK 21
- MySQL 8.0+
- Maven 3.6+

### 配置文件 (application.yml)
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hdbPilot?useSSL=false&serverTimezone=UTC
    username: root
    password: rootpassword
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  mapper-locations: classpath:/mapper/*.xml
  type-aliases-package: com.iss.hdbPilot.model.entity
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 数据库初始化
执行 `src/main/resources/sql/scheme.sql` 文件创建数据库表结构。

## 常见问题

### 1. MyBatis Mapper 找不到
确保主启动类包含正确的扫描路径：
```java
@MapperScan("com.iss.hdbPilot.mapper")
```

### 2. 跨域问题
项目已配置 CORS，如需修改请查看 `CorsConfig.java`。

### 3. 认证拦截
使用 `@AuthCheck` 注解进行接口权限控制。

## 开发建议

1. **代码规范**: 遵循阿里巴巴Java开发手册
2. **日志记录**: 使用 `@Slf4j` 注解，合理记录日志
3. **异常处理**: 优先使用全局异常处理，避免重复的 try-catch
4. **数据校验**: 在 DTO 中使用 Bean Validation 注解
5. **性能优化**: 合理使用 MyBatis-Plus 的条件构造器
6. **安全考虑**: 敏感信息不要在日志中输出，密码要加密存储

## API 文档 (Swagger)

项目已集成 **SpringDoc OpenAPI**，启动后可访问：

### 访问地址
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API JSON**: http://localhost:8080/v3/api-docs

### 特性
- ✅ **自动扫描**: 无需额外注解，自动生成所有API文档
- ✅ **接口测试**: 可直接在页面测试API接口
- ✅ **JSON/YAML导出**: 支持OpenAPI 3.0格式导出
- ✅ **实时更新**: 代码修改后文档自动更新

### Swagger注解说明

**核心原则：所有Swagger注解都是可选的！**

```java
// 最简洁的Controller - 无需任何Swagger注解
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @PostMapping("/login")
    public BaseResponse<Long> login(@RequestBody UserLoginRequest request) {
        // 业务逻辑
    }
}
```

#### 可选的增强注解

如果需要更详细的文档说明，可以添加：

**Controller级别：**
```java
@Tag(name = "用户管理", description = "用户相关接口")  // 接口分组
public class UserController { ... }
```

**方法级别：**
```java
@Operation(summary = "用户登录", description = "详细描述")  // 接口说明
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "成功"),
    @ApiResponse(responseCode = "400", description = "失败")
})
public BaseResponse<Long> login(...) { ... }
```

**DTO/VO级别：**
```java
@Schema(description = "用户登录请求")
public class UserLoginRequest {
    @Schema(description = "用户名", example = "admin")
    private String username;
}
```

### 配置说明

项目使用的配置：

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.7.0</version>
</dependency>
```

```java
// SwaggerConfig.java - 基本信息配置
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("FlashJudge Server API")
                .version("1.0.0")
                .description("API接口文档"));
    }
}
```

### 使用建议

1. **开发阶段**: 可暂时不加Swagger注解，专注业务逻辑
2. **文档完善**: 后期为重要接口添加详细注解
3. **团队协作**: 统一决定 注解使用规范 