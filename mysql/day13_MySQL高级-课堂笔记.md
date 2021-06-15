## 今日内容

存储过程

触发器

事务

DBA

## 1. 存储过程(函数)(>理解)

### 1.1 概念

存储过程优势：

- 简单：可以重复使用，类似于`java`中方法可以多次调用

- 高性能：存储过程经编译后，会比一条一条`SQL`语句执行快。

- 安全：存储过程和函数位于服务器上，调用的时候只需要传递名称和参数即可

  ​	减少数据在数据库和应用服务器之间的传输，可以提高数据处理的效率
  ​	将一些业务逻辑在数据库层面来实现，可以减少代码层面的业务处理

存储过程不足

- 维护、迭代成本高：过程化编程，业务逻辑放在数据库上,复杂业务的书写难度高

- 不方便调试：`SQL`调试不友好

- 可移植性差：每个数据库的存储过程语法几乎不一样，不通用且难以维护



存储过程和函数的区别

- 函数必须有返回值
- 存储过程没有返回值，如果需要返回值，通过出参传递



存储过程和视图的区别：目的不同

- 存储过程往往涉及很多的数据处理，是一个复杂的过程，它相当于一个方法可以接受参数产生结果，内部可以处理业务逻辑及相关数据；为了处理逻辑和数据；
- 视图只存储一个`SQL`语句，简化了一些复杂查询，最终目的是为了呈现数据。




### 1.2 数据准备

```sql
-- 创建db6数据库
CREATE DATABASE db6;

-- 使用db6数据库
USE db6;

-- 创建学生表
CREATE TABLE student(
	id INT PRIMARY KEY AUTO_INCREMENT,	-- 学生id
	NAME VARCHAR(20),			-- 学生姓名
	age INT,				-- 学生年龄
	gender VARCHAR(5),			-- 学生性别
	score INT                               -- 学生成绩
);
-- 添加数据
INSERT INTO student VALUES (NULL,'张三',23,'男',95),(NULL,'李四',24,'男',98),
(NULL,'王五',25,'女',100),(NULL,'赵六',26,'女',90);




-- 按照性别进行分组，查询每组学生的总成绩。按照总成绩的升序排序
SELECT gender,SUM(score) getSum FROM student GROUP BY gender ORDER BY getSum ASC;
```

### 1.3 创建和调用

创建和调用

```sql
/*
	创建存储过程

	-- 修改分隔符为$，不要使用# \
	DELIMITER $

	-- 标准语法
	CREATE PROCEDURE 存储过程名称(参数列表)
	BEGIN
		SQL 语句列表;
	END$

	-- 修改分隔符为分号
	DELIMITER ;
*/

-- 修改默认的结束符号，结束符号和关键字之间有空格
DELIMITER $$

-- 创建之前，删除一下，避免一些不必要的错误
--  ，删除语句的结束符号使用修改后的
DROP PROCEDURE IF EXISTS stu_test$$
CREATE PROCEDURE stu_test()
BEGIN
-- 存储过程要执行的SQL语句组合
-- 这里的SQL语句的结束还是使用;
	SELECT * FROM student;

-- 这里##前面不需要空格
END$$


-- 恢复默认的结束符号，结束符号和关键字之间有空格
DELIMITER ;

/*
	调用存储过程
	CALL 存储过程名称(实际参数);
*/
-- 调用
-- 调用的时候，如果没有参数，后面的括号可以不带
CALL stu_test();
```



### 1.4 查看和删除

```sql
/*
	查询数据库中所有的存储过程
	SELECT * FROM mysql.proc WHERE db='数据库名称';
*/
-- 查看db6数据库中所有的存储过程
SELECT * FROM mysql.proc WHERE db='db6';


/*
	删除存储过程
	DROP PROCEDURE [IF EXISTS] 存储过程名称;
*/
DROP PROCEDURE IF EXISTS stu_test;
```



### 1.5 变量

