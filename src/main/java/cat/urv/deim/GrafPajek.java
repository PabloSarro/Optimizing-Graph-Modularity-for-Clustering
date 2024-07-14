package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.VertexNoTrobat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

// --------------------------------------------------------------------------------------------------------------------------------
// ------------------------------------- L'EXPLICACIÓ DEL PROGRAMA ES TROBA A L'ARXIU README --------------------------------------
// --------------------------------------------------------------------------------------------------------------------------------

public class GrafPajek {

    // Atributs de la classe GrafPajek:
    // Graf que emmagatzema els vèrtexs del graf i les seves adjacències.
    Graf<Integer,Integer> graf;
    // Llista (de llistes) que, en cada posició, desa una llista amb els vèrtexs que es troben en la comunitat de dita posició.
    ArrayList<LlistaNoOrdenada<Integer>> llista_coms;
    // Llista que, en cada posició, desa el nombre d'adjacències que hi ha dins de cada comunitat (desa el paràmetre: 'Lc').
    ArrayList<Integer> adj_coms; 

    // Constructor de la classe GrafPajek que llegeix les dades d'un arxiu. Aquest és el graf "pare", i es farà servir per desar les dades de l'arxiu '.net'.
    public GrafPajek(int mida, String filePath) throws IOException {
        
        graf = new Graf<Integer,Integer>(mida);
        // S'inicialitza la classe Random (útil per assignar una comunitat aleatòria a cada vèrtex).
        Random rand = new Random();

        try {
            FileReader lectura = new FileReader(filePath);          // Obrim l'arxiu en mode lectura,
            BufferedReader contingut = new BufferedReader(lectura); // definim la variable que llegirà les dades,
            String dades = "";
            boolean llegir_arestes = false;
            
            while ((dades = contingut.readLine()) != null) {

                if (dades.startsWith("*Vertices") || dades.startsWith("*vertices")) {
                    // Es llegeix el nombre de vèrtexs.
                    int num_vertexs = Integer.parseInt(dades.split("\\s+")[1]);

                    // I s'inicialitzen els atributs.
                    llista_coms = new ArrayList<LlistaNoOrdenada<Integer>>(num_vertexs);
                    // Els vèrtexs aniran de 1 fins a N.
                    for (int i = 1; i <= num_vertexs; i++) {
                        llista_coms.add(new LlistaNoOrdenada<>());
                    }
                    adj_coms = new ArrayList<>(num_vertexs);
                    // Mentre que les comunitats aniran de 0 fins a N-1 (per conveniència).
                    for (int i = 0; i < num_vertexs; i++) {
                        adj_coms.add(0);
                    }

                    // S'inclouen els vèrtexs al graf.
                    for (int vert = 1; vert <= num_vertexs; vert++) {
                        int comun = rand.nextInt(num_vertexs); // S'escull un valor aleatori entre 0 i N-1 per la comunitat de cada vèrtex (on N = nombre de vèrtexs del graf).
                        graf.inserirVertex(vert,comun);        // S'insereix el vèrtex amb aquesta comunitat al graf.
                        llista_coms.get(comun).inserir(vert);  // I s'insereix el vèrtex a la llista de vèrtexs de la seva comunitat.
                    }
                } else if (dades.startsWith("*Edges") || dades.startsWith("*edges")) { // D'aquesta manera, s'ignora: "*Arcs". Com que el graf no és dirigit, no ens importen les arestes dirigides.
                    llegir_arestes = true;
                } else if (llegir_arestes) { // A partir d'ara es llegeixen les arestes del graf.
                    // Se separa cada línia en trossos.
                    String[] parts = dades.trim().split("\\s+");
                    // El primer i segon tros, son els vèrtexs que son adjacents.
                    int ver1 = Integer.parseInt(parts[0]);
                    int ver2 = Integer.parseInt(parts[1]);
                    // S'afegeix l'aresta al graf.
                    int comun1 = consultarComunitat(ver1);
                    int comun2 = consultarComunitat(ver2);
                    if ((comun1 == comun2)&&(!existeixAresta(ver1,ver2))) { // Si les comunitats son iguals i no existeix ja l'aresta en el graf (això darrer s'imposa per si de cas l'arxiu '.net' conté per separat les arestes 1-->2 i 2-->1).
                        int adjacencies_comunitat = adj_coms.get(comun1);   // Obtenir les adjacències d'aquella comunitat.
                        adj_coms.set(comun1,adjacencies_comunitat+1);       // I actualitzar-lo en 1 més.
                    }
                    graf.inserirAresta(ver1,ver2);                          // I s'insereix l'aresta al graf.
                }
            }
            contingut.close();
        } catch (IOException | ElementNoTrobat | VertexNoTrobat exc) {   // Es comprova si hi ha algun error en la lectura de l'arxiu i si l'arxiu existeix.
            System.out.println("Hi ha hagut un error durant la càrrega de les dades. Torneu-ho a provar, si us plau");
            exc.printStackTrace();    // Excepció de Java que ens diu què ha passat, i on en el codi ha passat. Es podria escriure
                                      // qualsevol altre missatge d'error, però amb aquest s'obté informació de què ha anat malament.
        }
    }

