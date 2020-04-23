/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.acronis_http_serve.classes;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Joel
 */
public class AcronisFile {
  /*
  {
        "uuid":"a0fe9642-d770-4c1e-82f3-970c66bf57cc",
        "name":"software",
        "is_directory":true,
        "size":null,
        "file_modification_date":"2020-04-19T15:29:13Z",
        "path":"software",
        "checksum":"",
        "parent_uuid":"0",
        "actions":{
            "download":"/fc/api/v1/sync_and_share_nodes/a0fe9642-d770-4c1e-82f3-970c66bf57cc/download",
            "upload":"/fc/api/v1/sync_and_share_nodes/a0fe9642-d770-4c1e-82f3-970c66bf57cc/upload",
            "preview":null
        }
    }
  */
  private String uuid;
  private String name;
  private boolean is_directory;
  private String file_modification_date;
  private String path;
  private String checksum;
  private String parent_uuid;
  private long size;

  public AcronisFile() {
  }

  public AcronisFile(String uuid, String name, boolean is_directory, String file_modification_date, String path, String checksum, String parent_uuid, long size) {
    this.uuid = uuid;
    this.name = name;
    this.is_directory = is_directory;
    this.file_modification_date = file_modification_date;
    this.path = path;
    this.checksum = checksum;
    this.parent_uuid = parent_uuid;
    this.size = size;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isIs_directory() {
    return is_directory;
  }

  public void setIs_directory(boolean is_directory) {
    this.is_directory = is_directory;
  }

  public String getFile_modification_date() {
    return file_modification_date;
  }

  public void setFile_modification_date(String file_modification_date) {
    this.file_modification_date = file_modification_date;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public String getParent_uuid() {
    return parent_uuid;
  }

  public void setParent_uuid(String parent_uuid) {
    this.parent_uuid = parent_uuid;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 11 * hash + Objects.hashCode(this.uuid);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AcronisFile other = (AcronisFile) obj;
    if (!Objects.equals(this.uuid, other.uuid)) {
      return false;
    }
    return true;
  }
  
  
}
