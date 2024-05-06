package com.AMDb.AMDb.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "song")
public class Song {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Integer Song_ID;

  @Column(name = "Song_Title")
  String Song_Title;

  @Column(name = "Genre")
  String Genre;

  @Column(name = "MinTempo")
  int MinTempo;

  @Column(name = "MaxTempo")
  int MaxTempo;

  @Column(name = "SMinLength")
  int SMinLength;

  @Column(name = "SMaxLength")
  int SMaxLength;

  @Column(name = "Explicit")
  Boolean Explicit;

  @Column(name = "Music_Release_ID")
  int Music_Release_ID;
}
