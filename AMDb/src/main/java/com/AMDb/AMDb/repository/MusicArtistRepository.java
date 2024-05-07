package com.AMDb.AMDb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.AMDb.AMDb.model.MusicArtist;

@Repository
public interface MusicArtistRepository extends JpaRepository<MusicArtist, Integer> {

}
