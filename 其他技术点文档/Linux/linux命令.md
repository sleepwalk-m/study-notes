#### 用户相关命令

```
用户相关
	创建用户：useradd 用户名
	用户口令：passwd  用户名
	修改用户：usermod 用户名
	删除用户：userdel 用户名
	
用户组相关
	创建用户组：	groupadd 用户组名
	修改用户组：	groupmod 用户组名
	查询用户所属组： groups 用户名
	删除用户组：	groupdel 用户组名
	
管理用户组内成员
	gpasswd （可选项）组名
	⚫ -a：添加用户到组；
	⚫ -d：从组删除用户；
	⚫ -A：指定管理员；
	⚫ -M：指定组成员和-A的用途差不多；
	⚫ -r：删除密码；
	⚫ -R：限制用户登入组，只有组中的成员才可以用newgrp加入该组。
```

#### 系统管理命令

```
日期管理
	date [参数选项]
	⚫ -d<字符串>：显示字符串所指的日期与时间。字符串前后必须加上双引号;
	⚫ -s<字符串>：根据字符串来设置日期与时间。字符串前后必须加上双引号;
	
显示用户
	logname [--help][--version]  //显示登录账号的信息
	
切换用户
	su 用户名
	
id命令
	id [用户名称]  查看当前用户的详细信息（用户id，群组id，所属组
	
sudo命令
	sudo 命令 提高普通用户的操作权限
```

#### 进程相关命令

```
top命令         结束监控快捷键：q       
	top 实时显示所有的进程信息
	top – c 实现显示所有的进程信息（显示完整命令）
	top –p PID  实时显示指定进程的信息
	
ps命令
	ps 显示当前正在运行的进程信息
	ps -A 显示系统中所有的进程信息
	ps -ef 显示系统中所有的进程信息（完整信息）
	ps –u 用户名 显示指定用户的进程信息
	
kill命令
	kill 进程PID 杀死指定进程
	kill -9 进程PID 彻底杀死指定进程
	kill -9 $(ps –ef | grep 用户名)  杀死指定用户所有进程
	killall –u 用户名 杀死指定用户所有进程
```

#### 关机重启命令

```
关机
	shutdown 不同的版本有所差异
	shutdown –h now 麻溜的立马关机
	shutdown +1 “警告信息” 墨迹一分钟再关机，并出现警告信息
	shutdown –r +1 “警告信息” 墨迹一分钟再重启，并出现警告信息
	shutdown -c 取消当前关机操作
重启
	reboot 麻溜的立马重启
```

#### 系统其它命令

```
who命令
	who 	显示当前登录系统的用户
	who –H  显示明细（标题）信息

timedatectl命令
	timedatectl status 显示系统的当前时间和日期
	timedatectl list-timezones 查看所有可用的时区
	timedatectl set-timezone "Asia/Shanghai“ 设置本地时区
	timedatectl set-ntp false 禁用时间同步
	timedatectl set-time “2019-03-11 20:45:00“ 设置时间
	timedatectl set-ntp true 启用时间同步
	
clear命令
	clear 清除屏幕
```

#### 目录管理命令

```
ls命令
	ls 显示不隐藏的文件与文件夹
	ls -l 显示不隐藏的文件与文件夹的详细信息
	ls –al 显示所有文件与文件夹的详细信息
	
pwd命令
	pwd -P 查看当前所在目录
	
cd命令
	cd 路径 切换目录
	
mkdir命令
	mkdir 文件夹名 创建目录
	mkdir -p aaa/bbb 创建多级目录
	
rmdir命令
	rmdir 文件夹名 删除目录
	mkdir -p aaa/bbb 删除bbb，如果删完之后aaa是空的，aaa也一起删除
	
cp命令
	cp 数据源 目的地 将指定的文件拷贝到指定目录里面
	cp aaa/a.txt ccc 将aaa文件夹中的a.txt拷贝到ccc文件夹中
	cp -r aaa/* ccc 将aaa文件夹中所有的内容都拷贝到ccc文件夹中
	
rm命令
	rm 文件路径 删除文件
	rm –r 目录路径 删除目录和目录里面所有的内容
	
mv命令
	mv 文件名 文件名 将源文件名改为目标文件名
	mv 文件名 目录名 讲文件移动到目标目录
	mv 目录名 目录名 目标目录已存在,将源目录移到目标目录;目标目录不存在则改名
	mv 目录名 文件名 出错
```

