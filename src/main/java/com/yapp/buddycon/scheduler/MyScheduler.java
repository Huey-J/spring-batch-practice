package com.yapp.buddycon.scheduler;

import com.yapp.buddycon.batch.BatchConfig;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MyScheduler {

  private final JobLauncher jobLauncher;
  private final BatchConfig batchConfig;

  @Scheduled(cron = "0/3 * * * * ?")
  public void sendFourteenNotification() {
    // 파라미터 설정
    Map<String, JobParameter> parameterMap = Map.of("time", new JobParameter(System.currentTimeMillis()));
    JobParameters jobParameters = new JobParameters(parameterMap);

    // 배치
    try {
      jobLauncher.run(batchConfig.helloJob(), jobParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println(">>>>>>>>>>>> Scheduler Ends : " +
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
  }

}
