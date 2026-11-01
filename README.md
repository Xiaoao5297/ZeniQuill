# ZeniQuill

轻量、原生的 Android 代码编辑器。无 WebView / 浏览器内核，基于
[sora-editor](https://github.com/Rosemoe/sora-editor)（TextMate 语法高亮）与
Jetpack Compose 构建。

## 功能（v0.1）

- SAF 打开 / 编辑 / 保存任意文本文件（无需存储权限）
- 最近文件
- 语法高亮：Python、YAML、C、Java、HTML、XML + 纯文本兜底
- 明 / 暗编辑器主题（Quiet Light / Darcula）
- 基础设置：字号、Tab 宽度、空格 vs Tab、自动换行、行号

## 构建

项目通过 GitHub Actions 自动构建：

- push 到 `main` → Debug APK artifact
- 推送 `v*` tag → 签名 Release APK 并附加到 GitHub Release

签名密钥通过仓库 Secrets 提供：`KEYSTORE_BASE64`、`KEYSTORE_PASSWORD`、
`KEY_ALIAS`、`KEY_PASSWORD`。

本地构建（需要 JDK 17 + Android SDK）：

```bash
./gradlew :app:assembleDebug
```

## License

GPL-3.0。第三方组件与资产来源见 [THIRD_PARTY.md](THIRD_PARTY.md)。
