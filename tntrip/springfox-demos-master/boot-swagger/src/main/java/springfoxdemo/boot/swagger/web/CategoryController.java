package springfoxdemo.boot.swagger.web;

import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;
import springfox.petstore.model.Pet;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Controller
public class CategoryController {
    @RequestMapping(value = "/category/Resource", method = RequestMethod.GET)
    public ResponseEntity<String> search(@RequestParam(value = "someEnum") Category someEnum) {
        return ResponseEntity.ok(someEnum.name());
    }

    @RequestMapping(value = "/category/map", method = RequestMethod.GET)
    public Map<String, Map<String, Pet>> map() {
        return newHashMap();
    }

    @RequestMapping(value = "/category/a/{id}", method = RequestMethod.POST)
    public ResponseEntity<Void> someOperation(@PathVariable long id, @RequestBody int userId) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/category/a/{id}/{userId}", method = RequestMethod.POST)
    public ResponseEntity<Void> ignoredParam(@PathVariable long id, @PathVariable @ApiIgnore int userId) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/category/a/{id}/map", method = RequestMethod.POST)
    public ResponseEntity<Void> map(@PathVariable String id,
                                    @ApiParam(value = "Parameter is Map", required = true)
                                    @RequestBody Map<String, String> test) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/category/idNameDate", method = RequestMethod.POST)
    public ResponseEntity<IdNameDate> map(
            @ApiParam(value = "Parameter is IdNameDate", required = true)
            @RequestBody IdNameDate ind, HttpServletResponse rsp) {
        int i=30;
        while(i-->0){
            try {
                rsp.getOutputStream().write(i);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ind.setCurDate(new Date(System.currentTimeMillis() + 10000L));
        return ResponseEntity.ok(ind);
    }
}