    // Constructor d'un graf auxiliar que emmagatzema dues comunitats. Aquest és el graf "fill", i es farà servir en la primera fase (mètode ModificarModularitat) en la variació de la comunitat d'un vèrtex.
    public GrafPajek(GrafPajek grafPajek, int comunitat_antiga, int comunitat_nova) {      
        // Es copien al graf auxiliar els vèrtexs de cadascuna de les dues comunitats (antiga i nova).

        // Primer, s'inicialitzen les llistes.
        llista_coms = new ArrayList<>(2);
        llista_coms.add(new LlistaNoOrdenada<>());
        llista_coms.add(new LlistaNoOrdenada<>());
        // Després, s'agafen les llistes amb els vèrtexs a copiar.
        LlistaNoOrdenada<Integer> llista_comunitat_ant = grafPajek.llista_coms.get(comunitat_antiga);
        LlistaNoOrdenada<Integer> llista_comunitat_nova = grafPajek.llista_coms.get(comunitat_nova);
        // I finalment, es copien al nou graf auxiliar.
        // IMPORTANT: en aquest graf auxiliar, s'estableixen les posicions 0 per l'antiga comunitat, i 1 per la nova.
        for (int vertex_com_ant : llista_comunitat_ant) {
            llista_coms.get(0).inserir(vertex_com_ant);
        }
        for (int vertex_com_nova : llista_comunitat_nova) {
            llista_coms.get(1).inserir(vertex_com_nova);
        }

        // Ara es copien les adjacències dins de cada comunitat (antiga i nova, recordant el conveni de posicions establert just abans):

        // Primer, s'inicialitzen les posicions.
        adj_coms = new ArrayList<>(2);
        adj_coms.add(0);
        adj_coms.add(0);
        // Després, s'agafen els valors de les adjacències a copiar.
        int adj_comunitat_ant = grafPajek.adj_coms.get(comunitat_antiga);
        int adj_comunitat_nova = grafPajek.adj_coms.get(comunitat_nova);
        // I finalment, es copien al nou graf auxiliar.
        adj_coms.set(0,adj_comunitat_ant);
        adj_coms.set(1,adj_comunitat_nova);
    }
    
    // Mètode auxiliar per obtenir el grau de tots els vèrtexs d'una llista donada (es farà servir pel càlcul de la modularitat, per trobar el grau de tots els vèrtexs d'una mateixa comunitat).
    public int GrauTotalLlista(LlistaNoOrdenada<Integer> llista) {
        try {
            int grau_llista = 0;
            for (int vert : llista) {
                grau_llista += numVeins(vert);
            }
            return grau_llista;
        } catch (ElementNoTrobat e) {
            System.out.println("Excepció ElementNoTrobat en GrauTotalLlista.");
            return -1;
        }
    }

