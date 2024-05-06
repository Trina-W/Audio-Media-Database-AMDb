package com.AMDb.AMDb.controller;

import com.AMDb.AMDb.model.Song;
import com.AMDb.AMDb.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class SongController {
  private final SongRepository songRepository;

  @Autowired
  public SongController(SongRepository songRepository) {
    this.songRepository = songRepository;
  }

  @GetMapping("/get-all-songs")
  public List<Song> retrieveSong() {
    return songRepository.findAll();
  }

  @GetMapping("/get-song/{identity}")
  public Song getSingleSong(@PathVariable("identity") Integer id) {
    return songRepository.findById(id).orElse(null);
  }
}
