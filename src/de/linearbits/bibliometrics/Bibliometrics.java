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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Main class giving access to the functionality implemented by this library
 * 
 * @author Fabian Prasser
 */
public class Bibliometrics {

    /** A cache for citations*/
    private ReaderCache cache;
    
    /**
     * Creates a new instance
     * @throws IOException
     */
    public Bibliometrics() throws IOException {
        this.cache = new ReaderCache();
    }

    /**
     * Returns a list of all articles that appeared in the proceedings of the given conference in the given year. Derived from DBLP.
     * @param conference
     * @param year
     * @return
     * @throws IOException
     */
    public List<ElementArticle> getArticlesInConference(ElementConference conference, int year) throws IOException {
        checkYear(year);
        return getMatchingValuesInTag("booktitle", conference.getValue(), "year", String.valueOf(year), "title", "inproceedings", ElementArticle.class);
    }

    /**
     * Returns a list of all articles that appeared in the given journal in the given year. Derived from DBLP.
     * @param journal
     * @param year
     * @return
     * @throws IOException
     */
    public List<ElementArticle> getArticlesInJournal(ElementJournal journal, int year) throws IOException {
        checkYear(year);
        return getMatchingValuesInTag("journal", journal.getValue(), "year", String.valueOf(year), "title", "article", ElementArticle.class);
    }

    /**
     * Returns a list of all available authors. Derived from DBLP.
     * @param author
     * @return
     * @throws IOException
     */
    public List<ElementAuthor> getAuthors(String author) throws IOException {
        return getMatchingValuesInTag("author", author, ElementAuthor.class);
    }

    /**
     * Returns the citation count of a given publication in a given year. Derived from DBLP.
     * @param article
     * @param year
     * @return
     * @throws IOException
     */
    public int getCitationCount(ElementArticle article, int year) throws IOException {

        checkYear(year);
        if (cache.contains(article.getValue(), Integer.valueOf(year))) {
            return cache.get(article.getValue(), Integer.valueOf(year));
        }
        
        ReaderGoogleScholar gs = new ReaderGoogleScholar();
        
        // If we did not find an id, there are no citations
        String id = gs.getId(article.getValue());
        if (id == null) {
            cache.put(article.getValue(), Integer.valueOf(year), 0);
            return 0;
        }
        
        // Retrieve and convert
        String count = gs.getCitationCount(id, String.valueOf(year), String.valueOf(year));
        try {
            int icount = Integer.valueOf(count);
            cache.put(article.getValue(), year, icount);
            return icount;
        } catch (Exception e) {
            throw new IOException("Error parsing Google Scholar. Locked out?");
        }
    }

    /**
     * Returns a list of all available conferences. Derived from DBLP.
     * @param conference
     * @return
     * @throws IOException
     */
    public List<ElementConference> getConferences(String conference) throws IOException {
        return getMatchingValuesInTag("booktitle", conference, ElementConference.class);
    }
    
    /**
     * Returns the impact factor of a given conference in a given year. Based on Google Scholar.
     * @param conference
     * @param year
     * @return
     * @throws IOException
     */
    public double getImpactFactor(ElementConference conference, int year) throws IOException {

        checkYear(year);
        int year1 = year - 2;
        int year2 = year - 1;

        List<ElementArticle> titles1 = this.getArticlesInConference(conference, year1);
        List<ElementArticle> titles2 = this.getArticlesInConference(conference, year2);

        titles1.addAll(titles2);

        int citations = 0;
        for (ElementArticle title : titles1) {
            citations += this.getCitationCount(title, year);
            this.persist();
        }

        return (double) citations / (double) titles1.size();
    }

    /**
     * Returns the impact factor of a given journal in a given year. Based on Google Scholar.
     * @param journal
     * @param year
     * @return
     * @throws IOException
     */
    public double getImpactFactor(ElementJournal journal, int year) throws IOException {

        checkYear(year);

        int year1 = year - 2;
        int year2 = year - 1;

        List<ElementArticle> titles1 = this.getArticlesInJournal(journal, year1);
        List<ElementArticle> titles2 = this.getArticlesInJournal(journal, year2);

        titles1.addAll(titles2);

        int citations = 0;
        for (ElementArticle title : titles1) {
            citations += this.getCitationCount(title, year);
            this.persist();
        }

        return (double) citations / (double) titles1.size();
    }

    /**
     * Returns a list of all available journals. Derived from DBLP.
     * @param journal
     * @return
     * @throws IOException
     */
    public List<ElementJournal> getJournals(String journal) throws IOException {
        return getMatchingValuesInTag("journal", journal, ElementJournal.class);
    }

    /**
     * Persists all data stored in the cache
     * @throws IOException
     */
    public void persist() throws IOException {
        cache.persist();
    }
    
    /**
     * Checks whether the given integer is a valid year
     * @param year
     */
    private void checkYear(int year) {
        if (year < 1800 || year > 2100) {
            throw new IllegalArgumentException("Invalid year: " + year);
        }
    }

    /**
     * Returns all values of the given tag that contain that contain the given value.
     * @param tag
     * @param value
     * @param clazz 
     * @return
     * @throws IOException
     */
    private <T extends Element> List<T> getMatchingValuesInTag(String tag, String value, Class<T> clazz) throws IOException {
        
        // Collect
        List<String> list = new ArrayList<String>(new ReaderDBLP().collect(tag));
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            if (!iter.next().contains(value)) {
                iter.remove();
            }
        }
        Collections.sort(list);
        
        // Create result
        List<T> result = new ArrayList<T>();
        for (String element : list) {
            try {
                result.add(clazz.getConstructor(String.class).newInstance(element));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * Returns all values of the given tag that contain that contain the given value.
     * @param tag1
     * @param value1
     * @param tag2
     * @param value2
     * @param tag3
     * @param tag4
     * @param class
     * @return
     * @throws IOException
     */
    private <T extends Element> List<T> getMatchingValuesInTag(String tag1, String value1, String tag2, String value2, String tag3, String tag4, Class<T> clazz) throws IOException {
        
        // Collect
        List<String> list = new ArrayList<String>(new ReaderDBLP().collect(tag1, value1, tag2, value2, tag3, tag4));
        list.remove("");
        Collections.sort(list);

        // Create result
        List<T> result = new ArrayList<T>();
        for (String element : list) {
            try {
                result.add(clazz.getConstructor(String.class).newInstance(element));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}