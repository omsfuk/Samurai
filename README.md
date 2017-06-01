# mini-framework

仿Spring的一款轻量级MVC框架，集成AOP，IOC，MVC, ORM, Transaction

## IOC 
实现三种作用域，Singleton，Prototype，Request。
实现Bean的自动满足依赖并创建，（循环依赖未处理）

## AOP 
支持类名，方法名，注解形式拦截

## MVC 
请求参数可自动绑定，控制器方法参数自动注入

## ORM 
简易的orm映射，只需要写Repository接口，自动生成类实例。
注解驱动，无需xml配置

## TX
集成事务管理，支持六种传播级别
