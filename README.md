 斗地主（cmd可摸鱼）

#### 介绍
使用java编写，可以使用cmd启动，可上班摸鱼

#### 安装教程

1.下载jar文件
2.新建一个bat批处理文件并输入：
start java -jar (jar文件的路径)
3.双击启动！！

#### 使用说明
出牌方法:输入序列号（例如 出第一张牌输入：0 出第二张牌则输入：1 以此类推）

#### 如需配置Java环境变量，看以下说明
1.右键此电脑-管理-高级系统设置-环境变量
2.在下方的环境变量中新建变量-变量名:JAVA_HOME 变量值:（为java安装目录）-配置好后保存
3.在下方环境变量中找到Path变量-编辑-新建-输入:%JAVA_HOME%\bin-继续新建-输入:%JAVA_HOME%\jre\bin-确定
4.在下方环境变量中新建ClassPath变量-变量名:ClassPath 变量值:.;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME\lib\tools.jar;-确定
5.依次返回
