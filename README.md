# toolbox
自己常用的一些工具类或者有用的封装类

#### 1. mapple  
对实体类对象进行map的转换.  
在请求返回数据的时候,对从数据库取出来的数据,不应该全部返回给前端,所以通常我会重新定义一个map,对实体类字段重新命名为前端好识别的字段,类似我们使用的vo.  
maple主要用来对实体类转化,按照自己的想法进行定制.

```
Map<String, Object> map = MapleUtil.wrap(u)
                .rename("child", "catty")
                .stick("tom", new Object())
                .stick("小猫", "hellokitty")
                .rename("catty", "bigBoy")
                .map();
```

目前有重命名,插入,修改值,最后转化为map.