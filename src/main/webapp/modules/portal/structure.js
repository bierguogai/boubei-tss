/* 后台响应数据节点名称 */
XML_MAIN_TREE = "SourceTree";
XML_SITE_INFO = "DetailInfo";
XML_COMPONENT_PARAMETERS = "ComponentParams";
XML_THEME_MANAGE = "ThemeList";
XML_CACHE_MANAGE = "CacheManage";

/* 默认唯一编号名前缀  */
CACHE_TREE_NODE_DETAIL = "_treeNode_id_";
CACHE_THEME_MANAGE = "_theme_id_";
CACHE_CACHE_MANAGE = "_cache_id_";

/* XMLHTTP请求地址汇总 */
URL_SOURCE_TREE   = AUTH_PATH + "portal/list";
URL_SECTION_LIST  = AUTH_PATH + "portal/activePages/";
URL_SOURCE_DETAIL = AUTH_PATH + "portal/";
URL_SOURCE_SAVE   = AUTH_PATH + "portal";
URL_DELETE_NODE   = AUTH_PATH + "portal/";
URL_STOP_NODE     = AUTH_PATH + "portal/disable/";
URL_SORT_NODE     = AUTH_PATH + "portal/sort/";
URL_MOVE_NODE     = AUTH_PATH + "portal/move/";
URL_VIEW_SITE     = AUTH_PATH + "portal/preview/";
URL_GET_COMPONENT_PARAMS =  AUTH_PATH + "component/params/";     // {id}
URL_GET_COMPONENT_TREE =  AUTH_PATH + "component/enabledlist/";  // {type}
URL_THEME_MANAGE      = AUTH_PATH + "portal/theme/list/";
URL_RENAME_THEME      = AUTH_PATH + "portal/theme/rename/"; // {themeId}/{name}  PUT
URL_DEL_THEME         = AUTH_PATH + "portal/theme/";
URL_COPY_THEME        = AUTH_PATH + "portal/theme/";  // {themeId}/{name} POST
URL_PREVIEW_THEME     = AUTH_PATH + "portal/preview/"; // {portalId} ? themeId=?
URL_SET_DEFAULT_THEME = AUTH_PATH + "portal/theme/default/";  // {themeId}
URL_GET_OPERATION     = AUTH_PATH + "portal/operations/";
URL_FLUSH_CACHE       = AUTH_PATH + "portal/cache/";
URL_CACHE_MANAGE      = AUTH_PATH + "portal/cache/";
URL_GET_FLOW_RATE     = AUTH_PATH + "portal/flowrate/";

if(IS_TEST) {
	URL_SOURCE_TREE   = "data/structure_tree.xml?";
    URL_SECTION_LIST  = "data/structure_tree.xml?";
	URL_SOURCE_DETAIL = "data/structure_detail.xml?";
	URL_SOURCE_SAVE   = "data/_success.xml?";
	URL_DELETE_NODE   = "data/_success.xml?";
	URL_STOP_NODE     = "data/_success.xml?";
	URL_SORT_NODE     = "data/_success.xml?";
    URL_MOVE_NODE     = "data/_success.xml?";
	URL_VIEW_SITE     = "portal!previewPortal.action";
	URL_GET_COMPONENT_PARAMS = "data/structure-params.xml?";
	URL_GET_COMPONENT_TREE = "data/component_tree.xml?";
	URL_THEME_MANAGE      = "data/theme_list.xml?";
	URL_RENAME_THEME      = "data/_success.xml?";
	URL_DEL_THEME         = "data/_success.xml?";
	URL_COPY_THEME        = "data/theme_copy.xml?";
	URL_PREVIEW_THEME     = "data/_success.xml?";
	URL_SET_DEFAULT_THEME = "data/_success.xml?";
	URL_GET_OPERATION     = "data/_operation.xml?";
	URL_FLUSH_CACHE       = "data/_success.xml?";
	URL_CACHE_MANAGE      = "data/cachemanage.xml?";
	URL_GET_FLOW_RATE     = "data/page_flow_rate.xml?";
}

