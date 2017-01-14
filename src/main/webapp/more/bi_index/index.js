/* 
$(function() {
    var scriptNode = document.createElement("script");
    scriptNode.src = "http://s11.cnzz.com/z_stat.php?id=1256153120&web_id=1256153120";
    scriptNode.async = false;
    $('head').appendChild(scriptNode);
});
*/

// accordion
;(function($) {

	var Accordion = function($el, multiple) {
		this.el = $el || {};
		this.multiple = multiple || false;

		var links = this.el.find('.link'), oThis = this;
		links.click( function() {
			oThis.dropdown(this);
		} );
	}

	Accordion.prototype.open = function(link) {
		// 计算高度，默认submenu的高度为 36 * 8 = 288
		var $parent = $(link.parentNode);
		var $submenu = $parent.find(".submenu");
		var liSize = $submenu.find("li").size();

		$parent.addClass("open");
		$submenu.addClass("open");
		$submenu.css("height", (liSize * 36) + "px");			
	}

	Accordion.prototype.openFirst = function() {
		var oThis = this;
		this.el.find('.link').each(function(i, link) {
			if( !$(link.parentNode).hasClass("hidden") ) {
				oThis.open(link);
				return false;
			}
		});
	}

	Accordion.prototype.dropdown = function(link) {
		var $el = this.el,
			$li = $(link),
			$parent = $(link.parentNode);

		$el.find('li').removeClass('open');

		var $submenu = $parent.find('.submenu');

		// 已经打开的则关闭掉
		if( $submenu.hasClass("open") ) {
			$submenu.removeClass("open").css("height", "0px");
		}
		else {
			if (!this.multiple) { // 如不允许打开多个，先关闭所有已经打开的
				$el.find('.submenu').removeClass('open').css("height", "0px");
			};

			this.open(link);
		}
	}	

	$.fn.extend({
		accordion : function(multiple) {
			return new Accordion(this, false);
		}
	})

})(tssJS);


// init
function initBIIndex($) {
	$.extend({ 
		openMenu: function(li, isFirst) {
			var $li = $(li);
			var mid = $li.attr("mid");
			if( !mid ) return;

			var firstAOpened = false;
			$(".link").each(function(i, link) {
				var $link = $(link), $ul = $(link.parentNode);
				if( $link.hasClass(mid) ) {
					$ul.removeClass("hidden");

					if( !firstAOpened ) {
						var $lia = $ul.find("li>a");
						$lia.length && $.openReport( $lia[0], true );
						firstAOpened = true;
					}
				} 
				else {
					$ul.addClass("hidden");
				}
			});

			$("header li").removeClass("active");
			$li.addClass("active");
			accordion.openFirst();

			hideOther();
		},

		openReport: function(a, isFirst) {
			var $a  = $(a);
			var rid = $a.attr("rid");
			if( !rid ) return;

			!isFirst && closeSwitch();

			var id = "rp_" + rid, iframeId = "iframe_" + rid;
			var $ul = $("footer ul");
			var $lis = $ul.find("li");
			var maxVisible = Math.floor( (document.body.offsetWidth - 225)/135 );

			$ul.find("li").removeClass("active");
			$(".other ul li").removeClass("active");
			$("section .main iframe").hide();

			var $li = $("#" + id);
			if( !$li.length ) {
				var li = $.createElement("li", "", id);
				li.a = a;
				li.index = $lis.length;

				// 插入到footer ul				 
				if( li.index < maxVisible ) {
					$ul.appendChild(li);
				}
				else {
					var ul = $ul[0];
					ul.insertBefore(li, $lis[maxVisible]);
				}

				$li = $(li);
				$li.html("<span>" + $a.html() + "</span><i>X</>");

				// 创建一个iframe，嵌入报表
				var iframeEl = $.createElement("iframe", "", iframeId);
				$("section .main").appendChild(iframeEl);

				var url = "../../modules/dm/report_portlet.html?leftBar=true&id=" + rid;
				if(rid.indexOf("x") >= 0) {
					url = "../../404.html";
				}
				$(iframeEl).attr("frameborder", 0).attr("src", url);

				// 添加事件
				$li.find("span").click(function() {
					$.openReport(a);
				});
				$li.find("i").click( function() {
					$li.remove();
					$(iframeEl).remove();

					// 如果关闭的是active li，则需要先切换至第一个li
					if( $li.hasClass("active") ) {
						var first = $ul.find("li")[0];
						first && first.a && $.openReport(first.a);
					}
				} );
			}

			$("#" + iframeId).show();
			$li.addClass("active");
			hideOther();

			// 如果li在不可见区域，则使之可见
			var li = $li[0];
			if( li.index >= maxVisible ) {
				var ul = $ul[0];
				ul.insertBefore(li, $lis[maxVisible]);
			}
		}
	});

	var accordion = $('#ad1').accordion(false);

	var switchOpen = true;
	$("footer .switch").click( function() {
		if(switchOpen) {
			closeSwitch();
		} else {
			openSwitch();
		} 
	});
	function openSwitch() {
		switchOpen = true;
		$("header").show();
		$("section>.left").show();
		$("section .main").css("width", "85.5%");
		$("section").css("padding-bottom", "66px");
	}
	function closeSwitch() {
		switchOpen = false;
		$("header").hide();
		$("section>.left").hide();
		$("section .main").css("width", "100%");
		$("section").css("padding-bottom", "30px");
	}

	$("header li").each(function(i, li) {
		$li = $(li);
		$li.attr("onclick", "$.openMenu(this)");

		if(i == 0) {
			$.openMenu(li, true);
		}
	});

	$(".submenu a").each(function(i, a) {
		$a = $(a);
		$a.attr("href", "javascript:void(0);");
		$a.attr("onclick", "$.openReport(this)");

		// 初次加载时，默认打开第一个首页
		if(i == 0) {
			$.openReport(a, true);
		}
	});

	// 右下角三条杠功能实现
	function hideOther() {
		$(".other").hide(); 

		var $lis = $(".other ul li[id]");
		$lis.each(function(i, li) {
			$("footer ul").appendChild( li );
		});
	}
	$("footer>div").toggle(
		function() { 
			$(".other").show(true); 

			var maxVisible = Math.floor( (document.body.offsetWidth - 225)/135 );
			var $lis = $("footer ul li");
			$lis.each(function(i, li) {
				if(i > maxVisible) $(".other ul").appendChild( li );
			});
		}, hideOther);

	$(".other li.b1").click(function() {
		window.location.href = "/tss/login.html";
	});
	$(".other li.b2").click(function() {
		window.location.href = "/tss/index.portal";
	});
	$(".other li.bc").click(function() {
		$("footer ul li").remove();
		$(".other ul li[id]").remove();
		$("section .main iframe").remove();
		hideOther();
	});

	$.ajax({
		url : "../../auth/user/operatorInfo",
		method : "POST",
		onresult : function() {
			var userName = this.getNodeValue("name");
			$("#iUser span").html( userName || '欢迎您' );
		}
	});
}

