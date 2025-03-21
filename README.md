# Prodotto

MONPNRR (Strumenti Monitoraggio Progetti PNRR)

## Versione

1.1.0

## Descrizione del prodotto

Nell’ambito del programma di finanziamento dell’Unione Europea denominato PNRR (Piano Nazionale di Ripresa e Resilienza), con riferimento in particolare ai progetti legati al comparto della Sanità, Regione Piemonte - nel seguito RP - ha incaricato il CSI di studiare la realizzazione di un applicativo software ad uso delle Aziende Sanitarie - nel seguito AS - e degli operatori regionali, che abbia un duplice obiettivo:

1.	Offrire una piattaforma per la compilazione di un elenco di domande/requisiti specifici per ogni progetto in carico ad un’AS, nel seguito indicato come “Checklist”.
2.	Offrire agli utenti regionali un cruscotto per il monitoraggio e la validazione delle Checklist compilate al punto precedente.
I progetti legati al PNRR in carico alle AS vengono censiti sulla piattaforma ministeriale ReGiS tramite la codifica univoca CUP (Codice Unico di Progetto) e per ognuno è prevista una specifica Checklist da compilare, con la quale verificare che il responsabile RUP (Responsabile Unico di Progetto) abbia svolto tutti i passaggi previsti nell’iter di svolgimento del progetto stesso, secondo la normativa di legge prevista. Tuttavia, il sistema ReGiS non ha informatizzato la fase di compilazione delle Checklist.
L’applicativo è un sistema che aiuta i RUP a tenere sotto controllo la Checklist di ogni progetto che hanno in carico, attraverso la sua compilazione progressiva nel corso del tempo. Inoltre, consentirà a RP di monitorare in parallelo l’avanzamento delle attività svolte indicate in ogni Checklist, e validare ogni singolo elemento presente nell’elenco.

In questo modo RP potrà effettuare un controllo incrociato tra le operazioni svolte dai RUP su ReGiS e quanto da essi dichiarato in seguito nella compilazione delle Checklist.

Elenco componenti:

* [MONPNRRBE](monpnrrbe) Componente backend contenente API per i servizi per la gestione dei progetti PNRR
* [MONPNRRFE](monpnrrfe) Componente frontend per la webapp per la gestione dei progetti PNRR

## Configurazioni iniziali

Si rimanda ai readme delle singole componenti

* [MONPNRRBE](monpnrrbe/README.md)
* [MONPNRRFE](monpnrrfe/README.md)

## Prerequisiti di sistema

Server Web:
Apache 2.4

Framework: 
Angular versione 13.3

Application Server:
Spring Boot environment version 3.2.12
on OpenJDK v17 Adoptium

Tipo di database:
PostgreSQL v15

## Dipendenze da sistemi esterni

### Sistema di autenticazione

Il sistema di autenticazione su cui si basa il MONPNRR è esterno al presente prodotto ed è basato sul framework SHIBBOLETH composto da Service Provider e Identity Provider. 
Gli operatori che accedono ai servizi online della Sanità regionale piemontese si basano su 
- credenziali della PA Piemontese
- certificati digitali

### Sistema di profilazione 

Il sistema di profilazione su cui si basa il MONPNRR è esterno al presente prodotto ed è basato sul prodotto [LCCE](https://github.com/regione-piemonte/lcce)

## Versioning (Obbligatorio)

Per il versionamento del software si usa la tecnica Semantic Versioning (http://semver.org).

## Authors

* Paola Ronco
* Simona Massa

## Copyrights

“© Copyright Regione Piemonte – 2025”

## License

EUPL-1.2 Compatibile
Vedere il file LICENSE.txt per i dettagli.