function init() {
    initMenus();
    initWorkSpace(false);
    initEvents();

    loadInitData();
}
 
function getStructureType() { return getTreeAttribute("type"); }
function isPortalNode()  { return getStructureType() == "0"; }
function isPageNode()    { return getStructureType() == "1"; }
function isSectionNode() { return getStructureType() == "2"; }
function isPortletNode() { return getStructureType() == "3"; }

function initMenus() {
    ICON = "images/";
    var item1 = {
        label:"新建门户",
        callback:function() { 
			addNewStructure("0"); 
		},
        visible:function() {return isTreeRoot() && getOperation("4");}
    }
    var item2 = {
        label:"新建页面",
        callback:function() {
			addNewStructure("1");
		},
        visible:function() {return isPortalNode() && getOperation("4");}
    }
    var item3 = {
        label:"新建版面",
        callback:function() {
			addNewStructure("2");
		},
        visible:function() {return (isPageNode() || isSectionNode()) && getOperation("4");}
    }
    var item4 = {
        label:"新建portlet实例",
        callback:function() {
			addNewStructure("3");
		},
        visible:function() {return (isPageNode() || isSectionNode()) && getOperation("4");}
    }
    var item5 = {
        label:"删除",
        callback:function() { delTreeNode() },
        icon:ICON + "icon_del.gif",
        visible:function() {return !isTreeRoot() && getOperation("3");}
    }
    var item6 = {
        label:"编辑",
        callback:editStructure,
        icon:ICON + "icon_edit.gif",
        visible:function() {return !isTreeRoot() && getOperation("2");}
    }
    var item7 = {
        label:"停用",
        callback: function() { stopOrStartTreeNode("1"); },
        icon:ICON + "icon_stop.gif",
        visible:function() {return !isTreeRoot() && !isTreeNodeDisabled() && getOperation("6");}
    }
    var item8 = {
        label:"启用",
        callback: function() { stopOrStartTreeNode("0"); },
        icon:ICON + "icon_start.gif",
        visible:function() {return !isTreeRoot() && isTreeNodeDisabled() && getOperation("7");}
    }
    var item9 = {
        label:"预览",
        callback:preview,
        icon:ICON + "preview.gif",
        visible:function() {return !isTreeRoot()  && getOperation("1");}
    }
    var item10 = {
        label:"主题管理",
        callback:themeManage,
        icon:ICON + "theme.gif",
        visible:function() {return isPortalNode() && getOperation("2");}
    }
    var item11 = {
        label:"缓存管理",
        callback:cacheManage,
        icon:ICON + "cache.gif",
        visible:function() {return isPortalNode() && getOperation("2");}
    }
	var item17 = {
        label:"资源管理",
        callback:function() {resourceManage();},
        visible:function() {return isPortalNode() && getOperation("2");}
    }
    var item12 = {
        label:"查看页面流量",
        callback:showPageFlowRate,
        visible:function() {return isPortalNode();}
    }
    var item13 = {
        label:"移动到...",
        callback:moveTo,
        icon:ICON + "icon_move.gif",
        visible: function() {return isPortletNode();}
    }

    var menu1 = new $.Menu();
    menu1.addItem(item1);
    menu1.addItem(item2);
    menu1.addItem(item3);
	menu1.addItem(item4);
    menu1.addItem(item6);
	menu1.addItem(item7);
    menu1.addItem(item8);
    menu1.addItem(item5);
    menu1.addItem(item13);
	menu1.addItem(item17);
    menu1.addSeparator();
    menu1.addItem(item9);
	menu1.addItem(item12);
	menu1.addItem(item10);
	menu1.addItem(item11);

    menu1.addItem(createPermissionMenuItem("4"));
	       
    $1("tree").contextmenu = menu1;
}

