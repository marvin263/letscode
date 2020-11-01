package com.tntrip.docases;

import com.tntrip.understand.EstimatingQueryPerformance;

import java.util.Arrays;

public class SyncRecvMaxCountForV4_4AndLater implements EachCase {
    @Override
    public void doOnLine(String line) {
        String[] strNumbers = line.substring(prefix()[0].length()).split("\\s+");
        int[] nums = Arrays.stream(strNumbers).map(Integer::parseInt).mapToInt(e -> e).toArray();
        int actualRowCount = nums[0] * 10000;
        
        int listenBacklog = 0;
        int somaxconn = 0;
        
        int tcp_max_syn_backlog;
        
        
        
        //当 accept queue 未满时，SyncRecv最大数量
        // SyncRecv最大数量 假设为X，则：
        // X <= min(listenBacklog, somaxconn)
        // 且，
        
        
        
        
        System.out.println("row_count= " + actualRowCount + ", index_length=" + nums[1] + ", need seek_count=" + EstimatingQueryPerformance.estimate(actualRowCount, nums[1]));
        System.out.println();
    }

    @Override
    public String[] prefix() {
        return new String[]{"004", "V4.4及之后，tcp_syncookies关闭，SyncRecv最大数量"};
    }
}
