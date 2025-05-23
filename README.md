CodeRater 后端项目
1. 项目概述
CodeRater 是一个旨在帮助用户分析 Java 代码质量并提供反馈的工具。后端系统提供 RESTful API，支持用户注册、登录、代码上传、代码结构解析、代码风格检查、复杂度分析、注释情况评估，并最终给出一个综合评分。用户可以管理自己上传的代码，包括查看和删除。
核心功能:

用户管理:
用户注册与登录：支持用户账户创建和基本认证（HTTP Basic Authentication）。
代码关联：上传的代码与用户关联，仅允许用户查看和删除自己的代码。


代码上传与解析: 用户可以上传 .java 文件，系统将解析其基本结构（类、方法、行数）。
代码质量分析:
代码风格检查: 使用 Checkstyle 根据预设规则（基于 Google Java Style）检查代码规范性。
复杂度分析: 计算方法的平均圈复杂度。
可读性分析: 评估代码的注释比例。
综合评分: 基于以上分析指标，给出一个量化的代码质量评分。



技术栈:

Java 17
Spring Boot 3.x
Spring Data JPA
Spring Security (基本认证，JWT 已禁用)
MySQL 8.0
Maven
JavaParser (用于代码结构解析、复杂度及注释分析)
Checkstyle (用于代码风格检查)

2. 环境准备与运行
2.1 所需环境

JDK 17 或更高版本
Maven 3.8+
MySQL 8.0 (确保数据库服务正在运行)
IntelliJ IDEA (推荐) 或其他 Java IDE
Postman (用于 API 测试)

2.2 项目配置

克隆项目:
git clone [你的项目GIT仓库地址]
cd coderater


数据库配置:

在 MySQL 中创建一个数据库，例如 coderater_db:CREATE DATABASE coderater_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


修改 src/main/resources/application.properties 文件中的数据库连接信息：spring.datasource.url=jdbc:mysql://localhost:3306/coderater_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=your_mysql_username # 替换为你的MySQL用户名
spring.datasource.password=your_mysql_password # 替换为你的MySQL密码


注意：建议将密码存储在环境变量中以提高安全性：spring.datasource.password=${DB_PASSWORD}




Maven 依赖:

项目使用 Maven 管理依赖。首次运行时，IDE 或 Maven 会自动下载所需依赖。
确保 pom.xml 包含以下关键依赖：
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
mysql-connector-j
javaparser-core
checkstyle






2.3 运行项目

使用 IDE:
在 IntelliJ IDEA 中打开项目，找到 com.se.coderater.CoderaterApplication.java 主类，右键选择 Run 'CoderaterApplication'.


使用 Maven 命令行:mvn spring-boot:run


项目默认运行在 http://localhost:8081（注意端口已改为 8081）。

3. API 接口文档
3.1 用户注册

URL: /api/auth/register
Method: POST
Content-Type: application/json
Request Body:{
    "username": "testuser",
    "password": "Test@123"
}


Success Response (200 OK):"User registered successfully"


Error Responses:
400 Bad Request (用户名已存在):"Username already exists"





3.2 用户登录

URL: /api/auth/login
Method: POST
Content-Type: application/json
Request Body:{
    "username": "testuser",
    "password": "Test@123"
}


Success Response (200 OK):"Login successful for user: testuser"


Error Responses:
401 Unauthorized (凭据错误):{
    "error": "Unauthorized",
    "message": "Bad credentials"
}







3.3 代码上传与解析

URL: /api/code/upload
Method: POST
Content-Type: multipart/form-data
Authentication: Basic Auth (用户名/密码)
Request Body:
file: (类型: File) 需要上传的 .java 文件。


Success Response (201 Created):{
    "id": 1,
    "uploader": {
        "id": 1,
        "username": "testuser"
    },
    "fileName": "Test.java",
    "content": "public class Test {\n    public void sayHello() {\n        System.out.println(\"Hello, World!\");\n    }\n}",
    "uploadedAt": "2025-05-23T12:00:00",
    "classCount": 1,
    "methodCount": 1,
    "lineCount": 4
}


Error Responses:
400 Bad Request (文件为空或非 .java 文件):{
    "error": "Invalid file.",
    "message": "Only .java files are allowed."
}


500 Internal Server Error (服务器处理错误):{
    "error": "File processing error.",
    "message": "Could not read or store the file: [具体错误信息]"
}





