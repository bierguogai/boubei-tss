
mv src/main/webapp/login.html src/main/webapp/login.htm
cp -r ../boubei-tss/src/main/webapp/  src/main/webapp/

rm -r src/main/java/com/boubei/tss/dm
rm -r src/main/java/com/boubei/tss/util
rm -r src/main/java/com/boubei/tss/cache
rm -r src/main/java/com/boubei/tss/framework

cp -r ../boubei-tss/src/main/java/com/boubei/tss/dm  src/main/java/com/boubei/tss/
# cp -r ../boubei-tss/src/main/java/com/boubei/tss/util  src/main/java/com/boubei/tss/
cp -r ../boubei-tss/src/main/java/com/boubei/tss/cache  src/main/java/com/boubei/tss/
# cp -r ../boubei-tss/src/main/java/com/boubei/tss/framework  src/main/java/com/boubei/tss/
# rm -r src/main/java/com/boubei/tss/framework/sso

cp -r ../boubei-tss/target/tss-all.jar  tools/
mv tools/tss-all.jar  tools/tss-4.3.jar

mv -f src/main/webapp/login.htm src/main/webapp/login.html