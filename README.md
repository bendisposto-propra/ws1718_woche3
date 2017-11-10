# Containerisierung
In dieser Aufgabe geht es darum ein System, bestehend aus einer [MySQL](https://www.mysql.com/) Datenbank, einem [NGINX](https://www.nginx.com/) Reverse Proxy und einer [Spring Boot](https://projects.spring.io/spring-boot/) Webanwendung aufzusetzen. Die einzelnen Module sind implementiert und zum Großteil auch konfiguriert.

Ihre Aufgabe ist es, eine ```docker-compose.yml``` Datei sowie ```Dockerfile``` Dateien für die Projekte (wo es nötig ist) zu schreiben. Am Ende soll man das System mit ```docker-compose up``` hochfahren und mit ```http://localhost``` auf die Anwendung zugreifen können. Am Ende soll pro Unterverzeichnis ein Container laufen.

In dieser Datei finden Sie auch einige Hintergrundinformationen zur Implementierung, Sie brauchen aber kein vollständiges Verständnis der Funktionsweise der Anwendung um die Konfiguration der Container vorzunehmen. Die Teile, die für die Containerkonfiguration wichtig sind, sind hervorgehoben. __Für die Datenbank ist der gesamte Abschnitt relevant.__

# Reverse Proxy
Der Reverse Proxy leitet hier die Anfragen des Browsers einfach nur weiter, insofern hätte man hier auch darauf verzichten können. Reverse Proxies spielen aber eine wichtige Rolle, sie werden normalerweise eingesetzt um

- Anfragen intern an mehrere Services weiterzuleiten und die Antworten dann zu integrieren. Zum Beispiel könnte ein Webshop aus einem Produktkatalog und einer Warenkorb-Anwendung bestehen, der Proxy würde eine Kundenanfrage annehmen, die beiden Services aufrufen und das Ergebnis in einer html Seite an den Client schicken.
- SSL Terminierung vorzunehmen. Clients würden den Proxy mit https addressieren, der Proxy würde aber dann intern per http mit den Anwendungen kommunizieren. Das dient der Beschleunigung von Anfragen. Voraussetzung ist ein sicheres internes Netzwerk.
-  die Last auf mehrere Server zu verteilen. Der Proxy fungiert dann als Loadbalancer und verteilt Anfragen von Clients auf verschiedene Server.
- einen Cache vorzuhalten. Der Proxy kann statische Inhalte, wie zum Beispiel Bilder direkt ausliefern und damit die Webanwendung entlasten.

__Der Proxy muss den Port 80 nach außen weiterleiten (z.B auf Port 80, aber Sie können da frei wählen). Es müssen ausserdem die beiden Dateien in dem ```proxy/conf``` Verzeichnis in den Container in den Ordner ```/etc/nginx/``` gebunden werden.__

# Datenbank

Es soll eine MySQL Datenbank in Version `5.7.19` verwendet werden. Das Verzeichnis ```/docker-entrypoint-initdb.d``` im Container soll mit dem Verzeichnis ```database/db``` verknüpft werden. Dadurch wird das SQL Skript, das sich in ```database/db``` befindet automatisch in der Datenbank ausgeführt. Das Skript legt die notwendige Tabelle für die Anwendung an. Das Verzeichnis ```/var/lib/mysql``` aus dem Container soll auf das Verzeichnis ```database/data``` abgebildet werden. Dort speichert MySQL die Datenbank. Die Datenbank soll auch zwischen dem Bauen von Containern erhalten bleiben, daher muss man sie nach aussen auf den Hostrechner mappen.

Für den Container werden folgende Umgebungsvariablen benötigt:
- `MYSQL_ROOT_PASSWORD`
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`

Der Wert für `MYSQL_DATABASE` muss auf `teilnehmer`gesetzt werden, die anderen Werte könen Sie selber wählen, müssen aber darauf achten, dass Sie zum Beispiel Benutzernamen und Passwort konsistent in Datenbank und Webapplikation verwenden.

# Webapplikation

Die Webapplikation ist mit Hilfe des Spring Frameworks implementiert worden. Sie dient dazu eine Tabelle von Github Benutzernamen auf Studierende der HHU abzubilden. Die Anwendung ist nur eine Übung für Docker, es fehlen eine ganze Reihe von wichtigen Dingen (z.b. ein Sicherheitskonzept, Gültigkeitsprüfungen der Eingaben, etc.).

Das Grundgerüst der Anwendung wurde mit Hilfe des [Spring Initializr](http://start.spring.io/) erstellt, als Dependencies wurden ```Web```, ```Thymeleaf```, ```JPA``` und ```MySQL``` ausgewählt.

## Web

Die ```Web``` Komponente stellt einen Webserver und das Spring MVC Framework bereit. Das MVC Framework erledigt einiges an Infrastruktur, die man sonst schreiben müsste. In der Klasse ```TeilnehmerController``` wird das Framework verwendet, um die drei Endpunkte ```/```, ```/add``` und ```addform``` bereit zu stellen.

## Thymeleaf

Thymeleaf ist eine Templating-Bibliothek, die genutzt wird, um Antworten der Anwendung zusammenzubauen. Ein Beispiel ist die Datei ```srv/main/resources/templates/teilnehmerlist.html```. Dort befindet sich das Seitenlayout, aber auch etwas Templating-Code:

```
<tr th:each="t,iterationStatus  : ${teilnehmerliste}">
					<td th:text="${t.github}">Github</td>
					<td th:text="${t.realname}">Name</td>
					<td th:text="${t.matrikelnummer}">Matrikel</td>
					<td th:text="${t.login}">Login</td>
</tr>
```

Schauen Sie sich dazu auch die Methode ```getAllUsers``` aus ```TeilnehmerController``` an.

__Die Webanwendung wird mit Hilfe von Gradle gebaut, der Gradle Wrapper ist mitgeliefert, Sie können aber auch Gradle im Container installieren. Die Anwendung wird entweder mit Gradle build gebaut und das `jar` file gestartet, Alternativ können Sie aber auch den ```bootRun``` Task verwenden. Die Webanwendung läuft auf Port `8080`, dieser soll aber nicht nach aussen gegeben werden, da der Proxy alle Anfragen weiterleitet. Es müssen folgende Umgebungsvariablen gesetzt sein: `DB_HOST`, `DB_USER` und `DB_PASSWORD`. Die letzteren beiden müssen mit den Variablen MYSQL_USER und MYSQL_PASSWORD aus der Datenbank-Konfiguration übereinstimmen. Als Basisimage empfielt sich ein openjdk in Version 8.__

## JPA

Die Java Persistency API ist eine Schnittstelle um zwischen Objekten und relationalen Datenbanken (wie MySQL) zu vermitteln. In Spring wird diese Schnittstelle standardmäßig durch das [Hibernate](http://hibernate.org/orm/) Framework zur Verfügung gestellt. Mit Hilfe von Hibernate können auf einfache Weise Objekte in die Datenbank geschrieben und wieder eingelesen werden werden. In der Webanwendung ist das Mapping ziemlich direkt. Das Objekt, das in die Datenbank geschrieben wird ist ```Teilnehmer```, um die Teilnehmer zu verwalten wird eine Standardklasse von Spring verwendet.

## MySQL

Das ist der Datenbanktreiber für die MySQL Datenbank.