#### 文件属性相关

```
chgrp命令
	chgrp root aaa 将aaa文件的属组改为root
	chgrp -v root aaa 将aaa文件的属组改为root,显示指令执行过程。

chown命令
	chown itcast:itcast aaa 将aaa文件的属主和属组改为itcast
	
chmod命令
	chmod –R 770 文件或目录   //-R:对目前目录下的所有档案与子目录进行相同的权限变更
	chmod u=rwx,g=rx,o=r a.txt  //u:属主权限 g:属组权限 o:其他权限
```

#### 文件管理相关

```
touch命令
	touch a.txt 不存在就创建，存在就修改时间属性
	touch a{1..10}.txt 批量创建空文件

stat命令
	stat a.txt 查看文件的详细信息
```

#### 文件查看相关

```
cat命令
	cat a.txt 查看a.txt的内容
	cat –n a.txt 查看a.txt的内容(加入行号)
	
less命令
	less a.txt 查看a.txt的内容
	less –N a.txt 查看a.txt的内容（加入行号）
	
tail命令
	tail –3 big.txt  显示文件最后3行
	tail -f big.txt 动态显示最后10行
	tail -4f big.txt 动态显示最后4行
	tail -n +2 big.txt 显示文件a.txt 的内容，从第 2 行至文件末尾
	tail -c 45 big.txt 显示最后一些字符
	
head命令
	head 文件 查看文件的前一部分(类似tail命令)
	
grep命令
	grep 关键字 small.txt  把包含关键字的行展示出来
	grep –n 关键字 small.txt  把包含关键字的行展示出来且加上行号
	grep –i 关键字 small.txt  把包含关键字的行展示出来，搜索时忽略大小写
	grep –v 关键字 small.txt  把不包含关键字的行展示出来
	ps -ef | grep 关键字 查找指定的进程信息，包含grep进程
	ps -ef | grep 关键字 | grep -v “grep” 查找指定的进程信息，不包含grep进程
	ps -ef|grep -c sshd 查找进程个数
```

#### vim相关命令

```
打开和新建文件
	vim 文件名 文件已存在就会打开,不存在,退出后会新建一个文件
	
进入编辑模式
	i  在当前字符前插入文本
	
进入末行模式
	:q 当vim进入文件没有对文件内容做任何操作可以按"q"退出
    :q! 当vim进入文件对文件内容有操作但不想保存退出
    :wq 正常保存退出
    :wq! 强行保存退出，只针对与root用户或文件所有人
    
vim定位行
	vim 文件名 +行数 查看文件并定位到具体行数 
```

#### awk语法相关

```
awk命令
	awk [参数选项] ‘语法’ 文件
举例
	cat a.txt | awk '/zhang|li/'   					 //搜索含有指定字符的
	cat a.txt | awk -F ' ' '{print $1,$2,$3,$4}'     // F:使用指定字符分割 $:获取第几段内容
	cat a.txt | awk -F ' ' '{OFS="=="}{print $1,$2}' //OFS:往外输出时按照指定的分割字符串输出
	cat a.txt | awk -F ' ' '{print toupper($1)}'     //toupper()转大写
	cat a.txt | awk -F ' ' '{print tolower($1)}'	 //tolower()转小写
	cat a.txt | awk -F ' ' '{print length($1)}'    	 //length()返回字符长度
	
awk 'BEGIN{初始化操作}{每行都执行}END{结束时操作}'
	cat a.txt | awk -F ' ' 'BEGIN{}{total=total+$4}END{print total}'

```

#### 压缩相关命令

