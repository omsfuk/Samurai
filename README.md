![ss](https://github.com/omsfuk/Samurai/blob/master/logo.png?raw=true)

## ![version](https://img.shields.io/badge/version-1.0.0-green.svg) ![license](https://img.shields.io/dub/l/vibe-d.svg)
## What is Samurai
`Samurai`是一款轻量级MVC框架，注解驱动，配置简单，容易上手

[Samurai手册](https://github.com/omsfuk/Samurai/blob/master/Samurai_Tutorial.md "Tutorial")
 [下载手册](https://github.com/omsfuk/Samurai/blob/master/Samurai_Tutorial.pdf "Download Tutorial")


## Features
* 依赖注入
* 面向切面
* 内置orm
* 事务管理
* restful风格支持
* 自定义视图


## Get Start
添加Maven依赖
```xml
<dependency>
	<groupId>cn.omsfuk.samurai</groupId>
    <artifactId>framework</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
添加一个`samuiai.properties`文件，内容如下
```
component.scan.path=cn.omsfuk.demo # bean扫描路径
response.view.json=cn.omsfuk.samurai.framework.mvc.view.JsonResponseView # json视图解析器
# 以下用于orm
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/smart?useSSL=true
jdbc.username=root
jdbc.password=root
```

创建一个`DemoController`类，加入如下方法
```
@RequestMapping("/index")
@View("json")
public String index() {
    return "hello";
}
```

用浏览器访问`http://localhost:8080/index`

## Contributor
* [omsfuk](https://github.com/omsfuk) 


## Licenses
* 参见 [MIT协议](https://github.com/omsfuk/mini-framework/blob/master/LICENSE)
