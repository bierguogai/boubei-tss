package com.boubei.tss.modules.param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.web.display.tree.LevelTreeParser;
import com.boubei.tss.framework.web.display.tree.TreeEncoder;
import com.boubei.tss.framework.web.display.xform.XFormEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.util.EasyUtils;
 
@Controller
@RequestMapping("/param")
public class ParamAction extends BaseActionSupport {

	@Autowired private ParamService paramService;
    
	/** 树型展示所有已配置参数 */
	@RequestMapping("/list")
	public void get2Tree(HttpServletResponse response) {
		List<?> allParams = paramService.getAllParams(false);
		TreeEncoder treeEncoder = new TreeEncoder(allParams, new LevelTreeParser());
		print("ParamTree", treeEncoder);
	}
	
	   /** 取可以新增的参数树 */
    @RequestMapping("/list/{id}" )
    public void getCanAddParamsTree(HttpServletResponse response, @PathVariable("id") Long id) {
    	Param param = paramService.getParam(id);
        List<?> paramGroups = paramService.getCanAddGroups();
        List<Param> groups = new ArrayList<Param>();
        for(Object obj : paramGroups) {
        	Param p = (Param) obj;
        	if( p.getDecode().indexOf( param.getDecode() ) < 0 ) { // 禁止移动到子节点
        		groups.add(p);
        	}
        }
        
        TreeEncoder paramTree = new TreeEncoder(groups, new LevelTreeParser());
        print("ParamTree", paramTree);
    }
	
	/** 取参数信息 */
	@RequestMapping("/detail")
	public void getParamInfo(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") int type) {
        
	    String mode = request.getParameter("mode");
        String uri = null;
        if(ParamConstants.GROUP_PARAM_TYPE.equals(type)){
        	uri = ParamConstants.XFORM_PARAM_GROUP;
        } 
        else if(ParamConstants.NORMAL_PARAM_TYPE.equals(type)){
            
        	if(ParamConstants.SIMPLE_PARAM_MODE.toString().equals(mode)){
            	uri = ParamConstants.XFORM_PARAM_SIMPLE;
        	} else {
        		uri = ParamConstants.XFORM_PARAM_COMPLEX;
        	}
        } else if(ParamConstants.ITEM_PARAM_TYPE.equals(type)){
        	uri = ParamConstants.XFORM_PARAM_ITEM;
        } else {
        	throw new BusinessException("param type not matched, type=" + type);
        }
        
        XFormEncoder xformEncoder;
        String paramIdValue = request.getParameter("paramId");
        if( paramIdValue == null ){
            String parentIdValue = request.getParameter("parentId"); 
            Long parentId;
            try {
            	parentId = EasyUtils.obj2Long(parentIdValue);
            } catch (Exception e) {
            	parentId = ParamConstants.DEFAULT_PARENT_ID; // null or "_root"
            }
            
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("parentId", parentId);
            map.put("type", type);
            map.put("modality", mode);
            xformEncoder = new XFormEncoder(uri, map);
        } 
        else {
        	Param param = paramService.getParam(EasyUtils.obj2Long(paramIdValue));
            xformEncoder = new XFormEncoder(uri, param);
        }
        print("ParamInfo", xformEncoder);
	}
 
	/** 删除 */
    @RequestMapping(value = "/{paramId}", method = RequestMethod.DELETE)
    public void delParam(HttpServletResponse response, @PathVariable("paramId") Long paramId) {
        paramService.delete(paramId);       
        printSuccessMessage();
    }
    
    /**  新建、编辑 */
    @RequestMapping(method = RequestMethod.POST)
    public void saveParam(HttpServletResponse response, Param param) {
        boolean isnew = (null == param.getId());
        paramService.saveParam(param);
        doAfterSave(isnew, param, "ParamTree");
    }
    
    /**  新建、编辑 */
    @RequestMapping(value = "/value", method = RequestMethod.POST)
    public void saveParamValue(HttpServletResponse response, String code, String value) {
    	Param param = paramService.getParam(code);
    	param.setValue(value);
    	paramService.saveParam(param);
    	
    	printSuccessMessage();
    }
    
	/** 停用、启用参数 */
	@RequestMapping(value = "/disable/{paramId}/{disabled}", method = RequestMethod.POST)
	public void startOrStopParam(HttpServletResponse response, 
	        @PathVariable("paramId") Long paramId, @PathVariable("disabled") int disabled) {
	    
		paramService.startOrStop(paramId, disabled);
		printSuccessMessage();
	}
	
