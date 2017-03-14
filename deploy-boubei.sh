mvn clean install -Pboubei -Dmaven.test.skip=true
#cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei/demo
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei/tomcat7-tss/webapps

cd /Users/jinpujun/Desktop/workspace/release/boubei/
zip -r tss.zip tomcat7-tss