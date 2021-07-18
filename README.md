# FFXIV Translation Patch Tool
FFXIV的中文漢化器。

English description is below.

相較於原版：
1. 針對5.57版本修正中文字庫補丁
2. 新增使用CSV進行漢化的功能。CSV是使用修改過的SaintCoinach輸出。
3. 刪除原版exe中與teemo連線的部分。

## 使用
![](https://i.imgur.com/ix4u2G3.png)

<img src="https://i.imgur.com/S2XC3U0.png" height="300">

0. 下載右邊的release版本或自行編譯
1. 開啟EXE程式
![](https://i.imgur.com/RPim0G0.png)
2. 點選「設置」
![](https://i.imgur.com/OypMCof.png)
3. 選擇FFXIV遊戲根目錄（例如：`D:\FFXIV\SquareEnix\FINAL FANTASY XIV - A Realm Reborn`）
4. 「檔案語言」：CSV代表使用`resource/rawexd`裡面的CSV檔案進行漢化（推薦），其他選項代表使用`resource/text`裡面的中國服版本檔案進行漢化或已經漢化過的覆蓋檔。 如果是用中國服檔案，請選擇「簡體中文」；如果是用漢化過的覆蓋檔，請選擇該覆蓋檔當初覆蓋的語言。
5. 「原始語言」：想要覆蓋遊戲中的哪種語言（我自己是覆蓋日文，不保證覆蓋其他語言會不會有問題）。
6. 「目標語言」：簡體中文或繁體中文（非CSV模式才需要選擇）。
7. 點擊「確定」
![](https://i.imgur.com/RPim0G0.png)
8. 點擊「漢化」


## 編譯筆記
[製作過程的筆記可以參考這裡。](https://hackmd.io/@GpointChen/SJi_gv-ad)

## 原項目說明
	
项目说明：

	此工具作用于：
	对国际服客户端(SE版)进行中文资源注入

	此程序
	默认只对国际服客户端打中文字库补丁
	不包含任何中文内容
	
	此项目于 2019-09-01 完全开源

使用方法：

	1.下载编译项目，或者直接下载release发布包
	2.解压运行项目
	3.选择FFXIV游戏根目录
	4.点击汉化按钮 等待
	5.Enjoy
	
	如果需要中文内容替换
	请自行将中文客户端的
	>最终幻想XIV/game/sqpack/ffxiv
	文件夹下的三个文件
	0a0000.win32.dat0
	0a0000.win32.index
	0a0000.win32.index2
	复制到汉化工具的
	>resource/text
	文件夹下重新运行程序即会自动读取
	
	PS:这次的补丁包含了字库内容
	所以不需要重新再打字库补丁
	PS2:每次汉化流程都会备份当前文件
	所以避免在已经汉化的文件上进行二度汉化
	这样会备份已汉化文件导致还原回滚失效
	PS3:因为不确定更新是否会覆盖文件
	所以在每次更新前尽量还原文件以免游戏导致不测
	
	注意:
	繁體中文/正體中文 版本
	可能因为翻译原因有部分的BUG存在
	请慎用使用
	
	特别注意：
	本程式采取修改客户端的形式进行中文资源的加载
	使用本程式表示你已经知晓这是违反官方规则的操作
	并且确认自行承担使用程式带来的任何后果

免责声明：

	1.此项目仅供学习技术以及技术交流使用
	2.严禁使用于任何商业用途
	3.请下载后24小时内删除


## English Description
This tool is used to translate the in-game text and patch the Chinese font files.

It is available to translate via the CSV files that are generated based on a modified version of SaintCoinach. You can also replace the in-game text with anything you want by editing the CSV files.

We only provide the CSV files for the translation, not the files from the CN servers.

This project is forked from [reusu/FFXIVChnTextPatch](https://github.com/reusu/FFXIVChnTextPatch).

### How To Use

0. Download the release pack or compile the source code by yourself.
1. Launch the .exe.
2. Clicking the 「設置」 on the top-right.
3. Setup your game folder.
4. 「檔案語言」：Choose CSV to use the CSV files in `resource/rawexd`. Other (Ja, En, De, Fr, Zh-cn) to use the `.dat0` and `.index` files in `resource/text`.
5. 「原始語言」：The language to be covered. There are Japanese, English, German, French, and simplified Chinese. Only Japanese is fully tested.
6. 「目標語言」：Only for not CSV mode. Choose simpliifed or traditional Chinese as the target langauge.
7. Click 「確定」 on the bottom-left.
8. Click 「漢化」 on the left.

### How to Compile

[The notes of compile process can be found here.](https://hackmd.io/@GpointChen/SJi_gv-ad) (written in zh-TW)
