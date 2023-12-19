package com.yapp.buddycon.batch.chunk;

import com.yapp.buddycon.repository.Gifticon;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.item.ItemReader;

public class SimpleReader implements ItemReader<Gifticon> {

  private List<Gifticon> list;

  public SimpleReader(List<Gifticon> list) {
    this.list = new ArrayList<>(list);
  }

  @Override
  public Gifticon read() {
    if (!list.isEmpty()) {
      return list.remove(0);
    }
    return null;
  }

}
