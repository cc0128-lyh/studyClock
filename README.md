# StudyClock

一款基于 Electron + Vue 3 + Spring Boot 的专注计时桌面应用，帮助你在电脑前保持专注、记录学习数据。

## 功能

### 🎯 专注计时

- **番茄钟模式** — 设定目标时长（1~999 分钟），专注期间不可暂停，倒计时归零即完成
- **正计时模式** — 不计时上限，自由专注，累积时长上不封顶
- **自定义时长** — 支持精确到秒的设定，默认时长可在设置中调整
- **全局快捷键** — 专注进行时右上角显示进度条，随时掌握剩余时间
- **休息提醒** — 专注完成后弹出玻璃态提示，展示刚完成的时长，可一键开始下一轮

### 📝 考试模式

- **计时考试** — 设定考试时长和试卷名称，进入考试模式后专注计时自动开始
- **考试复盘** — 考试时间到后自动进入复盘阶段，可记录错题内容、考试满分与得分
- **历史保存** — 考试记录存入数据库，统计数据中可查看每次考试的试卷名、得分等详情
- **错题回顾** — 在专注详情页可查看每场考试的错题记录

### 📚 学科管理

- **自定义学科** — 自由创建学习科目，如数学、英语、编程等
- **专注关联** — 每次专注开始前可选择学科，也可选择无学科
- **学科统计** — 按学科维度统计专注时长和次数，追踪各科投入时间

### 📊 数据统计

- **多时间维度** — 日视图、周视图、月视图、年视图，灵活切换
- **核心指标** — 今日/本周/本月专注次数与累计时长一览
- **趋势图表** — 每日专注时长柱状图，直观展示学习规律
- **详细记录** — 查看每条专注记录的起止时间、时长、学科，考试模式可查看详情

### ⚡ 快捷工具

- **快速启动** — 专注页侧边栏可放置常用应用或网站，一键启动
- **桌面导入** — 支持从 `.lnk` 和 `.exe` 文件导入快捷方式，自动读取图标与路径
- **自定义浏览器** — 可为每个网址快捷方式指定使用默认浏览器 / Chrome / Edge / Firefox 打开
- **管理面板** — 在设置页集中管理所有快捷工具，添加、删除、排序

### 🖼️ 壁纸系统

- **自定义壁纸** — 支持上传图片作为应用背景
- **纯色背景** — 除图片外也可选择纯色作为背景
- **多壁纸管理** — 可上传多张壁纸，在界面设置中随时切换
- **持久保存** — 壁纸数据持久化到本地数据库，重启不丢失

### 🎨 界面定制

- **主题模式** — 深色模式与浅色模式一键切换
- **专注布局** — 三种专注页样式：经典居中、顶部精简、侧边显示
- **计时模式** — 倒计时与正计时可选
- **字体设置** — 多种中文字体可选（楷体、黑体、宋体、苹方等）
- **字体大小** — 可自由调节专注时间数字的大小（1.5rem ~ 16rem）
- **字体配色** — 12 种预设颜色 + 自定义颜色选择器

### 🔔 通知音效

- **专注完成提示** — 专注结束后播放一段轻柔的四音上行旋律（do-mi-sol-do）
- **可开关** — 在设置页可随时开启或关闭音效
- **静音设计** — 音量轻柔，不会打扰他人

### ⚙️ 其他

- **用户资料** — 自定义昵称和头像（支持图片上传裁剪）
- **调试模式** — 手动添加历史专注记录，方便测试与数据填充
- **清空数据** — 一键清空所有专注记录、学科、快捷工具、壁纸和设置
- **关于页面** — 应用版本与制作者信息

## 技术栈

| 层 | 技术 |
|---|---|
| 前端框架 | Vue 3 + TypeScript |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| HTTP 请求 | Axios |
| 桌面框架 | Electron 30 |
| 构建工具 | Vite 5 |
| 后端框架 | Spring Boot 3 + JPA / Hibernate |
| 数据库 | H2（嵌入式文件数据库） |
| Java 运行时 | 自定义 JRE（jlink 裁剪） |
| 打包工具 | electron-builder + NSIS |

## 项目结构

