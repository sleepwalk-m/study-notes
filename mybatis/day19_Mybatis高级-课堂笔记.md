## 1. 注解开发(理解)

==对于简单的`SQL`，建议使用注解；对于复杂`SQL`，建议使用`xml`配置映射配置文件。==

### 	==1.1 常用注解==

- ==@Insert：实现新增		相当于<insert>标签==

- ==@Update：实现更新		相当于<update>标签==

- ==@Delete：实现删除		相当于<delete>标签==

- ==@Select：实现查询		相当于<select>标签==

  


### ==1.3查询-单表==

```java
interface StudentMapper{
    // 标注在方法是，可以通过注解获取到方法的返回值类型、参数类型、该方法属于哪个类
    @Select("select * from student")
    List<Student> findAll();
}
```



### ==1.4 新增-单表==

`StudentMapper`接口

```java
interface StudentMapper{
    //新增操作
    @Insert("INSERT INTO student VALUES (#{id},#{name},#{age})")
    Integer insert(Student stu);
}
```

测试代码

```java
@Test
public void insert() throws Exception{
    //1.加载核心配置文件
    InputStream is = Resources.getResourceAsStream("MyBatisConfig.xml");

    //2.获取SqlSession工厂对象
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);

    //3.通过工厂对象获取SqlSession对象
    SqlSession sqlSession = sqlSessionFactory.openSession(true);

    //4.获取StudentMapper接口的实现类对象
    StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);

    //5.调用实现类对象中的方法，接收结果
    Student stu = new Student(4,"赵六",26);
    Integer result = mapper.insert(stu);

    //6.处理结果
    System.out.println(result);

    //7.释放资源
    sqlSession.close();
    is.close();
}
```



### ==1.5 修改-单表==

`StudentMapper`接口

```java
interface StudentMapper{
    //修改操作
    @Update("UPDATE student SET name=#{name},age=#{age} WHERE id=#{id}")
    public abstract Integer update(Student stu);
}
```

测试代码

```java
@Test
public void update() throws Exception{
    //1.加载核心配置文件
    InputStream is = Resources.getResourceAsStream("MyBatisConfig.xml");

    //2.获取SqlSession工厂对象
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);

    //3.通过工厂对象获取SqlSession对象
    SqlSession sqlSession = sqlSessionFactory.openSession(true);

    //4.获取StudentMapper接口的实现类对象
    StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);

    //5.调用实现类对象中的方法，接收结果
    Student stu = new Student(4,"赵六",36);
    Integer result = mapper.update(stu);

    //6.处理结果
    System.out.println(result);

    //7.释放资源
    sqlSession.close();
    is.close();
}
```



### ==1.6 删除-单表==

`StudentMapper`接口

```java
interface StudentMapper{
    //删除操作
    @Delete("DELETE FROM student WHERE id=#{idx}")
    public abstract Integer delete(@Param("idx")Integer id);
}
```

测试代码

```java
@Test
public void delete() throws Exception{
    //1.加载核心配置文件
    InputStream is = Resources.getResourceAsStream("MyBatisConfig.xml");

    //2.获取SqlSession工厂对象
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);

    //3.通过工厂对象获取SqlSession对象
    SqlSession sqlSession = sqlSessionFactory.openSession(true);

    //4.获取StudentMapper接口的实现类对象
    StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);

    //5.调用实现类对象中的方法，接收结果
    Integer result = mapper.delete(4);

    //6.处理结果
    System.out.println(result);

    //7.释放资源
    sqlSession.close();
    is.close();
}
```



### 1.7 一对一查询

`CardMapper.java`

```java
public interface CardMapper {
    //查询全部
    @Select("SELECT * FROM card")  //@Select 相当于Select标签
    @Results({                      //@Results 相当于 ResultMap标签
            @Result(column = "id",property = "id",id = true), // @Result + id=true相当于id标签
            @Result(column = "number",property = "number"), //// @Result + id=fasle相当于result标签
            @Result(                                            //@Result 相当于association
                    property = "p",             // 被关联对象的变量名
                    javaType = Person.class,    // 被关联对象的实际数据类型
                    column = "pid",             // 根据查询出的card表中的pid字段来查询person表
                    /*
                        one = @One(select="") 一对一分步查询，延迟加载的固定写法
                        select属性：指定查询关联对象时，调用哪个接口中的哪个方法
                     */
                    one = @One(select = "com.itheima.one_to_one.PersonMapper.selectById")
            )
    })
    public abstract List<Card> selectAll();
}
```



