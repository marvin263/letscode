/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marvin.testjsf;

import java.io.Serializable;
import java.util.Objects;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author marvin
 */
@Named(value = "myFile")
@SessionScoped
public class FileInfo implements Serializable {

    private String filename = "mySubscriber.txt";
    private long size = 1024 * 1024;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
        hash = 23 * hash + Objects.hashCode(this.filename);
        hash = 23 * hash + (int) (this.size ^ (this.size >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileInfo other = (FileInfo) obj;
        if (!Objects.equals(this.filename, other.filename)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        return true;
    }

}
