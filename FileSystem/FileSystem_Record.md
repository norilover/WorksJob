# Server&Client（4/6/2020-4/17）

>  用 JAVA 实现一个基于 HTTP 协议的简易文件服务器 Server 端和 Client 端  

### Server

> 主要使用 Jetty、Derby、Servlet、JDBC完成持久层



> 功能

1. 完成于前端服务器之间的通信
2. 数据存储、解密



> 记录

* 表结构

```
name: FileSystem
property:
	raw_name //the name before uploaded 
	name //UUID
	created_time
	saved_dir
	encry_data	
	size
	type
```




* 所需基本SQL操作
```sql
Derby> CREATE TABLE file_system(id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)primary key,
    -> raw_name VARCHAR(20) NOT NULL UNIQUE KEY,
    -> UUID_name VARCHAR(20) NOT NULL UNIQUE KEY,
    -> saved_dir VARCHAR(20) NOT NULL,
    -> saved_time TIMESTAMP NOT NULL,
    -> encry_data VARCHAR(20) NOT NULL,
    -> file_size VARCHAR(20) NOT NULL,
    -> file_type VARCHAR(20) NOT NULL);
    
CREATE TABLE file_system(id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)primary key,raw_name VARCHAR(20) NOT NULL,UUID_name VARCHAR(20) NOT NULL,saved_dir VARCHAR(20) NOT NULL,saved_time TIMESTAMP NOT NULL,encry_data VARCHAR(20) NOT NULL,file_size VARCHAR(20) NOT NULL,file_type VARCHAR(20) NOT NULL);

//id自动增长不用添加，在代码中使用PreparePreparedStatdStatement实现插入
Derby>  INSERT INTO file_system1(raw_name, UUID_name, saved_dir, saved_time, encry_data, file_size, file_type) VALUES('nori','norilover', '/etc/sys/', CURRENT_TIMESTAMP, 'encryPart', '100M', 'file');

Derby> SELECT raw_name FROM file_system;

//delete table
Derby> DROP TABLE table_name;

//clear the data of table
Derby> DELETE FROM table_name;
```



* Derby 辅助命令

```
//show structure of table;
describe table_name
```



* 使用Derby连接数据库

```
//connect
ij> connect 'jdbc:derby:testdb;create=true';
```



> JDBC记录



* Statements

To execute SQL statements against a database, an application uses *Statements* (*java.sql.Statement*) and *PreparedStatements* (*java.sql.PreparedStatement*), or *CallableStatements* (*java.sql.CallableStatement*) for stored procedures.

the relationship: use class diagram

​	Statements  <- *PreparePreparedStatdStatement*  <- *CallableStatement* 

```java
//例子：
Connection conn = JDBCUtil.getInstance().getConnection();
		String sql = "insert into user(name,birthday,money)values(?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1,name);
		ps.setDate(2,new java.sql.Date(birthday.getTime()));
		ps.setFloat(3, money);
		ps.executeUpdate();
```



* ResultSets

```
Executing a *Statement* that returns values gives a *ResultSet* (*java.sql.ResultSet*), allowing the application to obtain the results of the statement

一次statements执行，只对应一个resultSet, 且下次执行的statement会覆盖本次的resultSet(这里指的是statement中的result)
```



* 取消自动提交transaction

```
conn.setAutoCommit(false);
```



* insert

```
//这里没有value,只有values
insert into table_name values('nor',22);
```




> Q&A



Q: java.lang.ClassNotFoundException: org.apache.derby.jdbc.EmbeddedDriver

A:将Derby中的库文件加入classpath中，（在IDE中开发，只需将DERBY_HOME/lib/下所需的.jar文件加入项目即可）

```
具体的加入可按具体的需求添加
这里使用embed-Derby所以只需添加：derby.jar，derbytools.jar
file:///D:/Resources_Own/Resource_SWB/db-derby-10.2.2.0-bin/docs/html/getstart/index.html
```



Q:

```
Exception in thread "main" java.sql.SQLException: No suitable driver found for jdbc:edu.nori.derby:NoriDB;create=true
```

A:缺少lib，将Derby文件夹下的lib内的jar导入即可





### Client

> 主要使用SpringBoot完成控制层、服务层




> 功能

1. 显示元数据
2. 加密文件、密钥
3. 响应浏览器的下载（File、JOSN）



> Q&A



Q:@RestController()与@Controller()

SpringBoot测试

A:其实@RestController()包含了@Controller和@ResponseBody

```java
//测试

@RestController("/")
public class FileSystemController {
    @GetMapping("/")
    public String testInfo(){
        return "having a info";
    }
}

@Controller("/")
@ResponseBody
public class FileSystemController {
    @GetMapping("/")
    public String testInfo(){
        return "having a info";
    }
}

//访问 http://localhost:8080/
//都显示“having a info”

the difference of @RestController and @Controller:
	the former is consisted of @ResponseBody and @Controller 
	
The job of @Controller is to create a Map of the model object and find a view but @RestController simply returns the object and object data is directly written into HTTP response as JSON or XML.
```



