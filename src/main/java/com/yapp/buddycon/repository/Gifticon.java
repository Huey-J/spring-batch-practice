package com.yapp.buddycon.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class Gifticon {

  private Long id;
  private String name;
  private Boolean used;

  public Gifticon(Long id, String name, Boolean used) {
    this.id = id;
    this.name = name;
    this.used = used;
  }
}
