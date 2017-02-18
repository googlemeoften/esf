package cn.edu.esf.utils;

import cn.edu.esf.BaseRequest;
import cn.edu.esf.RemotingConstants;
import cn.edu.esf.RpcRequest;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.serialize.Decoder;
import cn.edu.esf.serialize.Encoder;
import cn.edu.esf.serialize.SerializeType;

import java.util.Map;

/**
 * @Author heyong
 * @Date 2017/1/2
 */
public class RemotingUtil {

    public static RpcRequest convert2RpcRequest(ESFRequest request, byte codecType, int timeout) throws ESFException {
        String targetInstanceName = request.getServiceName();
        String methodName = request.getMethodName();
        String[] argTypes = request.getMethodArgTypes();
        Object[] args = request.getMethosArgs();

        Map<String, Object> requestProps = request.getRequestProps();

        Encoder encoder = SerializeType.getEncoder(codecType);
        if (encoder == null) {
            throw new ESFException("no this encoder");
        }
        byte[][] argBytes = new byte[argTypes.length][];
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
        int codecType = request.getCodecType();

        Decoder decoder = SerializeType.getDecoders(codecType);
        String[] argTypes = request.getArgTypes();
        byte[][] argBytes = request.getRequestObjects();
        Object[] args = new Object[argBytes.length];
        for(int i = 0;i<argBytes.length;i++){
            args[i] = decoder.decode(argBytes[i]);
        }

        wapper.setMethodArgTypes(argTypes);
        wapper.setMethosArgs(args);
        return wapper;
    }


}
