package com.AMDb.AMDb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Song {

  @Id
  Integer Song_ID;

  String Song_Title;

  String Genre;

  int MinTempo;

  int MaxTempo;

  int SMinLength;

  int SMaxLength;

  Boolean Explicit;

  int Music_Release_ID;
}
