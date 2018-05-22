# Digital Humanities Journal Metadata Federation (DHJMF)

The DHJMF offers a web crawling service that automatically imports newly published articles from Digital Humanities journals into the DHJMF BibSonomy group (https://www.bibsonomy.org/group/dhjmf).

RSS feeds from specified journals (see below) are analyzed and all articles contained in these feeds are scraped via the BibSonomy Scraping Service. The data is saved in different files for documentation purposes and afterwards uploaded to BibSonomy.

Problems during execution of the crawling service are documented as well, so that they can be inspected and corrected by hand. (Possible problems include missing information in RSS feeds, wrong RSS entries, moved links, etc.)


## Journals
So far, the following journals are supported:
* Zeitschrift für digitale Geisteswissenschaften (ZfdG) (http://zfdg.de/)
* Umanistica Digitale (https://umanisticadigitale.unibo.it/)


## Dependencies
The DHJMF web crawler depends on some other applications:
* BibSonomy Publication Sharing System (https://www.bibsonomy.org/)
* BibSonomy Scraping Service (https://scraper.bibsonomy.org/)

During development of new scrapers, it is necessary to have these applications running on your local machine. In production mode, you can use the publicly available interfaces/APIs as provided by the above mentioned applications.

## Deployment
The DHJMF crawling service needs to run on a server with at least JRE 8.0. Localhost is just fine; if you want to regularly run the application, a cron job possibly is the best option.

All configuration is done in the config.properties file in the resources directory. Especially, you need a BibSonomy account and the API key for uploading posts to BibSonomy.

## Adding new journals
### Scrapers
See the BibSonomy wiki for information about setup and usage of the BibSonomy source code (https://bitbucket.org/bibsonomy/bibsonomy/wiki/development/Setup).

The basic workflow for adding new scrapers (i.e., new journals) is as follows:
* Set up the BibSonomy Scraping Service on your local machine (see wiki and the explanations at the bottom of this page).
* Develop new scrapers; usually, you should be able to use already existent Scrapers or to configure them according to your needs. Be inspired by the already included Digital Humanities Scrapers.
* When everything is working locally, send a Pull Request for your changes to the BibSonomy Bitbucket repository.
* After the BibSonomy team has accepted your request, the scrapers are available via the BibSonomy Scraping Service.

### Crawling Service
New journals need to be integrated into the DHJMF crawling service as well. The source code is commented comprehensively, so it should be easy to configure and add new journals to the system.

To this day, only RSS feeds can be crawled and analyzed - feel free to add more sources for article information, if necessary.

<hr/>

## Brief instruction for setting up BibSonomy in Eclipse
### General
* Follow this instruction: https://bitbucket.org/bibsonomy/bibsonomy/wiki/development/Setup
* After forking the repo to your Bitbucket account, create a new branch in your own BibSonomy repo.
* Clone your BibSonomy repo to Eclipse.
* Develop, commit and push on the new branch.
* When ready, send a pull request to BibSonomy/default branch.
* A tutorial for the interaction Git <-> Elipse can be found here: http://www.vogella.com/tutorials/EclipseGit/article.html#git-support-for-eclipse

### Single steps in Eclipse
* Create a new Mercurial project and put it in a new folder on your local machine.
* Use "Import Maven Project" to import "BibSonomy Scraper" and "BibSonomy Scraping Service" from the whole BibSonomy project (as cloned in a previous step) to your Eclipse workbench.
* Work with these projects. All changes are automatically added to the BibSonomy parent project (the one you cloned) and will be included when committing and pushing to your BibSonomy repo.

When testing on localhost: 
* Compile newly added scrapers with "Maven install" and run "Maven install" with the scraping service afterwards.
* A WAR file will be created. Rename this file to "ROOT.war" and deploy it on your server (for example Tomcat). Now the scraping service should be available under "localhost:8080" in your browser.
* When facing problems with Tomcat, consulting the log files (usually in /usr/share/tomcat/log) may help.

### Problems that may arise
* If Tomcat is telling you that dependencies are missing, you need to add them manually to the WEB-INF/lib folder of the deployed WAR file (happened e.g. with "commons logger" and "log4j").
