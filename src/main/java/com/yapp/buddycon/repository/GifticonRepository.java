package com.yapp.buddycon.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GifticonRepository {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public GifticonRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Gifticon> getAllGifticons() {
    List<Gifticon> result = jdbcTemplate.query(
        "SELECT gifticon_id, name, used FROM gifticon", (rs, rowNum) ->
            new Gifticon(
                rs.getLong("gifticon_id"),
                rs.getString("name"),
                rs.getBoolean("used")
            )
    );
    return result;
  }

}