```sql
/*
	局部变量（存储过程中定义的变量）定义变量
	DECLARE 变量名 数据类型 [DEFAULT 默认值];
*/
-- 定义一个int类型变量，并赋默认值为10
DELIMITER $
DROP PROCEDURE IF EXISTS pro_test1$
CREATE PROCEDURE pro_test1()
BEGIN
	DECLARE num1 INT ;
	-- 在存储过程定义的时候，局部变量的声明必须在存储过程的最开始
	
	-- 定义变量
	-- 必须先赋值后使用
	-- 声明的时候赋值 DEFAULT 关键字实现
	-- DECLARE num INT DEFAULT 10;
	DECLARE num2 INT ;
	
	-- 或者在使用之前会局部变量赋值
	SET num1=20;
	
	-- 使用变量
	SELECT num1;
END$

DELIMITER ;

-- 调用pro_test1存储过程
CALL pro_test1();



/*
	变量赋值-方式一
	SET 变量名 = 变量值;
*/
-- 定义一个varchar类型变量并赋值
DELIMITER $

CREATE PROCEDURE pro_test2()
BEGIN
	-- 定义变量
	DECLARE NAME VARCHAR(10);
	-- 为变量赋值
	SET NAME = '存储过程';
	-- 使用变量
	SELECT NAME;
END$

DELIMITER ;

-- 调用pro_test2存储过程
CALL pro_test2();




/*
	变量赋值-方式二
	SELECT 列名 INTO 变量名 FROM 表名 [WHERE 条件];
*/
-- 定义两个int变量，用于存储男女同学的总分数
DELIMITER $

CREATE PROCEDURE pro_test3()
BEGIN
	-- 定义两个变量
	DECLARE men,women INT;
	-- 查询男同学的总分数，为men赋值
	SELECT SUM(score) INTO men FROM student WHERE gender='男';
	-- 查询女同学的总分数，为women赋值
	SELECT SUM(score) INTO women FROM student WHERE gender='女';
	-- 使用变量
	SELECT men,women;
END$

DELIMITER ;

-- 调用pro_test3存储过程
CALL pro_test3();


/*
	用户变量  会话变量
	在整个会话范围内有效
	格式：
		@变量名

*/
-- 用户变量的定义方式：使用set定义，没有默认值，需要在定义的时候通过=赋值
SET @user_var = 20;
SELECT @user_var user_var;

DELIMITER $
DROP PROCEDURE IF EXISTS pro_test4$
CREATE PROCEDURE pro_test4()
BEGIN
	SELECT @user_var user_var1;
	SET @user_var = 40;
	SELECT @user_var user_var2;
	SELECT @user_var user_var3;
END$

DELIMITER ;

CALL pro_test4;

/*
	全局系统变量
	格式：
		@@变量名
	一般情况下，我们不需要定义，也不会去改变的。对其使用主要是获取查看。
	eg：@@AUTOCOMMIT

*/

SELECT @@AUTOCOMMIT;
-- 当前会话所有修改都不会自动提交了。一般不要这么玩
-- SET @@AUTOCOMMIT=0;
-- SELECT @@AUTOCOMMIT;
```



### 1.6 if条件判断

```sql
/*
	if语句
	IF 判断条件1 THEN 执行的sql语句1;
	[ELSEIF 判断条件2 THEN 执行的sql语句2;]
	...
	[ELSE 执行的sql语句n;]
	END IF;
*/

/*
	定义一个int变量，用于存储班级总成绩
	定义一个varchar变量，用于存储分数描述
	根据总成绩判断：
		380分及以上   学习优秀
		320 ~ 380     学习不错
		320以下       学习一般
*/
DELIMITER $
DROP PROCEDURE IF EXISTS pro_test4$
CREATE PROCEDURE pro_test4()
BEGIN
	-- 定义变量
	DECLARE total INT;
	DECLARE info VARCHAR(10);
	
	-- 查询总成绩，为total赋值
	SELECT SUM(score) INTO total FROM student;
	
	-- 对总成绩判断
	IF total > 380 THEN
		SET info = '学习优秀';
	ELSEIF total >= 320 AND total <= 380 THEN
		SET info = '学习不错';
	ELSE
		SET info = '学习一般';
	END IF;
	-- 查询总成绩和描述信息
	SELECT total,info;
END$

DELIMITER ;


-- 调用pro_test4存储过程
CALL pro_test4();
```



### 1.7 while循环

