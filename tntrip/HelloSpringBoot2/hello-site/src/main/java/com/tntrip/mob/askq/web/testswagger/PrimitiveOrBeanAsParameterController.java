package com.tntrip.mob.askq.web.testswagger;

import com.tntrip.mob.askq.common.util.DateUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

/**
 * Created by Daniel on 16/10/2016.
 */
@RestController
public class PrimitiveOrBeanAsParameterController {

    @RequestMapping(value = "/api/primitiveParameter", method = RequestMethod.GET)
    public UserModel primitiveParameter(int id) {
        return new UserModel(id, "张三" + DateUtil.dateToString(new Date()), new Random().nextInt(50));
    }

    @RequestMapping(value = "/api/beanParameter", method = RequestMethod.GET)
    public UserModel beanParameter(UserModel um) {
        return new UserModel(um.getId() * 10, um.getName() + DateUtil.dateToString(new Date()), um.getAge() + 5);
    }
}
