mvn clean install -Pxbi -Dmaven.test.skip=true
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei

# 此测试BI连的boubei.com/xbi|xbidata库
# 备份一个xbi和xbidata，以后每周用这个备份数据覆盖（防止试用者改坏了） 

# mysqldump -uroot -p xbidata > /home/tssbi/mysql_bk/xbidata_init.sql
# mysqldump -uroot -p xbi > /home/tssbi/mysql_bk/xbi_init.sql

# 把tssbi、tssx、tssdata同步至xbi和xbidata，并修改掉Admin的密码（为www.boubei.com）、数据源、Title：卜贝数据-X
# xbidata里，删除卜贝公司相关的数据、关掉部分Job，删除所有报表、录入表，用开发者JK重建