	/** 参数排序 */
	@RequestMapping(value = "/sort/{paramId}/{targetId}/{direction}", method = RequestMethod.POST)
	public void sortParam(HttpServletResponse response, 
	        @PathVariable("paramId") Long paramId, 
	        @PathVariable("targetId") Long targetId,  
	        @PathVariable("direction") int direction) {
	    
		paramService.sortParam(paramId, targetId, direction);
		printSuccessMessage();
	}
	
	/**  参数复制 */
	@RequestMapping(value = "/copy/{paramId}/{toParamId}", method = RequestMethod.POST)
	public void copyParam(HttpServletResponse response, 
	        @PathVariable("paramId") Long paramId, 
	        @PathVariable("toParamId") String toParamId) {
	    
        Long targetId = "_root".equals(toParamId) ? ParamConstants.DEFAULT_PARENT_ID : new Long(toParamId);
		
		List<?> result = paramService.copyParam(paramId, targetId);
		TreeEncoder encoder = new TreeEncoder(result, new LevelTreeParser());
		encoder.setNeedRootNode(false);
		print("ParamTree", encoder);
	}
	
	/** 移动参数 */
	@RequestMapping(value = "/move/{paramId}/{toParamId}", method = RequestMethod.POST)
	public void moveParam(HttpServletResponse response, 
	        @PathVariable("paramId") Long paramId, 
	        @PathVariable("toParamId") String toParamId) {
	    
		Long targetId = "_root".equals(toParamId) ? ParamConstants.DEFAULT_PARENT_ID : new Long(toParamId);
		paramService.move(paramId, targetId);
		printSuccessMessage();
	}
	
    
    /***************************************** 以下为应用系统初始化相关 *************************************************/
    
    /**
     * 初始化应用系统，主要是生成appServer配置信息。
     * @return
     */
	@RequestMapping(value = "/apps/{code}/{name}/{value}", method = RequestMethod.POST)
    public void initAppConfig(HttpServletResponse response, 
            @PathVariable("code") String code, 
            @PathVariable("name") String name, 
            @PathVariable("value") String value) {
	    
		String config = "<server code=\"" + code + "\" framework=\"tss\" name=\""
                + name + "\" sessionIdName=\"JSESSIONID\" baseURL=\"" + value  + "\"/>";
		
        Param param = paramService.getParam(code);
        if(param == null) {
            param = ParamManager.addSimpleParam(ParamConstants.DEFAULT_PARENT_ID, code, name, config);
        }
        else {
        	param.setValue(config);
            paramService.saveParam(param);
        }
       
        String msg = "当前应用里设置【" + code + "】应用配置信息成功";
        printSuccessMessage(msg);
    }
	
	/*************************************** 以下为Param的json格式服务，供前台调用 *************************************/
	
	/** 系统配置参数 */
	@RequestMapping(value = "/json/simple/{code}", method = RequestMethod.GET)
	@ResponseBody
	public Object getSimpleParam2Json(@PathVariable("code") String code) {
		return new Object[] { ParamConfig.getAttribute(code) };
	}
	
	/** 获取下拉类型/树型参数列表  */
	@RequestMapping(value = "/json/combo/{code}", method = RequestMethod.GET)
	@ResponseBody
	public Object getComboParam2Json(@PathVariable("code") String code, boolean isTree) {
    	try{
			List<Param> list = isTree ? paramService.getTreeParam(code) : paramService.getComboParam(code);
			if(list != null) {
				List<Object[]> result = new ArrayList<Object[]>();
				for (Param param : list) {
					result.add(new Object[] { param.getId(), param.getText(), param.getParentId(), param.getValue() });
				}
				return result;
			}
    	} catch (Exception e) {
    		log.warn("获取参数信息失败!code=" + code + ", " + e.getMessage());
		}
    	return "";
    }
	
	@RequestMapping(value = "/xml/combo/{code}")
	public void getComboParam2XML(HttpServletResponse response, 
			@PathVariable("code") String code, boolean isTree) {
		
		try{
			List<Param> list = isTree ? paramService.getTreeParam(code) : paramService.getComboParam(code);
			TreeEncoder treeEncoder = new TreeEncoder(list, new LevelTreeParser());
			treeEncoder.setNeedRootNode(false);
			print("ParamTree", treeEncoder);
    	} 
		catch (Exception e) {
    		log.warn("获取参数信息失败!code=" + code + ", " + e.getMessage());
		}
	}
}	