# StudyClock

一款基于 Electron + Vue 3 + Spring Boot 的专注计时桌面应用。

## 功能

- **专注计时** — 番茄钟模式与正计时模式，支持自定义时长
- **考试模式** — 设定考试时长，结束后进入复盘阶段，记录错题与得分
- **学科管理** — 为每次专注记录关联学科，统计各学科学习时长
- **数据统计** — 日/周/月/年视图，专注次数与时长分布图表
- **快捷工具** — 在专注页快速启动应用或打开网站
- **壁纸系统** — 自定义背景壁纸，支持图片上传与纯色背景
- **界面定制** — 多种布局样式（居中/顶部/侧边），深色/浅色主题
- **通知音效** — 专注结束播放提示音，可开关
- **清空数据** — 一键清空所有专注记录与设置

## 技术栈

| 层 | 技术 |
|---|---|
| 前端框架 | Vue 3 + TypeScript + Pinia |
| 桌面框架 | Electron 30 |
| 构建工具 | Vite 5 |
| 后端框架 | Spring Boot 3 + JPA |
| 数据库 | H2 (文件存储) |
| 打包 | electron-builder + NSIS |
| Java 运行时 | 自定义 JRE (jlink) |

## 快速开始

### 开发环境

```bash
# 1. 启动后端
cd backend
mvn package -DskipTests
java -jar study-clock-bootstrap/target/study-clock-bootstrap-1.0.0.jar

# 2. 启动前端（新终端）
cd frontend
npm install
npm run dev

# 3. 启动 Electron（新终端）
cd frontend
npm run electron:dev
```

### 构建安装包

```bash
build.bat
```

执行后将依次：
1. 编译后端 JAR
2. 构建前端资源
3. 使用 jlink 生成便携 JRE
4. 打包为 NSIS 安装包与便携版 exe

输出位于 `frontend/dist-electron/`。

## 项目结构

```
StudyClock/
├── backend/                          # Spring Boot 后端
│   ├── study-clock-bootstrap/        # 启动入口
│   ├── study-clock-common/           # 公用 DTO 与工具
│   ├── study-clock-settings/         # 设置模块
│   ├── study-clock-shortcut/         # 快捷工具模块
│   ├── study-clock-statistics/       # 统计模块
│   ├── study-clock-subject/          # 学科模块
│   ├── study-clock-timer/            # 专注计时模块
│   ├── study-clock-wallpaper/        # 壁纸模块
│   └── study-clock-web/              # Web 控制器与配置
├── frontend/                         # Vue 3 + Electron 前端
│   ├── electron/                     # Electron 主进程
│   ├── src/                          # Vue 应用源码
│   │   ├── api/                      # API 接口
│   │   ├── components/               # 通用组件
│   │   ├── router/                   # 路由
│   │   ├── stores/                   # Pinia 状态管理
│   │   ├── styles/                   # 全局样式
│   │   ├── utils/                    # 工具函数
│   │   └── views/                    # 页面视图
│   └── scripts/                      # 构建脚本
└── build.bat                         # 一键构建脚本
```

## 数据存储

应用数据存储在用户数据目录：

- **Windows**: `%APPDATA%/StudyClock/`
- 包含：H2 数据库文件、壁纸文件、头像上传文件

可通过 `--studyclock.home=<路径>` 自定义数据目录。

## 许可

MIT
