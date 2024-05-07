package com.AMDb.AMDb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.AMDb.AMDb.model.MusicArtist;
import com.AMDb.AMDb.repository.MusicArtistRepository;


@Controller
public class MusicArtistController {
  private final MusicArtistRepository musicArtistRepository;

  @Autowired
  public MusicArtistController(MusicArtistRepository musicArtistRepository) {
    this.musicArtistRepository = musicArtistRepository;
  }

  @GetMapping("/get-all-music-artists")
  public @ResponseBody List<MusicArtist> getAllMusicArtists() {
    return musicArtistRepository.findAll();
  }

  @GetMapping("/get-music-artist/{identity}")
  public @ResponseBody MusicArtist getSingleMusicArtist(@PathVariable("identity") Integer id) {
    return musicArtistRepository.findById(id).orElse(null);
  }
}
