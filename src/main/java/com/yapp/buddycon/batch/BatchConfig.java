package com.yapp.buddycon.batch;

import com.yapp.buddycon.batch.chunk.SimpleProcessor;
import com.yapp.buddycon.batch.chunk.SimpleReader;
import com.yapp.buddycon.batch.chunk.SimpleWriter;
import com.yapp.buddycon.repository.Gifticon;
import com.yapp.buddycon.repository.GifticonRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@RequiredArgsConstructor
@Configuration
public class BatchConfig {

  // Job, Step을 생성하는 빌더 팩토리
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private final DataSource dataSource;

  private final GifticonRepository gifticonRepository;

  @Bean
  public Job helloJob() throws Exception {
    // Job 생성
    return this.jobBuilderFactory.get("helloJob")
        .start(helloStep1())
        .next(jdbcStep2())
        .next(easyChunkStep3())
        .next(chunkStep4())
        .next(jdbcCursorChunkStep5())
        .next(jdbcPagingChunkStep6())
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
  public Step easyChunkStep3() {
    return stepBuilderFactory.get("easyChunkStep3")
        .<String, String>chunk(3)
        .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3","item4", "item5", "item6")))
        .processor((ItemProcessor<String, String>) item -> "my_" + item)
        .writer(items -> items.forEach(item -> System.out.println(item)))
        .build();
  }

  //////////////////////////////////
  //********simple chunk********////
  //////////////////////////////////
  @Bean
  public Step chunkStep4() {
    return stepBuilderFactory.get("chunkStep4")
        .<String, String>chunk(3)
        .reader(simpleItemReader())
        .processor(simpleItemProcessor())
        .writer(simpleItemWriter())
        .build();
  }

  @Bean
  public ItemReader simpleItemReader() {
    return new SimpleReader(Arrays.asList(
        new Gifticon(1l, "gifticon1", true),
        new Gifticon(2l, "기프티콘 두번째", true),
        new Gifticon(3l, "스타벅스 5천원", false)
    ));
  }

  @Bean
  public ItemProcessor simpleItemProcessor() {
    return new SimpleProcessor();
  }

  @Bean
  public ItemWriter simpleItemWriter() {
    return new SimpleWriter();
  }

  //////////////////////////////////
  //***jdbc cursor with chunk***////
  //////////////////////////////////
  @Bean
  public Step jdbcCursorChunkStep5() {
    return stepBuilderFactory.get("jdbcCursorChunkStep5")
        .<Gifticon, Gifticon>chunk(5)
        .reader(jdbcCursorItemReader())
        .writer(items -> items.forEach(item -> System.out.println(item.toString())))
        .build();
  }

  @Bean
  public JdbcCursorItemReader<Gifticon> jdbcCursorItemReader() {
    return new JdbcCursorItemReaderBuilder()
        .name("jdbcCursorItemReader")
        .fetchSize(5)
        .sql("SELECT gifticon_id AS id, name, used FROM gifticon")
        .beanRowMapper(Gifticon.class)
//        .maxItemCount(20)       // ?
//        .currentItemCount(5)    // ?
//        .maxRows(100)           // ?
        .dataSource(dataSource)
        .build();
  }

  //////////////////////////////////
  //***jdbc paging with chunk***////
  //////////////////////////////////
  @Bean
  public Step jdbcPagingChunkStep6() throws Exception {
    return stepBuilderFactory.get("jdbcPagingChunkStep6")
        .<Gifticon, Gifticon>chunk(5)
        .reader(jdbcPagingItemReader())
        .writer(items -> items.forEach(item -> System.out.println(item.toString())))
        .build();
  }

  @Bean
  public JdbcPagingItemReader<Gifticon> jdbcPagingItemReader() throws Exception {
    HashMap<String, Object> parameters = new HashMap<>();
    parameters.put("gifticon_id", "3");

    return new JdbcPagingItemReaderBuilder<Gifticon>()
        .name("jdbcPagingItemReader")
        .pageSize(5)
        .fetchSize(5)
        .dataSource(dataSource)
        .rowMapper(new BeanPropertyRowMapper<>(Gifticon.class))
        .queryProvider(createQueryProviderForJdbcPagingItemReader())
        .parameterValues(parameters)
        .build();
  }

  @Bean
  public PagingQueryProvider createQueryProviderForJdbcPagingItemReader() throws Exception {
    SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
    queryProvider.setDataSource(dataSource);
    queryProvider.setSelectClause("gifticon_id, name, used");
    queryProvider.setFromClause("from gifticon");
    queryProvider.setWhereClause("where gifticon_id > :gifticon_id");

    //Paging은 매번 다른 Connection을 맺기 때문에 Order가 필수
    Map<String, Order> sortKeys = new HashMap<>(1);
    sortKeys.put("gifticon_id", Order.ASCENDING);
    queryProvider.setSortKeys(sortKeys);

    return queryProvider.getObject();
  }

}