function loadInitData() {
    var onresult = function() {
        var tree = $.T("tree", this.getNodeValue(XML_MAIN_TREE));
        tree.onTreeNodeMoved = function(ev) { sortTreeNode(URL_SORT_NODE, ev); }
        tree.onTreeNodeRightClick = function(ev) { onTreeNodeRightClick(ev, true); }
        tree.onTreeNodeDoubleClick = function(ev) {
            var treeNode = getActiveTreeNode();
            getTreeOperation(treeNode, function(_operation) {
                var canEdit = checkOperation("2", _operation);
                if("_root" != treeNode.id && canEdit) {
                    editStructure();
                }
            });
        }
    }

	$.ajax({url: URL_SOURCE_TREE, onresult: onresult});
}

function addNewStructure(treeType) {
    var treeID = DEFAULT_NEW_ID;
    var names = ["门户", "页面", "版面", "portlet"];
	var treeName = names[parseInt(treeType)];

    var tree = $.T("tree");
    var treeNode = tree.getActiveTreeNode();
	var parentID = treeNode.id;

	var callback = {};
	callback.onTabChange = function() {
		setTimeout(function() {
			loadStructureDetailData(treeID, parentID, treeType);
		}, TIMEOUT_TAB_CHANGE);
	};
    callback.onTabClose = function() {
        delete $.cache.XmlDatas[DEFAULT_NEW_ID];
    };

	var inf = {};
	inf.defaultPage = "page1";
	inf.label = OPERATION_ADD.replace(/\$label/i, treeName);
	inf.callback = callback;
	inf.SID = CACHE_TREE_NODE_DETAIL + treeType + treeID;
	ws.open(inf);
}

function editStructure() {
    var tree = $.T("tree");
    var treeNode = tree.getActiveTreeNode();
	var treeID   = treeNode.id;
	var treeName = treeNode.name;
	var treeType = treeNode.getAttribute("type");

	var callback = {};
	callback.onTabChange = function() {
		setTimeout(function() {
			loadStructureDetailData(treeID, null, treeType);
		},TIMEOUT_TAB_CHANGE);
	};

	var inf = {};
	inf.label = OPERATION_EDIT.replace(/\$label/i, treeName);
	inf.SID = CACHE_TREE_NODE_DETAIL + treeID;
	inf.defaultPage = "page1";
	inf.callback = callback;
	ws.open(inf);
}

function loadStructureDetailData(treeID, parentID, treeType) {
    delete $.cache.XmlDatas[DEFAULT_NEW_ID];
    var dataXmlNode = $.cache.XmlDatas[treeID];
    if(dataXmlNode) {
        return initForm();
    }

	var request = new $.HttpRequest();
	request.url = URL_SOURCE_DETAIL + treeID;

	// 如果是新增
	if( treeID == DEFAULT_NEW_ID ) {
		request.addParam("type", treeType);
		request.addParam("parentId", parentID == "_root" ? "0" : parentID);
	} 

	request.onresult = function() {
		dataXmlNode = this.getNodeValue(XML_SITE_INFO);
		preProcessXml(dataXmlNode, treeType);

		$.cache.XmlDatas[treeID] = dataXmlNode;
		initForm();
	}
	request.send();

    function initForm() {
        var page1Form = $.F("page1Form", dataXmlNode);

        attachReminder(treeID, page1Form);

        $("#page1BtSave").click(function() {
            saveStructure(treeID, parentID);
        });
    }
}

