Bibliometrics
====

This project integrates bibliometric data from DBLP and Google Scholar.

The main purpose was to compute 2-year impact factors of journals based on the Google Scholar corpus and
to compare them to the official values from Thomson Reuters/ISI. The former corpus is maintained by machines and covers 
a large proportion of the world's scientific literature. The latter corpus is curated manually and thus more accurate 
but it covers much fewer publications. By integrating DBLP and Google Scholar it is possible to calculate impact factors of 
journals using a larger corpus and also to compare them with impact factors of conference proceedings, which are typically 
not covered by ISI. Because of its use of the DBLP library, this project is limited to evaluating computer science literature.

Results
------

As an example, I have computed impact factors for a few journals and conferences from the areas of biomedical informatics,
database systems and data privacy.

The following figure compares the official 2-year impact factors of journals to impact factors calculated with this library.
Impact factors for the Journal of the American Medical Informatics Association (JAMIA), the Journal of Biomedical Informatics (JBI)
and BMC Medical Informatics and Decision Making are taken from Thomson Reuters/ISI. The impact factor for Transactions on Data Privacy
(TDP) is based on the Scimagojr corpus. 

[![Result-1](https://raw.github.com/prasser/biblliometrics/master/img/img1.png)](https://raw.github.com/prasser/biblliometrics/master/img/img1.png)

The following figure shows some impact factors for conference proceedings, specifically for the International Conference on Data Engineering
(ICDE), the International Conference on Very Large Data Bases (VLDB), the International Conference on Extending Database Technology (EDBT),
the International Conference on Privacy, Security, Risk and Trust (PASSAT) and the International Symposium on Computer-Based Medical Systems (CBMS).
The data for PASSAT is for 2013, while the data for all other conferences is for 2014.

[![Result-2](https://raw.github.com/prasser/biblliometrics/master/img/img2.png)](https://raw.github.com/prasser/biblliometrics/master/img/img2.png)

Setup
------
Download dblp.xml and dblp.dtd from http://dblp.uni-trier.de/xml/ and put them into the data folder.

Documentation
------
Online documentation can be found [here](https://rawgithub.com/prasser/biblliometrics/master/doc/index.html). 

License
------
EPL 1.0