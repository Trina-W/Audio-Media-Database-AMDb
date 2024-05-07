package com.AMDb.AMDb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AMDb.AMDb.repository.MusicArtistRepository;

@Service
public class MusicArtistService {
  private final MusicArtistRepository musicArtistRepository;

  @Autowired
  public MusicArtistService(MusicArtistRepository musicArtistRepository) {
    this.musicArtistRepository = musicArtistRepository;
  }
}