    // Mètode que calcula la modularitat donat un graf (ja sigui el graf "pare" o el "fill", l'auxiliar). Es fa servir l'expressió que suma per comunitats.
    public double CalcularModularitat(GrafPajek grafPajek) {
        double modularitat = 0;
        double m = grafPajek.numArestes(); // (Es fa que m sigui de tipus 'double' per a que Java reconegui la divisió decimal que es farà més endavant, ja que fent 'int m = ...' la divisió (double) (Lc/m) dóna 0!)
        // Però l'atribut 'llista_coms' del bucle de sota, de qui es? Doncs depèn de com es cridi aquest mètode! Quan es vulgui calcular la Modularitat de tot el graf, es farà grafPajek.CalcularMod.(), i llista_coms serà l'atribut del graf sencer (per tant tindrà tots els vèrtexs de cada comunitat).
        // En canvi, si es crida aquest mètode com graf_aux.CalcularMod.(), aleshores llista_coms serà l'atribut de graf_aux (és a dir, es tindrà graf_aux.llista_coms), i per tant només s'agafaran les comunitats del graf auxiliar (que només seran l'antiga i la nova comunitat, les úniques que influeixen en un canvi de comunitat! Més detalls en la FASE 1).
        for (int coms = 0; coms < llista_coms.size(); coms++) {
            // Nombre d'adjacències dins de cada comunitat.
            int Lc = adj_coms.get(coms);
            // Llista amb els vèrtexs dins de cada comunitat.
            LlistaNoOrdenada<Integer> vertexs_com = llista_coms.get(coms);
            // Suma dels graus de tots els vèrtexs dins de cada comunitat.
            int kc = grafPajek.GrauTotalLlista(vertexs_com);
            double primer_terme = (double) (Lc/m);
            double segon_terme = (double) (kc/(2*m));
            modularitat += primer_terme - Math.pow(segon_terme,2);
        }
        return modularitat;                           // Es retorna el valor de la modularitat obtingut.
    }

    // -------------------------------- MÈTODES AUXILIARS NECESSARIS PER A LES FASES 1 I 2 DEL MÈTODE DE LOUVAIN: --------------------------------
    // Mètode auxiliar que retorna una llista amb totes les comunitats dels veins d'un vèrtex (per tal de saber quins poden ser possibles canvis de comunitat d'aquest vèrtex, ja que si el vèrtex no té adj. a la comunitat C, no té sentit moure'l cap aquí!)
    public LlistaNoOrdenada<Integer> obtenirComunitatsVeines(Integer ver1) throws ElementNoTrobat {
        try {
            return graf.obtenirComunitatsVeines(ver1);
        } catch (VertexNoTrobat e) {
            throw new ElementNoTrobat();
        }
    }
    
    // Mètode auxiliar que retorna el nombre de veins que té un vèrtex dins d'una certa comunitat.
    public int NombreVeinsVertexComunitat(int vertex, int comunitat, GrafPajek grafPajek) throws ElementNoTrobat {
        try{
            // S'inicialitza el nombre de veins a 0.
            int nombre_veins_com = 0;
            // S'agafa la llista amb tots els vèrtexs de la comunitat que es vol observar.
            LlistaNoOrdenada<Integer> llista_comunitat = llista_coms.get(comunitat);
            for (int vert : llista_comunitat) {
                // Si existeix una adjacència entre el vèrtex donat i algun de la llista,
                if (grafPajek.existeixAresta(vert, vertex)) {
                    // és que hi ha una adjacència (almenys) entre el vèrtex i un altre de la comunitat a observar.
                    nombre_veins_com += 1;
                }
            }
            return nombre_veins_com;
        } catch (ElementNoTrobat e) {
            System.out.println("Excepció ElementNoTrobat en el mètode NombreVeinsVertexComunitat");
            throw new ElementNoTrobat();
        }
    }

