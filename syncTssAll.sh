# 登陆页保持不变
# mv src/main/webapp/login.html src/main/webapp/login.htm
mv src/main/webapp/WEB-INF/web.xml src/main/webapp/WEB-INF/web.xml0
mv src/main/resources/META-INF/spring.xml src/main/resources/META-INF/spring.xml0
mv src/main/resources/application.properties src/main/resources/application.properties0


cp -r ../boubei-tss/src/main/webapp/  src/main/webapp/

# rm -r src/main/java/com/boubei/tss/

cp -r ../boubei-tss/src/main/java/com/boubei/tss  src/main/java/com/boubei/
cp -r ../boubei-tss/src/main/resources  src/main/

# cp -r ../boubei-tss/target/tss-all.jar  tools/
# mv tools/tss-all.jar  tools/tss-4.4.jar

# mv -f src/main/webapp/login.htm src/main/webapp/login.html
mv src/main/webapp/WEB-INF/web.xml0 src/main/webapp/WEB-INF/web.xml
mv src/main/resources/application.properties0 src/main/resources/application.properties
mv src/main/resources/META-INF/spring.xml0 src/main/resources/META-INF/spring.xml
rm -f src/main/resources/*.key
rm -f src/main/resources/*.license

rm -rf temp
rm -rf upload