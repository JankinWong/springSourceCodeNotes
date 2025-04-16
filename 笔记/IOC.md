## IOC高级特性

### Lazy-init延迟加载

指的是Bean的延迟加载（延迟创建）

ApplicationContext 容器的默认⾏为是在启动服务器时将所有 singleton bean 提前进⾏实例化。提前 实例化意味着作为初始化过程的⼀部分，ApplicationContext 实例会创建并配置所有的singleton bean。

#### 为什么需要延迟加载？

1. 开启延迟加载⼀定程度提⾼容器启动和运转性能
2. 对于不常使⽤的 Bean 设置延迟加载，这样偶尔使⽤的时候再加载，不必要从⼀开始该 Bean 就占⽤资源

#### 延迟加载的形式

```xml
<bean id="testBean" calss="cn.lagou.LazyBean" lazy-init="true" />
```

设置 lazy-init 为 true 的 bean 将不会在 ApplicationContext 启动时提前被实例化，⽽是**==第⼀次向容器通过 getBean 索取 bean 时实例化的。==**

### FactoryBean和BeanFactory

BeanFactory接⼝是容器的顶级接⼝，定义了容器的⼀些基础⾏为，负责⽣产和管理Bean的⼀个⼯⼚， 具体使⽤它下⾯的⼦接⼝类型，⽐如ApplicationContext。

Spring中Bean有两种，⼀种是普通Bean，⼀种是⼯⼚Bean（FactoryBean），FactoryBean可以⽣成某⼀个类型的Bean实例（返回给我们），也就是说我们可以借助于它⾃定义Bean的创建过程。

### 后置处理器

Spring提供了两种后处理bean的扩展接⼝，分别为 BeanPostProcessor 和 
BeanFactoryPostProcessor