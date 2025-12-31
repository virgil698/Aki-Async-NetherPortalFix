# Aki-Async-NetherPortalFix

[![许可证](https://img.shields.io/github/license/LeavesMC/leaves-plugin-template)](LICENSE)

基于 [NetherPortalFix](https://modrinth.com/mod/netherportalfix) 为 Luminol 环境开发的地狱门修复mixin插件

## 特性

- 修复地狱门行为，只要玩家使用同一个传送门返回主世界，他们最终都会回到他们来时所在的地方

## 依赖

此模板需要 JDK 21 或更高版本

## 快速开始

1. **克隆本模板：**

   ```bash
   git clone https://github.com/LeavesMC/leaves-plugin-template.git
   ```

2. **配置项目**

   按照项目中的 `// TODO` 注释完成对应内容，并根据你的插件需求修改 `src/mixin/resources` 下的 `leaves-template-plugin.accesswidener` 与 `leaves-template-plugin.mixins.json` 文件

3. **编写插件 ：**

   在 `src/main` 模块下编写你的插件代码
   在 `src/mixin` 模块下编写你的 Mixin 代码

4. **构建插件：**

   ```bash
   ./gradlew build
   ```

   构建成功后，插件 JAR 文件会生成在 `build/libs` 目录下。

5. **部署插件：**

   将生成的 `.jar` 文件放入支持 Leaves 插件的服务器的 `plugins` 文件夹，并重启服务器。

## 常见问题

**可以用于哪些服务端？**

推荐使用 [Luminol](https://github.com/LuminolMC/Luminol) ，Leaves 已有此修复配置


**为什么 Mixin 不起作用？**

Leavesclip 默认禁用了 Mixin 功能。你需要在服务器启动脚本中加入 `-Dleavesclip.enable.mixin=true`

模板中附带的开发服务器已默认启用 Mixin，无需额外设置。

## 贡献

欢迎通过 Issue 或 Pull Request 提交你的建议或改进！

## 许可证

本项目遵循 [MIT License](LICENSE) 开源协议。