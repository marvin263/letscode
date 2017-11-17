package com.tntrip.mob.askq.web.di;

import com.tntrip.mob.askq.biz.di.service.Party;
import com.tntrip.mob.askq.biz.pojo.Pojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <pre>
 * @Autowired and @Inject
 * 1.Matches by Type
 * 2.Restricts by Qualifiers
 * 3.Matches by Name
 * </pre>
 * <p>
 * <pre>
 * @Resource
 * 1.Matches by Name
 * 2.Matches by Type
 * 3.Restricts by Qualifiers (ignored if match is found by name)
 * </pre>
 */
@RestController
public class AutowireBeanController {
    @Resource(name = "ppp1")
    private Party psn1;

    @Resource(name = "ppp2")
    private Party psn2;

    @Resource(name = "ppp3")
    private Party psn3;

    @Autowired
    @Qualifier(value = "pojo1")
    private Pojo pj1;

    @Autowired
    @Qualifier(value = "pojo2")
    private Pojo pj2;


    @RequestMapping(value = "/api/autowireBean", method = RequestMethod.GET)
    public String noParameter() {
        String str1 = (psn1 == psn2) + "----" + (psn1 == psn3);
        String str2 = (pj1 == pj2) + "";
        return str1 + ", " + str2;
    }
}
