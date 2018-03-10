
// 检查输入号码是否正确 
function checkPhone(num) {
	var arr = [1];
	// var arr = [138, 137, 136, 135, 134, 147, 150, 151, 152, 157, 158, 159, 178, 182, 183, 184, 187, 188, 130, 131, 132, 155, 156, 185, 186, 145, 176, 133, 153, 177, 173, 180, 181, 189, 170, 171];
	return $.isNumeric(num) && (num + '').length == 11 && arr.contains((num + '').substr(0, 1) * 1)
}

//日期格式化
Date.prototype.format = function(format) {
	var date = {
		"M+": this.getMonth() + 1,
		"d+": this.getDate(),
		"h+": this.getHours(),
		"m+": this.getMinutes(),
		"s+": this.getSeconds(),
		"q+": Math.floor((this.getMonth() + 3) / 3),
		"S+": this.getMilliseconds()
	};
	if (/(y+)/i.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
	}
	for (var k in date) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
		}
	}
	return format;
}
//计算两个日期的差
function dateDiffer(a,b) {
    if (a == '' || b == '') {
        return '';
    } else {
        var date1 = new Date(a)
        var date2 = new Date(b)
        var s1 = date1.getTime(),
        s2 = date2.getTime();
        var total = (s2 - s1) / 1000;
        var day = parseInt(total / (24 * 60 * 60)); //计算整数天数
        var afterDay = total - day * 24 * 60 * 60; //取得算出天数后剩余的秒数
        var hour = parseInt(afterDay / (60 * 60)); //计算整数小时数
        var afterHour = total - day * 24 * 60 * 60 - hour * 60 * 60; //取得算出小时数后剩余的秒数
        var min = parseInt(afterHour / 60); //计算整数分
        var afterMin = total - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60; //取得算出分后剩余的秒数
        return day+'天'+hour+'小时'+min+'分钟'
    }
}
// 日期框只显示月份
function showMonth(id) {
	$(id).datebox({
		onShowPanel: function() {
			span.trigger('click');
			if (!tds)
				setTimeout(function() {
					tds = p.find('div.calendar-menu-month-inner td');
					tds.click(function(e) {
						e.stopPropagation();
						var year = /\d{4}/.exec(span.html())[0],
							month = parseInt($(this).attr('abbr'), 10) + 1;
						$(id).datebox('hidePanel')
							.datebox('setValue', year + '-' + month);
					});
					$(".datebox-button a").css('display','none');
				}, 0);
		},
		parser: function(s) {
			if (!s)
				return new Date();
			var arr = s.split('-');
			return new Date(parseInt(arr[0], 10), parseInt(arr[1], 10) - 1, 1);
		},
		formatter: function(d) {
			if (d.getMonth() == 0) {
				return d.getFullYear() - 1 + '-' + 12;
			} else {
				return d.getFullYear() + '-' + d.getMonth();
			}
		}
	});
	var p = $(id).datebox('panel'),
		tds = false,
		span = p.find('span.calendar-text');
}

function findObj(obj, data, str1, str2) {
	str2 = str2 || str1;
	str1 = str1.split(',');
	str2 = str2.split(',');
	if (!data.length) {
		return false;
	};
	for (var i = data.length - 1; i >= 0; i--) {
		var isSame = true;
		for (var j = str1.length - 1; j >= 0; j--) {
			if (data[i][str2[j]] != obj[str1[j]]) {
				isSame = false;
				break;
			}
		}
		if (isSame) {
			return data[i];
		}
	}
	return false;
}

/** <<waiting>><<TODO>> 打开页面，// 非tab格式下，在当前页面，打开面板； tab格式下，新增tab页
 * @param stirng type 服务类型，即id对应的类型，url=网页、rec=录入表id&参数、rep=报表id&参数
 * @param string name 服务名称，即id对应的文字解释，tab名称或panel名称
 * @param string id 服务，可以是网页、数字、数字&参数
 * @return 打开新的tab页或panel页
 * @example addTab("rec", RECORDINFO[0].name, RECORDINFO[0].id );
 * @version v001：20171130章敏 
 */
