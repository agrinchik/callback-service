package com.xyz.callbackservice.service.scheduling;

import com.xyz.callbackservice.domain.Registration;
import com.xyz.callbackservice.service.rest.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
@Service
@EnableScheduling
public class CallbackSchedulingService implements SchedulingConfigurer {

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    private ScheduledTaskRegistrar scheduledTaskRegistrar;

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    RestService restService;

    private class CallbackTask implements Runnable {
        String url;

        CallbackTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            log.info("Triggered execution of scheduled job");
            log.info("CallbackTask.run(): Number of scheduled tasks in scheduledTasks map: " + scheduledTasks.keySet().size());

            String localTime = getCurrentTimeParsed();

            log.info("Sending POST request to url: {}, localTime: {}", url, localTime);
            restService.sendPost(this.url, localTime, String.class);
        }

        private String getCurrentTimeParsed() {
            LocalTime localTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return localTime.format(formatter);
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (this.scheduledTaskRegistrar == null) {
            this.scheduledTaskRegistrar = taskRegistrar;
        }

        taskRegistrar.setScheduler(this.taskScheduler);
    }

    public synchronized void scheduleTask(Registration registration) {
        if (this.scheduledTaskRegistrar == null) {
            throw new RuntimeException("ScheduledTaskRegistrar is not initialized");
        }

        if (registration == null) {
            throw new RuntimeException("Registration information is not provided");
        }

        ScheduledFuture<?> scheduledFuture = this.scheduledTaskRegistrar.getScheduler().scheduleAtFixedRate(
            new CallbackTask(registration.getUrl()), registration.getFrequencyMillis()
        );

        scheduledTasks.put(registration.getToken(), scheduledFuture);

        log.info("Scheduled a new task for registration token: {}, url: {}, frequency: {}", registration.getToken(), registration.getUrl(), registration.getFrequency());
        ScheduledExecutorService ses = ((ThreadPoolTaskScheduler)this.scheduledTaskRegistrar.getScheduler()).getScheduledExecutor();
        ScheduledThreadPoolExecutor stpe = (ScheduledThreadPoolExecutor)ses;
        log.info("Number of scheduled tasks in scheduledTasks map: " + scheduledTasks.keySet().size());
        log.info("Size of working queue in ThreadPoolExecutor: " + stpe.getQueue().size());
    }

    public synchronized void unscheduleTask(String token) {
        if (token == null) {
            throw new RuntimeException("Registration information is not provided");
        }

        ScheduledFuture<?> future = scheduledTasks.get(token);

        boolean cancel = future.cancel(true); // the task can be interrupted immediately
        log.info("future.cancel(): cancel={}, isCancelled()={}, isDone()={}",
                 cancel, future.isCancelled(), future.isDone());

        scheduledTasks.remove(token);

        log.info("Unscheduled the task for registration token: {}", token);
        log.info("Number of scheduled tasks in scheduledTasks map: " + scheduledTasks.keySet().size());
    }

    public synchronized void amendTask(Registration registration) {
        if (this.scheduledTaskRegistrar == null) {
            throw new RuntimeException("ScheduledTaskRegistrar is not initialized");
        }

        if (registration == null) {
            throw new RuntimeException("Registration information is not provided");
        }

        log.info("Amending the currently running task for registration token: {}, url: {}, frequency: {}", registration.getToken(), registration.getUrl(), registration.getFrequency());

        unscheduleTask(registration.getToken());
        scheduleTask(registration);

    }
}