function saveStructure(treeID, parentID) {
    var page1Form = $.F("page1Form");
    if( !page1Form.checkForm() ) {
        switchToPhase(ws, "page1");
        return;
    }

    var request = new $.HttpRequest();
    request.url = URL_SOURCE_SAVE;

    if( treeID == DEFAULT_NEW_ID ) {
        request.addParam("code", $.now());
    }

	var dataXmlNode = $.cache.XmlDatas[treeID];
	var dataNode = dataXmlNode.querySelector("data");
	dataNode = dataNode.cloneNode(true);
	var rowNode = dataNode.querySelector("row");

	// 门户、页面节点需要拼接supplement属性：将css,js部分拼合成一个xml文档
	var type = dataXmlNode.getAttribute("type");
	if("0" == type || "1" == type) {
		var rootName = ("0" == type? "portal" : "page");
		var name    = $.XML.getCDATA(rowNode, "name") || "";
		var js      = $.XML.getCDATA(rowNode, "js") || "";
		var jsCode  = $.XML.getCDATA(rowNode, "jsCode") || "";
		var css     = $.XML.getCDATA(rowNode, "css") || "";
		var cssCode = $.XML.getCDATA(rowNode, "cssCode") || "";
		
		var str = [];
		str[str.length] = "<" + rootName + ">";
		str[str.length] = "<property>";
		str[str.length] = "  <name>" + name + "</name>";
		str[str.length] = "  <description><![CDATA[]]></description>";
		str[str.length] = "</property>";
		str[str.length] = "<script>";
		str[str.length] = "  <file><![CDATA[" + js + "]]></file>";
		str[str.length] = "  <code><![CDATA[" + jsCode + "]]></code>";
		str[str.length] = "</script>";
		str[str.length] = "<style>";
		str[str.length] = "  <file><![CDATA[" + css + "]]></file>";
		str[str.length] = "  <code><![CDATA[" + cssCode + "]]></code>";
		str[str.length] = "</style>";
		str[str.length] = "</" + rootName + ">";

		$.XML.setCDATA(rowNode, "supplement",str.join(""));
	} 
	else {
		$.XML.removeCDATA(rowNode, "supplement");
	}
	$.XML.removeCDATA(rowNode, "js");
	$.XML.removeCDATA(rowNode, "jsCode");
	$.XML.removeCDATA(rowNode, "css");
	$.XML.removeCDATA(rowNode, "cssCode");

	request.setFormContent(dataNode);
 
    syncButton([$1("page1BtSave")], request); // 同步按钮状态

    request.onresult = function() {
        afterSaveTreeNode.call(this, treeID, parentID);
    }
    request.onsuccess = function() {
        afterSaveTreeNode(treeID, page1Form);
    }
    request.send();	
}

