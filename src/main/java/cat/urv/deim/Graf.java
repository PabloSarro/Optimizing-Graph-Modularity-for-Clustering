package cat.urv.deim;

import cat.urv.deim.exceptions.VertexNoTrobat;

import java.util.Iterator;

import cat.urv.deim.exceptions.ArestaNoTrobada;
import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;

public class Graf<K extends Comparable<K>,V extends Comparable<V>> implements IGraf<K,V> {

    public HashMapIndirecte<K,NodeVertex<V>> vertexs; // Taula de vèrtexs.
    public int mida_vertexs; // Mida de la taula de vèrtexs.
    public int num_arestes; // Nombre d'arestes del graf.
    public double mod;

    public class NodeVertex<Val> {
        // Atributs de la classe NodeVèrtex
        public V v; // Valor
        public NodeAresta<K> a; // Primera aresta del vèrtex.
        public NodeAresta<K> elem_fant = new NodeAresta<K>(null);
        public int n_are; // Nombre d'arestes del vèrtex.
        // public NodeVertex<K,V,NodeAresta> seg; // Seguent vèrtex en una mateixa posició (en cas de col·lisions)

        public NodeVertex(V valor) {
            // Constructor de la classe NodeVèrtex
            this.v = valor;
            this.a = elem_fant; // Quan es crea un nou vèrtex, inicialment aquest tindrà com a primera aresta "elem_fant", una posició que fa la funció de l'element fantasma en una llista.
            n_are = 0; // Inicialment, no té arestes.
        }
    }

    public class NodeAresta<Key extends Comparable<K>>{
        // Atributs de la classe NodeAresta
        public K k; // Clau del vèrtex adjacent
        public NodeAresta<K> seg; // Següent aresta

        public NodeAresta(K clauadj) {
            // Constructor de la classe NodeAresta
            this.k = clauadj;
            seg = null;
        }
    }

    public Graf(int mida_vertexs) {
        vertexs = new HashMapIndirecte<K,NodeVertex<V>>(mida_vertexs);
        num_arestes = 0;
    }

    public void inserirVertex(K key, V value) {
        NodeVertex<V> nou_vertex = new NodeVertex<>(value);
        try {
            NodeVertex<V> vertex_existent = vertexs.consultar(key);
            vertex_existent.v = value;
        } catch (ElementNoTrobat e) {
            vertexs.inserir(key, nou_vertex);
        }
    }

    // Metode per a obtenir el valor d'un vertex del graf a partir del seu identificador
    public V consultarVertex(K key) throws VertexNoTrobat {
        try {
            NodeVertex<V> vertex_a_consultar = vertexs.consultar(key);
            return vertex_a_consultar.v;
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }
    }

    // public boolean existeixVertex(K key) {
    //     try {
    //         consultarVertex(key);
    //         return true;
    //     } catch (VertexNoTrobat e) {
    //         return false;
    //     }
    // }

    // Mètode auxiliar per esborrar totes les adjacències d'un vèrtex.
    public void esborrarAdjacencies(K key) throws VertexNoTrobat {
        /* Diguem-li 'V' al vèrtex a esborrar. */
        try {
            NodeVertex<V> vertex_a_esborrar = vertexs.consultar(key);
            LlistaNoOrdenada<K> adjacencies = new LlistaNoOrdenada<>();   // Llista que desarà les claus dels vèrtexs adjacents al vèrtex V.
            NodeAresta<K> aresta = vertex_a_esborrar.a.seg;             // Inicialitzem a la primera aresta (.seg, ja que busquem arestes, és a dir, després de l'element fantasma).

            while (aresta != null) {                                      // Mentre hi segueixi havent arestes,
                adjacencies.inserir(aresta.k);                            // s'afegeix la seva clau a la taula,
                aresta = aresta.seg;                                      // i es passa a la següent aresta.
            }
            for (K clau : adjacencies) {                                  // Per totes les claus dels vèrtexs adjacents a V:
                NodeVertex<V> vertex_aux = vertexs.consultar(clau);     // Accedim al vèrtex adjacent en qüestió (V_aux).
                NodeAresta<K> aresta_aux = vertex_aux.a;                // Accedim a la primera posició (fantasma) de les arestes (entre les quals es troba la que es vol esborrar, és a dir, l'aresta: V_aux <--> V, amb claus: clau <--> key, respectivament).
                while (!aresta_aux.seg.k.equals(key)) {                   // I mentre no es trobi l'aresta
                    aresta_aux = aresta_aux.seg;                          // es busca en la següent.
                }
                aresta_aux.seg = aresta_aux.seg.seg;                      // I així fins trobar-la, moment en què se surt del bucle i s'esborra l'aresta.
                vertex_aux.n_are -= 1;                                    // Finalment, el nombre d'arestes d'aquest vèrtex es decrementa en 1,
                num_arestes -= 1;                                         // i per tant el nombre total d'arestes al graf també.
            }

        } catch (ElementNoTrobat e) {                                     // En cas que no es trobi el vèrtex que es vol esborrar.
            e.printStackTrace();
        }
    }
    // Cost O(e/n)·O(e)

