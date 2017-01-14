# deploy to my mac localhost

TOMCAT_HOME="/Users/jinpujun/Desktop/tomcat7063"

#找到tomcat进程的id并kill掉
ps -ef |grep tomcat  |awk {'print $2'} | sed -e "s/^/kill -9 /g" | sh -

#删除日志文件
rm  $TOMCAT_HOME/logs/* -rf

mvn clean install -Pdev -Dmaven.test.skip=true


set -m

rm -rf $TOMCAT_HOME/webapps/tss

cp target/tss.war $TOMCAT_HOME/webapps

cd $TOMCAT_HOME/bin

./startup.sh

tail -f $TOMCAT_HOME/logs/catalina.out

# mysql -uroot -p800best@com tssbi < /Users/jinpujun/Desktop/workspace/release/boubei/mysql_bk/tssbi_01-12-2017.sql