package com.wfzcx.ieos.data.bean;

import com.wfzcx.ieos.app.Const;

import java.io.Serializable;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
public class ServerAddr implements Serializable {

    private String ipAddr;
    private int ipPort;

    public ServerAddr() {

    }

    public ServerAddr(String ipAddr, int ipPort) {
        this.ipAddr = ipAddr;
        this.ipPort = ipPort;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public int getIpPort() {
        return ipPort;
    }

    public void setIpPort(int ipPort) {
        this.ipPort = ipPort;
    }

    @Override
    public String toString() {
        return Const.HTTP_SUFFIX + ipAddr + ":" + ipPort + "/" + Const.DEFAULT_SERVER_WEB + "/";
    }
}
