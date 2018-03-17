package a.b.c;

import java.util.List;

public class EachPage {
    private int pageNumber;
    private List<ContentInfo> listContentInfo;
    private String queryJson;

    public static EachPage create(int pageNumber, List<ContentInfo> listContentInfo, String queryJson) {
        EachPage ep = new EachPage();
        ep.pageNumber = pageNumber;
        ep.listContentInfo = listContentInfo;
        ep.queryJson = queryJson;
        return ep;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<ContentInfo> getListContentInfo() {
        return listContentInfo;
    }

    public void setListContentInfo(List<ContentInfo> listContentInfo) {
        this.listContentInfo = listContentInfo;
    }

    public String getQueryJson() {
        return queryJson;
    }

    public void setQueryJson(String queryJson) {
        this.queryJson = queryJson;
    }
}
