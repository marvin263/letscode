package com.example.myboot224;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class GreetingController {
    private final static Logger LOG = LoggerFactory.getLogger(GreetingController.class);
    private final GreetingService service;

    public GreetingController(GreetingService service) {
        this.service = service;
    }

    @RequestMapping("/greeting")
    public @ResponseBody
    String greeting(@RequestParam(name = "cost") long cost) {
        try {
            Thread.sleep(cost);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        LOG.info(format);
        return format;
    }

}
