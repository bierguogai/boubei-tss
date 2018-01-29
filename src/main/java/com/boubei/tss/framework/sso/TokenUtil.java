package com.boubei.tss.framework.sso;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.InfoEncoder;

/**
 * <p> 令牌处理 </p>
 */
public class TokenUtil {
	
	private static InfoEncoder infoEncoder = new InfoEncoder();

	/**
	 * <p>
	 * 根据sessionId和UserId生成令牌
	 * </p>
	 * @param sessionId
	 * @param userId
	 * @return
	 */
	public static String createToken(String sessionId, Long userId) {
		if (sessionId != null && userId != null) {
			String originalToken = sessionId + "," + System.currentTimeMillis() + "," + userId;
			return infoEncoder.createEncryptor(originalToken);
		}
		return null;
	}

	/**
	 * <p>
	 * 根据用户令牌获取标准用户ID
	 * </p>
	 * @param token
	 * @return
	 */
	public static Long getUserIdFromToken(String token) {
		String originalToken;
		try {
			originalToken = infoEncoder.createDecryptor(token);
		} catch(Exception e) {
			throw new BusinessException(EX.U_04 + e.getMessage());
		}
		
		int beginIndex = originalToken.lastIndexOf(",");
		String userId = originalToken.substring(beginIndex + 1);
		return new Long(userId);
	}
}
