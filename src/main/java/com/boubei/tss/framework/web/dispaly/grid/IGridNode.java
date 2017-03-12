package com.boubei.tss.framework.web.dispaly.grid;

/** 
 * <p>  Grid数据对象接口。 </p> 
 *
 * 所有需要用Grid展示的实体类都需要实现本接口。
 * 
 */
public interface IGridNode {
    
	/**
	 * 实体类实现本方法时，将需要在Grid里展示的属性放入到GridAttributesMap中
	 * @return
	 */
	GridAttributesMap getAttributes(GridAttributesMap map);
}
