FROMEWORK_CODE = "TSS";
APP_CODE    = "TSS";
APPLICATION = APP_CODE.toLowerCase();
CONTEXTPATH = APPLICATION + "/";

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
        $.tssTip("您的站内信箱里有<b> " + num + " </b>条新的待办事项，<a href='javascript:void(0)' onclick='openMsgPage()'>点击查看。</a>");
      }
    }
  });
}

function feedback() {
	var url = encodeURI('modules/dm/record.html?_default=系统使用反馈');
  	window.open(url);
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