```sql
/*
	while循环
	初始化语句;
	WHILE 条件判断语句 DO
		循环体语句;
		条件控制语句;
	END WHILE;
*/
-- 计算1~100之间的偶数和
DELIMITER $
DROP PROCEDURE IF EXISTS pro_test6$
CREATE PROCEDURE pro_test6()
BEGIN
	-- 定义求和变量
	DECLARE result INT DEFAULT 0;
	-- 定义初始化变量
	DECLARE num INT DEFAULT 1;
	-- while循环
	WHILE num <= 100 DO
            IF num % 2 = 0 THEN
                SET result = result + num;
            END IF;
		-- 这里条件递增，不要忘记
		SET num = num + 1;
	END WHILE;
	
	-- 查询求和结果
	SELECT result;
END$

DELIMITER ;


-- 调用pro_test6存储过程
CALL pro_test6();
```



### 1.8 存储过程传参

入参：调用的时候输入的参数，供存储过程需要以入参作为基础进行运算。相当于Java中的形参。

出参：存储过程产生的结果，可以通过出参传递给调用者。相当于Java中的返回值。

```sql
/*
	参数传递
	CREATE PROCEDURE 存储过程名称([IN|OUT|INOUT] 参数名 数据类型)
	BEGIN
		SQL 语句列表;
	END$
	
	IN 入参，如果什么都不写，默认是入参IN
	OUT	出参，相当于Java中的返回值，把存储过程的结果传递给调用者，调用者一般使用用户变量接收。
	INOUT	= IN + OUT
	格式：
		IN 变量名 变量的数据类型
*/
/*
	输入总成绩变量，代表学生总成绩
	输出分数描述变量，代表学生总成绩的描述信息
	根据总成绩判断：
		380分及以上  学习优秀
		320 ~ 380    学习不错
		320以下      学习一般
*/
DELIMITER $

CREATE PROCEDURE pro_test5(IN total INT,OUT info VARCHAR(10))
BEGIN
	-- 对总成绩判断
	IF total > 380 THEN
		SET info = '学习优秀';
	ELSEIF total >= 320 AND total <= 380 THEN
		SET info = '学习不错';
	ELSE
		SET info = '学习一般';
	END IF;
END$

DELIMITER ;

-- 调用pro_test5存储过程
-- 调用存储过程时候的用户变量无需声明和赋值，直接使用即可
CALL pro_test5(350,@info);

-- 调用存储过程的时候，可以把子查询的结果作为实参传递
CALL pro_test5((SELECT SUM(score) FROM student),@info);

SELECT @info;
```



### 1.9 存储函数(了解)

```sql
/*
	创建存储函数
	CREATE FUNCTION 函数名称([参数 数据类型])
	RETURNS 返回值类型
	BEGIN
		执行的sql语句;
		RETURN 结果;
	END$
*/
-- 定义存储函数，获取学生表中成绩大于95分的学生数量
DELIMITER $
DROP FUNCTION IF EXISTS fun_test1$
CREATE FUNCTION fun_test1()
RETURNS INT
BEGIN
	-- 定义变量
	DECLARE s_count INT;
	-- 查询成绩大于95分的数量，为s_count赋值
	SELECT COUNT(*) INTO s_count FROM student WHERE score > 95;
	-- 返回统计结果
	RETURN s_count;
END$

DELIMITER ;


/*
	调用函数
	SELECT 函数名称(实际参数);
*/
-- 调用函数
SELECT fun_test1();


/*
	删除函数
	DROP FUNCTION 函数名称;
*/
-- 删除函数
DROP FUNCTION fun_test1;
```



## 2. 触发器(理解)

### 2.1 概念

在对数据库表中记录进行增删改，自动监控到并执行相应`SQL`语句的一个对象（机制）。

- 应用场景：保证数据的完整性、日志记录、数据校验等操作。
- 可以使用别名OLD和 NEW ，在触发器中引用`增删改操作修改前后的数据`。

- Insert、Update、Delete

三种类型触发器使用别名的规则

| 触发器类型      | OLD                            | NEW                           |
| --------------- | ------------------------------ | ----------------------------- |
| INSERT 型触发器 | 无 (因为插入前无数据)          | NEW表示将要或者已经新增的数据 |
| UPDATE 型触发器 | OLD表示修改之前的数据          | NEW表示将要或已经修改后的数据 |
| DELETE 型触发器 | OLD 表示将要或者已经删除的数据 | 无 (因为删除后状态无数据)     |

### 2.2 Insert型触发器

