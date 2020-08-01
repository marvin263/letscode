package com.example.myboot224;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class PostCostMuchController {

    @RequestMapping(value = "/costMuch", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String greeting(HttpServletResponse rsp, @RequestBody String str) {
        System.out.println(str);
//        try {
//            ServletOutputStream os = rsp.getOutputStream();
//            int i = 0;
//            while (true) {
//                os.write((++i + ", ").getBytes());
//                Thread.sleep(5);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return str + "-->" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
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