    // Metode per a esborrar un vertex del graf a partir del seu identificador
    // Aquest metode també ha d'esborrar totes les arestes associades a aquest vertex
    public void esborrarVertex(K key) throws VertexNoTrobat {
        /* Sigui V el vèrtex a esborrar */
        try {
            if (!vertexAillat(key)) {       // Si V té adjacències (és a dir, no és un vèrtex aillat),
                esborrarAdjacencies(key);   // S'eliminen les arestes d'altres vèrtexs amb V.
            }
            vertexs.esborrar(key);          // I s'esborra el vèrtex del graf (la resta en el nombre d'arestes ja s'ha fet en el mètode auxiliar esborrarAdjacències).
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }
    }

    // Metode per a comprovar si hi ha algun vertex introduit al graf
    public boolean esBuida() {
        return vertexs.numElements() == 0;
    }

    // Metode per a comprovar el nombre de vertexs introduits al graf
    public int numVertex() {
        return vertexs.numElements();
    }

    // Metode per a obtenir tots els ID de vertex de l'estrucutra
    public ILlistaGenerica<K> obtenirVertexIDs() {
        ILlistaGenerica<K> IDs = vertexs.obtenirClaus();
        return IDs;
    }



    ////////////////////////////////////////////////////////////////////////////////////
    // Operacions per a treballar amb les arestes

    // Metode per a insertar una aresta al graf. Els valors de vertex 1 i vertex 2 son els vertex a connectar i E es el pes de la aresta
    // Si ja existeix l'aresta se li actualitza el seu pes
    public void inserirAresta(K v1, K v2) throws VertexNoTrobat {
        try {

            // Inserir aresta ver1 <--> ver2:
            NodeVertex<V> ver1 = vertexs.consultar(v1);
            NodeAresta<K> are1 = ver1.a;                    // Inicialitzem al fantasma.

            // Bucle en què se cercarà l'aresta. Si es troba, s'actualitza el pes. Si no, se segueix buscant fins que s'arribi al final de la llista, moment en què s'insereix la nova aresta.
            boolean existeix_are1 = false;

            while (are1.seg != null && !existeix_are1) {      // Mentre segueixi havent arestes i no s'hagi trobat l'aresta desitjada:
                if (are1.seg.k.equals(v2)) {                  // >>> 1) Mirem si l'aresta coincideix (mirem si 'ver1' té a v2 (clau de 'ver2') com a veí)
                    existeix_are1 = true;
                } else {                                      // >>> 2) Si l'aresta no és la que es busca,
                    are1 = are1.seg;                          //        Es mira la següent.
                }
            }
            // Se sortirà del bucle amb 'are1' = darrera aresta de la llista.
            if (!existeix_are1) {                             // Si s'ha sortit del bucle i no s'ha trobat l'aresta, s'ha d'inserir una nova instància d'aresta.
                NodeAresta<K> are1_a_inserir = new NodeAresta<K>(v2); // Aquí es crea l'aresta.
                are1.seg = are1_a_inserir;                    // Aquesta nova aresta és apuntada per la darrera aresta de la llista, que és 'are1' (inserció al final de la llista d'arestes).
                ver1.n_are += 1;                              // El nombre d'arestes (de veins) de 'ver1' s'incrementa en 1.
                num_arestes += 1;                             // I un cop es veu que s'ha de dur a terme la inserció d'una nova aresta, es fa que el nombre d'arestes del graf s'incrementi en 1.
            }


            // Inserir aresta ver2 <--> ver1 (anàlog), la qual cosa només es farà si
            if (!v1.equals(v2)) {
            // És a dir, s'insereix l'aresta a l'altre vèrtex sempre que aquests siguin diferents. Si l'aresta és d'un vèrtex a ell mateix, fer el procediment contrari no té sentit, perquè ja s'ha fet!
                NodeVertex<V> ver2 = vertexs.consultar(v2);
                NodeAresta<K> are2 = ver2.a;

                boolean existeix_are2 = false;

                while (are2.seg != null && !existeix_are2) {
                    if (are2.seg.k.equals(v1)) {
                        existeix_are2 = true;
                    } else {
                        are2 = are2.seg;
                    }
                }
                if (!existeix_are2) {
                    NodeAresta<K> are2_a_inserir = new NodeAresta<K>(v1);
                    are2.seg = are2_a_inserir;
                    ver2.n_are += 1;
                }
            }

        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }
    }

