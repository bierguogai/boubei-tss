
# release for demo.boubei.com
mvn clean install -Pxbi -Dmaven.test.skip=true

# release for t.boubei.com
# mvn clean install -Pt -Dmaven.test.skip=true

cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei
