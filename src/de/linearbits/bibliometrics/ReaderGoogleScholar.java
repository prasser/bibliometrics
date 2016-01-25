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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;

import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;

/**
 * Reads data from GS
 * @author Fabian Prasser
 *
 */
class ReaderGoogleScholar {
    
    /**
     * Reads citation counts
     * @author Fabian Prasser
     */
    private static class CountReader extends HTMLHandler {
        
        /** Flag*/
        private boolean collect = false;
        /** Count*/
        private String count = null;

        /**
         * Returns the count
         * @return
         */
        public String getCount() {
            return this.count;
        }

        @Override
        protected void end(String tag) {
            
            // No results
            if (tag != null && tag.equals("div") && collect && count == null) {
                count = "0";
            }
        }

        @Override
        protected void payload(String payload) {
            if (collect) {
                
                // No results
                if (payload.trim().length()==0) {
                    if (this.count == null) {
                        this.count = "0";
                    }   
                    
                // Some results
                } else {
                    
                    String count = payload.split(" ")[0];
                    
                    try {
                        // Case "x results"
                        Integer.valueOf(count);
                    } catch (Exception e) {
                        // Case "About x results"
                        count = payload.split(" ")[1];
                    }
                    
                    if (this.count == null) {
                        this.count = count;
                    }
                }
            }
        }        
        
        @Override
        protected void start(String tag, Map<String, String> attributes) {

            if (tag != null && tag.equals("div") && attributes != null &&
                attributes.containsKey("id") && attributes.get("id").equals("gs_ab_md")) {
                collect = true;
            } else {
                collect = false;
            }
        }
    }
    
    /**
     * Reads the ID from GS
     * @author Fabian Prasser
     *
     */
    private static class IDReader extends HTMLHandler {

        /** Id*/
        private String id        = null;
        /** Count*/
        private int    countGsID = 0;

        /**
         * Returns the id
         * @return
         */
        public String getId() {
            return this.id;
        }

        @Override
        protected void end(String tag) {
            // Empty by design
        }

        @Override
        protected void payload(String payload) {
            // Empty by design
        }        
        
        @Override
        protected void start(String tag, Map<String, String> attributes) {

            // We only extract from the first entry
            if (this.countGsID == 1) {
                if (tag != null && tag.equals("a") && attributes != null &&
                    attributes.containsKey("href") && attributes.get("href").startsWith("/scholar?cites=")) {
                    String id = attributes.get("href");
                    id = id.substring(15);
                    id = id.split("&")[0];
                    if (this.id == null) {
                        this.id = id;
                    }
                }
            }
            
            // Count entries
            if (tag != null && tag.equals("div") && attributes != null &&
                    attributes.containsKey("class") && attributes.get("class").equals("gs_ri")) {
                this.countGsID++;
            }
            
        }
    }

    private static final String AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)";

    /** Charset*/
    private static final Charset CHARSET = Charset.defaultCharset();

    /**
     * Reads all data from the given URL
     * @param url
     * @return
     * @throws IOException
     */
    private String read(String url) throws IOException {

        try {
            StringBuilder builder = new StringBuilder();
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);
            connection.setRequestProperty("User-Agent", AGENT);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHARSET));
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line).append("\n");
            }
            in.close();
            
            return builder.toString();
        } catch (Exception e) {
            throw new IOException("Error parsing Google Scholar", e);
        }
    }

    /**
     * Returns the citation count for the given title
     * @param id
     * @param from
     * @param to
     * @return
     * @throws IOException
     */
    String getCitationCount(String id, String from, String to) throws IOException {

        String url = "https://scholar.google.de/scholar?hl=de&as_sdt=2005&sciodt=0%2C5&cites=" + id + "&scipsc=&as_ylo=" + from +
                     "&as_yhi=" + to;
    
        try {
            String html = read(url);
            
            if (html.contains("gs_captcha_f")) {
                throw new IOException("Error parsing Google Scholar. Locked out!");
            }

            InputStream input = new ByteArrayInputStream(html.getBytes(CHARSET));
            CountReader reader = new CountReader();
            SAXParserImpl.newInstance(null).parse(input, reader);
            return reader.getCount();
        } catch (Exception e) {
            throw new IOException("Error parsing Google Scholar", e);
        }
    }

    /**
     * Returns the id for the given title
     * @param title
     * @return
     * @throws IOException
     */
    String getId(String title) throws IOException {

        String query = title.replace(" ", "+");
        String url = "https://scholar.google.de/scholar?hl=de&q=" + query + "&btnG=&lr=";
        
        try {
            String html = read(url);
            
            if (html.contains("gs_captcha_f")) {
                throw new IOException("Error parsing Google Scholar. Locked out!");
            }
            
            InputStream input = new ByteArrayInputStream(html.getBytes(CHARSET));
            IDReader reader = new IDReader();
            SAXParserImpl.newInstance(null).parse(input, reader);
            return reader.getId();
        } catch (Exception e) {
            throw new IOException("Error parsing Google Scholar", e);
        }
    }
}
