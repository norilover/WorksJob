package edu.nori.utils;

import javax.servlet.http.HttpServletRequest;

/***
 * 验证签名是否合法
 */
public class InspectorUtil {
    //签名属性
    public static final String X_SID = "X-SID";
    public static final String X_SIGNATURE= "X-Signature";
    /***
     * 计算Header头部信息
     * @param httpRequest
     * @return
     */
    public static boolean isLegalRequest(HttpServletRequest httpRequest){
        //获取Header中的数据
        String originalStr = httpRequest.getHeader(X_SID);
        String encryStr =  httpRequest.getHeader(X_SIGNATURE);

        byte[] byteArr = AES_Util.stringKey2ByteKey(encryStr);

        //解密
        byteArr = RSA_Util.RSADecode(byteArr);

        //解密后字符串
        String decodeStr = new String(byteArr);

        //返回结果
        return originalStr.equals(decodeStr);
    }
}