/* 根据树节点type属性，预先处理xform数据 */
function preProcessXml(xmlIsland, treeType) {
    // 在根节点上加type属性，用于saveStructure时判断
    xmlIsland.setAttribute("type", treeType);

    // 清除有showType属性，但与当前treeType不匹配的节点
    var showTypeNodes = xmlIsland.querySelectorAll("*[showType]");
    for(var i=0; i < showTypeNodes.length; i++) {
        var curNode = showTypeNodes[i];
        var showType = curNode.getAttribute("showType").split(",");
        var flag = true;
        for(var j=0; j < showType.length; j++) {
            if(treeType == showType[j]) {
                flag = false;
                break;
            }
        }
        if(flag) {
            $.removeNode(curNode);			
        }
    }

    // 控制配置按钮可见性
    var rowNode = xmlIsland.querySelector("data>row");
    var definerName   = $.XML.getCDATA(rowNode, "definer.name")||"";
    var decoratorName = $.XML.getCDATA(rowNode, "decorator.name")||"";

    var configDefinerBtNode   = xmlIsland.querySelector("*[id='configDefinerBt']");
    var configDecoratorBtNode = xmlIsland.querySelector("*[id='configDecoratorBt']");

    var parameters = $.XML.getCDATA(rowNode, "parameters");
    if(parameters) {
        var xmlNode = $.XML.toNode(parameters);
        var portletParams   = xmlNode.querySelector("portlet");
        var layoutParams    = xmlNode.querySelector("layout");
        var decoratorParams = xmlNode.querySelector("decorator");

        if(configDefinerBtNode) {
            switch(treeType) {
                case "0":
                case "1":
                case "2":
                    if(layoutParams == null) {
                        configDefinerBtNode.setAttribute("disabled", "true");                
                    }
                    break;
                case "3":
                    if(portletParams == null) {
                        configDefinerBtNode.setAttribute("disabled", "true");
                    }
                    break;
            }
        }
        if( configDecoratorBtNode && (decoratorName == "" || decoratorParams == null)) {
            configDecoratorBtNode.setAttribute("disabled", "true");
        }
    } 
	else {
        if(configDefinerBtNode) {
            configDefinerBtNode.setAttribute("disabled", "true"); 
        }
        if(configDecoratorBtNode) {
            configDecoratorBtNode.setAttribute("disabled", "true");
        }
    }

    var definerNode   = xmlIsland.querySelector("column[name='definer.name']");
    var decoratorNode = xmlIsland.querySelector("column[name='decorator.name']");
    var rowNode = xmlIsland.querySelector("data>row");

    var componentType;
    switch(treeType) {
        case "0":
        case "1":
        case "2":
			componentType = "1";
            definerNode.setAttribute("caption", "布局器");
            decoratorNode.setAttribute("caption", "修饰器");
            break;
        case "3":
			componentType = "3";
            definerNode.setAttribute("caption", "Portlet");
            decoratorNode.setAttribute("caption", "修饰器");
            break;
    }

	// 根据treeType，给definerNode, decoratorNode节点设置不同属性
    var layoutCmd = definerNode.getAttribute("cmd");
    definerNode.setAttribute("cmd", layoutCmd.replace(/\${definerType}/i, componentType));

    // 门户、页面类型节点需要预解析supplement属性
    switch(treeType) {
        case "0":
        case "1":
            // 预解析supplement，分别设置到js,css,jsCode和cssCode上
            var supplement = $.XML.getCDATA(rowNode, "supplement");
            if(supplement) {
                var supplementNode = $.XML.toNode(supplement);
                var jsNode      = supplementNode.querySelector("script>file");
                var cssNode     = supplementNode.querySelector("style>file");
                var jsCodeNode  = supplementNode.querySelector("script>code");
                var cssCodeNode = supplementNode.querySelector("style>code");

                if(jsNode) {
                    $.XML.setCDATA(rowNode, "js", $.XML.getText(jsNode));
                }
                if(cssNode) {
                    $.XML.setCDATA(rowNode, "css", $.XML.getText(cssNode));
                }
                if(jsCodeNode) {
                    $.XML.setCDATA(rowNode, "jsCode", $.XML.getText(jsCodeNode));
                }
                if(cssCodeNode) {
                    $.XML.setCDATA(rowNode, "cssCode", $.XML.getText(cssCodeNode));
                }
            }
           $.XML.removeCDATA(rowNode, "supplement");

            break;
        case "2":
        case "3":
            break;
    }
}

function preview() {
    var treeNode = getActiveTreeNode();
	var portalID = treeNode.getAttribute("portalId");

	var url	= URL_VIEW_SITE + portalID;
	if( !isPortalNode() ) {
		url += "?pageId=" + treeNode.id;
	}
	window.open(url);
}

function getComponent(type, idField, nameField, parametersName) {
    var page1Form = $.F("page1Form");
	var url = URL_GET_COMPONENT_TREE + type;

    popupTree(url, "SourceTree", {}, function(component){
        page1Form.updateDataExternal(idField, component.id);
        page1Form.updateDataExternal(nameField, component.name);

        // 加载布局器配置项
        $.ajax({
            url: URL_GET_COMPONENT_PARAMS + component.id,
            onresult: function() {
                var newNode = this.getNodeValue(XML_COMPONENT_PARAMETERS);
                updateParameters(newNode);
                
                // 是否允许进行配置
                var disabled = ( 0 == newNode.attributes.length );
                if(type == "2") {
                    $1("configDecoratorBt").disabled = disabled;
                } else {
                    $1("configDefinerBt").disabled = disabled;
                }
                
            }
        });
    });
}

/* 将parameters字符串解析为xml对象 */
function parseParameters(parameters) {
    parameters = parameters || "<" + XML_COMPONENT_PARAMETERS + "/>";
    return $.XML.toNode(parameters);    
}

