package com.util.msa_frontend.bizservice;

public class BizServiceInfo {
    private String serviceName;
    private String bizPath;

    public BizServiceInfo(String bizPath, String serviceName) {
        this.bizPath = bizPath;
        this.serviceName = serviceName;
    }

    public String getBizPath() {
        return bizPath;
    }

    public String getServiceName() {
        return serviceName;
    }
}