3.4 查看用户上传的代码

URL: /api/code/my-codes
Method: GET
Authentication: Basic Auth (用户名/密码)
Request Body: 无
Success Response (200 OK):[
    {
        "id": 1,
        "uploader": {
            "id": 1,
            "username": "testuser"
        },
        "fileName": "Test.java",
        "content": "public class Test {\n    public void sayHello() {\n        System.out.println(\"Hello, World!\");\n    }\n}",
        "uploadedAt": "2025-05-23T12:00:00",
        "classCount": 1,
        "methodCount": 1,
        "lineCount": 4
    }
]


Error Responses:
401 Unauthorized (未认证):{
    "error": "Unauthorized",
    "message": "Authentication required"
}


500 Internal Server Error:{
    "error": "Failed to retrieve codes.",
    "message": "[具体错误信息]"
}





3.5 删除用户上传的代码

URL: /api/code/{id}
Method: DELETE
Authentication: Basic Auth (用户名/密码)
Path Variable:
id: (类型: Long) 需要删除的代码记录的 ID。


Success Response (200 OK):{
    "status": "success",
    "message": "Code deleted successfully"
}


Error Responses:
403 Forbidden (代码不存在或无权限):{
    "error": "Invalid request",
    "message": "Code not found or you do not have permission to delete it."
}


401 Unauthorized (未认证):{
    "error": "Unauthorized",
    "message": "Authentication required"
}


500 Internal Server Error:{
    "error": "Failed to delete code",
    "message": "[具体错误信息]"
}





3.6 代码质量分析与评分

URL: /api/analysis/{codeId}
Method: POST
Path Variable:
codeId: (类型: Long) 需要分析的代码记录的 ID (来自上传接口返回的 id)。


Request Body: 无
Success Response (200 OK):{
    "id": 8,
    "code": {
        "id": 2,
        "uploader": {
            "id": 1,
            "username": "testuser"
        },
        "fileName": "Test.java",
        "content": "public class Test {\n    // ... \n}",
        "uploadedAt": "2025-05-23T12:00:00",
        "classCount": 1,
        "methodCount": 2,
        "lineCount": 8
    },
    "styleIssueCount": 3,
    "cyclomaticComplexity": 1,
    "commentRatio": 0.13,
    "commentLineCount": 1,
    "nonEmptyLineCount": 8,
    "analyzedAt": "2025-05-23T12:05:00",
    "overallScore": 79,
    "styleScore": 85,
    "complexityScore": 100,
    "commentScore": 55
}


Error Responses:
400 Bad Request (codeId 无效):{
    "error": "Bad Request",
    "message": "Code not found with id: 999"
}


500 Internal Server Error (分析失败):{
    "error": "Analysis Failed",
    "message": "An error occurred during code analysis: [具体错误信息]"
}





3.7 评分指标详解
分析结果中的评分相关字段含义如下：

styleIssueCount (代码风格问题数量):
含义: Checkstyle 根据 src/main/resources/checkstyle.xml 配置文件检查出的代码风格问题的总数。
影响: 该值越低越好。


cyclomaticComplexity (平均圈复杂度):
含义: 代码中所有方法圈复杂度的平均值。圈复杂度衡量代码逻辑分支的多少，值越高通常意味着代码越难理解和测试。
参考范围:
1-5: 低复杂度，良好。
6-10: 可接受的复杂度。
11-20: 中等复杂度，可能需要关注。
20以上: 高复杂度，建议重构。




commentRatio (注释比例):
含义: 注释行数占非空代码行数的百分比。
参考范围: 理想范围通常在 10% - 30% 之间。过低可能表示文档不足，过高（如大量注释掉的代码）也可能不是好现象。


commentLineCount (注释行数量):
含义: 代码中实际的注释行数。


nonEmptyLineCount (非空行数量):
含义: 代码中排除了纯空行后的总行数。


styleScore (代码风格单项得分, 0-100):
含义: 基于 styleIssueCount 计算得出。问题越少，得分越高。
计算简述: 满分100，每发现一个风格问题扣除一定分数（当前为5分/问题），最低为0分。


complexityScore (圈复杂度单项得分, 0-100):
含义: 基于 cyclomaticComplexity (平均圈复杂度) 计算得出。复杂度越低，得分越高。
计算简述: 平均圈复杂度在理想值（如<=5）以内得满分。超过理想值后，随着复杂度增加，扣分逐渐增多，超过上限（如20）则得0分。


