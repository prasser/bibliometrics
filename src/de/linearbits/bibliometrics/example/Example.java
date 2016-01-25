package de.linearbits.bibliometrics.example;

import java.io.IOException;

import de.linearbits.bibliometrics.Bibliometrics;
import de.linearbits.bibliometrics.ElementConference;
import de.linearbits.bibliometrics.ElementJournal;

/**
 * Example for how to use the bibliometrics library
 * @author Fabian Prasser
 *
 */
public class Example {

    public static void main(String[] args) throws IOException {

        Bibliometrics bibliometrics = new Bibliometrics();
        
        ElementJournal JAMIA =  bibliometrics.getJournals("JAMIA").get(0);
        ElementJournal JBI =  bibliometrics.getJournals("Journal of Biomedical Informatics").get(0);
        ElementConference CBMS = bibliometrics.getConferences("CBMS").get(0);
        ElementConference EDBT = bibliometrics.getConferences("EDBT").get(0);
        ElementConference PASSAT = bibliometrics.getConferences("SocialCom/PASSAT").get(0);
        
        System.out.println("2-year Impact Factor of JAMIA in 2014: " + bibliometrics.getImpactFactorOfJournal(JAMIA, 2014));
        System.out.println("2-year Impact Factor of JBI in 2014: " + bibliometrics.getImpactFactorOfJournal(JBI, 2014));
        System.out.println("2-year Impact Factor of CBMS in 2014: " + bibliometrics.getImpactFactorOfConference(CBMS, 2014));
        System.out.println("2-year Impact Factor of EDBT in 2014: " + bibliometrics.getImpactFactorOfConference(EDBT, 2014));
        System.out.println("2-year Impact Factor of SocialCom/PASSAT in 2013: " + bibliometrics.getImpactFactorOfConference(PASSAT, 2013));
    }
}
