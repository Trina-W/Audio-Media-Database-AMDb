package com.AMDb.AMDb.repository;

import com.AMDb.AMDb.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Integer> {

}
