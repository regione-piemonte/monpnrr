# Repository per la componente MONPNRRBE del prodotto MONPNRR
Questo è il repository per lo sviluppo della componente monpnrrbe in tecnologia spring-boot con la reference per lo sviluppo API.

Il progetto spring-boot è già stato inizializzato con il nome della componente e si basa sulla versione di spring-boot  __3__ , maven JDK Adoptium Temurin 17.

## Implementazioni di default e modalità di sviluppo

### Sviluppo contract first

Il progetto è impostato per lo sviluppo in modalità contract-first:
* nella cartella ```src/main/resources``` è presente il file monpnrrbe_v1.1.0.yaml contenente le specifiche openapi 3, contenente la lista delle api implementate sulle funzionalità specifiche dell'applicativo
* il pom.xml prevede l'utilizzo del plugin swagger (customizzato CSI) per la generazione delle sole interfacce JAX-RS a partire da tale file di specifiche. La generazione avviene in fase di compilazione (quindi può essere scatenata con il comando ```mvn clean compile```)
* il generatore non genera invece le classi di implementazione delle API, che devono essere definite manualmente ed implementare le interfacce JAX-RS (vedere esempio ```DecodificheApiImpl```)
* le api sono definite sotto il prefisso ```/api/v1```
* sono predefinite (ed implementate) le seguenti api di base:
  * ```/api/v1/decodifiche/*```, espone alcune api per la restituzione al frontend di liste di decodifica impostate sul database.
  * ```/api/v1/login```, espone l'api che serve per recuperare profili e funzionalità dell'utente loggato attraverso l'interfacciamento con il configuratore.
  * ```/api/v1/progetti/*```, espone le api per la gestione dei progetti PNRR (lista di progetti, checklist di un progetto e relativa modifica, recupero file pdf e csv.


Il progetto è impostato per esporre una implementazione minimale delle API strumentali per:
* health check:
  * status: ```/api/v1/status```

E' possibile estendere l'implementazione a seconda delle esigenze specifiche.

### Eseguire l'applicativo in locale
Abilitare un tunnel con Putty per il database.
Run As -> Run Configurations -> Spring Boot App -> sotto il tab Arguments / VM arguments impostare: 
-Dspring.profiles.active=local
Il file application.yml deve essere configurato impostando i parametri in base all'ambiente di installazione.

