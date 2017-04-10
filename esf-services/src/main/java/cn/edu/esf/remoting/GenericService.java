package cn.edu.esf.remoting;

import cn.edu.esf.exception.ESFException;

public interface GenericService {
    Object $invoke(String methodName, String[] parameterTypes, Object[] args) throws ESFException;
}