```sql
/*
	触发器，会让整个系统中的的数据库瓶颈更严重，所以在项目上线后是绝对不允许使用。

	创建触发器
	DELIMITER $

	CREATE TRIGGER 触发器名称
	BEFORE|AFTER INSERT|UPDATE|DELETE
	ON 表名
	FOR EACH ROW
	BEGIN
		触发器要执行的功能;
	END$

	DELIMITER ;
*/
-- 创建INSERT型触发器。用于对account表新增数据进行日志的记录
DELIMITER $

CREATE TRIGGER account_insert
AFTER INSERT
ON account
FOR EACH ROW
BEGIN
	INSERT INTO account_log VALUES (NULL,'INSERT',NOW(),new.id,CONCAT('插入后{id=',new.id,',name=',new.name,',money=',new.money,'}'));
END$

DELIMITER ;

-- 向account表添加一条记录
INSERT INTO account VALUES (NULL,'王五',2000);

-- 查询account表
SELECT * FROM account;

-- 查询account_log表
SELECT * FROM account_log;
```



### 2.3 UPDATE型触发器

```sql
/*
	创建触发器
	DELIMITER $

	CREATE TRIGGER 触发器名称
	BEFORE|AFTER INSERT|UPDATE|DELETE
	ON 表名
	FOR EACH ROW
	BEGIN
		触发器要执行的功能;
	END$

	DELIMITER ;
*/
-- 创建UPDATE型触发器。用于对account表修改数据进行日志的记录
DELIMITER $

CREATE TRIGGER account_update
AFTER UPDATE
ON account
FOR EACH ROW
BEGIN
	INSERT INTO account_log VALUES (NULL,'UPDATE',NOW(),new.id,CONCAT('更新前{id=',old.id,',name=',old.name,',money=',old.money,'}','更新后{id=',new.id,',name=',new.name,',money=',new.money,'}'));
END$

DELIMITER ;


-- 修改account表中李四的金额为2000
UPDATE account SET money=2000 WHERE id=2;

-- 查询account表
SELECT * FROM account;

-- 查询account_log表
SELECT * FROM account_log;
```



### 2.4 Delete型触发器

```sql
/*
	创建触发器
	DELIMITER $

	CREATE TRIGGER 触发器名称
	BEFORE|AFTER INSERT|UPDATE|DELETE
	ON 表名
	FOR EACH ROW
	BEGIN
		触发器要执行的功能;
	END$

	DELIMITER ;
*/
-- 创建DELETE型触发器。用于对account表删除数据进行日志的记录
DELIMITER $

CREATE TRIGGER account_delete
AFTER DELETE
ON account
FOR EACH ROW
BEGIN
	INSERT INTO account_log VALUES (NULL,'DELETE',NOW(),old.id,CONCAT('删除前{id=',old.id,',name=',old.name,',money=',old.money,'}'));
END$

DELIMITER ;

-- 删除account表中王五
DELETE FROM account WHERE id=3;

-- 查询account表
SELECT * FROM account;

-- 查询account_log表
SELECT * FROM account_log;
```



### 2.5 触发器的查看和删除

```sql
/*
	查看触发器
	SHOW TRIGGERS;
*/
-- 查看触发器
SHOW TRIGGERS;


/*
	删除触发器
	DROP TRIGGER 触发器名称;
*/
-- 删除account_delete触发器
DROP TRIGGER account_delete;
```







## 3. 事务(重点)

### 3.1 概念

事务：本质上是数据库操作的一种安全机制

作用：

1. 能保证一组对数据库的操作要么同时成功要么同时失败。
2. 多个同时对数据库的操作不受相互影响。

如果成功了，就表示提交了；如果失败了，就要回滚。



### 3.2 事务的操作

**start transaction**：开启事务，在一组对数据库的操作开始之前开启事务。

**rollback**：回滚事务，该组操作结束前出了问题，为了保证数据准确，就需要回滚事务。

**commit**：提交事务，整组操作过程中没有出任何问题，操作完整之后就可以提交事务，让记录的修改真正持久化到数据库中。

```sql
-- 张三给李四转账500元
/*
	try{
		// 开启事务
		// Java代码实现张三扣款500
		// int i = 1/0;
		// Java代码实现李四收款500
		// 提交事务
	}catch(exception e){
		// 回滚事务
	}
*/

-- 开启事务
START TRANSACTION;

-- 1.张三账户-500
UPDATE account SET money=money-500 WHERE NAME='张三';

 出错了...

-- 2.李四账户+500
UPDATE account SET money=money+500 WHERE NAME='李四';

-- 回滚事务
ROLLBACK;

-- 提交事务
COMMIT;
```



