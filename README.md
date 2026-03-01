# TestNetty V0.8.0.1

# Unity端项目github地址: 
https://github.com/GoldSprite/NetcodeLearn_HumanEatCoin  

# 当前内容

## 本地 Maven 发布（Gradle + Java17，供 MagicDungeon2 引用）

1. 在 `TestNetty` 根目录执行：

```bash
./gradlew clean publishToMavenLocal
```

2. 发布成功后可在本机 `~/.m2/repository/goldsprite/myUdpNetty/TestNetty/0.8.0` 看到产物。

3. 在 `MagicDungeon2` 的 Gradle 模块中确保仓库包含 `mavenLocal()`，然后添加依赖：

```gradle
api 'goldsprite.myUdpNetty:TestNetty:0.8.0'
```

## 设计原则（请严格遵守）

- TestNetty 只提供通用网络基础框架能力（编解码、收发、回调、扩展点）。
- TestNetty 不承载任何具体游戏业务协议与业务处理逻辑。
- 业务侧（例如 MagicDungeon2）需基于扩展点自行定义包体、指令与处理流程。
- 历史日志已迁移并保留至 `docs/历史更新日志.md`，README 仍保留概要信息。

# 待办

## 更新历史

- 历史更新正文已迁移至 [docs/历史更新日志.md](docs/历史更新日志.md)。
- README 只保留当前版本使用说明与框架边界约束。
