FROMEWORK_CODE = "TSS";
APP_CODE    = "TSS";
APPLICATION = APP_CODE.toLowerCase();
CONTEXTPATH = APPLICATION + "/";

var notice_channel = 2;  // 顶部通知跑马灯 boubei.com = 2

window.history.forward(1);  // 产生一个“前进”的动作，以抵消浏览器后退功能

function initUserInfo(callback) {
	$.ajax({
		url : "/tss/auth/user/operatorInfo",
		method : "POST",
		onresult : function() {
			var userName = this.getNodeValue("name");
			$("#userInfo").html(userName || '个人信息');

			callback();
		}
	});
}

function logout() {
	$.ajax({
		url : "/tss/logout.in",
		method : "GET",
		onsuccess : function() { 
			$.Cookie.del("token", "");
			$.Cookie.del("token", "/");
			$.Cookie.del("token", "/tss");
			$.Cookie.del("token", "/tss/");
			location.href = "/tss/login.html";
		}
	});
}

function dbsx() {
  $.ajax({
    url: "/tss/auth/message/num",
    method: "GET",
    type : "json",
    ondata: function() {
      var num = this.getResponseJSON();
      if(num > 0) {
        $.tssTip("您的站内信箱里有<b> " + num + " </b>条新的消息，<a href='javascript:void(0)' onclick='openMsgPage()'>点击查看。</a>");
      }
    }
  });
}

/* 禁止鼠标右键 
(function() {
	document.oncontextmenu = function(ev) {
		ev = ev || window.event;
		var srcElement = $.Event.getSrcElement(ev);
		var tagName = srcElement.tagName.toLowerCase();
		if("input" != tagName && "textarea" != tagName) {
			$.Event.cancel(ev);            
		}
	}
})();
*/

function openUrl(url, dialog, title) {
    if (url == "#") return;

    var id = "if" + tssJS.hashCode(url);
    if( dialog ) { // 弹窗打开
        window.open("/tss/" + url);
        return;
    }

    // 在框架页里打开
    var $iframe = $("#" + id);
    if( $iframe.length == 0 ) {
        var iframe = $.createElement("iframe", "tssIFrame", id);
        $iframe = $(iframe);
        $iframe.attr("frameborder", "0").attr("scrolling", "auto");
        $(document.body).appendChild(iframe);

        resizeIFrame();
        $iframe.attr("src", url);
    } 
    else { // 打开内嵌页的leftBar，如果有的话
        var sonWindow = $iframe[0].contentWindow;
        sonWindow.openPalette && sonWindow.openPalette();
    }

    $(".tssIFrame").addClass("hidden").removeClass("open");
    $iframe.removeClass("hidden").addClass("open");
}

// report_portlet.html  的 feedback()方法调用到这里
function feedback(module) {
    openUrl( encodeURI('modules/dm/recorder.html?name=系统使用反馈' + (module ? "&udf="+module : "")), true, "建议反馈" );
}

function manageJob() {
    openUrl( encodeURI('modules/dm/recorder.html?name=系统定时器') );
}

function manageETLTask() {
    openUrl( encodeURI('modules/dm/recorder.html?name=ETL任务') );
}

function manageToken() {
    openUrl( encodeURI('modules/dm/recorder.html?name=对外用户令牌发放') );
}

function releaseModule() {
    openUrl( encodeURI('modules/dm/recorder.html?name=功能模块发布') );
}

function fixUserInfo() {
    $.openIframePanel("p1", "个人信息", 760, 320, "modules/um/_userInfo.htm", true);
    tssJS.getJSON("/tss/auth/user/has?refreshFlag=true", {}, function(info) { }, "GET");
}

function openMsgPage() {
    openUrl('modules/um/message.html');
}

function changePasswd() {
    $.openIframePanel("p2", "修改密码", 440, 300, "modules/um/_password.htm", true);
}

// ----------------------------------------- 顶部跑马灯公告栏 ----------------------------------------------------
function showNotice(id, title) {
    $.openIframePanel("noticePanel", title, 960, 480, "/tss/notice.html?articleId=" + id);
}

tssJS(function(){
    if( $("#notice").length == 0 ) return;
    
    var request = new $.HttpRequest();
    request.url =  "/tss/auth/article/list/xml/" +notice_channel+ "/1/5/false";
    request.method = "GET";
    request.onresult = function(){
        var articleList = this.getNodeValue("ArticleList");
        $("item", articleList).each(function(i, item){
            var id = $( "id", item ).text(), title = $( "title", item ).text();
            var a = tssJS.createElement("a");
            a.href = "javascript:void(0);";
            $(a).attr("onclick", "showNotice(" +id+ ", '" +title+ "')");
            $(a).html( "【" + $( "issueDate", item ).text() + "】" + title);

            $("#s1").appendChild(a);
        });

        showMQ();
    }
    request.send();

    function showMQ() {
        var speed = 100
        var nDiv = $("#notice")[0];
        var s1 = $("#s1")[0];
        var s2 = $("#s2")[0];

        $(s2).html( $(s1).html() );

        function marquee(){
            if(nDiv.scrollLeft >= s2.offsetWidth) {
                nDiv.scrollLeft -= s1.offsetWidth
            } else {
                nDiv.scrollLeft += 4;
            }
        }

        var mq = setInterval(marquee, speed);
        nDiv.onmouseover = function() { clearInterval(mq); };
        nDiv.onmouseout  = function() { mq = setInterval(marquee, speed); };
    }
});
// ----------------------------------------- 顶部跑马灯公告栏End ----------------------------------------------------

