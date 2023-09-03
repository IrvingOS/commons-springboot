# commons-springboot

## 1. 使用

```xml

<dependency>
    <groupId>top.isopen.commons</groupId>
    <artifactId>commons-springboot</artifactId>
    <version>1.2.8</version>
</dependency>
```

## 2. TODO

1. RedLock#key 多 EL 表达式支持

## 3. 注意

1. BaseType 中的 `@TableLogic` 配置无法传递，所以没有注释，需要在项目的配置文件中手动配 `logic-delete-field: deleted` 才能达到效果
