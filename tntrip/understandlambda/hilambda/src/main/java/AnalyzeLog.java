import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nuc on 2016/1/14.
 */
public class AnalyzeLog {
    public static final BigDecimal _03 = new BigDecimal("0.30");
    public static final int FAILURE_TIME_SPAN = 10;
    public static EachLine EOF = EachLine.createEOF();
    public static Map<String, String> MONTH2INT = new HashMap<String, String>();

    static {
        MONTH2INT.put("Jan", "01");
        MONTH2INT.put("Feb", "02");
        MONTH2INT.put("Mar", "03");
        MONTH2INT.put("Apr", "04");
        MONTH2INT.put("May", "05");
        MONTH2INT.put("Jun", "06");
        MONTH2INT.put("Jul", "07");
        MONTH2INT.put("Aug", "08");
        MONTH2INT.put("Sep", "09");
        MONTH2INT.put("Oct", "10");
        MONTH2INT.put("Nov", "11");
        MONTH2INT.put("Dec", "12");
    }

    public static final String STR_REG = "([\\d\\.]+)\\s\\[(.+)\\]\\s\"\\w+\\s(.+?)\\s.+?(\\d+)\\s(\\d+)\\s(\\d+)";
    public static final Pattern PTN = Pattern.compile(STR_REG);

    private ConcurrentHashMap<String, APIStatistic> apiMap = new ConcurrentHashMap<String, APIStatistic>();
    private ThreadAndQueue taq;

    public AnalyzeLog() {
        taq = new ThreadAndQueue(apiMap, 3);
    }

    public static class EachLine {
        public String ip;
        public long timeStampInSecond;
        public String url;
        public int status;
        public long repLen;
        public long costInMilli;

        public static EachLine createEOF() {
            EachLine el = new EachLine();
            el.timeStampInSecond = -1L;
            return el;
        }

        private boolean isEOF() {
            return timeStampInSecond == -1L;
        }
    }


    private static EachLine parseALine(String line) {
        EachLine el = new EachLine();
        Matcher m = PTN.matcher(line);
        while (m.find()) {
            el.ip = m.group(1);
            el.timeStampInSecond = str2Longdate(m.group(2));
            el.url = m.group(3);
            el.status = str2int(m.group(4));
            el.repLen = str2long(m.group(5));
            el.costInMilli = str2long(m.group(6));
        }
        return el;
    }

    public static class APIStatistic {
        public String url;
        public int totalCount = 0;
        public int response200TotalCount = 0;
        public long response200TotalMilliSecond;
        public int response200GT2sCount;
        public Map<String, Integer> ip2Count = new HashMap<String, Integer>();
        public CountWithinSecond nowCountWithinSecond;
        public CalcFailureTime cft = new CalcFailureTime();

        public double avgRepInMilliSecond() {
            BigDecimal _200CostInMilliSecond = new BigDecimal(response200TotalMilliSecond);
            BigDecimal _200Cnt = new BigDecimal(response200TotalCount);
            BigDecimal rst = _200CostInMilliSecond.divide(_200Cnt, 2, BigDecimal.ROUND_HALF_UP);
            return rst.doubleValue();
        }

        public String mostFreqIP() {
            int max = -1;
            String thatIP = null;
            Set<Map.Entry<String, Integer>> set = ip2Count.entrySet();
            for (Map.Entry<String, Integer> e : set) {
                if (e.getValue() > max) {
                    max = e.getValue();
                    thatIP = e.getKey();
                }
            }
            return thatIP;
        }

        public String toRequiredString() {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            sb.append(" ");

            sb.append(totalCount);
            sb.append(" ");

            sb.append(avgRepInMilliSecond());
            sb.append(" ");

            sb.append(response200GT2sCount);
            sb.append(" ");

            sb.append(mostFreqIP());
            sb.append(" ");
            // error time
            sb.append(cft.failureTimeByFar);

            return sb.toString();
        }
    }


    public static class ThreadAndQueue {
        private int count;
        private BlockingQueue<EachLine>[] queues;
        private Thread[] threads;
        private CountDownLatch cdl;
        private ConcurrentHashMap<String, APIStatistic> apiMap;

