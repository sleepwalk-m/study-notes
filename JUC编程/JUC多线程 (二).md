# JUC多线程 (二) 

### CAS介绍

**什么是CAS？**

CAS：Compare and Swap，即比较再交换。

jdk5增加了并发包java.util.concurrent.*,其下面的类使用CAS算法实现了区别于synchronouse同步锁的一种乐观锁。JDK 5之前Java语言是靠synchronized关键字保证同步的，这是一种独占锁，也是是悲观锁。

**CAS算法理解**

对CAS的理解，CAS是一种无锁算法，CAS有3个操作数，内存值V，旧的预期值A，要修改的新值B。当且仅当预期值A和内存值V相同时，将内存值V修改为B，否则什么都不做。

![1583024085533](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\1583024085533.png)

```
假如说有3个线程并发的要修改一个AtomicInteger的值，他们底层的机制如下：
1.首先，每个线程都会先获取当前的值。接着走一个原子的CAS操作，原子的意思就是这个CAS操作一定是自己完整执行完的，不会被别人打断。
2.然后CAS操作里，会比较一下，现在你的值是不是刚才我获取到的那个值。如果是，说明没人改过这个值，那你给我设置成累加1之后的一个值。
3.同理，如果有人在执行CAS的时候，发现自己之前获取的值跟当前的值不一样，会导致CAS失败，失败之后，进入一个无限循环，再次获取值，接着执行CAS操作。
```

### CAS缺陷 

CAS虽然高效地解决了原子操作，但是还是存在一些缺陷的，主要表现在三个方
法：循环时间太长、只能保证一个共享变量原子操作、ABA问题 

存在问题：

1.可能cas 会一直失败，然后自旋

2.如果一个值原来是A，变成了B，然后又变成了A，那么在CAS检查的
时候会发现没有改变，但是实质上它已经发生了改变，这就是所谓的ABA问题。
对于ABA问题其解决方案是加上版本号，即在每个变量都加上一个版本号，每次
改变时加1，即A —> B —> A，变成1A —> 2B —> 3A。 

### J.U.C之atomic包 

#### 基本类型

使用原子的方式更新基本类型
AtomicInteger：整形原子类
AtomicLong：长整型原子类
AtomicBoolean ：布尔型原子类 

#### 引用类型 

AtomicStampedReference：原子更新引用类型里的字段原子类
AtomicMarkableReference ：原子更新带有标记位的引用类型 

#### 数组类型 

使用原子的方式更新数组里的某个元素
AtomicIntegerArray：整形数组原子类
AtomicLongArray：长整形数组原子类
AtomicReferenceArray ：引用类型数组原子类 

#### 对象的属性修改类型 

AtomicIntegerFieldUpdater:原子更新整形字段的更新器
AtomicLongFieldUpdater：原子更新长整形字段的更新器
AtomicReferenceFieldUpdater ：原子更新引用类形字段的更新器 

#### JDK1.8新增类 

DoubleAdder：双浮点型原子类
LongAdder：长整型原子类
DoubleAccumulator：类似DoubleAdder，但要更加灵活(要传入一个函数
式接口)
LongAccumulator：类似LongAdder，但要更加灵活(要传入一个函数式接
口) 

### J.U.C之AQS 

什么是AQS？ （锁获取和锁释放）

它只是一个抽象类 ，但是JUC中的很多组件都是
基于这个抽象类，也可以说这个AQS是多数JUC组件的基础。 

