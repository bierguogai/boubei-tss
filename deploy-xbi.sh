mvn clean install -Pxbi -Dmaven.test.skip=true
cd /Users/jinpujun/Desktop/workspace/release/boubei/

# 此测试BI连的boubei.com/xbi|xbidata库
# 定期把tssbi、tssx、tssdata同步至xbi和xbidata，并修改掉Admin的密码（为www.boubei.com）、数据源、Title
# xbidata里，删除卜贝公司相关的数据、关掉部分Job

# 上面导完了，先备份一个xbi和xbidata，以后每周用这个备份数据覆盖（防止试用者改坏了）