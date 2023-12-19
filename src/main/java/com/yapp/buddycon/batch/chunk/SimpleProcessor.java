package com.yapp.buddycon.batch.chunk;

import com.yapp.buddycon.repository.Gifticon;
import org.springframework.batch.item.ItemProcessor;

public class SimpleProcessor implements ItemProcessor<Gifticon, Gifticon> {

  @Override
  public Gifticon process(Gifticon gifticon) throws Exception {
    gifticon.setName("기프티콘 이름은 " + gifticon.getName() + "입니다.");
    return gifticon;  // null 반환하면 writer에 전달되지 않음
  }

}