用于JUC包下的，核心组件  AQS(AbstractQueuedSynchronizer），即队列同步器。 

### J.U.C之锁

### ReentrantLock 可重入锁

获取锁  sync.lock();

释放锁 sync.release(1); 

ReentrantLock与synchronized的区别

1.功能比synchronized 要多，拓展性更强

2.对待线程等待，唤醒操作更加详细和灵活。

3.ReentrantLock提供了可轮询的锁请求。它会尝试着去获取锁，如果成功则继续，否则可以等到下次运行时处理，而synchronized则一旦进入锁请求要么成功要么阻塞，所以相比synchronized而言，ReentrantLock会不容易产生死锁些。

4.ReentrantLock支持更加灵活的同步代码块，但是使用synchronized时，只能在同一个synchronized块结构中获取和释放。

5.entrantLock支持中断处理，且性能较synchronized会好些。

### 读写锁ReentrantReadWriteLock

读写锁维护着一对锁，一个读锁和一个写锁。通过分离读锁和写锁，使得并发性比一般的互斥锁有了较大的提升：在同一时间可以允许多个读线程同时访问，但是在写线程访问时，所有读线程和写线程都会被阻塞。



### J.U.C之Condition

Lock提供了条件Condition，对线程的等待、唤醒操作更加详细和灵活。

![1583046605439](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\1583046605439.png)

### J.U.C之并发工具类

### CyclicBarrier (同步屏障)

允许一组线程全部等待彼此达到共同屏障点的同步辅助。 循环阻塞在涉及固定大小的线程方的程序中很有用，这些线程必须偶尔等待彼此。 屏障被称为循环，因为它可以在等待的线程被释放之后重新使用。

CyclicBarrier好比一扇门，默认情况下关闭状态，堵住了线程执行的道路，直到所有线程都就位，门才打开，让所有线程一起通过。

### Semaphore(信号量)

其本质上是一个“共享锁”。Semaphore常用于约束访问一些（物理或逻辑）资源的线程数量。

### J.U.C之并发容器ConcurrentHashMap（重点）

1. hashMap  1.7版本  存储结构，扩容机制 ，线程不安全  会引发并发修改异常，在并发下会出现环链
2. ConcurrentHashMap 1.7版本    存储结构  ，分段锁机制，线程安全，不会引发并发修改异常
3. hashMap 1.8版本    存储结构（Node数组+链表+红黑树），扩容机制（>12）,线程不安全，会引发并发修改异常，不会出现环链问题。
4. ConcurrentHashMap  1.8 存储结构 ， cas + syn ,线程安全，不会引发并发修改异常
5. hashTable 1.8  安全   会引发并发修改异常.
6. 了解即可 ConcurrentSkipListMap    Skip List ，称之为跳表，它是一种可以替代平衡树的数据结构，其数据元素默认按照key值升序，天然有序。

### J.U.C队列

####  非阻塞队列ConcurrentLinkedQueue

注意：

1. ConcurrentLinkedQueue的.size() 是要遍历一遍集合的，很慢的，所以尽量要避免用size
2. 使用了这个ConcurrentLinkedQueue 类之后还是需要自己进行同步或加锁操作。例如queue.isEmpty()后再进行队列操作queue.add()是不能保证安全的，因为可能queue.isEmpty()执行完成后，别的线程开始操作队列。

### ArrayBlockingQueue

### LinkedBlockingQueue

1. **队列的数据结构不同**

   ArrayBlockingQueue是一个由数组支持的有界阻塞队列

   LinkedBlockingQueue是一个基于链表的有界（可设置）阻塞队列

    

2. **队列中锁的实现不同**

ArrayBlockingQueue实现的队列中的锁是没有分离的，即生产和消费用的是同一个锁；

LinkedBlockingQueue实现的队列中的锁是分离的，即生产用的是putLock，消费是takeLock

 

1. **在生产或消费时操作不同**

ArrayBlockingQueue实现的队列中在生产和消费的时候，是直接将枚举对象插入或移除的；

LinkedBlockingQueue实现的队列中在生产和消费的时候，需要把枚举对象转换为Node进行插入或移除，会影响性能

 

1. **队列大小初始化方式不同**

ArrayBlockingQueue实现的队列中必须指定队列的大小；

LinkedBlockingQueue实现的队列中可以不指定队列的大小，但是默认是Integer.MAX_VALUE

#### PriorityBlockingQueue（有序队列）

#### SynchronousQueue（阻塞队列）

### J.U.C线程池（重点）

​        线程是一个程序员一定会涉及到的概念，但是线程的创建和切换都是代价比较大的。所以，我们需要有一个好的方案能做到线程的复用，这就涉及到一个概念——线程池。合理的使用线程池能够带来3个很明显的好处：

1. 降低资源消耗：通过重用已经创建的线程来降低线程创建和销毁的消耗
2. 提高响应速度：任务到达时不需要等待线程创建就可以立即执行。
3. 提高线程的可管理性：线程池可以统一管理、分配、调优和监控。

### 4大线程池

#### FixedThreadPool 正规线程

 我的理解这是一个有指定的线程数的线程池，有核心的线程，里面有固定的线程数量，响应的速度快。正规的并发线程，多用于服务器。固定的线程数由系统资源设置。

#### SingleThreadExecutor  单线程线程池

作为单一worker线程的线程池，它把corePool和maximumPoolSize均被设置为1，和FixedThreadPool一样使用的是无界队列LinkedBlockingQueue,所以带来的影响和FixedThreadPool一样。

####  CachedThreadPool 缓存线程池

这个线程池在执行 大量短生命周期的异步任务时，可以显著提高程序性能。它把corePool设置为0，maximumPoolSize设置为Integer.MaxValue

#### ScheduledThreadPool 延迟线程池

可以实现线程的周期和延迟调度



## 线程池七大参数介绍

（1）corePoolSize：线程池中常驻核心线程数

（2）maximumPoolSize：线程池能够容纳同时执行的最大线程数，此值必须大于等于1

（3）keepAliveTime：多余的空闲线程存活时间。当前线程池数量超过corePoolSize时，当空闲时间到达keepAliveTime值时，多余空闲线程会被销毁直到只剩下corePoolSize个线程为止。

（4）unit：keepAliveTime的时间单位

（5）workQueue：任务队列，被提交但尚未执行的任务

（6）threadFactory：表示生成线程池中的工作线程的线程工厂，用于创建线程，一般为默认线程工厂即可

（7）handler：拒绝策略，表示当队列满了并且工作线程大于等于线程池的最大线程数（maximumPoolSize）时如何来拒绝来请求的Runnable的策略



## 拒绝策略场景分析

AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。 （默认）

DiscardPolicy：丢弃任务，但是不抛出异常。如果线程队列已满，则后续提交的任务都会被丢弃，且是静默丢弃。

DiscardOldestPolicy：丢弃队列最前面的任务，然后重新提交被拒绝的任务。

CallerRunsPolicy：由调用线程处理该任务

### 公平锁和非公平锁

1、公平锁能保证：老的线程排队使用锁，新线程仍然排队使用锁。
2、非公平锁保证：老的线程排队使用锁；**但是无法保证新线程抢占已经在排队的线程的锁**。