```
gzip命令
	gizp a.txt 压缩文件
	gzip * 压缩当前目录下所有文件
	gzip -dv * 解压文件并列出详细信息
	
gunzip命令
	gunzip 压缩文件 解压
	
tar命令
	tar [必要参数][选择参数][文件]  打包、压缩和解压（文件/文件夹）
	
	参数选项：
		-c 建立新的压缩文件
		-v 显示指令执行过程
		-f<备份文件> 指定压缩文件
		-z 通过gzip指令处理压缩文件。
		-t 列出压缩文件中的内容
		-x 表示解压
	
	tar -cvf 打包文件名 文件名 打包文件并指定打包之后的文件名（仅打包不压缩）
	tar -zcvf 压缩文件名 文件名/文件夹名 压缩文件或者文件夹并指定压缩文件名（打包压缩）
	tar -ztvf 压缩文件名 查看压缩文件中有哪些文件
	tar –zxvf 压缩文件名 解压
	
zip命令	
	zip [必要参数][选择参数][文件]  压缩
	
	参数选项：
		-q 不显示指令执行过程。
		-r 递归处理，将指定目录下的所有文件和子目录一并处理
		
	zip -q -r 压缩文件名 文件/文件夹 压缩
	
unzip命令
	unzip [必要参数][选择参数][文件]  解压
	
	参数选项：
		-l 显示压缩文件内所包含的文件。
		-d<目录> 指定文件解压缩后所要存储的目录
		
	unzip -l 压缩文件名 查看这个压缩文件中有多少内容
	unzip -d 指定文件夹 压缩文件 解压
	
bzip2命令
	bzip2 a.txt 压缩
	
bunzip2命令
	bunzip2 -v a.bz2  解压并显示详细信息
```

#### 网络管理相关

```
ifconfig命令
	ifconfig 显示激活的网卡信息
	ifconfig ens37 down 关闭网卡
	ifconfig ens37 up 启动网卡
	ifconfig ens37 192.168.23.199 配置ip地址
	ifconfig ens37 192.168.23.133 netmask 255.255.255.0 配置ip地址和子网掩码
	
ping命令
	ping www.baidu.com 检测是否与百度连通
	ping -c 2 www.baidu.com 指定接收包的次数
	
netstat命令
	netstat –a 显示详细的连接状况
	netstat –i 显示网卡列表
```



#### 磁盘管理相关

```
lsblk命令
	lsblk 列出硬盘的使用情况
	lsblk –f 显示系统信
	
df命令
	df  显示整个硬盘使用情况
	df 文件夹 显示文件夹使用情况
	df –total 显示所有的信息
	df -h  将结果变成KB，MB，GB形式展示，利于阅
	
mount命令
	mkdir 文件夹 创建文件夹（也是创建一个挂载点）
	mount -t auto /dev/cdrom 文件夹 开始挂载
	umount 文件夹 卸载
```

#### yum工具使用

```
yum常用命令
    1.列出所有可更新的软件清单命令：yum check-update
    2.更新所有软件命令：yum update
    3.仅安装指定的软件命令：yum install <package_name>
    4.仅更新指定的软件命令：yum update <package_name>
    5.列出所有可安裝的软件清单命令：yum list
    6.删除软件包命令：yum remove <package_name>
    7.查找软件包 命令：yum search <keyword>
    8.清除缓存命令:
        yum clean packages: 清除缓存目录下的软件包
        yum clean headers: 清除缓存目录下的 headers
        yum clean oldheaders: 清除缓存目录下旧的 headers
        yum clean, yum clean all (= yum clean packages; yum clean oldheaders) :清除缓存目录		下的软件包及旧的headers

安装Tree
	yum -y install tree 安装tree
	tree 执行tree，展示当前目录结构
	yum remove tree 移除tree
	yum list tom* 找出以 tom 为开头的软件名称
	
yum源
	yum install -y wget 	安装下载工具wget
	wget -O CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo 下载阿里云的Centos-7.repo文件
	yum clean all 清理之前的缓存，并重新加载yum
	yum makecache 建立一个缓存文件
	yum search tomcat 查找软件，验证阿里云的yum源是否可以正常使用
```



#### 其他命令

```
echo命令
	echo 字符串 展示文本
	echo 字符串 >文件名 将字符串写到文件中（覆盖文件中内容）
	echo 字符串 >> 文件名 将字符串写到文件中（不覆盖文件中内容）
	cat 不存在的目录 &>> error.log 将命令的失败结果 追加 error.log文件的后面
	
软连接
	ln -s 目标文件路径 快捷方式路径
	
find命令
	find . -name “*.txt” 查找当前目录及其子目录下所有后缀名名是txt的文件
	find . -ctime -1 查找当前目录及其子目录下所有最近 1天内更新过的文件
	find / -name 'czbk' /代表是全盘搜索,也可以指定目录搜索
```

