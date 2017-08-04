mvn clean install -Pebi -Dmaven.test.skip=true
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei

# 此测试BI连的boubei.com/tss_init库

# 集成Tomcat，打成zip包
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei/tomcat-ebi/webapps

cd /Users/jinpujun/Desktop/workspace/release/boubei/
zip -r tss.zip tomcat-ebi
cp tss.zip /Users/jinpujun/GitHub/boubei.com/source/

# 顺带导出初始化库
#mysqldump -uroot -p800best@com tss > tss_ebi.sql
#zip  /Users/jinpujun/GitHub/boubei.com/source/tss_ebi.sql.zip  tss_ebi.sql
#rm -f tss_ebi.sql
