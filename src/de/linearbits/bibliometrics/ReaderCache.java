/* ******************************************************************************
 * Copyright (c) 2015 Fabian Prasser.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Fabian Prasser - initial API and implementation
 * ****************************************************************************
 */
package de.linearbits.bibliometrics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A simple file-based cache
 * 
 * @author Fabian Prasser
 */
class ReaderCache {

    /** Map*/
    private final Map<String, Map<Integer, Integer>> map = new LinkedHashMap<String, Map<Integer, Integer>>();
    
    /**
     * Creates a new instance
     * @throws IOException
     */
    ReaderCache() throws IOException {
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("data/cache.csv")));
            line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(";");
                String title = parts[0];
                int year = Integer.valueOf(parts[1]);
                int count = Integer.valueOf(parts[2]);
                put(title, year, count);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(line);
            throw new IOException(e);
        }
    }
    
    /**
     * Check entry
     * @param title
     * @param year
     * @return
     */
    boolean contains(String title, int year) {
        title = title.replace(";", "");
        return map.containsKey(title) && map.get(title).containsKey(year);
    }
    
    /**
     * Get entry
     * @param title
     * @param year
     * @return
     */
    int get(String title, int year) {
        title = title.replace(";", "");
        return map.get(title).get(year);
    }
    
    /**
     * Store to disk
     * @throws IOException
     */
    void persist() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("data/cache.csv")));
        for (String key : map.keySet()) {
            for (Entry<Integer, Integer> entry : map.get(key).entrySet()) {
                writer.write(key);
                writer.write(";");
                writer.write(String.valueOf(entry.getKey()));
                writer.write(";");
                writer.write(String.valueOf(entry.getValue()));
                writer.write("\n");
            }
        }
        writer.close();
    }
    
    /**
     * Put entry
     * @param title
     * @param year
     * @param count
     */
    void put(String title, int year, int count) {
        title = title.replace(";", "");
        Map<Integer, Integer> entry = map.get(title);
        if (entry == null) {
            entry = new HashMap<Integer, Integer>();
            map.put(title, entry);
        }
        entry.put(year, count);
    }
}
