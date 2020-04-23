/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.acronis_http_serve.classes;

/**
 *
 * @author Joel
 */
public abstract class AbstractHierachyAdapter {
  public abstract AcronisFile lookupPath(String path);
}