function addTab(type, name, id, panelId, width, height) {
	width = width || 1200;
	height = height || 600;
	panelId = panelId;
	// type = type || "rec";
	if (window.parent.parent.document.getElementById("tabs") == null) {

		if (window.parent.document.getElementById("tabs") == null) {
			if (type == "url") { /*网页*/
				openEasyuiIframe(panelId, id, name, width, height);
			} else if (type == "rec") { /*录入表*/
				openEasyuiIframe(panelId, BASEURL + "/tss/modules/dm/recorder.html?id=" + id, name, width, height);
			} else if (type == "rep") { /*报表*/
				openEasyuiIframe(panelId, BASEURL + "/tss/modules/dm/report_portlet.html?leftBar=true&id=" + id, name, width, height);
			}
		} else {
			if (type == "url") { /*网页*/
				window.parent.addTab(name, id);
			} else if (type == "rec") { /*录入表*/
				window.parent.addTab(name, BASEURL + "/tss/modules/dm/recorder.html?id=" + id);
			} else if (type == "rep") { /*报表*/
				window.parent.addTab(name, BASEURL + "/tss/modules/dm/report_portlet.html?leftBar=true&id=" + id);
			}
		}
	} else {
		if (type == "url") { /*网页*/
			window.parent.parent.addTab(name, id);
		} else if (type == "rec") { /*录入表*/
			window.parent.parent.addTab(name, BASEURL + "/tss/modules/dm/recorder.html?id=" + id);
		} else if (type == "rep") { /*报表*/
			window.parent.parent.addTab(name, BASEURL + "/tss/modules/dm/report_portlet.html?leftBar=true&id=" + id);
		}
	}
}

/** <<waiting>><<DONE>> dialog 内嵌页打开
 * @param string 打开的ele的id
 * @param string 目标url
 * @param string title名
 * @param number ele的宽
 * @param number ele的高
 * @return  
 * @example 
 * @version v001：20170720 HK new
 */
function openEasyuiIframe(panelId, src, title, width, height) {
	title = title || 'new';
	width = width || _$.getInner().width - 20;
	height = height || _$.getInner().height - 20;
	$panel = $('#' + panelId);
	$panel.remove();
	var panel = document.createElement("div")
	document.body.appendChild(panel);
	//console.log(panel)
	$panel = $(panel);
	$panel.addClass("easyui-dialog");
	$panel.attr("id", panelId).attr("closed", false);
	$('#' + panelId).dialog({
		title: title,
		width: width,
		height: height,
	})
	$panel.append('<iframe frameborder="0"></iframe>')
	$panel.find('iframe').attr('src', src).css('width', width - 15).css('height', height - 38);
	//$.parser.parse()
}

function getIncome(str,col,title1,title2,tableid,url,param){
	param = param || {str:'?'};
	$(tableid).datagrid({
		url:url,
		queryParams:param,
		fit: true,
		singleSelect: true,
		title: title1 + (param[str] || '?') + title2,
		columns: col,
		showFooter:true
	})
}

