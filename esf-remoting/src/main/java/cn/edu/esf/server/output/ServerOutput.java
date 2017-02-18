package cn.edu.esf.server.output;

import cn.edu.esf.Connection;
import cn.edu.esf.domain.ESFResponse;

public abstract class ServerOutput {
    protected final Connection connection;
    protected final long startTime;

    public ServerOutput(final Connection connection, final long startTime) {
        this.connection = connection;
        this.startTime = startTime;
    }

    public Connection getConnection() {
        return connection;
    }

    public long getStartTime() {
        return startTime;
    }

    public abstract void writeHSFResponse(ESFResponse object);
}