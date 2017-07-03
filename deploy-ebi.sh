mvn clean install -Pebi -Dmaven.test.skip=true
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei

# 此测试BI连的boubei.com/tss_init库

# 集成Tomcat，打成zip包
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei/tomcat-ebi/webapps

cd /Users/jinpujun/Desktop/workspace/release/boubei/
zip -r tss.zip tomcat-ebi
cp tss.zip /Users/jinpujun/GitHub/boubei.com/source/

# 顺带导出Demo库
mysqldump -uroot -p800best@com tss_demo1 > tss-demo1.sql
zip  /Users/jinpujun/GitHub/boubei.com/source/tss-demo1.sql.zip  tss-demo1.sql
rm -f tss-demo1.sql
# 更新demo1库
# cd ../tomcat7/webapps/boubei.com/source/
# mysql -uroot -p tss_demo1 < tss-demo1.sql