    // Mètode auxiliar que retorna una llista amb les comunitats d'un graf que contenen vèrtexs (aquelles que no són buides).
    public LlistaNoOrdenada<Integer> ComunitatsNoBuides() {
        LlistaNoOrdenada<Integer> llista_comunitats_no_buides = new LlistaNoOrdenada<>();
        // Com que hi pot haver tantes comunitats com vèrtexs en el graf, hi pot haver N comunitats (de 0 a N-1).
        for (int com = 0; com < numVertexs(); com++) { 
            // Llista amb els vèrtexs de cada comunitat.
            LlistaNoOrdenada<Integer> llista_vert_comunitat = llista_coms.get(com);
            // I si aquesta llista no és buida, és que conté algun vèrtex, i per tant es desa en la llista a retornar.
            if (!llista_vert_comunitat.esBuida()) {
                llista_comunitats_no_buides.inserir(com);
            }
        }
        return llista_comunitats_no_buides;
    }

    // ---------------------------------------- FASE 1 DEL MÈTODE DE LOUVAIN: ----------------------------------------
    public void ModificarModularitat(int num_iter, int max_iter, GrafPajek grafPajek) {
        // S'inicialitza a 0 la variable que comptarà les iteracions de la primera fase en què la modularitat no millora (d'aquesta manera), quan iteracions_sense_canvi > max_iter, s'aturarà la primera fase.
        int iteracions_sense_canvi = 0;
        try {
            // Bucle que s'iterarà 'num_iter' cops.
            for (int i = 0; i < num_iter; i++) {
                int N = numVertexs();
                // S'inicialitza a 0 l'increment de la modularitat (modularitat_nova - modularitat_antiga).
                double Delta_Q_max = 0;
                Random rand = new Random();
                
                // S'escull aleatòriament un vèrtex del graf: "vertex_a_variar_comunitat".
                int vert_a_variar_comunitat = 1+rand.nextInt(N);
                // Desem la comunitat en la que es troba de moment (la que serà la seva antiga comunitat, ja que aquesta es voldrà canviar).
                int comunitat_antiga = consultarComunitat(vert_a_variar_comunitat);
                // Voldrem moure a aquest vèrtex a la "millor" comunitat (la que optimitzi la modularitat). Per tant, inicialitzem la que serà la seva millor comunitat a moure's.
                int millor_comunitat = comunitat_antiga;

                // Ara es calcula el nombre d'adjacències que hi haurà dins de la comunitat antiga un cop es canvii de comunitat al vèrtex "vert_a_variar_comunitat".
                
                // Això es fa de la següent manera:
                // S'agafa el nombre d'adjacències dins la comunitat l'antiga.
                // Com que es vol moure de comunitat al vèrtex (antiga --> nova) se li resta el nombre d'adjacències que té el vèrtex dins de la comunitat antiga.
                // Fent aquesta resta, queda el nombre d'adjacències en la comunitat 0 (l'antiga) un cop fet el canvi de comunitat del vèrtex.
                // Ídem per la nova comunitat, però aquest cop se li hauran de sumar les adjacències del vèrtex dins la comunitat 1 (això es farà més a sota).
                
                // Nombre de veins que té el vèrtex "vert_a_variar_comunitat" dins l'antiga comunitat.
                int nombre_veins_comunitat_antiga = grafPajek.NombreVeinsVertexComunitat(vert_a_variar_comunitat,comunitat_antiga,grafPajek);
                // Nou nombre d'adjacències dins de l'antiga comunitat (es farà servir a sota).
                int nou_nombre_adj_antiga = grafPajek.adj_coms.get(comunitat_antiga) - nombre_veins_comunitat_antiga;

                // Nombre d'adjacències que hi haurà en la millor comunitat un cop fet el canvi.
                int nou_nombre_adj_millor = 0;
                // Llista de possibles comunitats a les quals es pot canviar el vèrtex.
                LlistaNoOrdenada<Integer> comunitats_veines = obtenirComunitatsVeines(vert_a_variar_comunitat);

                // Bucle que observarà l'increment de modularitat per cada possibilitat (per cada canvi).
                for (int comunitat_nova : comunitats_veines) {
                    // Es crea un graf auxiliar (graf "fill") que copiarà els vèrtexs i les adjacències que hi ha dins de l'antiga i nova comunitat.
                    GrafPajek graf_aux = new GrafPajek(grafPajek,comunitat_antiga,comunitat_nova);

                    // Es calcula la modularitat de les dues comunitats (les afectades) ABANS de fer el canvi.
                    // Això és molt eficient, ja que al realitzar un canvi de comunitat d'un vèrtex, només les comunitats antiga i nova afecten al valor total de la modularitat!!!
                    double modularitat_antiga = graf_aux.CalcularModularitat(grafPajek);

                    // Ara es fa el canvi de comunitat d'aquest vèrtex:

                    // En el graf auxiliar, la posició 0 fa ref. a la comunitat antiga, i la posició 1, a la nova.
                    // Primer s'esborra al vèrtex de la comunitat antiga.
                    graf_aux.llista_coms.get(0).esborrar(vert_a_variar_comunitat);
                    // I ara s'insereix aquest en la comunitat nova.
                    graf_aux.llista_coms.get(1).inserir(vert_a_variar_comunitat);

                    // I ara es fa el canvi en les adjacències dins de cada comunitat:
                    // Nombre de veins que té el vèrtex "vert_a_variar_comunitat" dins la comunitat 1 (la nova en el graf auxiliar):
                    int nombre_veins_comunitat_nova = graf_aux.NombreVeinsVertexComunitat(vert_a_variar_comunitat,1,grafPajek);
                    // Nou nombre d'adjacències dins de la nova comunitat.
                    int nou_nombre_adj_nova = graf_aux.adj_coms.get(1) + nombre_veins_comunitat_nova;
                    // I aquí s'actualitzen tal com s'ha esmentat.
                    graf_aux.adj_coms.set(0,nou_nombre_adj_antiga);
                    graf_aux.adj_coms.set(1,nou_nombre_adj_nova);
                    
                    // I ara es calcula la modularitat d'aquestes dues comunitats DESPRÉS de fer el canvi.
                    double modularitat_nova = graf_aux.CalcularModularitat(grafPajek);
                    
                    // Increment de la modularitat
                    double Delta_Q = modularitat_nova-modularitat_antiga;

                    // Aquí es mira si l'increment de la modularitat (fruit d'haver canviat el vèrtex de la seva comunitat antiga a la nova) ha estat el més gran dins de cada canvi.
                    if (Delta_Q > Delta_Q_max) {
                        // En aquest cas, s'actualitza el màxim.
                        Delta_Q_max = Delta_Q;
                        // Ens desem la comunitat que ha donat lloc a la millor modularitat (aquesta serà la millor comunitat per a fer el canvi, la que incrementarà en major grau la modularitat total del graf).
                        millor_comunitat = comunitat_nova;
                        // Juntament amb les noves adjacències de la millor comunitat.
                        nou_nombre_adj_millor = nou_nombre_adj_nova;
                    }
                }
                // Un cop fets tots els canvis possibles d'un vèrtex (de la seva comunitat, a cadascuna de les seves veines), s'aplica el canvi de comunitat a aquella que ha maximitzat l'increment de la modularitat (Delta_Q)
                // (Sempre que el màxim s'hagi produit en una altra comunitat. Si el millor és quedar-te en la teva comunitat, no canvies! Per tant:)
                if (comunitat_antiga != millor_comunitat) {  
                    // S'insereix el vèrtex en la seva nova comunitat.
                    grafPajek.inserirVertex(vert_a_variar_comunitat,millor_comunitat);
                    // S'actualitza la llista de vèrtexs en la comunitat antiga i nova.
                    grafPajek.llista_coms.get(comunitat_antiga).esborrar(vert_a_variar_comunitat);
                    grafPajek.llista_coms.get(millor_comunitat).inserir(vert_a_variar_comunitat);
                    // Juntament amb el nombre d'adjacències de cadascuna d'aquestes dues.
                    grafPajek.adj_coms.set(comunitat_antiga,nou_nombre_adj_antiga);
                    grafPajek.adj_coms.set(millor_comunitat,nou_nombre_adj_millor);
                    // Finalment, com hi ha hagut un canvi, es reinicia el comptador d'iteracions sense canvi a 0.
                    iteracions_sense_canvi = 0;
                } else {
                    // Si no s'ha millorat la modularitat amb cap canvi (i per tant millor_comunitat = comunitat_antiga, tal i com s'ha inicialitzat abans del bucle) s'incrementa en un les iteracions en què no s'ha millorat.
                    iteracions_sense_canvi += 1;
                    // Quan aquest superi el nombre d'iteracions màxim, se surt del mètode.
                    if (iteracions_sense_canvi > max_iter) {
                        System.out.println("Fase 1 finalitzada (motiu: no s'ha aconseguit millorar la modularitat després de "+max_iter+" iteracions).");
                        return;
                    }
                }
            }
            System.out.println("Fase 1 finalitzada (motiu: s'ha arribat a les "+num_iter+" iteracions).");
        } catch (ElementNoTrobat e) {
            System.out.println("Excepció ElementNoTrobat en el mètode ModificarModularitat.");
        }
    }

