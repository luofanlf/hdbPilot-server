# 部署配置说明

## GitHub Actions CI/CD 流程

这个项目配置了自动化的 CI/CD 流程，包括：
- ✅ 自动运行单元测试
- 🐳 构建并推送 Docker 镜像
- 🚀 自动部署到 AWS EC2

## 必需的 GitHub Secrets 配置

在您的 GitHub 仓库中，需要配置以下 Secrets：

### Docker Hub 配置
```
DOCKER_USERNAME    # Docker Hub 用户名
DOCKER_PASSWORD    # Docker Hub 密码或访问令牌
```

### AWS EC2 配置
```
EC2_HOST          # EC2 实例的公网 IP 地址
EC2_USERNAME      # EC2 登录用户名（通常为 ubuntu 或 ec2-user）
EC2_PRIVATE_KEY   # EC2 实例的私钥文件内容
EC2_PORT          # SSH 端口（可选，默认为 22）
```

## EC2 实例准备

### 1. 安装 Docker
```bash
# Ubuntu
sudo apt update
sudo apt install -y docker.io
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER



### 2. 配置安全组
确保 EC2 安全组允许以下端口：
- **SSH (22)**: 用于部署访问
- **HTTP (8080)**: 应用程序端口

### 3. 生成 SSH 密钥对（如果没有）
```bash
# 在本地生成密钥对
ssh-keygen -t rsa -b 4096 -f ~/.ssh/ec2-hdbpilot

# 将公钥添加到 EC2 实例
ssh-copy-id -i ~/.ssh/ec2-hdbpilot.pub ubuntu@YOUR_EC2_IP
```

## 设置 GitHub Secrets

1. 进入 GitHub 仓库
2. 点击 **Settings** → **Secrets and variables** → **Actions**
3. 点击 **New repository secret** 添加以下secrets：

### Docker Hub 设置
- `DOCKER_USERNAME`: 您的 Docker Hub 用户名
- `DOCKER_PASSWORD`: 您的 Docker Hub 密码或访问令牌
docker login -u luofan036
dckr_pat_ajwLTIMiVM697AiiXd2WTZ_y-WI

### EC2 设置
- `EC2_HOST`: EC2 实例的公网 IP
- `EC2_USERNAME`: SSH 用户名（如 `ubuntu` 或 `ec2-user`）
- `EC2_PRIVATE_KEY`: 私钥文件的完整内容
- `EC2_PORT`: SSH 端口（可选，默认 22）

### 获取私钥内容
```bash
cat ~/.ssh/ec2-hdbpilot
```
复制完整输出（包括 `-----BEGIN` 和 `-----END` 行）

## 工作流触发条件

- **测试**: 所有 push 和 pull request 都会触发
- **部署**: 仅在推送到 `main` 分支时触发

## 应用程序访问

部署成功后，您可以通过以下地址访问应用：
```
http://YOUR_EC2_IP:8080
```

## 生产环境配置

建议为生产环境创建 `application-prod.yml` 配置文件：

```yaml
# src/main/resources/application-prod.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://YOUR_RDS_ENDPOINT:3306/hdbpilot?useSSL=true&useUnicode=true&characterEncoding=utf-8
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl

logging:
  level:
    com.iss.hdbPilot: INFO
    root: WARN
```

## 故障排除

### 检查 EC2 上的容器状态
```bash
# SSH 到 EC2 实例
ssh -i ~/.ssh/ec2-hdbpilot ubuntu@YOUR_EC2_IP

# 查看容器状态
docker ps -a

# 查看容器日志
docker logs hdbpilot

# 重启容器
docker restart hdbpilot
```

### 查看 GitHub Actions 日志
1. 进入 GitHub 仓库
2. 点击 **Actions** 标签
3. 选择相应的工作流运行记录查看详细日志 