Q:Checks autowiring problems in a bean class

A:在使用SpringBoot、SpringMVC时，未对服务层的类加@Service注解

```
在Service的实现类上添加@Service
若有DAO则在其实现类上添加@Repository
```



Q:怎样将字符数组密钥转换为字符串，且在之后可以再还原为字符数组密钥

A:使用：commons-codec-1.9.jar

```
byte[] key = generateKey();
//转换为字符串
String str = Base64.encodeBase64String(key)
//转换Q:为字符数组
byte[] fKey = Base64.decodeBase64(str);

//注意这里fKey和key的元素完全相同
byte[] data = fromOriginalData();
//加密
byte[] enData = encryData(data, key);
byte[] enData = encryData(data, fKey);
//下面使用fKey也可以解密
byte[] data1 = decodeData(enData, key);
byte[] data2 = decodeData(enData, fKey);

```



Q:怎样在两台服务器上生成对应的RSA密钥

A:事先，生成随机的一对RSA密钥，再将公钥、私钥对应的byte[]进行存储（这里我分别将其转换为字符串），将这些数据放在对应的服务器上，再使用时，对其进行还原，生成对应的公钥（私钥）。

这里是我随机生成的RSA私钥和公钥

```
private：
MIICcgIBADANBgkqhkiG9w0BAQEFAASCAlwwggJYAgEAAoGADy3RrM9dc3DYP40ywHgGBdXxdcgx6THqCm6IAdRM5OGANd0IVY+OLvQwAksoOnIML1S2GbbQaVCpAyVfY8pYZhcrH+m4ZUp6uuLxFxs6WVz0Kz4pYMh2F7RvDGOuuFpSvboGqv86x1HUfHOcPpi4e1am296L2oRlv+ZeZ8FWbaMCAwEAAQKBgACFpOZ6o04rHWTiKFKyThK5TvqsDftlOhVtw8O8V4CvuMcheP3oWA2JXHVXvGR2M7EVCsZLoVNd8NIo4OT/upaT/6+OgoBQYa/vAXcEtMFc4KA6BX9dNpYF6YKH22P0NHYAjW+FXAqISlSWUTrYBKHPNNweT8LN17Ao6PoQYJ2xAkA+viB3PofcDh9urQbCKDHVURunomEGL2j1JG4LrfVRGS0N+eI038eP2pvYpFxDCZ0fSs12rMIk9S2PBkP3EfWPAkA97sCBAdXVxzR5ySwKgIrWO5TaP/hoyLM70tv5SidBgulfkEv+bEZdP/ezhX6zVEELq6LUdrSyv57EP38trsStAkA0RSS68ucwVrus8oz814uckTOe1lJKWtjv0We0ZzpGU9kLGbBwKDYTDCZlt5f9aVbyqNi/E3GyZGeODcQ1Y0rVAkAwqAJVWDvHWFnB1GrlVVe+N9EaJmfi6srllSN9FuUAcvkmOxd5K1ecq0TPpXGFMgxoPtAOW7RbOnBhfgFMwqGBAkAhfyVUJLkjuyDt6/cSFGyz3lGZmSLPfYfkM5T1hMd1PhVklYZ+WiUkKUWUc2UDeH+SDvrfVqAHFuYRg8vzz6I8

public：
MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgA8t0azPXXNw2D+NMsB4BgXV8XXIMekx6gpuiAHUTOThgDXdCFWPji70MAJLKDpyDC9Uthm20GlQqQMlX2PKWGYXKx/puGVKerri8RcbOllc9Cs+KWDIdhe0bwxjrrhaUr26Bqr/OsdR1HxznD6YuHtWptvei9qEZb/mXmfBVm2jAgMBAAE=
```



Q:AES解密时报错

```
javax.crypto.BadPaddingException: Given final block not properly padded. Such issues can arise if a bad key is used during decryption.
```

A:查了许多，但都没有明确的答案，后来分析，发现是自己加密方式不对

最初加密、解密方式

```
加密：
	一次将文件读入输入流中，使用buffer数组，分段加密，将加密结果写入输出流
解密：
	加密方式相同
//直接一次全部加密，不分段也报这样的错误
```

使用Cipher输入、输出流方式

```
将文件放入Cipher自带的输出，写入Cipher自带的输出流，就可以解决问题，其实就是将原来的标准的输入、输出换成Cipher自带的，具体操作上面的一样。
```



A:Web项目的目录结构

Q:

![icon](./img/direction.jpg)






