// 录入表新增记录
function createNew(dialogid,fromid,title){
	$(fromid).form('clear');
	$(dialogid).dialog({
		"modal": true
	}).dialog('open').dialog('setTitle', title).dialog('center');
	$('#code').textbox('readonly', false);
	$('#lockVersion').val('0'); // 給版本号设置默认值
}
// 关闭dialog
function closeDlg(dialogid,formid) {
    $(dialogid).dialog('close'); // close the dialog
    $(formid).form('clear');
}
// 修改历史记录
function updateOld(url,tableid,formid,dlgid,title) {
	var row = getSelectedRow(tableid);
	// console.log(row)
	if (row) {
		$.getJSON(url + row.id, {}, function(select_data) {
			$(formid).form('load', select_data);
			$(dlgid).dialog({
				"modal": true
			}).dialog('open').dialog('setTitle', title).dialog('center');
			$('#code').textbox('readonly', true);
		}, "GET");
	}
}
// 获取当前月最后一天
function getCurrentMonthLast() {
	var date = new Date();
	var currentMonth = date.getMonth();
	var nextMonth = ++currentMonth;
	var nextMonthFirstDay = new Date(date.getFullYear(), nextMonth, 1);
	var oneDay = 1000 * 60 * 60 * 24;
	return new Date(nextMonthFirstDay - oneDay);
}
// 获取当前月第一天
function getCurrentMonthFirst() {
	var date = new Date();
	date.setDate(1);
	return date;
}
// 单元格点击样式
styler2 = function(value, row, index) {
	return 'cursor: pointer; text-decoration: underline; font-weight: bold; background-color:RGB(234,242,255);';
}
// tooltip
function tooltip(value) {
	if (value != null) {
		return "<span title='" + value + "'>" + value + "</span>";
	}
}
//针对单个json格式数据str属性distinct，并返回
function getComboboxList(data,str) {
	var arr = [];
	for (var i = data.length - 1; i >= 0; i--) {
		if (!findObj(data[i], arr, str) && data[i][str]) {
			arr.push(data[i])
		}
	}
	return arr
}

//身份证号验证正确与否
function IdentityCodeValid(code) {
	var city = {11: "北京",12: "天津",13: "河北",14: "山西",15: "内蒙古",21: "辽宁",22: "吉林",23: "黑龙江",31: "上海",32: "江苏",33: "浙江",34: "安徽",35: "福建",36: "江西",37: "山东",41: "河南",42: "湖北",43: "湖南",44: "广东",45: "广西",46: "海南",50: "重庆",51: "四川",52: "贵州",53: "云南",54: "西藏",61: "陕西",62: "甘肃",63: "青海",64: "宁夏",65: "新疆",71: "台湾",81: "香港",82: "澳门",91: "国外"};
	var tip = "";
	var pass = true;
	if (!code || !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[12])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(code)) {
		tip = "身份证号格式错误";
		pass = false;
	} else if (!city[code.substr(0, 2)]) {
		tip = "地址编码错误";
		pass = false;
	} else {
		//18位身份证需要验证最后一位校验位
		if (code.length == 18) {
			code = code.split('');
			//∑(ai×Wi)(mod 11)
			//加权因子
			var factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
			//校验位
			var parity = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2];
			var sum = 0;
			var ai = 0;
			var wi = 0;
			for (var i = 0; i < 17; i++) {
				ai = code[i];
				wi = factor[i];
				sum += ai * wi;
			}
			var last = parity[sum % 11];
			if (parity[sum % 11] != code[17]) {
				tip = "校验位错误";
				pass = false;
			}
		}
	}
	return pass;
}

// 获取用户登录信息
function loginInfo(callback){
    tssJS.ajax({
        url : "http://t.boubei.com/tss/auth/user/has",
        method : "GET",
        type : "json",
        ondata: function() {
            var info = this.getResponseJSON();
            if(info && info.length>0){
                var obj={
                    login_code : info[3],
                    login_name : info[4],
                    all_info : info
                }
                callback && callback(obj)
            }

        }
    })
}

//合并单元格
function mergeCellsByField(tableID, colList) {
    var ColArray = colList.split(",");
    var tTable = $("#" + tableID);
    var TableRowCnts = tTable.datagrid("getRows").length;
    var tmpA;
    var tmpB;
    var PerTxt = "";
    var CurTxt = "";
    var alertStr = "";
    // console.log(TableRowCnts);
    for (j = ColArray.length - 1; j >= 0; j--) {
        PerTxt = "";
        tmpA = 1;
        tmpB = 0;

        for (i = 0; i <= TableRowCnts; i++) {
            if (i == TableRowCnts) {
                CurTxt = "";
            }
            else {
                CurTxt = tTable.datagrid("getRows")[i][ColArray[j]];
            }
            if (PerTxt == CurTxt) {
                tmpA += 1;
            }
            else {
                tmpB += tmpA;
                
                tTable.datagrid("mergeCells", {
                    index: i - tmpA,
                    field: ColArray[j],　　//合并字段
                    rowspan: tmpA,
                    colspan: null
                });
                
                tmpA = 1;
            }
            PerTxt = CurTxt;
        }
    }   
}

