package com.util.msa_frontend.main;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.util.msa_frontend.common.CommandMap;

@Controller
@RequestMapping("/api")
public class MainController {

    @Resource(name = "mainSerivce")
    MainService mainService;

    @RequestMapping("/getWeather.do")
    public List<Map<String, Object>> getWeather(CommandMap map) throws Exception {
        List<Map<String, Object>> result = mainService.getWeatherList(map);

        return result;
    }
}