// user & permission
CONTEXTPATH = "tss";
;(function($) {

	if( !$("#iUser").length ) return;

	var x = $.Query.get("x");
	if(!x) { 
		return initBIIndex(tssJS);
	}

	// 过滤报表的权限
	$.ajax({
		url : "../../auth/rp/my/ids",
		method : "POST",
		type : "json",
		ondata : function() {
			var list = this.getResponseJSON(), permissions = [];
			list.each(function(i, id) {
				permissions.push(id + "");
			});

			$("#ad1 li>a[rid]").each(function(i, a) {
				var rid = $(a).attr("rid");
				permissions.contains(rid) || $(a.parentNode).remove();
			});

			initBIIndex(tssJS);
		}
	});

})(tssJS);


;(function($) {

	if( $("#iUser").length ) return;

	// 从后台获取三级目录结构
	var topGid = parseInt($.Query.get("id") || "0");
	$.ajax({
		url : "../../auth/rp/my/" + topGid,
		method : "POST",
		type : "json",
		ondata : function() {
			var reports = this.getResponseJSON();

			var $headerUL = $("header ul"), $leftUL = $("section .left ul");
			reports.each(function(i, report) {
				var top1Id = report[0];
				if(top1Id == topGid) { // 根节点
					$("header .logo .forename").html(report[1]);
					return true;
				}

				// 一级目录
				if(report[2] === topGid && report[3] === 0) {
					var li = $.createElement("li", (i == 0 ? "active" : ""));
					$(li).attr("mid", "m" + top1Id).html(report[1]);
					$headerUL.appendChild(li);				

					reports.each(function(i2, report2) {
						var top2Id = report2[0];

						// 二级目录
						if(report2[2] === top1Id && report2[3] === 0) {
							var li = $.createElement("li");
							$(li).html('<div class="link m' +top1Id+ '">' +report2[1]+ '<i class="tag"></i></div><ul class="submenu"></ul>');
							$leftUL.appendChild(li);
						
							var $ul2 = $(li).find("ul");
							reports.each(function(i3, report3) {
								var top3Id = report3[0];

								// Report
								if(report3[2] === top2Id && report3[3] === 1) {
									var li = $.createElement("li");
									$(li).html('<a href="#" rid="' +report3[0]+ '">' +report3[1]+ '</a>');
									$ul2.appendChild(li);
								}
							});
						}
					});
				}
			});

			var li = $.createElement("li", "", "iUser");
			$(li).html('<span>欢迎您</span>');
			$headerUL.appendChild(li);	

			initBIIndex(tssJS);
		}
	});

})(tssJS);
