此repo仍在迁移和建设中

* * *

# 概述

这是一个简单的流程引擎，核心就是节点之间的顺序流转，从一个节点指向另一个节点

节点分为两种，自动节点和手动节点。自动节点正常情况下不会停留，而手动节点会暂停并等待唤醒

## quick start

下文如无特殊说明，示例代码都是`Java`

### step by step

1.  引入依赖

    未deploy到maven仓库，使用请自行deploy。

2.  创建流程

    流程通过`.xml`格式定义，下面是一个简单的例子

    ```xml
    <!-- 流程总是由一个process标签定义，内部嵌套复数的节点元素来描述流程的流转 -->
    <process name="process">
        <!-- 流程节点:start 在默认Parser实现中会被解析为第一个节点 -->
        <start name="i'm start">
            <paths>
                <path to="state1"/>
            </paths>
        </start>
        <!-- 流程节点:state 最普通的节点，可以执行el脚本 -->
        <state name="state1">
            <paths>
                <path to="state2" expr="goto==2"/>
                <path to="state3" expr="goto==3"/>
            </paths>
        </state>
        <state name="state2">
            <invokes>
                <!-- 脚本执行的支持，通过pojo和springBean都可以 -->
                <script return="a" pojos="test.TestBean -> test, test.TestBean2 -> test2">
                    test2.testMethod(test.testMethod(i))
                </script>
                <!-- 可以通过expr表达式来判断是否执行这段script -->
                <script return="a" pojos="test.TestBean -> test, test.TestBean2 -> test2" expr="test2.judge()">
                    test2.testMethod(test.testMethod(2))
                </script>
                <!-- async属性使script异步执行，默认为false，在异步下return属性是无效的 -->
                <script pojos="test.TestBean -> test, test.TestBean2 -> test2" async="true">
                    test2.testMethod(test.testMethod(2))
                </script>
            </invokes>
            <paths>
                <path to="end"/>
            </paths>
        </state>
        <state name="state3">
            <paths>
                <path to="end"/>
            </paths>
        </state>
        <state name="end"/>
    </process>
    ```

    节点元素除了引擎内置的少量类型，也可以进行自定义和扩展

3.  调用流程

    在任何流程开始执行之前，你需要初始化整个引擎。视需要添加的扩展功能不同，需要初始化的动作可能会增加

    ```java
    import com.aixforce.bulbasaur.core.Bulbasaur
    ...
    Bulbasaur.require();
    ```

    使用`Machine`创建和驱动流程

    ```java
    import com.aixforce.bulbasaur.core.Machine
    ...
    Machine m = Machine.apply("process");
    // 放入执行需要的上下文
    m.addContext("i", 1); // scala有更加scala的api来操作上下文
    m.run();
    // 获取执行后的上下文
    m.context("i");
    // 或者获取执行后的流程当前位置
    m.getCurrentStateName(); // scala m.currentStateName
    ```

## 模块

在应用初始化阶段使用'Bulbasaur.require()'初始化bulbasaur并加载需要的模块

```java
Bulbasaur.require(); // 初始化bulbasaur 只加载默认的CoreModule
Bulbasaur.require(new ModuleDefine[] {PersistModule.module(), ...}); // 加载入参数组中的所有Module
// scala的api Bulbasaur.require(PersistModule, ...)
```

**tips:**`Bulbasaur.require()`方法应该被调用且只被调用一次。即便不依赖任何模块也要调用，在空调用中`CoreModule`会被默认加载

现在bulbasaur的模块组织

-   `bulbasaur-core`

    ```xml
    <dependency>
        <groupId>com.aixforce.bulbasaur</groupId>
        <artifactId>bulbasaur-core</artifactId>
        <version>${bulbasaur.version}</version>
    </dependency>
    ```

    包含的module

    -   核心模块`com.aixforce.bulbasaur.core.CoreModule`

        提供核心服务，是必须的。在这里实现了默认的获取流程模板并解析执行的行为

        -   默认实现中流程模板应该放在哪里？

            >   在`CoreModule`的默认实现中，流程模板按照名字中`.`替换为`/`并加后缀`.xml`的方式从classpath下获取。也就是说当你调用`Machine.apply("pokemon.process.testProcess")`时，会从`/pokemon/process/testProcess.xml`获取流程定义文件

        -   `CoreModule`中有些什么配置？

            >   你可以
            >
            >   -   指定一个spring的`ApplicationContext`给引擎，让引擎可以获取到被spring管理的bean
            >   -   指定一个身份标识`ownSign`，为后续需要隔离不同的应用或者环境留下配置，默认为`default`
            >   -   指定一个自定义的或内置的`Parser`来改变引擎获取和解析流程定义的行为
            >   -   增加或替换自定义的或内置`State`来支持新的节点，或者扩展节点的行为

-   `bulbasaur-ext`

    ```xml
    <dependency>
        <groupId>com.aixforce.bulbasaur</groupId>
        <artifactId>bulbasaur-ext</artifactId>
        <version>${bulbasaur.version}</version>
    </dependency>
    ```

    包含的module

    -   持久化模块`com.aixforce.bulbasaur.persist.PersistModule`

        提供带版本支持的数据库流程模板获取，扩展自`Machine`的`PersistMachine`提供回滚和持久化流程状态及路程的特性

        -   `PersistModule`的配置

            >   在`Bulbasaur.require(...)`之前，需要使用`PersistModule.setDataSource(ds)`给出一个数据源
            >
            >   三个数据库表名称的配置项：`tableNameP, tableNameS, tableNameD`，让你可自定义表名称
            >
            >   `usePersistParser`配置项用以决定是否使用持久化带版本支持的数据库流程模板。默认为true，但是在开发阶段false会更方便

        -   `PersistMachine`的使用

            >   所有通过`PersistMachine`执行的流程都会被持久化
            >
            >   通过`PersistMachine.apply(bizId, processName)`创建一个新的持久化Machine
            >
            >   通过`PersistMachine.apply(bizId)`获取一个已经存在的持久化Machine
            >
            >   通过`rollback`和`rollbackTo`方法来回滚一个节点或者回滚到指定节点

        -   持久化带版本支持的数据库模板方式

            >   在`com.aixforce.bulbasaur.persist.DefinitionHelper`中有三个接口分别用于自动部署一个模板、部署一个模板为指定版本以及指定已经存在的某版本为该流程的默认版本
            >
            >   典型的使用方式是在修改模板后每次都使用自动部署的方式生成一个新版本并自动成为默认版本，当此版本有问题时使用设置默认版本的接口回到之前的版本