/*
 *	更新布局器、修饰器的配置参数节点
 *	参数：	string:parametersName   xform列名
            XmlNode:newNode         XmlNode实例
 */
function updateParameters(newNode) {
	var type = newNode.nodeName;

	var page1Form = $.F("page1Form");
	var parameters = page1Form.getData("parameters")||"";
	var xmlNode = parseParameters(parameters);
	var oldNode = xmlNode.querySelector(type);

	if(oldNode) {
		var attrNames = [];
        $.each(oldNode.attributes, function(i, attr) {
            attrNames.push(attr.nodeName);
        });
		attrNames.each(function(i, attrName) {
            oldNode.removeAttribute(attrName);
        });

        $.each(newNode.attributes, function(i, attr) {
            oldNode.setAttribute(attr.nodeName, attr.value);
        });

		if(oldNode.firstChild) {
			var oldText = oldNode.firstChild;
			$.removeNode(oldText);
		}
		if(newNode.firstChild) {
			var newText = newNode.firstChild;
			oldNode.appendChild(newText);
		}
	} 
	else {
		xmlNode.appendChild(newNode);
	}

	// 更新xform中的parameters值
	page1Form.updateDataExternal("parameters", $.XML.toXml(xmlNode));
}

/*
 *	更改布局器、修饰器、portlet的配置
 *	参数：	string:paramsType       类型(布局器、修饰器、portlet)
            string:id               xform列名
            string:name             xform列名
    eg: "<decorator bgColor=\"#ABCDEF\">model/decorator/decorator-8/paramsXForm.xml</decorator>";
 */
function configParams(paramsType, id, name) {
    var page1Form = $.F("page1Form");
    var nameValue  = page1Form.getData(name) || "";
    var parameters = page1Form.getData("parameters");
    if($.isNullOrEmpty(parameters)) return;

    var xmlNode = parseParameters(parameters.revertEntity());
    var oldParamsNode = xmlNode.querySelector(paramsType);

    var title = "配置【" + nameValue + "】的参数";
    var url = "#";
    if( oldParamsNode && oldParamsNode.firstChild){
        url = $.XML.getText(oldParamsNode.firstChild); // 组件参数配置模板所在目录
    } else {
        return;
    }

    var _params = {};
    $.each(oldParamsNode.attributes, function(i, attr){
        _params[attr.nodeName] = attr.value;
    });
    popupForm(url, "ConfigParams", _params, function(condition) {
        var newParamsNode = $.XML.toNode("<" + paramsType + "/>")
        $.each(condition, function(key, value){
            newParamsNode.setAttribute(key, value); // 复制到新参数节点
        });

        newParamsNode.appendChild(oldParamsNode.firstChild);
        updateParameters(newParamsNode);

    }, title);
}

/* 主题管理  */
function themeManage() {
    var treeNode = $.T("tree").getActiveTreeNode();
	var treeName = treeNode.name;
	var portalId = treeNode.getAttribute("portalId");

	var callback = {};
	callback.onTabChange = function() {
		setTimeout(function() {
			loadThemeManageData(portalId);
		},TIMEOUT_TAB_CHANGE);
	};

	var inf = {};
	inf.defaultPage = "page2";
	inf.label = OPERATION_SETTING.replace(/\$label/i, treeName);
	inf.callback = callback;
	inf.SID = CACHE_THEME_MANAGE + portalId;
	ws.open(inf);
}

/* 主题管理详细信息加载数据 */
function loadThemeManageData(portalId) {
	$.ajax({
		url: URL_THEME_MANAGE + portalId,
		onresult: function() {
            var themeManageNode = this.getNodeValue(XML_THEME_MANAGE);
            var page2Tree = $.T("page2Tree", themeManageNode);

            page2Tree.onTreeNodeRightClick = function(ev) {
                page2Tree.el.contextmenu.show(ev.clientX, ev.clientY);
            }
     
            initThemeTreeMenu(portalId);

            $("#page2BtSave").hide();
        }
	});
}

