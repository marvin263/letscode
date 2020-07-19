package com.example.myboot224;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;

@RestController
public class DeferredController {
    private final static Logger LOG = LoggerFactory.getLogger(GreetingController.class);

    @GetMapping("/deferred")
    public DeferredResult<String> handleReqDefResult(Model model) {
        LOG.info("Received async-deferredresult request");
        DeferredResult<String> output = new DeferredResult<>();

        ForkJoinPool.commonPool().submit(() -> {
            LOG.info("Processing in separate thread");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
            }
            output.setResult(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        });

        LOG.info("servlet thread freed");
        return output;
    }
}
