package com.AMDb.AMDb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.AMDb.AMDb.model.Song;
import com.AMDb.AMDb.repository.SongRepository;

@Controller
public class SongController {
  private final SongRepository songRepository;

  @Autowired
  public SongController(SongRepository songRepository) {
    this.songRepository = songRepository;
  }

  @GetMapping("/get-all-songs")
  public @ResponseBody List<Song> getAllSongs() {
    return songRepository.findAll();
  }

  @GetMapping("/get-song/{identity}")
  public @ResponseBody Song getSingleSong(@PathVariable("identity") Integer id) {
    return songRepository.findById(id).orElse(null);
  }
}
