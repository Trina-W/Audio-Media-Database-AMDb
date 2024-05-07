package com.AMDb.AMDb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "music_artist")
public class MusicArtist {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  int Artist_ID;

  @Column(name = "artist_name")
  String Artist_Name;
}
