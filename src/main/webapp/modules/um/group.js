    /* 后台响应数据节点名称 */
    XML_MAIN_TREE = "GroupTree";
    XML_USER_LIST = "SourceList";
 
    XML_USER_INFO = "UserInfo";
    XML_USER_TO_GROUP_TREE = "User2GroupTree";
    XML_USER_TO_GROUP_EXIST_TREE = "User2GroupExistTree";
    XML_USER_TO_ROLE_TREE = "User2RoleTree";
    XML_USER_TO_ROLE_EXIST_TREE = "User2RoleExistTree";

    XML_GROUP_INFO = "GroupInfo";
    XML_GROUP_TO_USER_TREE = "Group2UserTree";
    XML_GROUP_TO_USER_LIST_TREE = "Group2UserListTree";
    XML_GROUP_TO_USER_EXIST_TREE = "Group2UserExistTree";
    XML_GROUP_TO_ROLE_TREE = "Group2RoleTree";
    XML_GROUP_TO_ROLE_EXIST_TREE = "Group2RoleExistTree";
 
    XML_SEARCH_SUBAUTH = "SUBAUTH_RESULT";
    XML_SEARCH_ROLE = "ROLE_RESULT";
    XML_SEARCH_USER = "USER_RESULT";
 
    /* 默认唯一编号名前缀 */
    CACHE_GRID_ROW_DETAIL = "_row_id";
    CACHE_TREE_NODE_DETAIL = "_treeNode_id";
    CACHE_MAIN_TREE = "_tree_id";
 
    /* XMLHTTP请求地址汇总 */
    URL_INIT          = AUTH_PATH + "group/list";
    URL_POPUP_TREE    = AUTH_PATH + "group/list/";
    URL_USER_GRID     = AUTH_PATH + "user/list/";    // user/list/{groupId}/{page}
    URL_USER_DETAIL   = AUTH_PATH + "user/detail/";  // user/detail/{groupId}/{userId}
    URL_GROUP_DETAIL  = AUTH_PATH + "group/detail/"; // group/detail/{parentId}/{id}/{type}
    URL_SAVE_USER     = AUTH_PATH + "user";   // POST
    URL_SAVE_GROUP    = AUTH_PATH + "group";  // POST
    URL_DELETE_GROUP  = AUTH_PATH + "group/"; 
    URL_DEL_USER      = AUTH_PATH + "user/";
    URL_STOP_GROUP    = AUTH_PATH + "group/disable/"; 
    URL_SORT_GROUP    = AUTH_PATH + "group/sort/";
    URL_MOVE_NODE     = AUTH_PATH + "group/move/"; // {id}/{toGroupId}
    URL_STOP_USER     = AUTH_PATH + "user/disable/";
    URL_GET_OPERATION = AUTH_PATH + "group/operations/"; 
    URL_GROUP_USERS   = AUTH_PATH + "group/users/";  // {groupId}
    URL_INIT_PASSWORD = AUTH_PATH + "user/initpwd/"; // user/initpwd/{groupId}/{userId}/{password}

    URL_SEARCH_USER   = AUTH_PATH + "user/search";
    URL_SEARCH_ROLE   = AUTH_PATH + "search/roles/";
    URL_SEARCH_SUBAUTH= AUTH_PATH + "search/subauth/";
    URL_SYNC_GROUP    = AUTH_PATH + "group/sync/";
    URL_SYNC_PROGRESS = AUTH_PATH + "group/progress/";  // {code} GET
    URL_CANCEL_SYNC   = AUTH_PATH + "group/progress/";  // {code} DELETE
    
    if(IS_TEST) {
        URL_INIT = "data/group_tree.xml?";
        URL_POPUP_TREE = "data/group_tree.xml?";
        URL_USER_GRID = "data/user_grid.xml?";
        URL_USER_DETAIL = "data/user_detail.xml?";
        URL_GROUP_DETAIL = "data/group_detail.xml?";
        URL_SAVE_USER = "data/_success.xml?";
        URL_SAVE_GROUP = "data/_success.xml?";
        URL_DELETE_GROUP = "data/_success.xml?";
        URL_STOP_GROUP = "data/_success.xml?";
        URL_SORT_GROUP = "data/_success.xml?";
        URL_MOVE_NODE  = "data/_success.xml?";
        URL_STOP_USER = "data/_success.xml?";
        URL_GROUP_USERS = "data/userlist.xml?";
        URL_DEL_USER = "data/_success.xml?";
        URL_INIT_PASSWORD = "data/_success.xml?";
        URL_GET_OPERATION = "data/operation.xml?";
        
        URL_SEARCH_USER    = "data/user_search.xml?";
        URL_SEARCH_ROLE    = "data/user_grid.xml?";
        URL_SEARCH_SUBAUTH = "data/user_grid.xml?";
        URL_SYNC_GROUP    = "data/_progress.xml?";
        URL_SYNC_PROGRESS = "data/_progress.xml?";
        URL_CANCEL_SYNC   = "data/_success.xml?";
    }
 
    function init() {
        initMenus();
        initWorkSpace();
        initEvents();

        loadInitData();

        bindTreeSearch("sk1", "page2Tree");
        bindTreeSearch("sk2", "page3Tree");
        bindTreeSearch("sk3", "page4Tree");
        bindTreeSearch("sk4", "page4Tree2");
    }

    function initMenus(){
        initTreeMenu();
        initGridMenu();
    }
    
    /* 是否为主用户组 */
    function isMainGroup(treeNode) {
        return (treeNode ? treeNode.getAttribute("groupType") : getTreeAttribute("groupType")) == "1";
    }
    
    /* 是否自注册用户组节点 */
    function isSelfRegisterGroup(id){
        if( id == null ) {
            var treeNode = $.T("tree").getActiveTreeNode();
            if( treeNode ) {
                id = treeNode.id;
            }            
        }
        return ("-7"==id );
    }
    
    function editable() {
        return !isTreeRoot() && getTreeNodeId() > 0 && getOperation("2");
    }

    function initTreeMenu(){
        ICON = "images/"
        var item1 = {
            label:"停用",
            callback:function() { stopOrStartTreeNode("1", URL_STOP_GROUP); },
            icon:ICON + "stop.gif",
            visible:function(){return editable() && !isTreeNodeDisabled();}
        }
        var item2 = {
            label:"启用",
            callback:function() { stopOrStartTreeNode("0", URL_STOP_GROUP); },
            icon:ICON + "start.gif",
            visible:function(){return editable() && isTreeNodeDisabled();}
        }
        var item3 = {
            label:"编辑",
            callback:editGroupInfo,
            icon:ICON + "edit.gif",
            visible:function(){return editable();}
        }
        var item4 = {
            label:"删除",
            callback:function() { delTreeNode(URL_DELETE_GROUP); },
            icon:ICON + "icon_del.gif",
            visible:function() { return editable(); }
        }
        var item6 = {
            label:"新建用户组",
            callback:addNewGroup,
            visible:function(){ return !isSelfRegisterGroup() && getOperation("2"); }
        }
        var item7 = {
            label:"新建用户",
            callback:addNewUser,
            visible:function(){ return !isTreeRoot() && isMainGroup() && editable(); }
        }
        var item8 = {
            label:"浏览用户",
            callback:function() { showUserList(); },
            icon:ICON + "view_list.gif",
            visible:function(){ return !isTreeRoot() && getTreeNodeId() != -2 && getTreeNodeId() != -3 && getOperation("1"); }
        }
        var item9 = {
            label:"搜索用户...",
            callback:searchUser,
            icon:ICON + "search.gif",
            visible:function() { return !isTreeRoot() && isMainGroup() && getOperation("1"); }
        }
        var item11 = {
            label:"移动到...",
            callback:moveNodeTo,
            icon:ICON + "move.gif",            
            visible:function() {return editable();}
        }

        var item12 = {
            label:"辅助功能",
            callback:null,
            visible:function() { return isMainGroup() && editable(); }
        }
        var subitem12_1 = {
            label:"初始化密码...",
            callback:resetPassword,
            icon:ICON + "init_password.gif"
        }
        var subitem12_2 = {
            label:"用户同步",
            callback:function() { syncGroup(); }
        }
        var subitem12_4 = {
            label:"综合查询",
            icon:ICON + "search.gif"
        }
        var subitem12_4_1 = {
            label:"用户角色",
            callback:generalSearchRole
        }
        var subitem12_4_2 = {
            label:"用户转授",
            callback:generalSearchSubauth
        }

        var submenu12_4 = new $.Menu();
        submenu12_4.addItem(subitem12_4_1);
        submenu12_4.addItem(subitem12_4_2);
        subitem12_4.submenu = submenu12_4;

        var submenu12 = new $.Menu();
        submenu12.addItem(subitem12_1);
        submenu12.addItem(subitem12_2);
        submenu12.addItem(subitem12_4);
        item12.submenu = submenu12;
 
        var menu1 = new $.Menu();
        menu1.addItem(item3);
        menu1.addItem(item1);
        menu1.addItem(item2);
        menu1.addItem(item4);
        menu1.addItem(item11);
        menu1.addItem(item6);
        menu1.addSeparator();
        // menu1.addItem(item7);
        menu1.addItem(item8);
        menu1.addItem(item9);
        menu1.addSeparator();
        menu1.addItem(item12);

        menu1.addItem(createPermissionMenuItem("1"));

        $1("tree").contextmenu = menu1;
    }
 
    function initGridMenu() {
        var item1 = {
            label:"停用",
            callback:function() { stopOrStartUser("1"); },
            icon:ICON + "stop.gif",
            visible:function() { return getUserOperation("2") && "0" == getUserState(); }
        }
        var item2 = {
            label:"启用",
            callback:function() { stopOrStartUser("0"); },
            icon:ICON + "start.gif",
            visible:function() { return getUserOperation("2") && "1" == getUserState(); }
        }
        var item3 = {
            label:"编辑",
            callback:editUserInfo,
            icon:ICON + "edit.gif",
            visible:function() { return getUserOperation("2"); }
        }
        var item4 = {
            label:"删除",
            callback: function() { delelteUser(); },
            icon:ICON + "del.gif",
            visible:function() { return getUserOperation("2"); }
        }
 
        var menu1 = new $.Menu();
        menu1.addItem(item1);
        menu1.addItem(item2);
        menu1.addItem(item3);
        menu1.addItem(item4);
 
        $1("grid").contextmenu = menu1;

        $1("grid").onRightClickRow = function() {
            $1("grid").contextmenu.show(event.clientX, event.clientY);
        }   
    }

    function delelteUser() {
        $.confirm("您确定要删除该用户记录吗？", "删除确认", function(){
            var grid = $.G("grid");
            var userID  = grid.getColumnValue("id");
            var groupId = grid.getColumnValue("groupId");
            if( userID ) {
                $.ajax({
                    url : URL_DEL_USER + groupId + "/" + userID,
                    method : "DELETE",
                    onsuccess : function() { 
                        grid.deleteSelectedRow();
                    }
                }); 
            }
        });
    }
 
    function loadInitData(defaultOpenId) {
        var onresult = function(){
            var groupTreeNode = this.getNodeValue(XML_MAIN_TREE);
            $.cache.XmlDatas[CACHE_MAIN_TREE] = groupTreeNode;
            var tree = $.T("tree", groupTreeNode);

            showUserList(defaultOpenId || -7);

            tree.onTreeNodeActived = function(ev){ onTreeNodeActived(ev); }
            tree.onTreeNodeDoubleClick = function(ev){
                var treeNode = getActiveTreeNode();
                getTreeOperation(treeNode, function(_operation) {
                    if(treeNode.id != -2) { // 防止浏览到Admin和匿名用户
                        showUserList();
                    }
                });
            }
            tree.onTreeNodeMoved = function(ev){ sort(ev); }
            tree.onTreeNodeRightClick = function(ev){ onTreeNodeRightClick(ev, true); }
        }
        
        $.ajax({url : URL_INIT, method : "GET", onresult : onresult});
    }
 
    function sort(ev) {
        var movedNode  = ev.dragNode;
        var movedNodeID = movedNode.id;     
        if("-2" == movedNodeID || "-3" == movedNodeID ) {
            alert("不能移动此节点!");
            return;
        }

        sortTreeNode(URL_SORT_GROUP, ev);
    }

    function moveNodeTo() {
        var tree = $.T("tree");
        var treeNode = tree.getActiveTreeNode();
        var id  = treeNode.id;
        var pId = treeNode.parent.id;
        var groupType = treeNode.getAttribute("groupType");

        var params = {id:id, parentID: pId};
        popupTree(URL_POPUP_TREE + groupType, "GroupTree", params, function(target) {
            if(target.id == -1 || target.id == -7) {
                return alert("不能移动到根目录或自注册用户组下面");
            }
            moveTreeNode(tree, id, target.id);
        });
    }
 
    /* 初始化密码  */
    function resetPassword(){
        var treeNode = $.T("tree").getActiveTreeNode();
        $.prompt("请输入新密码", "初始化【" + treeNode.name + "】的密码", function(value) {
            if ( $.isNullOrEmpty(value) ) return alert("密码不能为空。");
            
            $.ajax({
                url : URL_INIT_PASSWORD + treeNode.id + "/0/" + value, 
                waiting: true
            });
        });
    }
 
    function editGroupInfo(newGroupID) { 
        var isAddGroup = (DEFAULT_NEW_ID == newGroupID);
    
        var treeNode = $.T("tree").getActiveTreeNode();
        var treeID   = isAddGroup ? newGroupID : treeNode.id;
        var treeName = isAddGroup ? "用户组" : treeNode.name;      
        var parentID = isAddGroup ? treeNode.id : treeNode.parent.id;       
        var groupType = treeNode.getAttribute("groupType");

        var phases = [];
        phases[0] = {page:"page1",label:"基本信息"};
        if( isMainGroup() ) { 
            phases[1] = {page:"page3",label:"拥有角色"};
        } else {
            phases[1] = {page:"page4",label:"用户列表"};
            phases[2] = {page:"page3",label:"拥有角色"};
        }       
        
        var callback = {};
        callback.onTabChange = function() {
            setTimeout(function() {
                loadGroupDetailData(treeID, parentID, groupType);
            }, TIMEOUT_TAB_CHANGE);

             $1("ws").style.display = "block";
        };
        callback.onTabClose = onTabClose;
        
        var inf = {};
        inf.defaultPage = "page1";
        inf.callback = callback;
        inf.label = (isAddGroup ? OPERATION_ADD : OPERATION_EDIT).replace(/\$label/i, treeName);
        inf.SID = CACHE_TREE_NODE_DETAIL + treeID;
                    
        inf.phases = phases;
        var tab = ws.open(inf);         
    }
 
    function addNewGroup() {
        editGroupInfo(DEFAULT_NEW_ID);
    }
      
    /*
     *  树节点数据详细信息加载数据
     *  参数： string:treeID               树节点id
                string:parentID             父节点id
     */
    function loadGroupDetailData(treeID, parentID, groupType) {
        var request = new $.HttpRequest();
        request.url = URL_GROUP_DETAIL + parentID + "/" + treeID + "/" + groupType;
        request.onresult = function(){
            var groupInfoNode = this.getNodeValue(XML_GROUP_INFO);
            var group2UserTreeNode = $.cache.XmlDatas[CACHE_MAIN_TREE].cloneNode(true);
            var group2UserGridNode = this.getNodeValue(XML_GROUP_TO_USER_EXIST_TREE);
            var group2RoleTreeNode = this.getNodeValue(XML_GROUP_TO_ROLE_TREE);
            var group2RoleGridNode = this.getNodeValue(XML_GROUP_TO_ROLE_EXIST_TREE);
 
            $.cache.XmlDatas[treeID + "." + XML_GROUP_INFO] = groupInfoNode;
            disableTreeNodes(group2RoleTreeNode, "treeNode[isGroup='1']");
                
            var page1Form = $.F("page1Form", groupInfoNode);
            attachReminder(page1Form.box.id, page1Form);
 
            var page3Tree  = $.T("page3Tree",  group2RoleTreeNode);
            var page3Tree2 = $.T("page3Tree2", group2RoleGridNode);
 
            if( !isMainGroup() ) { // 辅助用户组
                var page4Tree3 = $.T("page4Tree3", group2UserGridNode);
                var page4Tree  = $.T("page4Tree",  group2UserTreeNode);
                
                page4Tree.onTreeNodeDoubleClick = function(ev) {
                    var treeNode = page4Tree.getActiveTreeNode();
                    $.ajax({
                        url : URL_GROUP_USERS + treeNode.id,
                        onresult : function() { 
                            var sourceListNode = this.getNodeValue(XML_GROUP_TO_USER_LIST_TREE);
                            $.T("page4Tree2", sourceListNode);
                        }
                    }); 
                }               
            }
            
            // 设置翻页按钮显示状态
            $1("page4BtPrev").style.display = "";
            $1("page3BtPrev").style.display = "";
            $1("page1BtNext").style.display = "";
            $1("page4BtNext").style.display = "";

            // 设置保存按钮操作
            $1("page1BtSave").onclick = $1("page4BtSave").onclick = $1("page3BtSave").onclick = function(){
                saveGroup(treeID, parentID, groupType);
            }

            // 设置添加按钮操作
            $1("page3BtAdd").onclick = function() {
                addTreeNode(page3Tree, page3Tree2);
            }

            // 设置添加按钮操作
            $1("page4BtAdd").onclick = function(){
                addTreeNode($.T("page4Tree2"), page4Tree3);
            }

            // 设置删除按钮操作
            $1("page3BtDel").onclick = function(){
                 removeTreeNode(page3Tree2);
            }
            $1("page4BtDel").onclick = function(){
                 removeTreeNode(page4Tree3);
            }
        }
        request.send();
    }
 
    /* 保存用户组 */
    function saveGroup(treeID, parentID, groupType){
        var page1Form = $.F("page1Form");
        if( !page1Form.checkForm() ) {
            ws.switchToPhase("page1");
            return;
        }

        var request = new $.HttpRequest();
        request.url = URL_SAVE_GROUP;
 
        //用户组基本信息
        var groupInfoNode = $.cache.XmlDatas[treeID + "." + XML_GROUP_INFO];
        var groupInfoDataNode = groupInfoNode.querySelector("data");
        request.setFormContent(groupInfoDataNode);

        // 用户组对用户
        if( !isMainGroup() ) {
            var group2UserIDs = $.T("page4Tree3").getAllNodeIds();
            request.addParam(XML_GROUP_TO_USER_EXIST_TREE, group2UserIDs.join(","));
        }

        // 用户组对角色
        var group2RoleIDs = $.T("page3Tree2").getAllNodeIds();
        request.addParam(XML_GROUP_TO_ROLE_EXIST_TREE, group2RoleIDs.join(","));
        
        // 同步按钮状态
        syncButton([$1("page1BtSave"), $1("page4BtSave"), $1("page3BtSave")], request);

        request.onresult = function() {
            afterSaveTreeNode.call(this, treeID, parentID);
        }
        request.onsuccess = function() {
            afterSaveTreeNode(treeID, page1Form);
        }
        request.send();
    }
    
    /* 同步用户组 */
    function syncGroup() {
        var treeNode = $.T("tree").getActiveTreeNode();
        var treeNodeID    = treeNode.id;
        var applicationId = treeNode.getAttribute("fromApp");
        var fromGroupId   = treeNode.getAttribute("fromGroupId");

        if(applicationId == null || fromGroupId == null) {
            alert("该组没有配置对应的外部系统及外部系统的组织。");
            return;
        }

        var onresult = function() {
            var data = this.getNodeValue("ProgressInfo");
            var progress = new $.Progress(URL_SYNC_PROGRESS, data, URL_CANCEL_SYNC);

            // 完成同步后，重新加载树，打开同步节点，并显示其下用户列表
            progress.oncomplete = function() {
                loadInitData(treeNodeID); 
                treeNode.openNode();                
            }
            progress.start();
        }

        $.ajax({url : URL_SYNC_GROUP + applicationId + "/" + treeNodeID, onresult : onresult});
    }
    
    function getUserOperation(code) {
        var groupId   = $.G("grid").getColumnValue("groupId");  
        var groupNode = $.T("tree").getTreeNodeById(groupId);
        var _operation = groupNode.getAttribute("_operation");
        return checkOperation(code, _operation);
    }
 
    /* 显示用户列表 */
    function showUserList(groupId) {
        groupId = groupId || getTreeNodeId();
        $.showGrid(URL_USER_GRID + groupId, XML_USER_LIST, editUserInfo);
    }
 
    function addNewUser() {
        var groupId = getActiveTreeNode().id;
        loadUserInfo(OPERATION_ADD, DEFAULT_NEW_ID, "用户", groupId);
    }
 
    function editUserInfo() {
        var rowID   = $.G("grid").getColumnValue("id");   
        var rowName = $.G("grid").getColumnValue("userName");   
        loadUserInfo(OPERATION_EDIT, rowID, rowName);
    }
    
    function loadUserInfo(operationName, rowID, rowName, groupId) {
        var phases = [];
        phases[0] = {page:"page1", label:"基本信息"};
        phases[1] = {page:"page2", label:"所属组织"};
        phases[2] = {page:"page3", label:"拥有角色"};

        var callback = {};
        callback.onTabChange = function() {
            setTimeout(function() {
                loadUserDetailData(rowID, groupId);
            }, TIMEOUT_TAB_CHANGE);

            $1("ws").style.display = "block";
        };
        callback.onTabClose = onTabClose;

        var inf = {};
        inf.label = operationName.replace(/\$label/i, rowName || "用户");
        inf.SID = CACHE_GRID_ROW_DETAIL + rowID;
        inf.defaultPage = "page1";
        inf.phases = phases;
        inf.callback = callback;
        ws.open(inf);
    }

    var onTabClose = function() {
        if( ws.noTabOpend() ) {
             $1("ws").style.display = "none";
        }      
    }
 
    function loadUserDetailData(userID, groupId) {
        var request = new $.HttpRequest();
        request.url = URL_USER_DETAIL + (groupId || 0) + "/" + userID;
        request.onresult = function(){
            var userInfoNode = this.getNodeValue(XML_USER_INFO);
            var user2GroupExistTreeNode = this.getNodeValue(XML_USER_TO_GROUP_EXIST_TREE);
            var user2GroupTreeNode = $.cache.XmlDatas[CACHE_MAIN_TREE].cloneNode(true);
            var user2RoleTreeNode = this.getNodeValue(XML_USER_TO_ROLE_TREE);
            var user2RoleGridNode = this.getNodeValue(XML_USER_TO_ROLE_EXIST_TREE);
            
            // 过滤掉 系统级用户组 和 角色组
            disableTreeNodes(user2GroupTreeNode, "treeNode[id^='-']");
            disableTreeNodes(user2RoleTreeNode,  "treeNode[isGroup='1']");
 
            $.cache.XmlDatas[userID + "." + XML_USER_INFO] = userInfoNode;
            
            var page1Form = $.F("page1Form", userInfoNode);
            attachReminder(page1Form.box.id, page1Form);
            
            var page3Tree  = $.T("page3Tree",  user2RoleTreeNode);
            var page3Tree2 = $.T("page3Tree2", user2RoleGridNode);
            var page2Tree  = $.T("page2Tree",  user2GroupTreeNode);
            var page2Tree2 = $.T("page2Tree2", user2GroupExistTreeNode);
            
            page2Tree2.groupType = "1"; // 标记当前page2Tree2是主(辅助)用户组

            // 设置翻页按钮显示状态
            $1("page2BtPrev").style.display = "";
            $1("page3BtPrev").style.display = "";
            $1("page1BtNext").style.display = "";
            $1("page2BtNext").style.display = "";

            //设置保存按钮操作
            $1("page1BtSave").onclick = $1("page2BtSave").onclick = $1("page3BtSave").onclick = function(){
                saveUser(userID, groupId);
            }

            // 设置添加按钮操作
            $1("page2BtAdd").onclick = function(){
                addTreeNode(page2Tree, page2Tree2, function(treeNode){
                    var result = {
                        "error":false,
                        "message":"",
                        "stop":true
                    };
                    var groupType = treeNode.getAttribute("groupType");
                    if( groupType == "1" && hasSameAttributeTreeNode(page2Tree2, "groupType", groupType)) {
                        result.error = true;
                        result.message = "一个用户只能对应一个主用户组。";
                        result.stop = true;
                    }

                    return result;
                });
            }
            $1("page3BtAdd").onclick = function() {
                addTreeNode(page3Tree, page3Tree2, function(treeNode) {
                    var result = {
                        "error": false,
                        "message": "",
                        "stop": true
                    };
                    if( treeNode.getAttribute("isGroup") == "1"){
                        result.error = true;
                        result.message = null;
                        result.stop = false;
                    }
                    return result;
                });
            }

            // 设置删除按钮操作
            $1("page2BtDel").onclick = function(){
                removeTreeNode(page2Tree2);
            }
            $1("page3BtDel").onclick = function(){
                removeTreeNode(page3Tree2);
            }
        }
        request.send();
    }

    function saveUser(userID, groupId){
        var page1Form = $.F("page1Form");
        if( !page1Form.checkForm() ) {
            ws.switchToPhase("page1");
            return;
        }

        // 校验用户对组page2Tree2数据有效性
        var page2Tree2 = $.T("page2Tree2");
        if( !hasSameAttributeTreeNode(page2Tree2, 'groupType', '1') ) {
            ws.switchToPhase("page2");
           
            $(page2Tree2.el).notice("至少要属于一个主用户组。");
            return;
        }

        var request = new $.HttpRequest();
        request.url = URL_SAVE_USER;
 
        // 用户基本信息
        var userInfoNode = $.cache.XmlDatas[userID + "." + XML_USER_INFO];
        var userInfoDataNode = userInfoNode.querySelector("data");
        request.setFormContent(userInfoDataNode);
 
        //用户对用户组
        var user2GroupIDs = $.T("page2Tree2").getAllNodeIds();
        request.addParam(XML_USER_TO_GROUP_EXIST_TREE, user2GroupIDs.join(","));

        // 主用户组id
        var mainGroupId;
        page2Tree2.getAllNodes().each(function(i, node){
            if(node.getAttribute('groupType') == '1') {
                mainGroupId = node.id;
            }
        });
        request.addParam("mainGroupId", mainGroupId);

        //用户对角色
        var user2RoleIDs = $.T("page3Tree2").getAllNodeIds();
        request.addParam(XML_USER_TO_ROLE_EXIST_TREE, user2RoleIDs.join(","));
        
        //同步按钮状态
        syncButton([$1("page1BtSave"), $1("page2BtSave"), $1("page3BtSave")], request);

        request.onsuccess = function(){
            detachReminder(page1Form.box.id);

            // 如果当前grid显示为此用户所在组，则刷新grid
            var gridGroupId = $.G("grid").getColumnValue("groupId");
            showUserList(groupId || gridGroupId || -7);
 
            ws.closeActiveTab();
        }
        request.send();
    }
 
    /* 获取用户状态 */
    function getUserState(){
        return $.G("grid").getColumnValue("disabled"); 
    }
 
    function stopOrStartUser(state) {
        var userID  = $.G("grid").getColumnValue("id");
        var groupId = $.G("grid").getColumnValue("groupId");  
        if(userID == null) return;

        $.ajax({
            url : URL_STOP_USER + groupId + "/" + userID + "/" + state,
            onsuccess : function() {  // 移动树节点                  
                // 成功后设置状态
                $.G("grid").modifySelectedRow("disabled", state);
                $.G("grid").modifySelectedRow("icon", "images/user_" + state + ".gif");
                
                if (state == "0") { // 启用组
                    var treeNode = $.T("tree").getTreeNodeById(groupId);
                    if(treeNode) {
                        refreshTreeNodeState(treeNode, "0");
                    }
                }
            }
        });
    }  
 
    function searchUser(){
        var treeNode = $.T("tree").getActiveTreeNode();
        var treeID   = treeNode.id;
        var treeName = treeNode.name;
        
        $.prompt("请输入查询条件", "查询【" + treeName + "】下用户", function(value) {
            if ( $.isNullOrEmpty(value) ) return alert("条件不能为空。");
            
            var params = {"groupId": treeID, "searchStr": value};
            $.showGrid(URL_SEARCH_USER, XML_USER_LIST, editUserInfo, "grid", 1, params);
        });
    }
 
    /* 综合查询(用户角色查询) */
    function generalSearchRole(){
        var treeNode = getActiveTreeNode();
        var title = "查看【" + treeNode.name +"】组下用户的角色信息";
        var url = URL_SEARCH_ROLE + treeNode.id;
        popupGrid(url, XML_SEARCH_ROLE, title);
    }
    
    /* 综合查询(用户转授查询) */
    function generalSearchSubauth() {
        var treeNode = getActiveTreeNode();
        var title = "查看组【" + treeNode.name +"】下用户的转授角色信息";
        var url = URL_SEARCH_SUBAUTH + treeNode.id;
        popupGrid(url, XML_SEARCH_SUBAUTH, title);
    }
 
    window.onload = init;