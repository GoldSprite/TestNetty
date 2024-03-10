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

### 2024.3.9.2-代码简洁度全面提升
1. 梳理理清心跳逻辑流程: 
   - 秒和毫秒转化有问题, 全部以毫秒为准, 显示转为日期时间, 再尝试一下
   - 心跳完成.
2. 尝试直接一个方法发包: 传入packet以及回调, ppid自动填充
   1. 将ppid写入packet
3. 完成登录与心跳发包与回调流程简化

### 2024.3.9.3-啥也没干
1. 不行不可以将所有响应包去掉与请求包融合: 
   - 一是数据太大, 
   - 二是请求与响应可能内容完全不同, 会有太多无用数据
2. 现在还没有到做内容的阶段, 做好结构以便扩展才是正事.
   - 得思考如何将项目供给Unity使用, 可以尝试打包dll
   - 尝试半天IKVM, 行不通, 依赖太多, 独立小jar包还可以
3. 现在代码还是太复杂: 得做一下单一职责

### 2024.3.10.0-移除陈旧tcp代码
 - [x] 限制没发登陆包(不在客户端列表)之前发什么都不接
 - 移除旧代码(tcp相关)
 
### 2024.3.10.1-修复bug, 统一代码, 云服ip更换(自己看对比[懒])
 
### 2024.3.10.2
 - [x] 取消callbacks-ppid设定, 将包类型作为callbacks键, 调用时直接调用并释放该类包下所有callbacks

### 2024.3.10.3-代码清空整理
 - 重写Packet结构: 
   1. Packet拥有: 
      1. 版本号: 用于识别编码版本
      2. 指令id: 编解码不同指令包
      3. 响应id: 标识不同反馈 
      4. 客户端Guid: 识别包来源身份, 区分不同客户端
 - [x] 新建IStatus存放响应id, 将ICommand冗余响应id移除, 并同步更新所有引用
 - [x] 美化代码结构: Packet, PacketEncoder.java, 
 - [x] 清空所有包处理逻辑

### 2024.3.11.0-项目重新转j11
 - 注释所有lombok.var
 - 项目重新转j11

### 2024.3.11.1-一堆, 自己看
 - 去除MessageResponsePacket.java多余变量
 - 处理handleMessageRequestPacket: 显示消息并直接发送回包
 - 删除除Message外所有包
 - Packet增加构造函数public Packet(int ownerGuid)以保证所有包必须传入客户端识别id
 - 增加未登录拦截开关并关闭: Server.strangerIntercept
 - 验证消息包回包
 - 增加isResponsePacket判定用于发包时不调用回调
 - 增加发包异常检查: 地址为空
 - 重新增加登录包收发: 必须基于登录做客户端地址存储才能继续回包
   - 编解码器增加登录指令表
   - 指令登录并发包,
   - 处理器处理登录录入客户端信息, 并回包
   - 客户端处理回包录入分配的guid, 并调用回调(如果有)
 - getStatusMsg
 - callbacks转列表
 - 验证
 - 统一NLog

### 2024.3.11.2-NLog嵌套了

### 2024.3.11.3
- 消息响应
- 重复登陆不回复的问题
- 完善IStatus回复文本
- 更新老方法 IStatus.msgMap.get(IStatus.RETURN_DEFEAT_LOGIN_REPEAT)
- 登录验证通过才可录入 UdpServer.Instance.endGuid++; 
- 状态码尾号1开始 RETURN_SUCCESS_LOGIN = 30100 
- PacketEncoder没有处理异常增加一个在write exceptionCaught 
- 编码和验证融合为encodeAuthentication并增加玩家不在线验证 networkAddress = client.address;

### 2024.3.11.4-修复一个报错
- 想显示给自己自己发了啥,在本地Log就行 ``MessageResponsePacket响应成功  ``
- 可能没put.arrList报空指针``if (pkCallbacks.size() > 0)``  
- ``pk.setCode(IStatus.RETURN_DEFEAT_LOGIN_REPEAT);  ``
- 简化.getCode ``IStatus.isSuccessStatus(rep.getCode())  ``
- 成功状态没有信息, 用不上``String MSG_RETURN_SUCCESS_LOGIN = "登录成功";  ``
- ``handleMessageResponsePacket(MessageResponsePacket pk) {}``
- 启动器类更名为server和client``UdpServer  ``
- 没写完, 准备加一个广播包``//广播``

### 2024.3.11.5-增加广播
- 新增包
  - 增加广播包 ``BroadcastRequestPacket``
  - 增加广播指令类型 ``BROADCAST_REQUEST``
  - 在编解码器增加表 ``packetTypeMap.put(BROADCAST_REQUEST, BroadcastRequestPacket.class);``
- 处理逻辑
  - 删掉无用的遍历 ``handleMessageResponsePacket``
  - 处理广播收发的两个case ``case ICommand.BROADCAST_REQUEST``
  - 在广播请求包广播给所有人广播响应 ``Server.Instance.clients.forEach((guid, v)->{``
  - 增加广播terminal指令 ``case "broadcast": {``
  - terminal帮助``"\n广播: /broadcast message..."``
- 运行测试+修改
  - 修bug可能没保存好无限嵌套了 ``return isReturnStatus(pk);``
  - 这块之后做成NLogDebug和NLogMsg区分开 ``收到包类型: BroadcastResponsePacket``
  - 忘了得继承ResponsePacket并重写构造 ``BroadcastResponsePacket extends Packet``
  - 连带更新别忘了传入返回码 ``new BroadcastResponsePacket(guid, IStatus.RETURN_SUCCESS, msg);``
  - 给玩家展示log``"收到广播消息: "+pk.getMessage()``

### 2024.3.11.6-明确日志等级
- 增加日志等级 ``public static int LogLevels;``
- 增加日志等级接口 ``interface ILogLevel {``
- 增加多个等级 ``final int LOG_MSG = 1;``
- 增加多种日志输出类型方法 ``static void NLogInfo(Object msg) {..``
- 替换所有NLog到NLogInfo ``replaceAll->NLogInfo``
- 将loglevels改为字典表筛选 ``put(ILogLevel.ERROR, true);``
- 条件为包含且开启则显示否则过滤该消息 ``if (!logLevels.containsKey(logLevel) || !logLevels.get(logLevel)) return;``
- terminal指令忘了msg改成broadcast ``msg.replaceFirst("broadcast ", "");``

### 2024.3.11.7


### 待办
1. 这块之后做成NLogDebug和NLogMsg区分开``收到包类型: BroadcastResponsePacket``
2. 把每种包处理逻辑分发不同的类处理
3. 统一所有输出日志到一个方法: 使用类似NLogV, NLogW, NLogD...
4. 我似乎忘了我做的是一个网络通信框架, 而不是一个服务器运行器
5. 所以我需要回到框架开发上来, 我需要开发一个基础代码结构, 它有几点是必须做到的:
    1. 首先, 它很小, 不需要花费大量的时间开发, 应该能只需要很快就可以完成
    2. 其次, 它能够让我不关心网络通信的细节, 可以专注于逻辑开发
    3. 最后明确它的功能:
        1.
        2. 可以开启指定ipport的服务端
        3. 可以设置心跳线程
        4. 可以获取维持心跳的在线端信息:
            1. 服务端获取所有客户端信息:
                1. guid识别代号
                2. ipport数据
                3. 登录时间
                4. 用户名
            2. 客户端获取服务端信息:
                1. ipport数据
已完成 **6** 项 