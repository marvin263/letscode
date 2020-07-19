package com.example.myboot224;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class PostCostMuchController {

    @RequestMapping(value = "/costMuch", consumes = "application/json")
    public String greeting(@RequestBody CostMuchReq req) {
        System.out.println(req.toString());
        return req.toString();
    }

    public static class CostMuchReq {
        private String name;
        private Integer id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "CostMuchReq{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    ", date=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                    '}';
        }
    }
}