commentScore (注释情况单项得分, 0-100):
含义: 基于 commentRatio (注释比例) 计算得出。
计算简述: 注释比例在理想区间（如10%-30%）内得满分。低于或高于此区间，得分会相应降低。


overallScore (综合评分, 0-100):
含义: 根据风格、复杂度、注释三个单项得分及其预设权重综合计算得出。
计算简述: (styleScore * 0.4) + (complexityScore * 0.3) + (commentScore * 0.3)，结果四舍五入并确保在0-100范围内。
解读:
85-100: 优秀
70-84: 良好
60-69: 及格
60以下: 有较大改进空间





4. 项目结构
coderater/
├── src/main/java/com/se/coderater/  # Java源代码根目录
│   ├── config/                    # 配置类 (SecurityConfig.java)
│   ├── controller/                # API 控制器 (CodeController.java, AuthController.java, AnalysisController.java)
│   ├── entity/                    # JPA 实体类 (Code.java, User.java, Analysis.java)
│   ├── repository/                # JPA 仓库接口 (CodeRepository.java, UserRepository.java, AnalysisRepository.java)
│   ├── service/                   # 业务逻辑服务 (CodeService.java, UserService.java, AnalysisService.java)
├── src/main/resources/
│   ├── static/                    # 静态资源
│   ├── templates/                 # 视图模板 (如果使用服务端渲染)
│   ├── application.properties     # Spring Boot 配置文件 (数据库等)
│   └── checkstyle.xml             # Checkstyle 规则配置文件
├── pom.xml                        # Maven 项目配置文件
└── README.md                      # 本文档

5. Checkstyle 配置
代码风格检查规则定义在 src/main/resources/checkstyle.xml 文件中。当前配置基于 Google Java Style Guide，并进行了部分调整。团队可以根据需要进一步自定义这些规则。
6. 认证与授权

认证方式: 使用 HTTP 基本认证（Basic Authentication），通过 HTTP 头 Authorization: Basic base64(username:password) 传递凭据。
权限控制:
/api/auth/**: 公开访问（注册、登录）。
/api/code/upload: 公开访问（可配置为需要认证）。
/api/code/my-codes: 需要认证，仅返回当前用户上传的代码。
/api/code/{id} (DELETE): 需要认证，仅允许删除自己的代码。
/api/analysis/**: 公开访问。
/swagger-ui/**, /api-docs/**: 公开访问。



注意:

上传代码时，需通过基本认证提供用户名/密码以关联用户（建议配置为需要认证）。
生产环境中，建议启用 HTTPS 确保基本认证安全。

7. Postman 测试指南

注册用户:

URL: POST http://localhost:8081/api/auth/register
Body (JSON):{
    "username": "testuser",
    "password": "Test@123"
}


预期: 200 OK, "User registered successfully"


登录验证:

URL: POST http://localhost:8081/api/auth/login
Body (JSON):{
    "username": "testuser",
    "password": "Test@123"
}


预期: 200 OK, "Login successful for user: testuser"


上传代码:

URL: POST http://localhost:8081/api/code/upload
Authorization: Basic Auth (testuser/Test@123)
Body (form-data): file = 上传 Test.java
预期: 201 Created, 返回代码详情（含 uploader）


查看用户代码:

URL: GET http://localhost:8081/api/code/my-codes
Authorization: Basic Auth (testuser/Test@123)
预期: 200 OK, 返回代码列表


删除代码:

URL: DELETE http://localhost:8081/api/code/1
Authorization: Basic Auth (testuser/Test@123)
预期: 200 OK, {"status": "success", "message": "Code deleted successfully"}



8. 后续开发计划

完善错误处理: 实现全局异常处理器（@ControllerAdvice）统一错误响应。
输入验证: 为实体类（如 User, Code）添加 @NotBlank 等注解。
单元测试与集成测试: 提高代码覆盖率和系统稳定性。
分页支持: 为 /api/code/my-codes 添加分页和排序。
前端对接: 与前端团队协作完成整个应用。
部署: 准备生产环境部署方案（HTTPS、环境变量、Docker 等）。

9. 协作
可以直接点击仓库页面的 Fork 按钮，在自己的账号下创建仓库副本，将个人 Fork 克隆到本地，修改后推送到自己的远程仓库，如果想要合并到本仓库可以提交 Pull Request。
