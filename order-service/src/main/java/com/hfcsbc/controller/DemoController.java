package com.hfcsbc.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangyunfei on 2017/6/12.
 */
@RestController
public class DemoController {
    @GetMapping("/demo")
    @PreAuthorize("hasAuthority('query-demo')")
    public String getDemo(){
        return "good";
    }
}
