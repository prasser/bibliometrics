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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A simple reader for the DBLP XML dump
 * 
 * @author Fabian Prasser
 *
 */
class ReaderDBLP {
    
   /**
 * Collects data
 * @author Fabian Prasser
 *
 */
private static class CollectionHandler extends DefaultHandler {

    /** Key*/
    private final String      key;
    /** Values*/
    private final Set<String> collection = new HashSet<String>();
    /** Flag*/
    private boolean           collect    = false;
    /** Buffer*/
    private StringBuilder     builder    = new StringBuilder();

    /**
     * Creates a new instance
     * @param key
     */
    public CollectionHandler(String key) {
        this.key = key;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (collect) {
            builder.append(new String(ch, start, length));
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName,
                           String rawName) throws SAXException {
        if (rawName.equals(key)) {
            collection.add(builder.toString());
            collect = false;
        }
    }

    /**
     * Returns the collected data
     * @return
     */
    public Set<String> getCollection() {
        return this.collection;
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes atts) throws SAXException {

        if (rawName.equals(key)) {
            collect = true;
            builder.setLength(0);
        }
    }
}
    
   /**
 * Collects data
 * @author Fabian Prasser
 */
private static class CollectionHandler2 extends DefaultHandler {

    /** Context*/
    private String context;
    
    /** Tag*/
    private final String      tag1;
    /** Value*/
    private final String      value1;
    /** Tag*/
    private final String      tag2;
    /** Value*/
    private final String      value2;
    /** Tag*/
    private final String      tag3;
    /** Tag*/
    private final String      tag4;
    /** Collection*/
    private final Set<String> collection = new HashSet<String>();
    /** Buffer*/
    private StringBuilder     builder1    = new StringBuilder();
    /** Buffer*/
    private StringBuilder     builder2    = new StringBuilder();
    /** Buffer*/
    private StringBuilder     builder3    = new StringBuilder();

    /** 
     * Creates a new instance
     * 
     * @param tag1
     * @param value1
     * @param tag2
     * @param value2
     * @param tag3
     * @param tag4
     */
    protected CollectionHandler2(String tag1, String value1, String tag2, String value2, String tag3, String tag4) {
        this.tag1 = tag1;
        this.value1 = value1;
        this.tag2 = tag2;
        this.value2 = value2;
        this.tag3 = tag3;
        this.tag4 = tag4;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (context != null) {
            if (context.equals(tag1)) {
                builder1.append(new String(ch, start, length));
            } else if (context.equals(tag2)) {
                builder2.append(new String(ch, start, length));
            } else if (context.equals(tag3)) {
                builder3.append(new String(ch, start, length));
            }
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName,
                           String rawName) throws SAXException {
        if (rawName.equals(tag4)) {
            if (builder1.toString().equals(value1) &&
                builder2.toString().equals(value2)) {
                collection.add(builder3.toString());
            }
            builder1.setLength(0);
            builder2.setLength(0);
            builder3.setLength(0);
        }
        this.context = null;
    }

    /** Returns the collection*/
    public Set<String> getCollection() {
        return this.collection;
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes atts) throws SAXException {

        this.context = rawName;
    }
}

    ReaderDBLP() {
            // Empty by design
        }

    /**
     * Starts the collection
     * @param tag1
     * @param value1
     * @param tag2
     * @param value2
     * @param tag3
     * @param tag4
     * @return
     * @throws IOException
     */
    public Set<String> collect(String tag1, String value1, String tag2, String value2, String tag3, String tag4) throws IOException {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            CollectionHandler2 handler = new CollectionHandler2(tag1, value1, tag2, value2, tag3, tag4);
            parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", true);
            parser.parse(new File("data/dblp.xml"), handler);
            return handler.getCollection();
        } catch (Exception e) {
            throw (new IOException("Error parsing DBLP", e));
        }
    }

    /**
        * Collects data 
        * @param field
        * @return
        * @throws IOException
        */
        Set<String> collect(String field) throws IOException {
            try {
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                SAXParser parser = parserFactory.newSAXParser();
                CollectionHandler handler = new CollectionHandler(field);
                parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", true);
                parser.parse(new File("data/dblp.xml"), handler);
                return handler.getCollection();
             } catch (Exception e) {
                throw(new IOException("Error parsing DBLP", e));
             }
        }
}
