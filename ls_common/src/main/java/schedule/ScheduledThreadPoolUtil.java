/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ScheduledThreadPoolUtil
 * Author:   Administrator
 * Date:     2019/4/16 15:10
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * TanYujie       修改时间           版本号              描述
 */
package schedule;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2019/4/16
 * @since 1.0.0
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolUtil {

    private static ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(PoolCfgConstants.corePoolSize, new KysRejectPolicy());

    private ScheduledThreadPoolUtil() {
    }

    private static ScheduledThreadPoolUtil instance = new ScheduledThreadPoolUtil();

    public static ScheduledThreadPoolUtil getInstance() {
        return instance;
    }


    private Map<Integer, ScheduledFuture> scheduleMap = new HashMap<>();

    private Map<String, ScheduledFuture> matchScheduleMap = new ConcurrentHashMap<>();

    private String buildMatchKey(int matchDbId) {
        return "match_schedule_key" + matchDbId;
    }

    public void addSchedule(int roomId, Runnable runTask, int firstDealy, int interval, TimeUnit t) {
        removeSchedule(roomId);
        ScheduledFuture schedule = scheduleAtFixedRate(runTask, firstDealy, interval, t);
        scheduleMap.put(roomId, schedule);
    }

    public void addRunByRoom(int roomId, Runnable runTask, int firstDealy, TimeUnit t) {
        removeSchedule(roomId);
        ScheduledFuture schedule = addRun(runTask, firstDealy, t);
        scheduleMap.put(roomId, schedule);
    }

    public boolean removeSchedule(int roomId) {
        if (scheduleMap.containsKey(roomId)) {
            cancle(scheduleMap.get(roomId));
            return true;
        }
        return false;
    }

    public void addRunByMatch(int matchId, Runnable runTask, int firstDealy) {
        removeMatchSchedule(matchId);
        ScheduledFuture schedule = addRun(runTask, firstDealy, TimeUnit.SECONDS);
        matchScheduleMap.put(buildMatchKey(matchId), schedule);
    }

    public boolean removeMatchSchedule(int matchId) {
        String key = buildMatchKey(matchId);
        if (matchScheduleMap.containsKey(key)) {
            cancle(matchScheduleMap.get(key));
            return true;
        }
        return false;
    }

    /**
     * 增加定时任务
     *
     * @param run
     * @param dealy
     * @return
     */
    public ScheduledFuture<?> addRun(Runnable run, int dealy, TimeUnit t) {
        ScheduledFuture<?> sf = stpe.schedule(run, dealy, t);
        return sf;
    }

    public void purge() {
        stpe.purge();
    }

    /***
     * 增加一个定时任务
     * @param runTask 任务
     * @param firstDealy 第一次支行推迟时间
     * @param interval 以后每次支行任务时间间隔
     */
    public ScheduledFuture scheduleAtFixedRate(Runnable runTask, int firstDealy, int interval, TimeUnit t) {
        return stpe.scheduleAtFixedRate(runTask, firstDealy, interval, t);
    }

    /**
     * 取消
     *
     * @param sf
     */
    public void cancle(ScheduledFuture<?> sf) {
        if (sf != null) {
            sf.cancel(false);
            sf = null;
        }
    }

    public void execute(Runnable cmd) {
        stpe.execute(cmd);
    }


}