`PersonMapper.java`

```java
public interface PersonMapper {
    //根据id查询
    @Select("SELECT * FROM person WHERE id=#{id}")
    public abstract Person selectById(Integer id);
}
```



### 1.8 一对多查询

`ClassesMapper.java`

```java
public interface ClassesMapper {
    //查询全部
    @Select("SELECT * FROM classes")
    @Results({
            @Result(column = "id",property = "id",id=true),
            @Result(column = "name",property = "name"),
            @Result(
                    property = "students",  // 被包含对象的变量名
                    javaType = List.class,  // 被包含对象的实际数据类型
                    column = "id",          // 根据查询出的classes表的id字段来查询student表
                    /*
                        many = @Many(select="") 一对多分步查询，延迟加载的固定写法
                        select属性：指定查询关联对象时，调用哪个接口中的哪个方法
                     */
                    many = @Many(select = "com.itheima.one_to_many.StudentMapper.selectByCid")
            )
    })
    public abstract List<Classes> selectAll();
}
```

`StudentMapper.java`

```java
public interface StudentMapper {
    //根据cid查询student表
    @Select("SELECT * FROM student WHERE cid=#{cid}")
    public abstract List<Student> selectByCid(Integer cid);
}
```





### 1.9 多对多

多对多的本质就是多个一对多，可以使用一对多映射多对多。

```java
public interface StudentMapper {
    //查询有选课信息的全部学生
    @Select("SELECT DISTINCT s.id,s.name,s.age FROM student s,stu_cr sc WHERE sc.sid=s.id")
    // 查询所有学生
    // @Select("SELECT * FROM student ")
    @Results({
            @Result(column = "id",property = "id"),
            @Result(column = "name",property = "name"),
            @Result(column = "age",property = "age"),
            @Result(
                    property = "courses",   // 被包含对象的变量名
                    javaType = List.class,  // 被包含对象的实际数据类型
                    column = "id",          // 根据查询出student表的id来作为关联条件，去查询中间表和课程表
                    /*
                        many = @Many(select="") 一对多分步查询，延迟加载的固定写法
                        select属性：指定查询关联对象时，调用哪个接口中的哪个方法
                     */
                    many = @Many(select = "com.itheima.many_to_many.CourseMapper.selectCourseByStudentid")
            )
    })
    public abstract List<Student> selectAll();
}
```



`CourseMapper.java`

```java
public interface CourseMapper {
    //根据学生id查询所选课程
    @Select("SELECT c.id,c.name FROM stu_cr sc,course c WHERE sc.cid=c.id AND sc.sid=#{id}")
    public abstract List<Course> selectCourseByStudentid(Integer studentId);
}
```







- - 

  
## 3. `SQL`构建(了解)

将`SQL`写在注解中不美观且手动写`SQL`容易出错，可以通过`mybatis`提供的构建自动生成`SQL`语句

select * from

### 3.1 注解

`@SelectProvider`

`@InsertProvider`

`@UpdateProvider`

`@DeleteProvider`

### 3.2 使用

标注在`dao`层接口的方法上，获取用户构建好的`SQL`语句

- `type`属性：指定构建`SQL`语句的类的类型  `Xxx.class`

- `method`属性：指定构建`SQL`语句的类中的方法名(字符串形式，无括号)，`"getAll"`

  如果说`SQL`语句需要传递参数，在构造`SQL`的方法形参位置定义与`dao`层接口对应方法相同类型的参数即可。





### 3.3 代码

构建`SQL`语句的`ReturnSql.java`

