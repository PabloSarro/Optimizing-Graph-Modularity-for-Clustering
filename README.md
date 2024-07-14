# Explicació del projecte: Implementació d'un algorisme de detecció de comunitats

Es vol implementar un algorisme que permeti **identificar les comunitats en un graf**. Les comunitats son grups de nodes del graf que estan densament connectats entre si, mentre que estan poc connectats amb nodes d'altres grups/comunitats. 

![exemple_comunitats](https://upload.wikimedia.org/wikipedia/commons/f/f4/Network_Community_Structure.svg)

Es pot trobar més informació sobre què és l'estructura de comunitats a la pàgina de la wikipedia: https://en.wikipedia.org/wiki/Community_structure

L'objectiu de l'activitat es crear un mètode que, donada una xarxa que s'haurà de carregar d'un fitxer, retorni la millor subdivisió en comunitats possible. Per a fer això es prenen les següents consideracions:

* La implementació es trobarà programada per grafs amb format: Pajek NET (https://gephi.org/users/supported-graph-formats/pajek-net-format/).

* Només es considerarà el problema per a grafs NO dirigits i NO etiquetats.
  
* Cada vèrtex tindrà un identificador que li servirà de clau, i el valor que es guardi en el vèrtex serà la comunitat a la qual pertany aquest vèrtex (que pot ser un numero enter en el rang 0 a N, on N seria el nombre de nodes).

* Per a mesurar la qualitat d'una separació en comunitats, es fa servir una mètrica que es coneix per Modularitat (https://en.wikipedia.org/wiki/Modularity_(networks)). Aquesta mètrica mesura la densitat de links que hi ha entre els nodes de la comunitat, i amb els nodes d'altres comunitats. Quan mes gran es el valor de la modularitat, millor es la subdivisió en comunitats.

* Aquest es un problema d’optimització NP-hard, amb la qual cosa no es podrà explorar totes les opcions possibles, i en la majoria de casos s'obtindrà un resultat sub-òptim.

* Al final de l’execució, es retornarà el valor màxim de modularitat obtingut, i la classificació de nodes en grups en algun fitxer de sortida (en el cas del Pajek el fitxer de sortida te el format .clu: per exemple, si com a entrada es rep la xarxa zachary.net, es retornarà com a sortida un fitxer anomenat zachary_0.41880.clu, on a dins del fitxer es desaran les particions en comunitats dels vèrtexs).

Finalment, també s'inclouen una sèrie d'exemples de grafs juntament amb exemples d'arxius .clu de sortida, resultat de l'execució del programa principal.


# Resolució del projecte: 

Es fa servir el MÈTODE DE LOUVAIN, un mètode molt eficient per trobar la partició en comunitat de xarxes grans. Aquest mètode, consta de dues fases, les quals es troben implementades en l'arxiu GrafPajek.java. A continuació, s'explica el seu funcionament.

## FASE 1 (variació de comunitat localment):

* Un cop desat el graf, s'agafa un vèrtex del graf de manera aleatòria. Es mira quines son les seves comunitats veines, i
es prova de canviar aquest vèrtex a cadascuna d'aquestes. Finalment, es realitza el canvi a aquella comunitat que ha produit
un major canvi en la modularitat. Es fa així de manera iterativa per un nombre prou elevat d'iteracions (assegurant així haver
passat per tots els vèrtexs del graf) i s'estableix també un màxim d'iteracions, en què s'atura aquesta primera fase si la
modularitat no millora després d'aquest nombre d'iteracions (es diu que aleshores s'ha arribat a un màxim local de la modularitat).

## Fase 2 (variació de comunitat globalment):

* Ara enlloc de variar vèrtexs de comunitat, es varien comunitats senceres; és a dir, per cada comunitat, es prova de fusionar-la amb
cadascuna de les que queden. Es duu a terme la fusió entre les comunitats que maximitzin la modularitat, i si per una comunitat donada
cap fusió maximitza la mod., aleshores s'avança a la següent comunitat. També es repetirà aquest procés un cert nombre d'iteracions, però en aquest cas no és necessari tenir moltes iteracions, ja que en la implementació que s'ha dut a terme d'aquesta fase, es passa per totes les comunitats del graf (cosa que no passava en la fase 1, degut a voler variar la comunitat d'un vèrtex escollit aleatòriament). També es podria haver implementat de tal manera que en cada iteració només es mira la fusió d'una comunitat amb la resta, però això és anàlog a la implementació que s'ha fet (on cada iteració mira totes les possibles fusions de cada comunitat amb la resta).

S'ha desat uns exemples d'execucions a l'inici de l'arxiu Main.java, per tal de tenir una idea del cost computacional (aproximat) de l'algorisme amb cadascun dels Grafs adjunts.

L'arxiu retornat conté un llistat amb les comunitats de cada vèrtex del graf, on el primer nombre es correspon amb la comunitat del primer vèrtex; el segon nombre, la comunitat del segon vèrtex; i així successivament.