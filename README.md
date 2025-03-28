## Dict-maker

[Qwerty Learner](https://qwerty.kaiyi.cool/)  [[Github](https://github.com/RealKai42/qwerty-learner)]
是一款为键盘工作者设计的单词记忆与英语肌肉记忆锻炼软件. 本项目通过 TXT 文本, 为 Qwerty Learner 生成单词库.

## 如何使用

### 本地运行
如果你是一个熟悉 Java 的开发者, 可以直接运行本项目. 你需要:
- 安装 Java 17
- 安装 Maven
- 克隆本仓库
- 将你需要处理的TXT文件放到 `src/main/resources/novels` 目录下
- 运行 `mvn clean test`
- 生成的单词库会在 `output` 目录下

### Github Action 运行 
如果你是一个不熟悉 Java 的开发者, 你可以使用 Github Action 运行本项目. 你需要:
- Fork 本仓库,并克隆
- 将你需要处理的TXT文件放到 `src/main/resources/novels` 目录下
- 提交代码到你的 Fork 仓库
- 等待 Action 运行完成, 
- 你可以在 Action 页面查看运行日志, 在 `Upload output` 标签下, 找到 Artifact download URL 进行下载.
- 下载的文件为一个 ZIP 压缩包, 解压后, 可以找到对应的 JSON 文件.

## 去哪里找英文书籍
-  [Project Gutenberg](https://www.gutenberg.org/) - 免费的公版电子书仓库

## 如何将单词库添加到 Qwerty Learning
参考 [如何导入新的词典 📚](https://github.com/RealKai42/qwerty-learner/blob/master/docs/toBuildDict.md)

## 本项目的工作原理
1. 通过 Qwerty Learning 原始的单词库, 将所有单词库聚合成一个大的字典全集
2. 处理 TXT 文件将文本转换为字典子集
3. 通过一些规则, 过滤掉一些不需要的或者重复的单词
4. 渲染 Example 部分
5. 生成 JSON 文件, 用于 Qwerty Learning 导入