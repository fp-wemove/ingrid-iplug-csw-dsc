Hier Spezialit�ten bzgl. Mapping im WSV iPlug:
    Geodatenkatalog der Wasser- und Schifffahrtsverwaltung (CSW DSC)
    /ingrid-group:iplug-csw-dsc-wsv 

MUSS bei Update des entsprechenden iPlugs BER�CKSICHTIGT WERDEN !!!
s. auch in-Step Ticket:
    AF-00146 WSV-CSW Download Links anpassen
bzw. Jira:
    https://dev2.wemove.com/jira/browse/GEOPORTALWSV-39

Hier auch noch mal aus dem Ticket:

----------------------------------
Martin Maidhof, 20.03.2013 18:23

Wurde jetzt mit der 3.3 Version neu installiert und komplett umkonfiguriert, so dass nach dem normalen Mapping zus�tzliche Mapper f�r die WSV eingebunden werden k�nnen, die hier z.B. die URLs pr�fen und ersetzen. Daf�r wurde das CSW-DSC iPlug entsprechend erweitert (in den Sourcen).

Suche nach allen Datens�tzen bei denen die URL ersetzt wurde mit:

iplugs:"/ingrid-group:iplug-csw-dsc-wsv" t017_url_ref.url_link:"http://geokat.wsv.bvbs.bund.de"

Doku auch in:
https://itpedia.dlz-it.bvbs.bund.de/confluence/display/GDIGIS/Handbuch+-+GeoPortal.WSV+%28ingrid%29#Handbuch-GeoPortal.WSV%28ingrid%29-iPlugCSWDSC