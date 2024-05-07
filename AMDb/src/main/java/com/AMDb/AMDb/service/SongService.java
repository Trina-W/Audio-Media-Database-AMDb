package com.AMDb.AMDb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AMDb.AMDb.repository.SongRepository;

@Service
public class SongService {
  private final SongRepository songRepository;

  @Autowired
  public SongService(SongRepository songRepository) {
    this.songRepository = songRepository;
  }
}