//根据colList相同的字段合并colList1字段
function mergeCellsByOtherField(tableID, colList,colList1) {
    var ColArray = colList.split(",");
    var ColArray1= colList1.split(",");
    var tTable = $("#" + tableID);
    var TableRowCnts = tTable.datagrid("getRows").length;
    var tmpA;
    var tmpB;
    var PerTxt = "";
    var CurTxt = "";
    var alertStr = "";
    // console.log(TableRowCnts);
    for (j = ColArray1.length - 1; j >= 0; j--) {
        PerTxt = "";
        tmpA = 1;
        tmpB = 0;

        for (i = 0; i <= TableRowCnts; i++) {
            if (i == TableRowCnts) {
                CurTxt = "";
            }
            else {
                CurTxt = tTable.datagrid("getRows")[i][ColArray[0]];
            }
            if (PerTxt == CurTxt) {
                tmpA += 1;
            }
            else {
                tmpB += tmpA;
                
                tTable.datagrid("mergeCells", {
                    index: i - tmpA,
                    field: ColArray1[j],　　//合并字段
                    rowspan: tmpA,
                    colspan: null
                });
                
                tmpA = 1;
            }
            PerTxt = CurTxt;
        }
    }
    
}

//下载报表
function exportCSV(reportId, params, url) { //空参数表示{param1:123,param2:345,param3:null或'',param4:456}
    var queryString = "?";
    $.each(params, function(key, value) {
        if (queryString.length > 1) {
            queryString += "&";
        }
        if (value == null || value === '') {
            queryString += ('null');
        } else {
            queryString += (key + "=" + value);
        }
    });
    var ua = window.navigator.userAgent;
    var msie = ua.indexOf("MSIE ");
    if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./)) //IE未验证
    {
        var IEwindow = window.open();
        IEwindow.document.write('sep=,\r\n' + CSV);
        IEwindow.document.close();
        IEwindow.document.execCommand('SaveAs', true, name + ".csv");
        IEwindow.close();
    } else { // If another browser, 
        if (url) {
            var uri = url + "/tss/data/export/" + reportId + "/1/100000" + queryString;
        } 
        else if(location.hostname=='t.boubei.com'){
            var uri = "/tss/data/export/" + reportId + "/1/100000" + queryString;
        }
        else {
            var uri = "/tss/data/export/" + reportId + "/1/100000" + queryString;
        }
        var link = document.createElement("a");
        link.href = uri;
        link.style = "visibility:hidden";
        link.download = name + ".csv";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}
// 动态实现两个datagrid数据相互添加删除到另一个datagrid 用于格式设置 注意次方法会修改column值
function removeAdd(removeid, addid, bl) {
	var rm = $(removeid).datagrid("getSelections"),
		index;
	if (rm) {
		for (var i = 0; i < rm.length; i++) {
			index = $(removeid).datagrid("getRowIndex", rm[i]);
			$(removeid).datagrid("deleteRow", index);
			if (bl) {
				$(addid).datagrid("appendRow", {
					field: rm[i].field,
					title: rm[i].title
				});
			} else {
				$(addid).datagrid("insertRow", {
					index: i,
					row: {
						field: rm[i].field,
						title: rm[i].title
					}
				});
			}
		}
	}
}
// 打开指定的dlg
function openDlg(dialogid,formid) {
    $(dialogid).dialog('open');
    $(formid).form('clear');
}

