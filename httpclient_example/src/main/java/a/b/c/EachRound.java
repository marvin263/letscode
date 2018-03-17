package a.b.c;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EachRound {
    private static AtomicInteger nextRound = new AtomicInteger(1);

    private int round;
    private List<EachPage> listPages;
    // contentInfo-->page
    private Map<ContentInfo, EachPage> uniqueContentInfo;

    public static EachRound create() {
        EachRound er = new EachRound();
        er.round = nextRound.getAndIncrement();
        er.listPages = new ArrayList<>();
        er.uniqueContentInfo = new LinkedHashMap<>();
        return er;
    }

    // true-->find duplicated
    // false-->no duplicated at all
    public boolean addPage(EachPage curPage) {
        for (ContentInfo curContentInfo : curPage.getListContentInfo()) {
            if (!uniqueContentInfo.containsKey(curContentInfo)) {
                uniqueContentInfo.put(curContentInfo, curPage);
                continue;
            }
            EachPage prevEP = uniqueContentInfo.get(curContentInfo);
            ContentInfo prevContentInfo = prevEP.getListContentInfo()
                    .stream()
                    .filter(e -> e.equals(curContentInfo))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Should find curContentInfo, but none is found"));

            String info = TnStringUtils.format("Find duplicated item, round={0}" +
                            "\n****prev****\nprevPageNumber={1}\nprevContentInfo={2}\nprevInsidePageIndex={3}\nprevQueryJson={4}" +
                            "\n****cur****\ncurPageNumber={5}\ncurContentInfo={6}\ncurInsidePageIndex={7}\ncurQueryJson={8}",
                    round,
                    prevEP.getPageNumber(), prevContentInfo, prevEP.getListContentInfo().indexOf(curContentInfo), prevEP.getQueryJson(),
                    curPage.getPageNumber(), curContentInfo, curPage.getListContentInfo().indexOf(curContentInfo), curPage.getQueryJson());
            System.out.println(info);
            return true;
        }
        return false;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<EachPage> getListPages() {
        return listPages;
    }

    public void setListPages(List<EachPage> listPages) {
        this.listPages = listPages;
    }

    public Map<ContentInfo, EachPage> getUniqueContentInfo() {
        return uniqueContentInfo;
    }

    public void setUniqueContentInfo(Map<ContentInfo, EachPage> uniqueContentInfo) {
        this.uniqueContentInfo = uniqueContentInfo;
    }
}
