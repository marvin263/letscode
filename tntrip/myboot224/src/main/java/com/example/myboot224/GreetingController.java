package com.example.myboot224;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
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
    String greeting(HttpServletResponse rsp, @RequestParam(name = "cost") long cost) {
//        try {
//            ServletOutputStream os = rsp.getOutputStream();
//            int i = 'a';
//            while (i < 'z') {
//                os.write(i);
//                os.flush();
//                //Thread.sleep(cost * 1000L);
//                i++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        LOG.info(format);
        return format;
    }

}
