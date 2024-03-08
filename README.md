# TestNetty

## Unity端项目github地址: 
https://github.com/GoldSprite/NetcodeLearn_HumanEatCoin  

### 2024.3.6.0
初始化项目： 简单登录Netty通信， 使用自定义PacketCodeC
定义协议格式: 魔数4->版本号1->序列化算法1->指令1->数据长度4->数据N

### 2024.3.6.1
增加消息包

### 2024.3.6.2
增加发消息输入
增加打包设置方便一键打包客户端与服务端

### 2024.3.7.0
修复客户端断连报错问题: 在服务端处理器捕获异常并注明客户端离线
尝试做C#客户端, 耗了一整天, 困难重重.

### 2024.3.7.1
调整了Log提示写明客户端ipport

### 2024.3.7.2 java C# tcp 通信
尝试java服务端+C#客户端:
- [x] 建立连接
- [x] 保持连接: 踩了半天坑, 原来C#Netty无法wait只能手动阻塞(现在采用Console.ReadKey()来阻塞才不会退出)
- [x] 已完成连接, 发送消息, 确认消息, 关闭连接流程.

### 2024.3.7.3-java tcp 通信(没有异步导致互相阻塞)
首先尝试模拟游戏通信流程: 检验tcp性能
 - 首先建立服务器1对多循环互发环境
 - 以每秒10次速度服务端与客户端互发并记录当前时间  
 - 结果是双方互相阻塞..(也可能是我没写好, 不过接下来改换udp继续尝试)

### 2024.3.7.4
制作discard丢弃型udp客服连接  
udp连接简单完成: 
 - 客户端向服务端持续发送数据包
 - 服务端受到数据包则回信来源端
 - 客户端收到服务端回信确认信息
 - 持续循环

### 2024.3.7.5
整理了下消息提示, 更明确了(时间, 对象..)  
改为互发消息, 测试流程次序:   
 - 好, 没问题: 双方互发(可能是本地的原因次序完全一致, 之后有网络延迟就不会这么舒畅了应该)

### 2024.3.7.6-java udp 通信
增加高频消息试试:  
 - 增加到125FPS速度持续10s: 结果13s完成, 次序几乎一致  
 - 增加到100个字试试: 效果同上完全一致  
 - 增加到2个客户端试试: 结果是3端互发也是一样, 效果同上基本都一致并不影响  
整理下消息整齐, 装上远程云服试验一下:   
 - 先在云服本地测试: OK没问题了

### 2024.3.7.7-java udp 外网通信
继续测试云服: 
 - 降低频率(60帧), 增加时长(1min), 本地客户端连接外网云服测试: 
   - 测试成功
   - 有个要点: Server/Client启动绑定写内网ip, 访问发送填外网ip; 以及Server需要做端口映射, client则不用

### 2024.3.7.8-此版本不可用
制作Log前缀信息工具类: NLog类log用来区分客户/服务端以及时间ipport等.  
**忘了, 先去C#制作udp客户端看是否兼容!!**
 - **不兼容**, C#可能因为类型实现不太一致这边不匹配, 收不到消息.

### 2024.3.8.0-初步完成java-C# udp互通信
最后最后试一波C#客户端/服务端自通信, 看是我代码问题还是javaC#类型确实不兼容.
 - **成功!**
 - 最后总结: 成功原因: 采用object类收发数据, 拿到之后再进行转码
   - 成功要点: 
     - udp与tcp区别在于两部分: 频道类型与连接类型
     - 频道: 
       - tcp需要频道: TcpServerSocketChannel与TcpSocketChannel
       - udp需要DatagramChannel
     - 连接: 
       - tcp服务器使用bind本地ipport, 客户端connect服务器ipport进行连接, 连接完成后可对向发送消息.
       - udp服务端客户端均采用bind本地ipport, 并由设定的datagram频道来读取发送的DatagramPacket中remoteAddress锁定目标地址并发送消息.
     - C#与Java通信问题: 
       - C#采用Microsoft-DotNetty_0.7.5, Java采用netty_4.1.35final
       - Handler处理器会筛选对应的类型通过, 可能由于双方内部实现不一致, 导致DatagramPacket类型无法被handler处理器识别
       - 但是Udp频道必须传输DatagramPacket包, 所以, 最后结论: 
         1. 使用DatagramChannel频道解析
         2. 使用通用handler处理器过筛
         3. handler内部强转将obj数据进行转换, 手动过筛.

### 2024.3.8.1-试验handler出栈入栈流程  
处理器方法: 
~~~    
ctx.fireChannelRead(data);  //拆解
ctx.fireChannelReadComplete();  //声明结束当前拆解
ctx.write(data);  //包裹
ctx.flush();  //发送
~~~
 - 事件传递: fire..方法用于跳转下一处理器的对应方法, 不调用则事件结束在当前处理器不会继续传递.
 - 传递顺序: 
   - 根据pipeline.addLast顺序, 
   - 入栈消息按InboundHandler自上到下read->readCompleted执行.
   - 出栈消息, 通过write与flush与writeAndFlush, 自下而上write->flush执行.
   - 调用即立即跳转执行, 无等待.
~~~
channel.pipeline().firstContext();  //可以获取第一个处理器
channel.pipeline().context(name);  //可以获取指定名字处理器
~~~

### 2024.3.9.0-完成收发包代码框架
做了个控制台输入指令来发不同包
完成收发包: 登录, 移动, 查询
完成可回调发包

### 2024.3.9.1-客户端guid(心跳不可用版本)
1. 尝试attr存储channel数据以区分客户端:   
   - 不行, 与tcp不同(tcp为每个客户在服务端分配channel), 绑定在服务端channel上的attr对udp的服务端无效.      
2. 也不能使用ipport, 有些ip会动态变动.  
3. 尝试为请求包不带Guid的客户端分配Guid
   - 并将ipport等信息以键值对存到服务端列表, 下次接收请求包时即可辨别客户端
   - 弊端是每个packet都得携带身份标识, 会比较耗带宽
   - 改为int型id比较好
   - 流程为: 
     - 客户端登录请求->服务端增加客户端信息->返回guid->客户端保存
4. 做了半个心跳检测

### 2024.3.9.2
梳理理清心跳逻辑流程
简化发包流程: encoder自动打包, guid

### 待办
考虑优化响应包: 封装一个父级响应包表示响应码, 使用回调来传递响应数据内容.
增加心跳检测
在服务端存储客户端信息字典:
- 包括: 客户端Guid, 对应: 客户端InetSocketAddress, 玩家名字, 登录时长, 心跳计时
- 在每个Packet携带OwnerGuid, 在Logic处理器判断客户端并处理逻辑  
  简化出栈步骤: 在客户端write出栈数据时, 通过Encoder处理器拦截并自动加上远程地址并编码信息.  