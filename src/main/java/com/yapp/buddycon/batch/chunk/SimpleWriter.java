package com.yapp.buddycon.batch.chunk;

import com.yapp.buddycon.repository.Gifticon;
import java.util.List;
import org.springframework.batch.item.ItemWriter;

public class SimpleWriter implements ItemWriter<Gifticon> {

  @Override
  public void write(List<? extends Gifticon> items) throws Exception {
    items.forEach(item -> System.out.println(item.toString()));
  }

}
