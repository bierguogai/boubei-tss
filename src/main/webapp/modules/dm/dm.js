/* 放置DM模块公用的JS代码 */
GET_DATA_SERVICE = AUTH_PATH + "rp/dataservice";

if(IS_TEST) {
	GET_DATA_SERVICE = "data/data_service_list.xml";
}

// 管理数据源
function manageDS() {
	$.openIframePanel("dsPanel", "管理数据源", 900, 420, "datasource.html", true);
	// 先关闭report or record的编辑Tab，待数据源维护完成后再由重新打开
	ws && ws.closeActiveTab();
}

function checkDataService() {
	var old = $("#_options").value(), oldId;
	if(old) {
		old = old.split("/");
		oldId = old[old.length - 1];
	}
	popupTree(GET_DATA_SERVICE, "DataServiceList", {"_title": "可选数据服务", "_default": oldId}, function(target) {
		var ds = target.id;
		if( /^\d*$/.test(ds) ) {
			ds = NO_AUTH_PATH + 'data/json/' + target.id;
		}
        $("#_options").focus().value(ds);
    });
}