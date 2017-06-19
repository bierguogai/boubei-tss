mvn clean install -Pdemo1 -Dmaven.test.skip=true
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei/tomcat-xbi/webapps

cd /Users/jinpujun/Desktop/workspace/release/boubei/
zip -r tss.zip tomcat-xbi
cp tss.zip /Users/jinpujun/GitHub/boubei.com/source/

mysqldump -uroot -p800best@com tss_demo1 > tss-demo1.sql
zip  /Users/jinpujun/GitHub/boubei.com/source/tss-demo1.sql.zip  tss-demo1.sql

# 此DemoBI连的boubei.com/tss-demo1库
# 更新demo1库
# cd ../tomcat7/webapps/boubei.com/source/
# mysql -uroot -pWeBuZhiDao@2017 tss_demo1 < tss-demo1.sql