// 返回两个数组的并集，并对并集之后的数据打上标记，用于区分共有、存在于data1、存在于data2
// var data1 = [{a:1,b:2},{a:2,b:5}],
// data2 = [{a:1,b:2},{a:2,b:4}]
// todo 
function dataSum(data1,data2,str1,str2){
	var sumData=[];
	str2 = str2 || str1;
	// console.log(data1.length,data2.length,)
	for (var i = 0; i < data1.length; i++) {
		if (findObj(data1[i],data2,str1,str2) && data1[i]) {
			// data1[i]存在于data2，即两者共有部分
			var thisobj  = findObj(data1[i],data2,str1,str2);
			thisobj.handle = 0;
			sumData.push(thisobj)
			// console.log(thisobj)
		} else {
			// data1[i]不存在于data2，即data1特有
			var thisobj = data1[i];
			thisobj.handle = 1;
			sumData.push(thisobj)
			// console.log(thisobj)
		}
	}
	// console.log(sumData)
	for (var i = 0; i < data2.length; i++) {
		// console.log(data2[i])
		if(!findObj(data2[i],sumData,str1,str2) && data2[i]){
			// data2[i]不存于sumData,即data2特有
			var thisobj = data2[i];
			thisobj.handle = -1
			sumData.push(thisobj)
			// console.log(thisobj)
		}
	}
	return sumData
}
// 复制对象
function cloneObj(obj) {
	var newObj = {};
	if (obj instanceof Array) {
		newObj = [];
	}
	for (var key in obj) {
		var val = obj[key];
		//newObj[key] = typeof val === 'object' ? arguments.callee(val) : val; //arguments.callee 在哪一个函数中运行，它就代表哪个函数, 一般用在匿名函数中。  
		if (val === null) {
			newObj[key] = null;
		} else {
			newObj[key] = typeof val === 'object' ? cloneObj(val) : val;
		}
	}
	return newObj;
}

// 获取后台表的数据，并只保留需要的str属性
function GetField(url, params, str, callback) {
	tssJS.getJSON(url, params, function(data) {
		var arr = [],
			str1 = str.split(",");
		for (var i = data.length - 1; i >= 0; i--) {
			if (!findObj(data[i], arr, str)) {
				arr.push(data[i])
			}
		}
		for (var j = 0; j < str1.length; j++) {
			for (var i = 0; i < arr.length; i++) {
				for (var x in arr[i]) {
					if (x != str1[j] && str.indexOf(x) == -1) {
						delete arr[i][x]
					}
				}
			}
		}
		callback && callback(arr)
	})
}
// 按照指定字段合并对象
function createImportDiv(remark, checkFileWrong, importUrl) {
	var importDiv = $1("importDiv");
	if (importDiv == null) {
		importDiv = tssJS.createElement("div", null, "importDiv");
		document.body.appendChild(importDiv);

		var str = [];
		str[str.length] = "<form id='importForm' method='post' target='fileUpload' enctype='multipart/form-data'>";
		str[str.length] = "  <div class='fileUpload'> <input type='file' name='file' id='sourceFile' onchange=\"$('#importDiv h2').html(this.value)\" /> </div> ";
		str[str.length] = "  <input type='button' id='importBt' value='确定导入' class='tssbutton blue'/> ";
		str[str.length] = "</form>";
		str[str.length] = "<iframe style='width:0; height:0;' name='fileUpload'></iframe>";

		tssJS(importDiv).panel(remark, str.join("\r\n"), false);
		tssJS(importDiv).css("height", "300px").center();
	}

	//每次 importUrl 可能不一样，比如导入门户组件时。不能缓存
	tssJS("#importBt").click(function() {
		var fileValue = $1("sourceFile").value;
		if (!fileValue) {
			return tssJS("#importDiv h2").notice("请选择导入文件!");
		}

		var length = fileValue.length;
		var subfix = fileValue.substring(length - 4, length);
		if (checkFileWrong && checkFileWrong(subfix)) {
			return tssJS("#importDiv h2").notice(remark);
		}

		var form = $1("importForm");
		form.action = importUrl;
		// console.log(form);
		form.submit();

		var emptystr;

		$("#sourceFile").val(emptystr);
		$('#importDiv h2').html(remark);

		$(importDiv).hide();
	});

	return importDiv;
}

