package com.util.msa_frontend.main;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.util.msa_frontend.bizservice.BizApiClient;
import com.util.msa_frontend.bizservice.BizConstants;
import com.util.msa_frontend.bizservice.BizServiceInfo;
import com.util.msa_frontend.common.CommandMap;

@Service("mainService")
public class MainService {

    private static final Logger log = LoggerFactory.getLogger(MainService.class);

    @Autowired
    private BizApiClient bizClient;
    private BizServiceInfo serviceInfo;

    public MainService() {
        serviceInfo = new BizServiceInfo(BizConstants.COMMON, "MainService");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getWeatherList(CommandMap map) throws Exception {
        List<Map<String, Object>> response = bizClient.requestApi(serviceInfo, "getWeatherList", map, List.class);
        return response;
    }
}
