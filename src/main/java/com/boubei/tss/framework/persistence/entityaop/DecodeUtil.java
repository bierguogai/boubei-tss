package com.boubei.tss.framework.persistence.entityaop;

import java.util.List;

import com.boubei.tss.framework.Config;
import com.boubei.tss.util.EasyUtils;

/**
 * 层码工具类
 */
public class DecodeUtil {

    public static final String DECODE_SECTSIZE = "decode.sectSize";// 层码每段的长度
    
    /**
     * <p>
     * 获取层码每段的长度，默认长度为5
     * </p>
     * @return
     */
    public static int getSectSize() {
        String size = Config.getAttribute(DECODE_SECTSIZE);
        size = (String) EasyUtils.checkNull(size, "5");
        return EasyUtils.obj2Int(size);
    }
    
    /**
     * <p>
     * 生成层码
     * </p>
     * @param seqNo
     * 			序号
     * @param sectSize
     * 			层码每段的长度
     * @return
     */
    public static String getDecode(Integer seqNo, int sectSize) {
        return getDecode("", seqNo, sectSize);
    }
    
    
    /**
     * <p>
     * 生成层码
     * </p>
     * @param parentDecode
     * 			父节点层码
     * @param seqNo
     * 			序号
     * @param sectSize
     * 			层码每段的长度
     * @return
     */
    public static String getDecode(String parentDecode, Integer seqNo, int sectSize) {
        parentDecode = parentDecode == null ? "" : parentDecode;
        seqNo = seqNo == null ? 0 : seqNo;
        return parentDecode + fillDecode(seqNo.toString(), sectSize);
    }
    
    /**
     * <p>
     * 补全层码
     * </p>
     * @param seqNo
     * 			序号
     * @param sectSize
     * 			层码每段的长度
     * @return
     */
    private static String fillDecode(String seqNo, int sectSize){
    	if (seqNo.length() < sectSize) {
			char[] chars = new char[sectSize - seqNo.length()];
			for (int i = 0; i < chars.length; i++) {
				chars[i] = '0';
			}
			
			return new String(chars) + seqNo;
		} 
		return seqNo;
    }
    
    /**
     * 排序、移动等操作后修复子节点的decode值
     * 
     * @param subNodes
     *              需要修复的子节点列表
     * @param oldParentCode
     *              父节点原先的decode
     * @param newParentCode
     *              父节点当前的decode
     */
    public static void repairSubNodeDecode(List<?> subNodes, String oldParentCode, String newParentCode) {
        int length = oldParentCode.length();
        for( Object temp : subNodes ){
            IDecodable entity = (IDecodable) temp;
            entity.setDecode(newParentCode + entity.getDecode().substring(length));
        } 
    }

}

	