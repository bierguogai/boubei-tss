
# ebi.war 供爱好者下载安装用，及部署至boubei.com:9091 用于面试、测试

mvn clean install -Pebi -Dmaven.test.skip=true
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei

# 此测试BI连的 boubei.com/ebi 库。

# 集成Tomcat，打成zip包
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei/tomcat-ebi/webapps

cd /Users/jinpujun/Desktop/workspace/release/boubei/
zip -r tss.zip tomcat-ebi
rm -f /Users/jinpujun/GitHub/boubei.com/source/tss.zip
cp tss.zip /Users/jinpujun/GitHub/boubei.com/source/

# 顺带导出初始化库
mysqldump -uroot -pboubei@com tss > tss_ebi.sql
rm -f /Users/jinpujun/GitHub/boubei.com/source/tss_ebi.sql.zip
zip  /Users/jinpujun/GitHub/boubei.com/source/tss_ebi.sql.zip  tss_ebi.sql
rm -f tss_ebi.sql