### 3.3 事务的提交方式

自动提交（默认的方式）：每个`SQL`语句，执行前都会自己开一个事务，执行完就自动提交了。

手动提交：把自动提交改成手动提交，这时，所有对数据库的修改必须手动`commit`之后才能最终保存。

设置成手动提交的两种方式：

1. 修改全局变量`@@AUTOCOMMIT = 0`（0表示手动提交，1表示自动提交<默认值>）
2. 手动开启事务，start transaction。开启事务后，事务不会自动提交，需要手动提交。

> 注意：通过全局变量修改提交方式的做法比较危险，不推荐这么做。
>
> 第二种做法也是工作中常用的做法。



```sql
/*
	查询事务提交方式：SELECT @@AUTOCOMMIT;  -- 1代表自动提交    0代表手动提交
	修改事务提交方式：SET @@AUTOCOMMIT=数字;
	
	1. 默认自动提交，但执行一个SQL操作时，会自动开启事务，成功操作做之后自动提交
	2. 修改为手动提交，如果想要最终保存到数据库中，需要手动提交 COMMIT;
		2.1 SET @@autocommit = 0;
		2.2 显式的开启了事务 START TRANSACTION;当前事务就不会自动提交了
*/
-- 查询事务的提交方式
SELECT @@autocommit;

UPDATE account SET money=2000 WHERE id=1;

COMMIT;


-- 修改事务的提交方式
SET @@autocommit = 1;


-- 开启事务
START TRANSACTION;

-- 对数据库记录进行修改操作
UPDATE account SET money = 5000 WHERE id = 1;

-- 提交事务
COMMIT;
```



### 3.4 事务的特性

ACID

- 原子性(Atomicity)
  当前事务下的一组操作是最小的操作单元，不能被拆分。

- 一致性(Consistency)

  该事务操作前后，数据库内的数据总量保持一致。相对概念。

- 隔离性(isolocation)

  多个事务对数据的操作之间尽量不受相互影响。

- 持久性(durability)

  事务一旦提交，该事物中对数据库的修改操作，必须要能够持久化(保存到硬盘)。

### 3.5 事务的隔离级别

| 序号 | 隔离级别         | 名称     |
| ---- | ---------------- | -------- |
| 1    | read uncommitted | 读未提交 |
| 2    | read committed   | 读已提交 |
| 3    | repeatable read  | 可重复读 |
| 4    | serializable     | 串行化   |



**并发访问的问题**

| 问题       | 现象                                                         |
| ---------- | ------------------------------------------------------------ |
| 脏读       | 在一个事务中读取到了另一个事务修改但未提交的数据。           |
| 不可重复读 | 在一个事务中多次读取，但是分别读取到了另一个事务中修改并已提交前后的数据, 导致两次查询结果不一致。 |
| 幻读       | 查询某数据不存在，准备插入此记录，但执行插入时发现此记录已存在，无法插入。或查询数据不存在执行删除操作，却发现删除成功。 |



查看/修改单当前数据库的隔离级别

> SELECT @@TX_ISOLATION; //查看当前的隔离界别。mysql默认的隔离级别是 repeatable read。
>
> ​															// Oracle默认的隔离级别是read committed

> SET GLOBAL TRANSACTION ISOLATION LEVEL 级别字符串; //设置隔离级别，需要重新打开连接才能生效
> 		// 生产环境中，隔离级别不要动！！！



### 3.6 脏读及解决办法

在一个事务处理过程中读取到了另一个未提交事务中的修改数据 。

演示：

> 窗口1：

```sql
/*
	脏读的问题演示和解决
	脏读：一个事务中读取到了其他事务未提交的数据
*/
-- 设置事务隔离级别为read uncommitted
SET GLOBAL TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- 开启事务
START TRANSACTION;

-- 转账
UPDATE account SET money = money-500 WHERE NAME='张三';
UPDATE account SET money = money+500 WHERE NAME='李四';

-- 查询account表
SELECT * FROM account;

-- 回滚
ROLLBACK;

-- 提交事务
COMMIT;
```



> 窗口2：

```sql
-- 查询事务隔离级别
SELECT @@tx_isolation;

-- 开启事务
START TRANSACTION;

-- 查询account表
SELECT * FROM account;

-- 提交事务
COMMIT;
```

