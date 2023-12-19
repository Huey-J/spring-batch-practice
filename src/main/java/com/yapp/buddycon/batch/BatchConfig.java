package com.yapp.buddycon.batch;

import com.yapp.buddycon.repository.Gifticon;
import com.yapp.buddycon.repository.GifticonRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class BatchConfig {

  // Job, Step을 생성하는 빌더 팩토리
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final GifticonRepository gifticonRepository;

  @Bean
  public Job helloJob() {
    // Job 생성
    return this.jobBuilderFactory.get("helloJob")
        .start(helloStep1())
        .next(jdbcStep2())
        .build();
  }

  @Bean
  public Step helloStep1() {
    return stepBuilderFactory.get("helloStep2")
        .tasklet((contribution, chunkContext) -> {
          System.out.println(" ============================");
          System.out.println(" >> Step1 has executed");
          System.out.println(" ============================");
          return RepeatStatus.FINISHED;
        })
        .build();
  }

  @Bean
  public Step jdbcStep2() {
    // Step 생성
    return stepBuilderFactory.get("helloStep1")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            List<Gifticon> gifticons = gifticonRepository.getAllGifticons();
            for (Gifticon allGifticon : gifticons) {
              System.out.println(allGifticon.toString());
            }

            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }

}
