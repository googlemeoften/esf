package cn.edu.esf.server.output;/**
 * Created by HeYong on 2017/2/13.
 */

import cn.edu.esf.*;
import cn.edu.esf.domain.ESFResponse;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.serialize.Encoder;
import cn.edu.esf.serialize.SerializeType;

import java.net.InetSocketAddress;
import java.text.MessageFormat;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class RPCServerOutput extends ServerOutput{

    private final RpcRequest request;

    public RPCServerOutput(final Connection connection, final RpcRequest request, final long startTime) {
        super(connection, startTime);
        this.request = request;
    }

    @Override
    public void writeHSFResponse(ESFResponse response) {
        if (response.isError()) {
            byte[] reponseObject = response.getErrorMsg().getBytes(RemotingConstants.DEFAULT_CHARSET);
            connection.writeResponseToChannel(new RpcResponse(request.getRequestID(), (byte) request.getCodecType(),
                    ResponseStatus.SERVER_ERROR, reponseObject));
        }else {
            try{
                byte codecType = request.getCodecType();
                byte[] reponseObject = null;

                Encoder encoder = SerializeType.getEncoder(codecType);
                reponseObject = encoder.encode(response.getResponseObject());

                connection.writeResponseToChannel(new RpcResponse(request.getRequestID(),codecType,
                        ResponseStatus.OK,reponseObject));
            } catch (Exception e) {
                byte[] reponseObject = MessageFormat.format("ESF-Provider-{0} Error log:{1}",
                        ((InetSocketAddress)connection.getLocalAddress()).getHostName(),e.getMessage()).getBytes();
                connection.writeResponseToChannel(new RpcResponse(request.getRequestID(), (byte) request.getCodecType(),
                        ResponseStatus.SERVER_ERROR, reponseObject));
            }
        }
    }
}
