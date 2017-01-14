// 14345 返回 15000，坐标展示用 
function $round(intNum) {
	intNum = Math.round(intNum);

	if(intNum <= 10) return 10;
	if(intNum <= 100) return 100;

	var toString = intNum.toString();
	var length = toString.length;

	var result = parseInt(toString.charAt(0) + toString.charAt(1)) + 1;
	for(var i = 0; i < length - 2; i++) {
		result = result + "0";
	}

	return parseInt(result);
}

// 14345 返回 14000，坐标展示用 
function $ceil(intNum) {
	intNum = Math.ceil(intNum);

	if(intNum <= 10) return 0;
	if(intNum <= 100) return 10;

	var toString = intNum.toString();
	var length = toString.length;

	var result = parseInt(toString.charAt(0) + toString.charAt(1)) ;
	for(var i = 0; i < length - 2; i++) {
		result = result + "0";
	}

	return parseInt(result);
}

/*
 * 读取给定数组指定字段的最大值、最小值、平均值、总和
 * 参数1  dataArray 数组（一维数组 或 二维数组 或 一维对象数组）
 * 参数2  fieldIndex 字段的下标或Key值，为空则是一维数组
 */
function selectEdge(dataArray, fieldIndex) {
	if( dataArray == null ||　dataArray.length == 0 ) {
		return {"max" : 0, "min" : 0, "avg" : 0, "total" : 0};
	}
	var length = dataArray.length;

	var maxValue = 0, minValue = 999999999, total = 0, avgValue = 0;
	for(var i = 0; i < length; i++) {

		var value = fieldIndex ? dataArray[i][fieldIndex] : dataArray[i];

		maxValue = Math.max(maxValue, value);
		minValue = Math.min(minValue, value);
		total += value;
	}
	avgValue = Math.round(total / length);

	return {"max" : maxValue, "min" : minValue, "avg" : avgValue, "total" : total};
}


/*
 * 处理图表横轴坐标个数（太多了显示难看）。
 * 如果大于12个，则截取n + 1个
 */
function processLabelSize(labels, n) {
	n = n || 6;

	var _length = labels.length;
	if(_length > 12) {
		var labels2 = [];

		for(var i = 0; i < n; i++) {
			labels2.push(labels[Math.round(_length * i / n)]);
		}
		labels2.push(labels[_length - 1]);

		labels = labels2;
	}

	return labels;
}

/*
 * 给定数据，按数据大小划定为N个区间，并计算出每个区间值的频度。
 */
function delimitScope(dataArray, n, fieldIndex) {
	var edge = selectEdge(dataArray, fieldIndex);
	var max = $round(edge.max);
	var min = $ceil(edge.min);

	var scopes = [];
	if(max <= 100) {
		n = 5;
		scopes = [0, 10, 30, 50, 80, 100];
	}
	else {
		for(var i = 0; i <= n; i++) {
			scopes.push( $round( min + ((max - min) * i / n)) );
		}
	}

	var result = {};
	for(var j = 1; j <= n; j++) {
		var key =  scopes[j - 1] + " ~ " +  scopes[j];
		result[key] = 0;
	}

	var _length = dataArray.length;
	for(var i = 0; i < _length; i++) {
		var value = fieldIndex ? dataArray[i][fieldIndex] : dataArray[i];

		for(var j = 1; j <= n; j++) {
			if(value < scopes[j]) {
				var key =  scopes[j - 1] + " ~ " +  scopes[j];
				result[key] = result[key] + 1;
				break;
			}
		}
	}

	return result;
}


// 合并两个对象的属性
function combine(obj1, obj2) {
	if(obj1 == null) return obj2;
    if(obj2 == null) return obj1;

    var obj3 = {};
    for (var attrname in obj1) { obj3[attrname] = obj1[attrname]; }
    for (var attrname in obj2) { obj3[attrname] = obj2[attrname]; }
    return obj3;
}

/*
 * 过滤参数集，剔除掉为空的参数项。
 * PASS: 因JQuery的ajax会发送空字符串，而TSS的ajax不发送，导致数据缓存条件失效。
 */
function filterEmptyParams(params) {
	for(var key in params) {
		if(params[key] == null || params[key] == "") {
			delete params[key];
		}
	}
}

// 读取画布上一级element的大小，以自动调整画布的大小
function autoAdjustSize(elementID) {
	var parentNode, node = $1(elementID);
	parentNode = node.parentNode ? node.parentNode : document.body;

	var _width  = Math.max(600, parentNode.offsetWidth - 5);
    var _height = Math.max(300, parentNode.offsetHeight - 5);
    tssJS(node).css("width", _width + "px").css("height", _height + "px");

    return [ _width, _height];
}

function getLastFlushTime() {
	var today = new Date();
	return today.format('yyyy-MM-dd hh:mm:ss');  
}

/* ---------------------------------- 标准图表模板相关 ----------------------------------------------- */

function initResearhBt() {
    if( window.parent.tssJS("#searchFormDiv .btSearch").length ) {
    	var researhBt = $1("researh");
		if( !researhBt ) {
			researhBt = tssJS.createElement("input", "tssbutton blue bibt", "research"); 
			researhBt.setAttribute("type", "button");
			researhBt.setAttribute("value", "查询条件");
			document.body.appendChild(researhBt);
		}
        researhBt.onclick = function() {
            window.parent.tssJS("#searchFormDiv").show(true);
        }
    }
}

tssJS(function() {
 	initResearhBt();
});