    // Metode per a saber si una aresta existeix a partir dels vertex que connecta
    public boolean existeixAresta(K v1, K v2) throws VertexNoTrobat {
        // Per evitar redundància en el codi, es crida al mètode consultarAresta.
        try {
            NodeVertex<V> ver1 = vertexs.consultar(v1);
            if (vertexs.buscar(v2)) {                           // Si el vèrtex 'ver2' (amb què 'ver1' és o no adjacent) existeix, es realitza la cerca de l'aresta.
                NodeAresta<K> are1 = ver1.a.seg;              // S'inicialitza 'are1' a la primera aresta de 'ver1'.
                while (are1 != null) {                          // Mentre segueixin quedant arestes per mirar,
                    if (are1.k.equals(v2)) {                    // si l'aresta té com a clau 'v2' (és a dir, si 'ver1' és adjacent a 'ver2')
                        return true;                          // es retorna el pes d'aquesta aresta.
                    }
                    are1 = are1.seg;                            // En cas contrari, se segueix buscant.
                }
                return false;                    // Si se surt del bucle i no s'ha retornat res, és que no s'ha trobat l'aresta, i per tant es llença ArestaNoTrobada.
            } else {
                throw new VertexNoTrobat();                     // Si no s'ha trobat el vèrtex 'ver2', es llença VertexNoTrobat.
            }
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();                         // Si no s'ha trobat el vèrtex 'ver1', es llença VertexNoTrobat.
        }
    }


    // Metode per a esborrar una aresta a partir dels vertex que connecta
    public void esborrarAresta(K v1, K v2) throws VertexNoTrobat, ArestaNoTrobada {
        try {

            // Esborrar aresta ver1 <--> ver2:
            NodeVertex<V> ver1 = vertexs.consultar(v1);
            boolean are1_esborrada = false;
            if (vertexs.buscar(v2)) {                           // Si el vèrtex 'ver2' (amb què 'ver1' és o no adjacent) existeix, es realitza la cerca de l'aresta.
                NodeAresta<K> are1 = ver1.a;                  // S'inicialitza 'are1' a l'element fantasma (ja que es farà la comparació amb .seg).
                while (are1.seg != null && !are1_esborrada) {   // Mentre segueixin quedant arestes per mirar, i no s'hagi esborrat ja l'aresta,
                    if (are1.seg.k.equals(v2)) {                // si l'aresta té com a clau 'v2' (és a dir, si 'ver1' és adjacent a 'ver2')
                        are1.seg = are1.seg.seg;                // l'aresta anterior a aquesta aresta, apunta a la posterior d'aquesta.
                        are1_esborrada = true;
                        ver1.n_are -= 1;                        // Es decrementa el nombre d'arestes de 'ver1' en 1 (el nombre total d'arestes es decrementarà més endavant).
                    }
                    are1 = are1.seg;                            // En cas contrari, se segueix buscant.
                }
            }
            if (!are1_esborrada) {                              // Si no s'ha aconseguit esborrar l'aresta, és que no s'ha trobat l'aresta,
                throw new ArestaNoTrobada();                    // i per tant es llença ArestaNoTrobada.
            }

            // Aquest bloc es farà només si s'ha esborrat l'aresta ver2 <--> ver1 (gràcies al retorn de l'excepció anterior en cas contrari), la qual cosa ens garantitza que existeix aquesta aresta.

            // Esborrar aresta ver2 <--> ver1:
            NodeVertex<V> ver2 = vertexs.consultar(v2);
            boolean are2_esborrada = false;
            // Ara ja s'ha comprovat que els dos vèrtexs existeixen, així que no cal fer cap comprovació prèvia al respecte.
            NodeAresta<K> are2 = ver2.a;
            while (!are2_esborrada) {        // No cal comprovar que are2.seg != null, ja que, com que se sap que l'aresta existeix, aquest es converteix en l'únic criteri de parada que necessitem (no es podrà arribar al final de la llista sense haver trobat l'aresta, precisament perquè se sap que existeix).
                if (are2.seg.k.equals(v1)) {
                    are2.seg = are2.seg.seg;
                    are2_esborrada = true;
                    ver2.n_are -= 1;
                }
                are2 = are2.seg;
            }
            // Altre cop, aquí no cal mirar si s'ha esborrat l'aresta (en cas de no haver-se trobat, aquest segon bloc no s'hagués executat, ja que l'excepció ArestaNoTrobada s'ha llençat en el primer bloc).
            num_arestes -= 1;                // El nombre total d'arestes del graf es decrementa en 1.
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();      // Si no s'ha trobat el vèrtex 'ver1', es llença VertexNoTrobat.
        }
    }

