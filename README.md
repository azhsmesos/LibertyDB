LibertyDB 寓意自由，我性格较为自由随意洒脱，用Java写的时候就决定了它非一个内核DB项目。

注意，该项目参考自 mydb

编译执行
```agsl
mvn compile
```

创建数据库
```agsl
mvn exec:java -Dexec.mainClass="com.kuaishou.libertydb.Launcher" -Dexec.args="-create /tmp/libertydb"
```



- TM 通过维护 XID 文件来维护事务的状态，并提供接口供其他模块来查询某个事务的状态。

- DM 直接管理数据库 DB 文件和日志文件。DM 的主要职责有：1) 分页管理 DB 文件，并进行缓存；2) 管理日志文件，保证在发生错误时可以根据日志进行恢复；3) 抽象 DB 文件为 DataItem 供上层模块使用，并提供缓存。

- VM 基于两段锁协议实现了调度序列的可串行化，并实现了 MVCC 以消除读写阻塞。同时实现了两种隔离级别。

- IM 实现了基于 B+ 树的索引，BTW，目前 where 只支持已索引字段。

- TBM 实现了对字段和表的管理。同时，解析 SQL 语句，并根据语句操作表。