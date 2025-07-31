# 数据库设置说明

## 1. 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS hdbPilot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 2. 导入数据
```bash
mysql -u root -p hdbPilot < src/main/resources/sql/scheme.sql
```

## 3. 配置数据库连接

### 方法一：使用环境变量（推荐）
设置环境变量：
```bash
export MYSQL_PASSWORD=your_actual_password
```

### 方法二：创建本地配置文件
复制 `src/main/resources/application-dev.yml` 为 `src/main/resources/application-local.yml`，
并修改其中的密码为你的实际MySQL密码。

### 方法三：直接修改主配置文件
在 `src/main/resources/application.yml` 中直接修改密码字段。

## 4. 启动应用
```bash
./mvnw spring-boot:run
```

## 注意事项
- 不要将包含真实密码的配置文件提交到Git
- 建议使用环境变量或本地配置文件来管理敏感信息 