> 现象：窗口2中读取到了窗口1中未提交的事务。



> 解决办法：提高隔离级别，该级别下可以解决脏读问题，但是另外两个问题无法解决
>
> 可以通过把事务的隔离级别从`read uncommitted`修改为`read committed`。
>
> SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED;



### 3.7 不可重复读及解决办法

 在一个事务处理过程中多次读取同一记录，但是分别读取到了另一个事务中修改并已提交前后的数据, 导致两次查询结果不一致。



> 一般不会在同一个事务中多次读取相同内容。
>
> 但是在`读已提交`这个隔离级别下，数据库集群相互复制数据的时候会有问题（复制数据的时候会造成数据短暂性的不一致）；通过调高隔离级别，在开启事务的时候就“拍摄快照”，本地复制所有内容都是基于该快照，最终避免复制时候的问题发生。

> 窗口1：

```sql
/*
	不可重复读的问题演示和解决
	不可重复读：一个事务中读取到了其他事务已提交的数据
*/
-- 设置事务隔离级别为read committed
SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED;
SET GLOBAL TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- 开启事务
START TRANSACTION;

-- 转账
UPDATE account SET money = money-500 WHERE NAME='张三';
UPDATE account SET money = money+500 WHERE NAME='李四';

-- 查询account表
SELECT * FROM account;

-- 提交事务
COMMIT;
```



> 窗口2：

```sql
-- 查询隔离级别
SELECT @@tx_isolation;

-- 开启事务
START TRANSACTION;

-- 查询account表
/*
	当数据库的隔离级别是 REPEATABLE READ的时候，
	下面的查询会在第一次执行时为目标表创建一个快照
	只要在本次事务中，所有的查询都是基于该快照的，
	所以，无论其他事务中对该表做何种修改以及是否提交，都不会影响本次事务中查询的结果。
*/
-- 
SELECT * FROM account;

-- 提交事务
COMMIT;
```



> 现象：在窗口2事务开启后关闭前，多次读取同一条记录结果却不一样，所以叫不可重复读。

**可重复读**

保证在**`同一个事`**务里面，多次读取同一记录的结果要一样。

> 解决办法：修改隔离级别为可重复读
>
> SET GLOBAL TRANSACTION ISOLATION LEVEL REPEATED READ;





### 3.8 幻读及解决办法

查询某数据不存在，准备插入此记录，但执行插入时发现此记录已存在，无法插入。或查询数据不存在执行删除操作，却发现删除成功。

> 窗口1：

```sql
/*
	幻读的问题演示和解决
	查询某记录是否存在，不存在
	准备插入此记录，但执行插入时发现此记录已存在，无法插入
	或某记录不存在执行删除，却发现删除成功
*/
-- 设置隔离级别为repeatable read
SET GLOBAL TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SET GLOBAL TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- 开启事务
START TRANSACTION;

-- 添加记录
INSERT INTO account VALUES (3,'王五',2000);
INSERT INTO account VALUES (4,'赵六',3000);

-- 查询account表
SELECT * FROM account;

-- 提交事务
COMMIT;	
```





> 窗口2：

```sql
-- 查询隔离级别
SELECT @@tx_isolation;

-- 开启事务
START TRANSACTION;

-- 查询account表
SELECT * FROM account;

-- 添加
INSERT INTO account VALUES (3,'王五',2000);

-- 提交事务
COMMIT;
```



> 解决办法：修改隔离级别为串行化，相当于锁表，知道有一个事务才操作这张表，其他任何事务不能再对该表做任何操作，直到前面的事务提交或回滚。
>
> SET GLOBAL TRANSACTION ISOLATION LEVEL serializable;



### 3.9 隔离级别汇总

是、否指的是在当前隔离级别下，是否会出现对应的并发访问问题。

| 序号 | 隔离级别         | 名称     | 脏读 | 不可重复读 | 幻读 | 数据库默认隔离级别 |
| ---- | ---------------- | -------- | ---- | ---------- | ---- | ------------------ |
| 1    | read uncommitted | 读未提交 | 是   | 是         | 是   |                    |
| 2    | read committed   | 读已提交 | 否   | 是         | 是   | Oracle             |
| 3    | repeatable read  | 可重复读 | 否   | 否         | 是   | MySQL              |
| 4    | serializable     | 串行化   | 否   | 否         | 否   |                    |

