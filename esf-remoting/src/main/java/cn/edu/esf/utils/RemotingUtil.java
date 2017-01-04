package cn.edu.esf.utils;

import cn.edu.esf.BaseRequest;
import cn.edu.esf.RemotingConstants;
import cn.edu.esf.RpcRequest;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.serialize.Encoder;
import cn.edu.esf.serialize.SerializeType;

import java.util.Map;

/**
 * @Author heyong
 * @Date 2017/1/2
 */
public class RemotingUtil {

    public static RpcRequest convert2RpcRequest(ESFRequest request, byte codecType, int timeout) throws Exception {
        String targetInstanceName = request.getServiceName();
        String methodName = request.getMethodName();
        String[] argTypes = request.getMethodArgTypes();
        String[] args = request.getMethosArgs();

        Map<String, Object> requestProps = request.getRequestProps();

        Encoder encoder = SerializeType.getEncoder(codecType);
        if (encoder == null) {
            throw new Exception("no this encoder");
        }
        byte[][] argBytes = null;
        byte[] requestPropsBytes = null;

        for (int i = 0; i < args.length; i++) {
            argBytes[i] = encoder.encode(args[i]);
        }
        if (requestProps != null) {
            requestPropsBytes = encoder.encode(requestProps);
        }
        return new RpcRequest(timeout, targetInstanceName, methodName, argTypes, argBytes, requestPropsBytes, codecType);
    }

    public static ESFRequest convert(RpcRequest request) throws Exception {
        ESFRequest wapper = new ESFRequest();
        wapper.setServiceName(request.getTargetInstance());
        wapper.setMethodName(request.getMethodName());


        return wapper;

    }


}
