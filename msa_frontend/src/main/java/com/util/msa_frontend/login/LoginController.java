package com.util.msa_frontend.login;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.util.msa_frontend.common.CommandMap;

@Controller
@RequestMapping("/api")
public class LoginController {

    @Autowired
    LoginService loginService;

    @RequestMapping("/loginProcess.do")
    public Map<String, Object> loginProcess(CommandMap map) throws Exception {
        Map<String, Object> result = new HashMap<>();

        return result;
    }
}