function initThemeTreeMenu(portalId) {
    var item1 = {
        label:"更名",
        callback:changeThemeName,
        visible:function() {return !isThemeRootNode();}
    }
    var item2 = {
        label:"删除",
        callback:delTheme,
        icon:ICON + "icon_del.gif",
        visible:function() {return !isThemeRootNode();}
    }
    var item3 = {
        label:"复制",
        callback:copyTheme,
        icon:ICON + "icon_copy.gif",
        visible:function() {return !isThemeRootNode();}
    }
    var item4 = {
        label:"预览",
        callback:function() {
            previewTheme(portalId);
        },
        icon:ICON + "preview.gif",
        visible:function() {return !isThemeRootNode();}
    }
    var item5 = {
        label:"设为默认",
        callback:setDefaultTheme,
        visible:function() {return !isThemeRootNode() && "1" != getThemeAttribute("isDefault");}
    }

    var menu1 = new $.Menu();
    menu1.addItem(item1);
    menu1.addItem(item2);
    menu1.addItem(item3);
    menu1.addSeparator();
    menu1.addItem(item4);
    menu1.addItem(item5);

    $1("page2Tree").contextmenu = menu1;
}

function getThemeId() {
    var treeNode = $.T("page2Tree").getActiveTreeNode();
    if(treeNode) {
        return treeNode.id;
    }
	return null;
}

function isThemeRootNode() {
	return "_root" == getThemeId(); 
}

function getThemeAttribute(attrName) {
    var treeNode = $.T("page2Tree").getActiveTreeNode();
    if(treeNode) {
        return treeNode.getAttribute(attrName);
    }
    return null;
}

/* 修改主题名 */
function changeThemeName() {
    var page2Tree = $.T("page2Tree");
    var treeNode = page2Tree.getActiveTreeNode();
    if(treeNode) {
        var treeID = treeNode.id;
        var treeName = treeNode.name;

		$.prompt("请输入新主题名",  "重新命名【" + treeName + "】为", function(value) {
 			if ( $.isNullOrEmpty(value) ) return alert("主题名不能为空。");
 			
			var newName = value.replace(/[\s　]/g, "");
			$.ajax({
				url: URL_RENAME_THEME + treeID + "/" + newName,
				method: "PUT",
				onsuccess: function() {
					treeNode.setAttribute("name", newName);
					modifyTreeNode(treeID, "name", newName, "page2Tree");
				}
			});
		});       
    }
}

/* 删除主题 */
function delTheme() {
    var page2Tree = $.T("page2Tree");
    var treeNode = page2Tree.getActiveTreeNode();
    if(treeNode) {
        var treeID = treeNode.id;
		$.ajax({
			url: URL_DEL_THEME + treeID,
			method: "DELETE",
			onsuccess: function() {
				page2Tree.removeTreeNode(treeNode);
			}
		});
    }
}

/* 复制主题  */
function copyTheme(portalId) {
    var page2Tree = $.T("page2Tree");
    var treeNode = page2Tree.getActiveTreeNode();
    if(treeNode) {
        var treeID = treeNode.id;
        var treeName = treeNode.name;
		var newName =  treeName + "_2";

		$.ajax({
			url: URL_COPY_THEME + treeID + "/" + newName,
			onresult: function() {
				var xmlNode = this.getNodeValue(XML_THEME_MANAGE).querySelector("treeNode");
				var rootNode = page2Tree.getTreeNodeById("_root");
                appendTreeNode("_root", xmlNode, "page2Tree");
			}
		});
    }
}

/* 预览主题 */
function previewTheme(portalId) {
    var treeNode = $.T("page2Tree").getActiveTreeNode();
    if(treeNode) {
        var url = URL_PREVIEW_THEME + portalId + "?themeId=" + treeNode.id;
        window.open(url);
    }
}

