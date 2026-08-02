package org.example.sotokenspringboot.utils;

import org.example.sotokenspringboot.pojo.wxlogin;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "testApi", url = "https://api.weixin.qq.com")
public interface wxopenfeign {

    @GetMapping("/sns/jscode2session")
    String getTest(@RequestParam("appid") String appid,
                    @RequestParam("secret") String secret,
                    @RequestParam("js_code") String js_code,
                    @RequestParam("grant_type") String grant_type);
}
