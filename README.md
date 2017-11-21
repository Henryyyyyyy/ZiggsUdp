# ZiggsUdp
udp发送工具类。具备发送超时返回自定义数据能力;处理回调无需切换ui线程

# Usage:


```java
     compile 'com.henry.ziggsudp:ziggslibrary:1.0.2'
```


## 1.在manifest添加权限以及service声明：

```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    
    <service android:name="me.henry.ziggslibrary.main.UdpService" />
   
```
## 2.在application类初始化:
```java
public class Myapp extends Application implements OnStartServiceFinishListener {
    @Override
    public void onCreate() {
        super.onCreate();
        UdpManager.getInstance()
                .context(this)
                .address("192.168.2.19")//udp服务器地址
                .port(8084)//端口
                .timeOut(5)//超时时间
                .CustomTimeOutString("FFFFFF")//超时的时候返回的信息
                .init()
                .createUdpService(this);

    }

    @Override
    public void onStartSuccess(UdpConnect udp) {
        ZiggsUdp.getInstance().setUdp(udp);//用ziggsUdp这个类来引用连接
    }
}

```
## 3.在页面中添加监听以及发送数据,回调
### 添加观察者：
```java
        mWatcher = new UdpDataWatcher(this);
        ZiggsUdp.getInstance().addWatcher(mWatcher);
```

### 发送数据：
```java
 ZiggsUdp.getInstance().send("hahaha".getBytes());
```

### 回调：
```java
  @Override
    public void udp_receive(byte[] buffer, InetAddress inetAddress, int port) {
        Log.e("data","data="+new String(buffer));
        
    }
```

### 删除该页面观察者:
```java
ZiggsUdp.getInstance().removeWatcher(mWatcher);
```
## 4.销毁服务:(一般退出app才销毁)
  ```java
UdpManager.getInstance().destoryUdpService();
```
## 5.该udp服务是全局的，所以一旦初始化，可以在一个或者多个页面使用，只要添加监听就可了。

License
-------

    Copyright (c) 2017 Henry

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