```
StudyClock/
├── backend/                              # Spring Boot 后端
│   ├── study-clock-bootstrap/            #   启动入口 & 配置
│   ├── study-clock-common/               #   公用 DTO 与工具类
│   ├── study-clock-settings/             #   设置模块（用户偏好）
│   ├── study-clock-shortcut/             #   快捷工具模块
│   ├── study-clock-statistics/           #   数据统计模块
│   ├── study-clock-subject/              #   学科模块
│   ├── study-clock-timer/                #   专注计时核心模块
│   ├── study-clock-wallpaper/            #   壁纸模块
│   └── study-clock-web/                  #   Web 控制器 & CORS 配置
├── frontend/                             # Vue 3 + Electron 前端
│   ├── electron/                         #   Electron 主进程
│   │   ├── main.cjs                      #   主进程入口（窗口、IPC、后端启动）
│   │   └── preload.cjs                   #   预加载脚本（contextBridge）
│   ├── src/
│   │   ├── api/                          #   API 接口层
│   │   ├── components/                   #   通用组件
│   │   ├── router/                       #   路由配置
│   │   ├── stores/                       #   Pinia 状态管理
│   │   ├── styles/                       #   全局样式 & CSS 变量
│   │   ├── utils/                        #   工具函数
│   │   └── views/                        #   页面视图
│   │       ├── FocusView.vue             #     专注页（主界面）
│   │       ├── StatisticsView.vue        #     统计页
│   │       ├── SettingsView.vue          #     设置页
│   │       ├── InterfaceView.vue         #     界面定制页
│   │       ├── SubjectManageView.vue     #     学科管理页
│   │       └── FocusDetailView.vue       #     专注详情页
│   └── scripts/                          #   构建辅助脚本
└── build.bat                             # 一键构建脚本
```

## 快速开始

### 开发环境

```bash
# 1. 编译后端
cd backend
mvn package -DskipTests

# 2. 启动后端（终端 1）
java -jar study-clock-bootstrap/target/study-clock-bootstrap-1.0.0.jar

# 3. 安装前端依赖并启动 Vite 开发服务器（终端 2）
cd frontend
npm install
npm run dev

# 4. 启动 Electron（终端 3）
cd frontend
npm run electron:dev
```

> 开发模式下后端默认监听 `localhost:18080`，前端 Vite 开发服务器监听 `localhost:5173` 并通过 proxy 转发 API 请求。

### 构建安装包

项目根目录运行 `build.bat`，将依次执行：

```
[1/4] 编译后端 JAR       → mvn package
[2/4] 构建前端资源        → vite build
[3/4] 生成便携 JRE        → jlink（裁剪 JDK 至 ~50MB）
[4/4] 打包 Electron 应用  → electron-builder
```

输出目录：`frontend/dist-electron/`

#### 产物

| 文件 | 说明 |
|---|---|
| `StudyClock Setup 1.0.0.exe` | NSIS 安装包，支持自定义安装目录 |
| `StudyClock-1.0.0-portable.exe` | 便携版，解压即用，无需安装 |

> 打包后的应用内置了 Spring Boot 后端 JAR 和裁剪版 JRE，用户无需安装 Java 即可运行。

## 数据存储

应用数据存储在用户数据目录：

- **Windows**: `%APPDATA%\StudyClock\`

包含文件：

```
%APPDATA%\StudyClock\
├── data\
│   └── studyclock.mv.db          # H2 数据库文件
├── wallpapers\
│   └── wp_xxxxxx.png             # 上传的壁纸文件
└── uploads\
    └── avatars\
        └── avatar_xxxx.png       # 上传的头像文件
```

可通过命令行参数 `--studyclock.home=<路径>` 自定义数据目录。

## 后台架构

Spring Boot 后端以子进程方式由 Electron 启动：

```
Electron 主进程
  ├── 启动 backend Java 子进程（spawn）
  │     └── java -jar backend.jar --studyclock.home=<userData>
  ├── 轮询 /api/health 等待后端就绪
  ├── 创建 BrowserWindow（隐藏启动遮罩）
  └── 前端通过 http://localhost:18080 调用后端 API
```

## 许可

MIT