/* 设置默认主题 */
function setDefaultTheme() {
    var page2Tree = $.T("page2Tree");
    var treeNode = page2Tree.getActiveTreeNode();
    if(treeNode) {
        $.ajax({
			url: URL_SET_DEFAULT_THEME + treeNode.id,
			method: "PUT",
			onsuccess: function() {
                // 先清除前次默认主题名称
                page2Tree.getAllNodes().each(function(i, node) {
                    if(node.getAttribute('isDefault') == '1') {
                        node.setAttribute("icon", ICON + "theme.gif");
                        node.setAttribute("isDefault", "0");
                        node.li.selfIcon.css("backgroundImage", "url(" + ICON + "theme.gif" + ")");
                    }
                })

                // 修改当前节点名称及属性
                treeNode.setAttribute("icon", ICON + "default_theme.gif");
                treeNode.setAttribute("isDefault", "1");    
                treeNode.li.selfIcon.css("backgroundImage", "url(" + ICON + "default_theme.gif" + ")");        
            }
		});
    }
}

/* 缓存管理  */
function cacheManage() {
    var tree = $.T("tree");
    var treeNode = tree.getActiveTreeNode();
	var treeID = treeNode.id;
	var treeName = treeNode.name;
	var portalId = treeNode.getAttribute("portalId");

	var callback = {};
	callback.onTabChange = function() {
		setTimeout(function() {
			loadCacheManageData(treeID, portalId);
		}, TIMEOUT_TAB_CHANGE);
	};

	var inf = {};
	inf.defaultPage = "page3";
	inf.label = OPERATION_SETTING.replace(/\$label/i, treeName);
	inf.callback = callback;
	inf.SID = CACHE_CACHE_MANAGE + treeID;
	ws.open(inf);
}

/* 缓存管理详细信息加载数据 */
function loadCacheManageData(treeID, portalId) {
	var onresult = function() {
		var cacheManageNode = this.getNodeValue(XML_CACHE_MANAGE);

		var listObj = $1("page3CacheList");
		var str = [];
        str[str.length] = "<table>";

        var cacheItems = cacheManageNode.querySelectorAll("cacheItem");
        for(var i=0; i < cacheItems.length; i++) {
            var cacheItem = cacheItems[i];
            var name = cacheItem.getAttribute("name");
            var id = cacheItem.getAttribute("id");
            str[str.length] = "<tr height='30'><td class='b' width='30%'>" + name + 
				"</td><td class='b'><input type='button' class='tssbutton small blue' value='刷新' onclick='flushCache(" + id + ", " + portalId + ")'/></td></tr>";
        }
        str[str.length] = "</table>";

        listObj.innerHTML = str.join("\r\n");

		// 设置按钮显示状态
		$("#page3BtSave").hide();
	}
	
	$.ajax({
		url:  URL_CACHE_MANAGE + portalId,
		onresult: onresult
	});
}

/* 刷新缓存  */
function flushCache(themeId, portalId) {
	$.ajax({
		url:  URL_FLUSH_CACHE + portalId + "/" + themeId,
		method: "DELETE"
	});
}

/* 查看页面流量  */
function showPageFlowRate() {
    var treeNode = $.T("tree").getActiveTreeNode();
	var portalId = treeNode.getAttribute("portalId");
	var url = URL_GET_FLOW_RATE + portalId;
    popupGrid(url, "PageFlowRate", "查看页面流量", {});
}

/* 组件资源管理 */
function resourceManage() {
    var treeNode = getActiveTreeNode();
	var code = treeNode.getAttribute("code");
	var title = "管理【" + treeNode.name + "】的文件资源："

    fileManage("code=" + code, title);
}

function moveTo() {
    var tree = $.T("tree");
    var treeNode = tree.getActiveTreeNode();
    var id = treeNode.id;
    var portalId = treeNode.getAttribute("portalId");

    var url = URL_SECTION_LIST + portalId;
    popupTree(url, "PageTree", {}, function(target) {
        moveTreeNode(tree, id, target.id)
    });
}

window.onload = init;