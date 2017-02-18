package cn.edu.esf.server;

import cn.edu.esf.Connection;
import cn.edu.esf.HeartBeatRequest;
import cn.edu.esf.HeartBeatResponse;

import java.util.concurrent.Executor;

public class HeartBeatPotocolHandler implements ProtocolHandler<HeartBeatRequest> {


    @Override
    public void handleRequest(HeartBeatRequest request, Connection connection, long startTime) {
        connection.writeResponseToChannel(new HeartBeatResponse(request.getRequestID()));
    }

    @Override
    public Executor getExecutor(final HeartBeatRequest request) {
        return null;
    }
}