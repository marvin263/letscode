import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by libing2 on 2016/1/9.
 */
public class AnalyseLog {
    public static class RotatedArray<T> {
        public Object[] elementData;
        public int startInclude;
        public int size;
        public final int arrayLen;

        public RotatedArray(int len) {
            this.arrayLen = len;
            elementData = new Object[len];
            startInclude = 0;
            size = 0;
        }

        public int size() {
            return size;
        }

        public int[] currentIndices() {
            return indices();
        }

        public T get(int absoluteIndex) {
            return (T) elementData[absoluteIndex];
        }

        public int add(T e) {
            int endIndex = calcNextTail();
            if (size == arrayLen) {
                elementData[endIndex] = e;
                startInclude = incr(startInclude);
            } else {
                elementData[endIndex] = e;
                size++;
            }
            return endIndex;
        }

        public T getFirst() {
            if (size == 0) {
                return null;
            }
            return (T) elementData[startInclude];
        }

        public T getLast() {
            if (size == 0) {
                return null;
            }
            return (T) elementData[calcCurrentTail()];
        }

        public void removeFirst() {
            if (size == 0) {
                return;
            }
            elementData[startInclude] = null;
            startInclude = incr(startInclude);
            size--;
        }

        public void removeLast() {
            if (size == 0) {
                return;
            }
            int nowEndIndex = calcCurrentTail();
            elementData[nowEndIndex] = null;
            size--;
        }

        /**
         * 注意：untilIndex并不是数组本身的index，而是，RotatedArray的index，这个第0号，是值 startInclude的值
         *
         * @param untilIndex
         * @param alsoRemoveUntilIndex
         * @return
         */
        public int removePrecursor(int untilIndex, boolean alsoRemoveUntilIndex) {
            int[] idx = indices();
            if (!contains(idx, untilIndex)) {
                return 0;
            }

            int cnt = 0;
            for (int i = 0; i < idx.length; i++) {
                int aIndex = idx[i];
                if (aIndex != untilIndex) {
                    removeFirst();
                    cnt++;
                }
                // aIndex== untilIndex
                else {
                    if (alsoRemoveUntilIndex) {
                        removeFirst();
                        cnt++;
                    }
                    break;
                }
            }
            return cnt;
        }

        /**
         * 注意：fromIndex并不是数组本身的index，而是，RotatedArray的index，这个第0号，是值 startInclude的值
         *
         * @param fromIndex
         * @param alsoRemoveFromIndex
         * @return
         */
        public int removeSuccessor(int fromIndex, boolean alsoRemoveFromIndex) {
            int[] idx = indices();
            if (!contains(idx, fromIndex)) {
                return 0;
            }
            int cnt = 0;
            for (int i = idx.length - 1; i >= 0; i--) {
                int aIndex = idx[i];
                if (aIndex != fromIndex) {
                    removeLast();
                    cnt++;
                }
                // aIndex == fromIndex
                else {
                    if (alsoRemoveFromIndex) {
                        removeFirst();
                        cnt++;
                    }
                    break;

                }
            }
            return cnt;
        }

        public int removeAll() {
            int cnt = size;
            startInclude = 0;
            size = 0;
            return cnt;
        }


        private int[] indices() {
            if (size == 0) {
                return new int[0];
            }
            int[] idx = new int[size];
            idx[0] = startInclude;

            for (int i = 1; i < size; i++) {
                idx[i] = incr(idx[i - 1]);
            }
            return idx;
        }

        private int calcNextTail() {
            int endIndex = startInclude + size;
            return endIndex >= arrayLen ? endIndex % arrayLen : endIndex;
        }

        public int calcCurrentTail() {
            int endIndex = startInclude + size - 1;
            return endIndex >= arrayLen ? endIndex % arrayLen : endIndex;
        }

        private int incr(int i) {
            i++;
            return (i == arrayLen) ? 0 : i;
        }

        private boolean contains(int[] array, int aEle) {
            for (int i : array) {
                if (i == aEle) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("startInclude=" + startInclude);
            sb.append(", size=" + size);
            sb.append(", elements: ");
            int[] idx = indices();
            for (int i : idx) {
                sb.append(elementData[i]);
                sb.append(", ");
            }
            return sb.toString();
        }

        public static void main(String[] args) {
            RotatedArray<String> ra = new RotatedArray<String>(5);
            ra.add("a");
            ra.add("b");
            ra.add("e");

            ra.add("1");
            ra.add("2");
            ra.add("d");
            ra.add("e");

            ra.add("1");
            ra.add("2");
            ra.add("3");
            ra.removeFirst();
            ra.add("f");
            ra.removePrecursor(2, false);
            System.out.println(ra);
        }
    }

    public static Map<String, String> MONTH2INT = new HashMap<String, String>();
    private static ConcurrentHashMap<String, ApiStatistics> apiStatisticsMap = new ConcurrentHashMap<String, ApiStatistics>();

    private ThreadAndQueue taq = new ThreadAndQueue(10);

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

