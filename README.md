# Milostný dopis
Semestrální práce ZČU KIV/UPS
Zadáním bylo vytvořit server a klienta pro síťovou hru.

## Server

Server je implementovaný pomocí jazyka C++ pro platformu Linux, pro komunikaci na síti využívá implementaci BSD soketů. 

Pro automatický překlad je ve složce build připravený Makefile pro nástroj make. Překlad bude proveden pomocí překladače gcc (g++)

Pro překlad a spuštění serveru:
    
    cd ./build/
    make
    ./MilostnyDopisServer -r

## Klient

Klient je implementovaný pomocí jazyka Java s grafickým uživatelským rozhraním JavaFX (verze Javy 8). K logování je použita knihovna Log4J a k provádění jednotkových testů je využita knihovna JUnit. Pokud se nechcete zapojit do vývoje postačí stáhnout soubor `client.jar` z aktuálního release.([Releases](https://github.com/vairad/zcu-ups/releases))

Pro pro automatický překlad je ve složce MilostnyDopisClient připravený souboru build.xml, pro nástroj Ant

Pro překlad a spuštění klienta je třeba nejprve do složky ./MilostnyDopisClient/lib/ umístit využívané knihovny.
Sekvence příkazů (pro překlad a spuštění):
     
     cd ./MilostnyDopisClient/
     ant runJar


### Využívané knihovny

Jednotkové testy (JUnit tests):

* hamcrest-core-1.3.jar
* junit-4-12.jar

Logování:

* log4j-api-2.3.jar
* log4j-core-2.3.jar