    // ---------------------------------------- FASE 2 DEL MÈTODE DE LOUVAIN: ----------------------------------------
    public void FusionarComunitats(int num_iter, int max_iter, GrafPajek grafPajek) {
        // S'inicialitza a 0 la variable que comptarà les iteracions de la primera fase en què la modularitat no millora (d'aquesta manera), quan iteracions_sense_canvi > max_iter, s'aturarà la primera fase.
        int iteracions_sense_canvi = 0;
        try {
            // Bucle que s'iterarà 'num_iter' cops.
            for (int i = 0; i < num_iter; i++) {
                // S'agafen les comunitats que "segueixen vives", és a dir, que encara contenen vèrtexs.
                LlistaNoOrdenada<Integer> coms_no_buides = ComunitatsNoBuides();
                // Per cadascuna d'elles, s'explorarà l'increment en la modularitat de fusionar-la amb cadascuna de les altres.
                for (int com_fusio0 : coms_no_buides) {
                    double Delta_Q_max = 0;
                    
                    // Aquí es desarà la millor comunitat amb què la comunitat "com_fusio0" es pot fusionar.
                    int millor_fusio = com_fusio0;
                    
                    // Nombre d'adjacències que hi haurà en la millor comunitat un cop fet el canvi.
                    int nou_nombre_adj_millor_fusio = 0;
                    // Bucle que examina la fusió de com_fusio0 amb cada comunitat 'com_fusio1'.
                    for (int com_fusio1 : coms_no_buides) {
                        // Si les dues son la mateixa, no té sentit fusionar-les, així que es passa a la següent comunitat no buida.
                        if (com_fusio0 == com_fusio1) {
                            continue;
                        }
                        // Es crea un graf auxiliar que copiarà els vèrtexs i les adjacències dins de l'antiga i nova comunitat.
                        GrafPajek graf_aux = new GrafPajek(grafPajek,com_fusio0,com_fusio1);

                        // Es calcula la modularitat de les dues comunitats (les afectades) ABANS de fer el canvi.
                        double modularitat_antiga = graf_aux.CalcularModularitat(grafPajek);

                        // Ara es fa el canvi en les adjacències dins de cada comunitat (es farà que tots els vèrtexs de la comunitat 0 passin a estar a la 1):
                        // Això es farà de manera anàloga a l'anterior. Per cadascun dels vèrtexs en la comunitat 0, s'ha de trobar el nombre d'adjacències que té aquest en la comunitat 1.
                        // D'aquesta manera, el nombre d'adjacències en la com. 1, s'anirà actualitzant pel nombre de veins que cada vèrtex de la com. 0 té en la com. 1.
                        LlistaNoOrdenada<Integer> llista_vert_com_fusio0 = graf_aux.llista_coms.get(0);
                        // Inicialment, el nombre d'adjacències en la comunitat 1 és el següent:
                        int nou_nombre_adj_fusio1 = graf_aux.adj_coms.get(1); // (És a dir, el que hi ha abans de fer cap canvi)
                        // I aquí es fa l'actualització mencionada:
                        for (int vert_com_fusio0 : llista_vert_com_fusio0) {
                            // Nombre de veins de cada vèrtex en la com. 0 dins de la comunitat 1 (la nova en el graf auxiliar).
                            int nombre_veins_comunitat_nova = graf_aux.NombreVeinsVertexComunitat(vert_com_fusio0,1,grafPajek);
                            // S'actualitza el nombre d'adjacències.
                            nou_nombre_adj_fusio1 += nombre_veins_comunitat_nova;

                            // I aquí té lloc la fusió de les comunitats:
                            // S'esborra a cada vèrtex de la comunitat 0.
                            graf_aux.llista_coms.get(0).esborrar(vert_com_fusio0);
                            // I s'insereix cadascun en la comunitat 1.
                            graf_aux.llista_coms.get(1).inserir(vert_com_fusio0);
                        }
                        
                        // Aquí, s'actualitzen les adjacències de cada comunitat tal com s'ha esmentat.
                        graf_aux.adj_coms.set(0,0); // 0 adjacències en la comunitat0, ja que tots els vèrtexs d'aquesta comunitat s'han mogut a la comunitat 1.
                        graf_aux.adj_coms.set(1,nou_nombre_adj_fusio1); 
                        
                        // I ara es calcula la modularitat d'aquestes dues comunitats DESPRÉS de fer el canvi.
                        double modularitat_nova = graf_aux.CalcularModularitat(grafPajek);
                        
                        // Increment de la modularitat
                        double Delta_Q = modularitat_nova-modularitat_antiga;

                        // Aquí es mira si l'increment de la modularitat (fruit d'haver fusionat dues comunitats) ha estat el més gran dins de cada fusió possible.
                        if (Delta_Q > Delta_Q_max) {
                            // Si ha estat així, s'actualitza el màxim de l'increment.
                            Delta_Q_max = Delta_Q;
                            // Ens desem la comunitat que ha donat lloc a la millor modularitat (aquesta serà la millor comunitat per a fer la fusió, la que incrementarà en major grau la modularitat total del graf).
                            millor_fusio = com_fusio1;
                            // Juntament amb les noves adjacències de la millor comunitat.
                            nou_nombre_adj_millor_fusio = nou_nombre_adj_fusio1;
                        }
                    }
                    // Un cop fetes totes les fusions possibles d'una comunitat (d'aquesta cap a la resta), s'aplica la fusió a aquella que ha maximitzat l'increment de la modularitat (Delta_Q)
                    // (Sempre que el màxim s'hagi produit fusionant-ne dues de diferents. Si el millor és no fusionar la comunitat amb cap de les restants, aleshores no es fa res!)
                    if (com_fusio0 != millor_fusio) {  
                        // S'insereix tota la comunitat "com_fusio0" dins de la comunitat "millor_fusio".
                        LlistaNoOrdenada<Integer> llista_vertexs_a_moure = grafPajek.llista_coms.get(com_fusio0);
                        // Per cada vèrtex de la comunitat antiga:
                        for (int vert_a_moure : llista_vertexs_a_moure) {
                            // S'insereixen els vèrtexs amb la seva nova comunitat al graf.
                            grafPajek.inserirVertex(vert_a_moure,millor_fusio);
                            // I s'actualitza la llista de vèrtexs en les dues comunitats.
                            grafPajek.llista_coms.get(com_fusio0).esborrar(vert_a_moure);
                            grafPajek.llista_coms.get(millor_fusio).inserir(vert_a_moure);
                        }
                        // Juntament amb el nombre d'adjacències de cadascuna d'aquestes dues.
                        grafPajek.adj_coms.set(com_fusio0,0);
                        grafPajek.adj_coms.set(millor_fusio,nou_nombre_adj_millor_fusio);
                    } else {
                        // Si no s'ha millorat la modularitat amb cap fusió (i per tant millor_fusio = com_fusio0, tal i com s'ha inicialitzat abans del bucle) s'incrementa en un les iteracions en què no s'ha millorat.
                        iteracions_sense_canvi += 1;
                        // Quan aquest superi el nombre d'iteracions màxim, se surt del mètode.
                        if (iteracions_sense_canvi > max_iter) {
                            System.out.println("Fase 2 finalitzada (motiu: no s'ha aconseguit millorar la modularitat després de "+max_iter+" iteracions).");
                            return;
                        }
                    }
                }
            }
        } catch (ElementNoTrobat e) {
            System.out.println("Excepció ElementNoTrobat en el mètode FusionarComunitats.");
        }
    }