    // Metode per a comptar quantes arestes te el graf en total
    public int numArestes() {
        return num_arestes;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // Metodes auxiliars per a treballar amb el graf

    // Metode per a saber si un vertex te veins
    public boolean vertexAillat(K v1) throws VertexNoTrobat {
        try {
            NodeVertex<V> ver1 = vertexs.consultar(v1);
            return (ver1.n_are == 0); // És a dir, serà un vèrtex aillat només si el seu nombre d'arestes és 0 (també s'hagués pogut fer: return (ver1.a.seg == null), amb què es comprova si existeix algun NodeAresta més enllà del fantasma).
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }
    }

    // Metode per a saber quants veins te un vertex
    public int numVeins(K v1) throws VertexNoTrobat {
        try {
            NodeVertex<V> ver1 = vertexs.consultar(v1);
            return ver1.n_are;
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }
    }

    // Metode per a obtenir tots els ID de vertex veins d'un vertex
    public ILlistaGenerica<K> obtenirVeins(K v1) throws VertexNoTrobat {
        try {
            NodeVertex<V> ver1 = vertexs.consultar(v1);               // Vèrtex del qual en vull trobar els seus veins.
            NodeAresta<K> are1 = ver1.a.seg;                          // Primera aresta del vèrtex ver1.
            ILlistaGenerica<K> IDs_veins = new LlistaNoOrdenada<>();    // Llista on es desaran les claus de tots els veins.

            while (are1 != null) {                                      // Mentre hi segueixi havent arestes,
                IDs_veins.inserir(are1.k);                              // s'insereix la clau del seu vèrtex adjacent a la llista.
                are1 = are1.seg;                                        // i es passa a la següent aresta de ver1.
            }
            return IDs_veins;
        } catch (ElementNoTrobat e) { // En cas que no es trobi el vèrtex ver1 en el graf.
            throw new VertexNoTrobat();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // Metodes OPCIONALS - Si es fa la part obligatoria la nota maxima sera un 8
    // Si s'implementen aquests dos metodes correctament es podra obtenir fins a 2 punts addicionals

    // Metode per a obtenir tots els nodes que estan connectats a un vertex
    // es a dir, nodes als que hi ha un cami directe des del vertex
    // El node que es passa com a parametre tambe es retorna dins de la llista!
    public ILlistaGenerica<K> obtenirNodesConnectats(K v1) throws VertexNoTrobat {
        try {
            LlistaNoOrdenada<K> nodes_connectats = new LlistaNoOrdenada<>();
            LlistaNoOrdenada<K> nodes_a_visitar = new LlistaNoOrdenada<>(); // Llista on es desaràn els nodes pendents de visitar (aquesta llista farà de cua!).
            // int pos_a_consultar = 0;
            nodes_connectats.inserir(v1);
            nodes_a_visitar.inserir(v1);

            while (!nodes_a_visitar.esBuida()) {                         // Mentre encara faltin nodes per visitar.
                K clau = nodes_a_visitar.consultar(0);               // S'agafa el node al principi de la cua.
                NodeVertex<V> ver_a_visitar = vertexs.consultar(clau);
                NodeAresta<K> are_a_observar = ver_a_visitar.a.seg;

                while (are_a_observar != null) {
                    if (!nodes_connectats.existeix(are_a_observar.k)) {  // Si aquest node veí NO es troba en la llista de nodes connectats,
                        nodes_connectats.inserir(are_a_observar.k);      // S'insereix en la llista de nodes connectats,
                        nodes_a_visitar.inserir(are_a_observar.k);       // i també en la de vèrtexs a consultar.
                    }
                    are_a_observar = are_a_observar.seg;                 // I es passa a la següent aresta.
                }
                // Així fins haver consultat tots els vèrtexs adjacents a cada vèrtex
                nodes_a_visitar.esborrar(clau);                         // S'elimina el node visitat de la cua de visitats (s'esborra el primer element de la cua, ja que 'clau' és la clau a l'inici de la llista).
            }

            return nodes_connectats;

        } catch (ElementNoTrobat | PosicioForaRang e) {
            throw new VertexNoTrobat();
        }
    }

    // Metode per a obtenir els nodes que composen la Component Connexa mes gran del graf
    public ILlistaGenerica<K> obtenirComponentConnexaMesGran() {
        ILlistaGenerica<K> conj_vertexs = vertexs.obtenirClaus();           // Llista amb tots els vèrtexs del graf.
        ILlistaGenerica<K> comp_connex_max = new LlistaNoOrdenada<>();      // Llista on s'emmagatzemarà tots els vèrtexs de la comp. connexa més gran del graf.

        // S'anirà escanejant les diferents components connexes del graf, i a mesura que es faci, s'esborrarà del conjunt de vèrtexs del graf, fins que s'hagin analitzat tots els vèrtexs.
        try {
            while (conj_vertexs.iterator().hasNext()) {                          // Mentre encara quedi vèrtexs (per explorar-ne la seva component connexa),
                K clau = conj_vertexs.iterator().next();                         // S'agafa el primer vèrtex del conjunt (per exemple)
                ILlistaGenerica<K> comp_connex = obtenirNodesConnectats(clau);   // I s'obté la seva component connexa.

                if (comp_connex_max.numElements() < comp_connex.numElements()) { // Si aquesta té un nombre de vèrtexs major a la màxima,
                    comp_connex_max = comp_connex;                               // Aquesta passa a ser la nova màxima component connexa.
                }
                // Ara s'esborren els vèrtexs d'aquesta component connexa del conjunt de vèrtexs totals del graf (ja no cal explorar-los!)
                Iterator<K> iterador = comp_connex.iterator();                   // Iterador de cada component connexa.
                while (iterador.hasNext()) {                                     // Mentre tingui un següent vèrtex,
                    K clau_a_esborrar = iterador.next();                         // S'avança al següent,
                    conj_vertexs.esborrar(clau_a_esborrar);                      // I s'esborra del conj. de vèrtexs del graf.
                }
            }

            return comp_connex_max;

        } catch (VertexNoTrobat | ElementNoTrobat e) {
            return null;
        }

    }

    public LlistaNoOrdenada<V> obtenirComunitatsVeines(K v1) throws VertexNoTrobat {
        try {
            NodeVertex<V> ver1 = vertexs.consultar(v1);               // Vèrtex del qual en vull trobar els seus veins.
            NodeAresta<K> are1 = ver1.a.seg;                          // Primera aresta del vèrtex ver1.
            LlistaNoOrdenada<V> comunitats_veins = new LlistaNoOrdenada<>();    // Llista on es desaran les claus de tots els veins.

            while (are1 != null) { 
                V comunitat_veina = consultarVertex(are1.k);
                if (!comunitats_veins.existeix(comunitat_veina)) {                                     // Mentre hi segueixi havent arestes,
                    comunitats_veins.inserir(comunitat_veina);                              // s'insereix la clau del seu vèrtex adjacent a la llista.
                }
                are1 = are1.seg;                                        // i es passa a la següent aresta de ver1.
            }
            return comunitats_veins;
        } catch (ElementNoTrobat e) { // En cas que no es trobi el vèrtex ver1 en el graf.
            throw new VertexNoTrobat();
        }
    }
}