```java
public class ReturnSql {
    //定义方法，返回查询的sql语句
    public String getSelectAll() {
        /*return new SQL() {
            {
                SELECT("*");
                FROM("student");
            }
        }.toString();*/
        return new SQL().SELECT("*").FROM("student").toString();
    }

    //定义方法，返回新增的sql语句
    public String getInsert(Student stu) {
        /*return new SQL() {
            {
                INSERT_INTO("student");
                INTO_VALUES("#{id},#{name},#{age}");
            }
        }.toString();*/
        return "INSERT INTO student VALUES (#{id},#{name},#{age})";
    }

    //定义方法，返回修改的sql语句
    public String getUpdate(Student stu) {
        return new SQL() {
            {
                /*UPDATE("student");
                SET("name=#{name}", "age=#{age}");
                WHERE("id=#{id}");*/
                UPDATE("student").SET("name=#{name}", "age=#{age}").WHERE("id=#{id}");
            }
        }.toString();
    }

    //定义方法，返回删除的sql语句
    public String getDelete(Integer id) {
        return new SQL() {
            {
                DELETE_FROM("student");
                WHERE("id=#{id}");
            }
        }.toString();
    }
}
```

数据持久层接口类`StudentMapper.java`

```java
public interface StudentMapper {
    //查询全部
    //@Select("SELECT * FROM student")
    /*
        type：生成SQL语句的类
        method：生成SQL语句的类的方法名称
    * */

    @SelectProvider(type = ReturnSql.class , method = "getSelectAll")
    public abstract List<Student> selectAll();


    //新增功能
    //@Insert("INSERT INTO student VALUES (#{id},#{name},#{age})")
        /*
        type：生成SQL语句的类
        method：生成SQL语句的类的方法名称
    * */
    @InsertProvider(type = ReturnSql.class , method = "getInsert")
    public abstract Integer insert(Student stu);

    //修改功能
    //@Update("UPDATE student SET name=#{name},age=#{age} WHERE id=#{id}")
    @UpdateProvider(type = ReturnSql.class , method = "getUpdate")
    public abstract Integer update(Student stu);

    //删除功能
    //@Delete("DELETE FROM student WHERE id=#{id}")
    @DeleteProvider(type = ReturnSql.class , method = "getDelete")
    public abstract Integer delete(Integer id);
}

```





## 3. 学生管理系统



整合

1. 导包 `mybatis`  `log4j`

2. 编写配置文件核心配置使用`xml`

3. 在接口中为每个方法编写`SQL`语句（映射配置使用注解）

4. 在`Service`中要要修改获取`dao`层接口实现类对象的方式

   从原来自己new一个，改成找`Mybatis`的`SQLSession`获取



`MybatisUtils.java`

```java
public class MybatisUtils<T> {

    private static SqlSession sqlSession = null;
    private static InputStream is = null;

    private MybatisUtils() {
    }


    // 提供获取Dao层接口代理对象的方法
    public static SqlSession getSqlSession() {
        try {
            //1.加载核心配置文件
            is = Resources.getResourceAsStream("MyBatisConfig.xml");

            //2.获取SqlSession工厂对象
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);

            //3.通过工厂对象获取SqlSession对象
            sqlSession = sqlSessionFactory.openSession(true);


        } catch (Exception e) {
			e.printStackTrace();
        }
       return sqlSession;
    }


    // 释放资源
    public static void realse(){
        sqlSession.close();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```





























































`Mybatis`缓存

- 总共分两级
  - 一级缓存（本地缓存，`SQLSession`级别，默认开启）
  - 二级缓存（全局缓存，`namespace`级别，手动开启）
- 一级缓存失效的四种情况
  - `SQLSession`不同
  - `SQLSession`相同，查询条件不同（当前`SQLSession`中没有要查询的数据）
  - `SQLSession`相同，查询条件相同，在第二次查询前进行了增删改（被查数据可能会被修改）
  - 手动清除了缓存
- 二级缓存
  - 工作机制
  - a.会话查询的数据，会被保存在当前`SQLSession`级别的一级缓存中
  - b. 如果会话关闭，一级缓存中的数据会保存在二级缓存中；新建会话查询时，可以从二级缓存获取。
  - 不同的`Dao`查询的数据会放在自己的`namespace`中

- 使用步骤
  - 开启二级缓存 `cacheAble=true`
  - `dao`映射配置文件中开启 cache标签
  - 实体类实现序列化接口