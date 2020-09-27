package com.tntrip.docases;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;

public class ReadMetadataCase implements EachCase {
    @Override
    public void doOnLine(String line) {
        String fullPath = line.substring(prefix()[0].length());
        long begin = System.currentTimeMillis();
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(new File(fullPath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.format("[%s] - %s = %s",
                        directory.getName(), tag.getTagName(), tag.getDescription());
                System.out.println();
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.format("ERROR: %s", error);
                    System.out.println();
                }
            }
        }
        System.out.println("Cost: " + (System.currentTimeMillis() - begin) + "ms");
        System.out.println();
    }

    @Override
    public String[] prefix() {
        return new String[]{"mf", "Get file metadata"};
    }
}