        @Override
        public String toString() {
            return "EachLine{" +
                    "ip='" + ip + '\'' +
                    ", timeStampInSecond=" + timeStampInSecond +
                    ", url='" + url + '\'' +
                    ", status=" + status +
                    ", repLen=" + repLen +
                    ", costInMilli=" + costInMilli +
                    '}';
        }
    }

    public static class CountWithinSecond {
        public CountWithinSecond(long timestampInSecond) {
            this.timestampInSecond = timestampInSecond;
        }

        private long timestampInSecond = -1L;
        private int _500Count = 0;
        private int totalCount = 0;
    }

    public static class ErrorTime {
        public static final BigDecimal _03 = new BigDecimal("0.30");
        public static final int FAILURE_DETECT_PERIOD = 10;
        public int errorTime;
        public long failureStartPoint = -1L;
        private RotatedArray<CountWithinSecond> array = new RotatedArray<CountWithinSecond>(FAILURE_DETECT_PERIOD);
        public int totalCountInArray = 0;
        public int _500CountInArray = 0;

        private boolean errorGT30Percent() {
            return (new BigDecimal(_500CountInArray).divide(new BigDecimal(totalCountInArray), 2, BigDecimal.ROUND_HALF_UP).compareTo(_03)) > 1;

        }

        public void add(CountWithinSecond cws) {
            array.add(cws);
            totalCountInArray += cws.totalCount;
            _500CountInArray += cws._500Count;


            CountWithinSecond first = array.getFirst();
            CountWithinSecond last = array.getLast();
            long timeSpan = last.timestampInSecond - first.timestampInSecond + 1;


            if (timeSpan < FAILURE_DETECT_PERIOD) {
                // do nothing
            } else if (timeSpan == FAILURE_DETECT_PERIOD) {
                // 恰好是10s

                // 错误数目 >30%
                if (errorGT30Percent()) {
                    //故障已经开始了
                    if (failureStarted()) {
                        totalCountInArray -= first.totalCount;
                        _500CountInArray -= first._500Count;
                        array.removeFirst();
                    }
                    // 故障尚未开始
                    else {
                        failureStartPoint = last.timestampInSecond;
                        array.removeAll();
                        totalCountInArray = 0;
                        _500CountInArray = 0;
                    }
                }
                // 错误数目 <=30%
                else {
                    //故障已经开始了。好，可以结束了
                    if (failureStarted()) {
                        errorTime += last.timestampInSecond - failureStartPoint;
                        failureStartPoint = -1L;
                        array.removeAll();
                        totalCountInArray = 0;
                        _500CountInArray = 0;

                        array.removeFirst();
                    }
                    // 故障尚未开始
                    else {
                        failureStartPoint = last.timestampInSecond;
                        array.removeAll();
                        totalCountInArray = 0;
                        _500CountInArray = 0;
                    }
                }
            } else {
            }


            int endInclude = array.calcCurrentTail();

            for (int i = array.startInclude; i != endInclude; i = array.incr(i)) {
                CountWithinSecond acws = array.get(i);
                if (last.timestampInSecond - acws.timestampInSecond >= FAILURE_DETECT_PERIOD) {
                    totalCountInArray = totalCountInArray - acws.totalCount;
                    _500CountInArray = _500CountInArray - acws._500Count;
                    array.removeFirst();
                } else {
                    // Good, < FAILURE_DETECT_PERIOD
                    break;
                }
            }


        }

        public boolean failureStarted() {
            return failureStartPoint != -1L;
        }
    }

    public static class ApiStatistics {
        public ApiStatistics(String url) {
            this.url = url;
        }

        public String url;
        public int totalCount = 0;
        public int response200TotalCount = 0;
        public long response200TotalMilliSecond;
        public int response200GT2sCount;
        public Map<String, Integer> ip2Count = new HashMap<String, Integer>();
        public CountWithinSecond nowCountWithinSecond;
        public ErrorTime et = new ErrorTime();

        public double avgRepInMilliSecond() {
            BigDecimal _200CostInMilliSecond = new BigDecimal(response200TotalMilliSecond);
            BigDecimal _200Cnt = new BigDecimal(response200TotalMilliSecond);
            BigDecimal rst = _200CostInMilliSecond.divide(_200Cnt, 1, 0);
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
            sb.append(errorTime);

            return sb.toString();
        }
    }

    private void consumeEachLine(EachLine el) {
        if (!apiStatisticsMap.containsKey(el.url)) {
            ApiStatistics apiStatistics = new ApiStatistics(el.url);
            apiStatistics.nowCountWithinSecond = new CountWithinSecond(el.timeStampInSecond);
            apiStatisticsMap.put(el.url, apiStatistics);
        }
        ApiStatistics apiStatistics = apiStatisticsMap.get(el.url);

        // Total Count
        apiStatistics.totalCount++;
        // 200
        if (el.status == 200) {
            apiStatistics.response200TotalCount++;
            apiStatistics.response200TotalMilliSecond += el.costInMilli;
            // > 2000
            if (el.costInMilli > 2000L) {
                apiStatistics.response200GT2sCount++;
            }
        }
        // within second count
        setupWithinSecondCount(apiStatistics, el);

        //IP
        Integer ipCount = apiStatistics.ip2Count.get(el.ip);
        if (ipCount == null) {
            apiStatistics.ip2Count.put(el.ip, 1);
        } else {
            apiStatistics.ip2Count.put(el.ip, (ipCount + 1));
        }
    }