        public ThreadAndQueue(ConcurrentHashMap<String, APIStatistic> apiMap, int count) {
            this.apiMap = apiMap;
            this.count = count;
            queues = new ArrayBlockingQueue[count];
            threads = new Thread[count];
            cdl = new CountDownLatch(count);

            for (int i = 0; i < queues.length; i++) {
                queues[i] = new ArrayBlockingQueue<EachLine>(500);
            }
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(new ConsumeEachLine(apiMap, queues[i], cdl));
            }
        }

        public void put2CorrespondingQueue(EachLine el) {
            if (el.url == null) {
                return;
            }
            try {
                //System.out.println(el.url.hashCode() % count);
                queues[Math.abs(el.url.hashCode() % count)].put(el);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void putEOF2AllQueue() {
            for (BlockingQueue<EachLine> aQueue : queues) {
                try {
                    aQueue.put(EOF);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void startThreads() {
            for (Thread t : threads) {
                t.start();
            }
        }

        public void await() {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static class ProduceEachLine implements Runnable {
        private String fullPath;
        private ThreadAndQueue taq;

        public ProduceEachLine(ThreadAndQueue taq, String fullPath) {
            this.taq = taq;
            this.fullPath = fullPath;
        }

        @Override
        public void run() {
            produceEachLine(fullPath);
        }

        private void produceEachLine(String fullPath) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(fullPath));
                String line = null;
                while ((line = br.readLine()) != null) {
                    EachLine el = parseALine(line);
                    taq.put2CorrespondingQueue(el);
                }
                taq.putEOF2AllQueue();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ConsumeEachLine implements Runnable {
        private BlockingQueue<EachLine> queue;
        private CountDownLatch cdl;
        private ConcurrentHashMap<String, APIStatistic> apiMap;

        public ConsumeEachLine(ConcurrentHashMap<String, APIStatistic> apiMap, BlockingQueue<EachLine> queue, CountDownLatch cdl) {
            this.apiMap = apiMap;
            this.queue = queue;
            this.cdl = cdl;
        }

        private void consumeEachLine(EachLine el) {
            if (!apiMap.containsKey(el.url)) {
                APIStatistic as = new APIStatistic();
                as.url = el.url;
                as.nowCountWithinSecond = new CountWithinSecond();
                as.nowCountWithinSecond.ts = el.timeStampInSecond;
                apiMap.put(el.url, as);
            }
            APIStatistic APIStatistic = apiMap.get(el.url);

            // Total Count
            APIStatistic.totalCount++;
            // 200
            if (el.status == 200) {
                APIStatistic.response200TotalCount++;
                APIStatistic.response200TotalMilliSecond += el.costInMilli;
                // > 2000
                if (el.costInMilli > 2000L) {
                    APIStatistic.response200GT2sCount++;
                }
            }
            // within second count
            setupWithinSecondCount(APIStatistic, el);

            //IP
            Integer ipCount = APIStatistic.ip2Count.get(el.ip);
            if (ipCount == null) {
                APIStatistic.ip2Count.put(el.ip, 1);
            } else {
                APIStatistic.ip2Count.put(el.ip, (ipCount + 1));
            }
        }

        private void setupWithinSecondCount(APIStatistic as, EachLine el) {
            long lastOccurOn = as.nowCountWithinSecond.ts;
            long currentOccurOn = el.timeStampInSecond;
            if (lastOccurOn != currentOccurOn) {
                as.cft.add(as.nowCountWithinSecond);
                if (as.cft.isRightFull()) {
                    //as.cft.calculateFailureTime();
                } else {
                    // do nothing
                }
                as.nowCountWithinSecond = new CountWithinSecond();
                as.nowCountWithinSecond.ts = el.timeStampInSecond;
            }

            as.nowCountWithinSecond.total++;
            if (el.status == 500) {
                as.nowCountWithinSecond._500++;
            }
        }

        @Override
        public void run() {
            BlockingQueue<EachLine> theQueue = this.queue;
            try {
                while (true) {
                    EachLine el = theQueue.take();
                    if (el == null) {
                        continue;
                    }
                    if (el.isEOF()) {
                        break;
                    }
                    consumeEachLine(el);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                cdl.countDown();
            }
        }

    }

    private static String convertMap2String(Map<String, APIStatistic> apiStatisticsMap) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, APIStatistic>> set = apiStatisticsMap.entrySet();

        for (Map.Entry<String, APIStatistic> e : set) {
            sb.append(e.getValue().toRequiredString());
            sb.append("\n");
        }
        return sb.toString();
    }

    private static void consumeLogFileProductResult(String logfileFullPath, String rstFileFullPath) {
        AnalyzeLog d = new AnalyzeLog();
        ProduceEachLine pel = new ProduceEachLine(d.taq, logfileFullPath);
        new Thread(pel).start();
        d.taq.startThreads();
        d.taq.await();

        for (Map.Entry<String, APIStatistic> entry : d.taq.apiMap.entrySet()) {
            entry.getValue().cft.calculateFailureTime();
        }

        String rst = convertMap2String(d.apiMap);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(rstFileFullPath));
            bw.write(rst);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static int str2int(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static long str2long(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private static long str2Longdate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss ZZZZ");
        for (Map.Entry<String, String> entry : MONTH2INT.entrySet()) {
            if (strDate.contains(entry.getKey())) {
                String strDateGood = strDate.replace(entry.getKey(), entry.getValue());
                try {
                    return sdf.parse(strDateGood).getTime() / 1000L;
                } catch (ParseException e) {
                    return -1L;
                }
            }
        }
        return -1;
    }


    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        String fullPath = "D:\\sample1.log";
        consumeLogFileProductResult(fullPath, "D:\\sample1.log111");
        System.out.println("Cost: " + (System.currentTimeMillis() - begin));
    }


    public static boolean gt30(int _500, int total) {
        return new BigDecimal(_500).divide(new BigDecimal(total), 2, BigDecimal.ROUND_HALF_UP).compareTo(_03) == 1;

    }

    public static long idealStartTs(long endTs) {
        long startTs = endTs - FAILURE_TIME_SPAN + 1;//10-->1
        return startTs;
    }

    public static boolean logLongEnough(long firstOccur, long tailOccur) {
        //1--10->bad;1--11->good
        return tailOccur - firstOccur >= FAILURE_TIME_SPAN;
    }

    public static long idealEndTs(long startTs) {
        long endTs = startTs + FAILURE_TIME_SPAN - 1;//11-->20
        return endTs;
    }

    public static class CountWithinSecond {
        public long ts;
        public int total = 0;
        public int _500 = 0;
    }

    public static class TimeSpanCount {
        public long idealEndTs = -1L;
        public int total = 0;
        public int _500 = 0;
    }

    public static class CalcFailureTime {
        public static final int NO_INDEX_FOUND_BY_IDEAL_TS = -1;

        public enum HowLong {UNKNOWN, LT_EQ, GT}

        public enum MoveDirection {EARLIER_TO_LEFT, LATTER_TO_RIGHT}

        public HowLong hl = HowLong.UNKNOWN;
        public List<CountWithinSecond> left = new ArrayList<>(FAILURE_TIME_SPAN + 1);
        public List<CountWithinSecond> right = new ArrayList<>(FAILURE_TIME_SPAN + 1);
        public TimeSpanCount failureStartPoint;
        public TimeSpanCount lastTSC;
        public long failureTimeByFar = 0L;


        /**
         * 给定一个时间戳，获取其所在的 index
         * <p>
         * 1. 能恰好找到，最好
         * <p>
         * 2. 根本找不到
         * LATTER_TO_RIGHT：找到 比它 大的那个index，然后 -1
         * EARLIER_TO_LEFT：找到 比它 小的那个index，然后 +1
         * <p>
         * 3. 找不到，则返回 -1
         *
         * @param listcws
         * @param standOnIndex
         * @param md
         * @param idealTargetTs
         * @return
         */
        public static int findIndexByIdealTs(List<CountWithinSecond> listcws, int standOnIndex, MoveDirection md, long idealTargetTs) {
            if (md == MoveDirection.LATTER_TO_RIGHT) {
                for (int i = standOnIndex + 1; i < listcws.size(); i++) {
                    long ts = listcws.get(i).ts;
                    if (ts == idealTargetTs) {
                        return i;
                    } else if (ts > idealTargetTs) {
                        return i - 1;
                    }
                    // ts<idealEndTs。继续循环
                    else {

                    }
                }
                return NO_INDEX_FOUND_BY_IDEAL_TS;
            }
            if (md == MoveDirection.EARLIER_TO_LEFT) {
                for (int i = standOnIndex - 1; i >= 0; i--) {
                    long ts = listcws.get(i).ts;

                    if (ts == idealTargetTs) {
                        return i;
                    } else if (ts < idealTargetTs) {
                        return i + 1;
                    }
                    // ts>idealEndTs。继续循环
                    else {

                    }
                }
                return NO_INDEX_FOUND_BY_IDEAL_TS;
            }
            throw new RuntimeException("Should never happen, should be able to find idealTargetTs");

        }

        public static int[] crossOverIndices(List<CountWithinSecond> listcws, int standOnIndex, MoveDirection md, long seconds) {
            List<Integer> listIndices = new ArrayList<>(FAILURE_TIME_SPAN);
            long standOnMoment = listcws.get(standOnIndex).ts;

            if (md == MoveDirection.LATTER_TO_RIGHT) {
                long bigReachMoment = standOnMoment + seconds;
                for (int i = standOnIndex + 1; i < listcws.size(); i++) {
                    if (listcws.get(i).ts <= bigReachMoment) {
                        listIndices.add(i);
                    } else {
                        break;
                    }
                }
                return list2array(listIndices);
            }

            if (md == MoveDirection.EARLIER_TO_LEFT) {
                long smallReachMoment = standOnMoment - seconds;
                for (int i = standOnIndex - 1; i >= 0; i--) {
                    if (listcws.get(i).ts >= smallReachMoment) {
                        listIndices.add(i);
                    } else {
                        break;
                    }
                }
                return list2array(listIndices);
            }
            return new int[0];
        }

        private static int[] list2array(List<Integer> listIndices) {
            int[] array = new int[listIndices.size()];
            for (int i = 0; i < listIndices.size(); i++) {
                array[i] = listIndices.get(i).intValue();
            }
            return array;
        }

        public boolean isRightFull() {
            return right.size() == FAILURE_TIME_SPAN + 1;
        }

        public void add(CountWithinSecond cws) {
            right.add(cws);
        }

        public CountWithinSecond getRightFirst() {
            if (right.isEmpty()) {
                return null;
            }
            return right.get(0);
        }

        public CountWithinSecond getRightLast() {
            if (right.isEmpty()) {
                return null;
            }
            return right.get(right.size() - 1);
        }

        private boolean alreadyHasFailureStartPoint() {
            return failureStartPoint != null;
        }

        public boolean leftEmpty() {
            return left.isEmpty();
        }

        private boolean goon() {
            if (hl == HowLong.UNKNOWN) {
                CountWithinSecond initial = getRightFirst();
                CountWithinSecond vanish = getRightLast();
                hl = (initial == null) ? HowLong.LT_EQ :
                        (logLongEnough(initial.ts, vanish.ts) ? HowLong.GT : HowLong.LT_EQ);
            }
            if (hl == HowLong.GT) {
                return true;
            }
            if (hl == HowLong.LT_EQ) {
                return false;
            }
            return false;
        }

        private int[] sumCount(List<CountWithinSecond> listcws, int startIndex, int endIndex) {
            int[] totalAnd500 = new int[]{0, 0};
            for (int i = startIndex; i <= endIndex; i++) {
                CountWithinSecond cws = listcws.get(i);
                totalAnd500[0] += cws.total;
                totalAnd500[1] += cws._500;
            }
            return totalAnd500;
        }

        public void calculateFailureTime() {
            if (!goon()) {
                return;
            }
            //left空空的
            if (leftEmpty()) {
                initialCalcFailureTime();
            }
            //left满满的
            else {
                continueCalculateFailureTime();
            }
        }

        private void initialCalcFailureTime() {
            CountWithinSecond initial = getRightFirst();
            long idealEndTs = idealEndTs(initial.ts);
            int endIndex = findIndexByIdealTs(right, 0, MoveDirection.LATTER_TO_RIGHT, idealEndTs);

            TimeSpanCount tsc = createTimeSpanCount(idealEndTs, right, 0, endIndex);
            digestTimeSpanCount(tsc);
            advanceIndexByIndex(endIndex + 1);

            left.clear();
            left.addAll(right);
            right.clear();
        }

        private void continueCalculateFailureTime() {
            for (int i = 0; i < right.size(); i++) {
                int oneEndIndex = i;
                long actualEndTs = right.get(oneEndIndex).ts;
                long idealStartTs = idealStartTs(actualEndTs);
                int oneStartIndex = findIndexByIdealTs(right, oneEndIndex, MoveDirection.EARLIER_TO_LEFT, idealStartTs);

                if (oneStartIndex != NO_INDEX_FOUND_BY_IDEAL_TS) {
                    TimeSpanCount tsc = createTimeSpanCount(actualEndTs, right, oneStartIndex, oneEndIndex);
                    digestTimeSpanCount(tsc);

                } else {
                    oneStartIndex = findIndexByIdealTs(left, left.size(), MoveDirection.EARLIER_TO_LEFT, idealStartTs);
                    TimeSpanCount tsc_left = createTimeSpanCount(actualEndTs, left, oneStartIndex, left.size() - 1);
                    TimeSpanCount tsc_right = createTimeSpanCount(actualEndTs, right, 0, oneEndIndex);
                    tsc_left.total += tsc_right.total;
                    tsc_left._500 += tsc_right._500;
                    digestTimeSpanCount(tsc_left);
                }
            }

            left.clear();
            left.addAll(right);
            right.clear();
        }

        private TimeSpanCount createTimeSpanCount(long idealEndTs, List<CountWithinSecond> listcws, int startIndex, int endIndex) {
            //终于计算出来了 第一个
            TimeSpanCount tsc = new TimeSpanCount();
            tsc.idealEndTs = idealEndTs;
            int[] totalAnd500 = sumCount(listcws, startIndex, endIndex);
            tsc.total = totalAnd500[0];
            tsc._500 = totalAnd500[1];

            return tsc;
        }

        /**
         * 设置了 failureTimeByFar, failureStartPoint, lastTSC
         *
         * @param tsc
         */
        private void digestTimeSpanCount(TimeSpanCount tsc) {
            // 已拥有了故障开始点
            if (alreadyHasFailureStartPoint()) {
                // >30%----故障在继续而已
                if (gt30(tsc._500, tsc.total)) {
                    //do nothing
                }
                // <=30%----故障结束了
                else {
                    long duration = increasedFailureTime(tsc, failureStartPoint);
                    failureTimeByFar += duration;
                    failureStartPoint = null;
                }
            }
            // 尚未拥有故障开始点
            else {
                // >30%----OK，拥有了一个故障开始点
                if (gt30(tsc._500, tsc.total)) {
                    failureStartPoint = tsc;
                }
                // <=30%----前面没有故障，现在依然没有故障
                else {
                    // do nothing
                }
            }
            lastTSC = tsc;
        }

        private void advanceIndexByIndex(int advanceFromIndex) {
            for (int i = advanceFromIndex; i < right.size(); i++) {
                int oneEndIndex = i;
                long actualEndTs = right.get(oneEndIndex).ts;
                long idealStartTs = idealStartTs(actualEndTs);
                int oneStartIndex = findIndexByIdealTs(right, oneEndIndex, MoveDirection.EARLIER_TO_LEFT, idealStartTs);

                TimeSpanCount tsc = createTimeSpanCount(actualEndTs, right, oneStartIndex, oneEndIndex);
                digestTimeSpanCount(tsc);
            }
        }

        private long increasedFailureTime(TimeSpanCount failureEndPoint, TimeSpanCount failureStartPoint) {
            return failureEndPoint.idealEndTs - failureStartPoint.idealEndTs;
        }
    }
}
