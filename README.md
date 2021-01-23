# EusAuthy

> 🍀 使用二步验证技术增强账号安全性

## 命令 Commands
```
/authy create # 创建 EusAuthy 二步验证
/authy delete # 删除 EusAuthy 二步验证
/authy help # 获取 EusAuthy 帮助
/authy remove <玩家名> # 移除指定玩家 EusAuthy 二步验证 !!! 需要管理员权限 !!!
/2fa <code> # 登录时使用，提供验证码以交互服务器
```

## 权限 Permissions

```
authy.general # EusAuthy 的主要权限
authy.op # EusAuthy 的管理员权限
2fa.general # EusAuthy 2FA 的主要权限
```

## 配置文件 config.yml

```yaml
Database: SQLite #SQLite, JSON, YAML (默认 SQLite，目前仅 SQLite 可以使用，其他数据库正在筹划开发)
```

## 版本说明 Versions

```
v1.1.0 Logic Update α
- 🛠️ 修复以控制台异常为主的 BUG
- 🛠️ 修复了二维码地图在使用 /authy create 后退出重进仍然存在的问题
- 🛠️修复了使用 /authy create 后再使用 /2fa <code> 引起的逻辑错误
- 🛠️ 修复了源码 utils.AuthyUtils 中 ramData() 方法的命名，原本是 remData()，雷姆我老婆 🌻

v1.0.0 Init EusAuthy
- 🔨 初始版本，梦开始的地方
```

## 下载 Download

| 🛠️ 插件正式版本 | 🌳 Minecraft 版本 | 🔗 下载链接 |
| :----: | :----: | :----: |
| 1.1.0 | 1.13~1.16.4 | [EusAuthy-1.1.0.jar](https://github.com/ElaBosak233/EusAuthy/releases/download/1.0.0/EusAuthy-1.1.0.jar) |
| 1.0.0 | 1.13~1.16.4 | [EusAuthy-1.0.0.jar](https://github.com/ElaBosak233/EusAuthy/releases/download/1.0.0/EusAuthy-1.0.0.jar) |  

## 开源协议 Open source

<img src="https://i.loli.net/2021/01/17/dAN4wq9Htb7noEa.png"  alt="MIT License" width=50% height=50% />

## 参与比赛 Competition

[![](https://www.mcbbs.net/template/mcbbs/image/logo_sc.png)](https://www.mcbbs.net)

本作品已参与 [**第四届 MCBBS 插件开发赛**](https://www.mcbbs.net/thread-1149442-1-1.html)