    private void setupWithinSecondCount(ApiStatistics apiStatistics, EachLine el) {
        long lastOccurOn = apiStatistics.nowCountWithinSecond.timestampInSecond;
        long currentOccurOn = el.timeStampInSecond;
        if (lastOccurOn != currentOccurOn) {
            apiStatistics.et.array.add(apiStatistics.nowCountWithinSecond);

            //Yes, started
            if (apiStatistics.et.failureStarted()) {


            } else {
                // No, not started

            }


            apiStatistics.nowCountWithinSecond = new CountWithinSecond(el.timeStampInSecond);
        }

        apiStatistics.nowCountWithinSecond.totalCount++;
        if (el.status == 500) {
            apiStatistics.nowCountWithinSecond._500Count++;
        }
    }

    public static class ThreadAndQueue {
        private int count;
        private BlockingQueue<EachLine>[] queues;
        private Thread[] threads;
        private CountDownLatch cdl;

        public ThreadAndQueue(int count) {
            this.count = count;
            queues = new ArrayBlockingQueue[count];
            threads = new Thread[count];
            cdl = new CountDownLatch(count);

            for (int i = 0; i < queues.length; i++) {
                queues[i] = new ArrayBlockingQueue<EachLine>(500);
            }
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(new ConsumeEachLine(queues[i], cdl));
            }
        }

        public void put2CorrespondingQueue(EachLine el) {
            if (el.url == null) {
                return;
            }
            try {
                queues[el.url.hashCode() % count].put(el);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void putEOF2AllQueue() {
            for (BlockingQueue<EachLine> aQueue : queues) {
                try {
                    aQueue.put(EachLine.createEOF());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void await() {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        public static class ConsumeEachLine implements Runnable {
            private BlockingQueue<EachLine> queue;
            private CountDownLatch cdl;

            public ConsumeEachLine(BlockingQueue<EachLine> queue, CountDownLatch cdl) {
                this.queue = queue;
                this.cdl = cdl;
            }

            @Override
            public void run() {
                BlockingQueue<EachLine> theQueue = this.queue;
                try {
                    while (true) {
                        EachLine el = theQueue.poll(1, TimeUnit.SECONDS);
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
    }

    private static String convertMap2String(Map<String, ApiStatistics> apiStatisticsMap) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, ApiStatistics>> set = apiStatisticsMap.entrySet();

        for (Map.Entry<String, ApiStatistics> e : set) {
            sb.append(e.getValue().toRequiredString());
            sb.append("\n");
        }
        return sb.toString();
    }

    private static void consumeLogFileProductResult(String logfileFullPath, String rstFileFullPath) {
        Map<String, ApiStatistics> apiStatisticsMap = readLogFile(logfileFullPath);


        String rst = convertMap2String(apiStatisticsMap);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(rstFileFullPath));
            bw.write(rst);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static class ProduceEachLine implements Runnable {
        private String fullPath;

        public ProduceEachLine(String fullPath) {
            this.fullPath = fullPath;
        }

        @Override
        public void run() {
            produceEachLine(fullPath);
        }

        private void produceEachLine(String fullPath) {
            try {
                ThreadAndQueue taq = null;
                BufferedReader br = new BufferedReader(new FileReader(fullPath));
                String line = null;
                while ((line = br.readLine()) != null) {
                    EachLine el = parseALine(line);
                    taq.put2CorrespondingQueue(el);
                }

                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


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

    private static void putLastStat(Map<String, ApiStatistics> mapUrl2Statistic) {
        Set<Map.Entry<String, ApiStatistics>> set = mapUrl2Statistic.entrySet();
        for (Map.Entry<String, ApiStatistics> e : set) {
            ApiStatistics apiStatistics = e.getValue();
            apiStatistics.statusList.add(apiStatistics.lastStat);
            if (apiStatistics.first500Index == -1 && apiStatistics.lastStat._500Count > 0) {
                apiStatistics.first500Index = apiStatistics.statusList.size() - 1;
            }
        }
    }

    private static void put2AccessLog(Map<String, ApiStatistics> apiStatisticsMap, EachLine el) {

    }


    public static final String STR_REG = "([\\d\\.]+)\\s\\[(.+)\\]\\s\"\\w+\\s(.+?)\\s.+?(\\d+)\\s(\\d+)\\s(\\d+)";
    public static final Pattern PTN = Pattern.compile(STR_REG);

    public static EachLine parseALine(String line) {
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
                    return sdf.parse(strDateGood).getTime() / 1000;
                } catch (ParseException e) {
                    return -1L;
                }
            }
        }
        return -1;
    }


    public static void main(String[] args) {

        String fullPath = "D:\\sample1.log";
        consumeLogFileProductResult(fullPath, "D:\\sample1.log111");
        Map<String, ApiStatistics> mapUrl2Statistic = readLogFile(fullPath);
        System.out.println(mapUrl2Statistic.size());
    }
}
