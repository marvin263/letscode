package com.tntrip.mob.askq.web.testswagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by libing2 on 2016/9/27
 */
@RestController
public class RequestResponseAsParameterController {
    private static final Logger LOG = LoggerFactory.getLogger(RequestResponseAsParameterController.class);

    @RequestMapping(value = "api/requestParameter", method = RequestMethod.POST)
    public String requestParameter(HttpServletRequest req) throws Exception {
        Thread.sleep(5000);
        String a = req.getReader().readLine();
        return req.getParameter("k")+"--alreadyReindexCount="+Thread.currentThread().getName()+a;
    }

    @RequestMapping(value = "api/responseParameter", method = RequestMethod.GET)
    public String responseParameter(HttpServletResponse rsp) {
        return "You can reindex questions now...";
    }

    @RequestMapping(value = "api/requestResponseParameter", method = RequestMethod.GET)
    public String requestResponseParameter(HttpServletRequest req, HttpServletResponse rsp) {
        return "Already enabled index question";
    }
}
