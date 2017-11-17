package springfoxdemo.boot.swagger.web;

import java.util.Date;

/**
 * Created by libing2 on 2017/2/3.
 */
public class IdNameDate {
    private int id;
    private String name;
    private Date curDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCurDate() {
        return curDate;
    }

    public void setCurDate(Date curDate) {
        this.curDate = curDate;
    }
}
