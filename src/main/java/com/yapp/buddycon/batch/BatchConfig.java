package com.yapp.buddycon.batch;

import com.yapp.buddycon.batch.chunk.SimpleProcessor;
import com.yapp.buddycon.batch.chunk.SimpleReader;
import com.yapp.buddycon.batch.chunk.SimpleWriter;
import com.yapp.buddycon.repository.Gifticon;
import com.yapp.buddycon.repository.GifticonRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
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
        .next(simpleChunkStep3())
        .next(chunkStep4())
        .build();
  }

  @Bean
  public Step helloStep1() {
    return stepBuilderFactory.get("helloStep1")
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
    return stepBuilderFactory.get("jdbcStep2")
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

  @Bean
  public Step simpleChunkStep3() {
    return stepBuilderFactory.get("chunkStep3")
        .<String, String>chunk(3)
        .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3","item4", "item5", "item6")))
        .processor((ItemProcessor<String, String>) item -> "my_" + item)
        .writer(items -> items.forEach(item -> System.out.println(item)))
        .build();
  }

  @Bean
  public Step chunkStep4() {
    return stepBuilderFactory.get("chunkStep4")
        .<String, String>chunk(3)
        .reader(itemReader())
        .processor(itemProcessor())
        .writer(itemWriter())
        .build();
  }

  @Bean
  public ItemReader itemReader() {
    return new SimpleReader(Arrays.asList(
        new Gifticon(1l, "gifticon1", true),
        new Gifticon(2l, "기프티콘 두번째", true),
        new Gifticon(3l, "스타벅스 5천원", false)
    ));
  }

  @Bean
  public ItemProcessor itemProcessor() {
    return new SimpleProcessor();
  }

  @Bean
  public ItemWriter itemWriter() {
    return new SimpleWriter();
  }

}