    // Mètode que retorna un arxiu '.clu' amb la comunitat de cada vèrtex.
    public void RetornarArxiu(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            // En la primera línia, s'escriu el nombre de vèrtexs del graf:
            writer.write("*vertices "+ obtenirVertexIDs().numElements() +"\n");
            Iterator<Integer> iter = obtenirVertexIDs().iterator();
            // Ara s'escriu la comunitat de cada vèrtex.
            while (iter.hasNext()) {
                int vert = iter.next();
                int comunitat = consultarComunitat(vert);
                writer.write(comunitat+"\n");
            }
            writer.close();
            System.out.println("L'arxiu "+filename+" s'ha generat correctament: ");
        } catch (IOException e) {
            System.out.println("Hi ha hagut un error durant l'escriptura de les dades. Torneu-ho a provar, si us plau\"" + e.getMessage());
        } catch (ElementNoTrobat e) {
            System.out.println("Excepció ElementNoTrobat en el mètode RetornarArxiu.");
        }
    }

    

    // Mètodes per la creació del graf i per obtenir-ne certa informació
    public void inserirVertex(int ver1, int comun) {
        graf.inserirVertex(ver1, comun);
    }

    public int consultarComunitat(int id) throws ElementNoTrobat {
        try {
            return graf.consultarVertex(id);
        } catch (VertexNoTrobat e) {
            throw new ElementNoTrobat();
        }
    }

    public int numVertexs() {
        return graf.numVertex();
    }

    public ILlistaGenerica<Integer> obtenirVertexIDs() {
        return graf.obtenirVertexIDs();
    }

    public void inserirAmistat(int ver1, int ver2) throws ElementNoTrobat  {
        try {
            graf.inserirAresta(ver1,ver2);
        } catch (VertexNoTrobat e) {
            throw new ElementNoTrobat();
        }
    }

    public boolean existeixAresta(int ver1, int ver2) throws ElementNoTrobat {
        try {
            return graf.existeixAresta(ver1,ver2);
        } catch (VertexNoTrobat e) {
            throw new ElementNoTrobat();
        }
    }

    public int numArestes() {
        return graf.numArestes();
    }
    // Mètode que retorna el grau d'un vèrtex (el nombre de veins, o d'adjacències, d'aquest vèrtex).
    public int numVeins(Integer ver1) throws ElementNoTrobat {
        try {
            return graf.numVeins(ver1);
        } catch (VertexNoTrobat e) {
            throw new ElementNoTrobat();
        }
    }

}
