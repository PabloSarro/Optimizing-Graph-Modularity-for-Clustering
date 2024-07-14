package cat.urv.deim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import cat.urv.deim.exceptions.ElementNoTrobat;

public class HashMapIndirecte<K extends Comparable<K>,V> implements IHashMap<K,V> {

    public ArrayList<NodeHash<K,V>> taula;

    public class NodeHash<A,B> {
        // Atributs de la classe NodeHash
        public K k; // Clau
        public V v; // Valor
        public NodeHash<K, V> seg; // Seguent

        private NodeHash(K clau, V valor, NodeHash<K,V> seguent) { // Constructor de la classe NodeHash
            this.k = clau;
            this.v = valor;
            this.seg = seguent;
        }
    }

    public int mida;
    public int num_elems;

    public HashMapIndirecte(int mida_taula) {                   // Li passem la mida de la taula
        taula = new ArrayList<NodeHash<K,V>>(mida_taula);       // Es crea la taula on es desaran les "mida" (nombre natural) llistes.
        num_elems = 0;                                          // Inicialitzem a 0 el nombre inicial d'elements.
        for (int i = 0; i < mida_taula; i++) {
            NodeHash<K,V> node_inici = new NodeHash<K,V>(null, null, null); // Inicialment, la taula comença estant buida, així que es crea un node buit a l'inici de cada llista,
            taula.add(node_inici);                                                             // el qual s'afegeix a totes les posicions de la taula.
        }
        mida = mida_taula;                                      // I la variable "mida" que emmagatzema les posicions de la taula de hash, passa a ser el valor "mida_taula" introduit per l'usuari en la creació de la taula.
    }

    //Mètode auxiliar que ens retornarà la posició en la taula en què s'ha d'inserir un element (funció de hashing).
    public int posicio(K key) {
        if (mida > 0) {
            return (int) key.hashCode() % mida;
        } else {
            // Això no passarà mai! Ja que en el constructor de HashMapPersones, s'imposarà que la mida hagi de ser estrictament positiva.
            return -2;
        }
    }

    //Mètode auxiliar per redimensionar la taula
    public ArrayList<NodeHash<K,V>> redimensionament(ArrayList<NodeHash<K,V>> taula) {
        mida = 2*mida;                                                            // Dupliquem la mida de la taula.
        ArrayList<NodeHash<K,V>> nova_taula = new ArrayList<NodeHash<K,V>>(mida); // Creem una nova taula del doble de mida.
        // La inicialitzem.
        for (int i = 0; i < mida; i++) {
            NodeHash<K,V> node_inici = new NodeHash<K,V>(null, null, null);
            nova_taula.add(node_inici);
        }

        int antiga_mida = mida/2;
        // Ara anem a l'antiga taula, amb l'objectiu de transferir-hi tots els elements a la nova.
        for (int i = 0; i < antiga_mida; i++) {
            NodeHash<K,V> node_a_reinserir = taula.get(i).seg;  // S'agafen els objectes de l'anterior taula (començant pel següent a l'inicial de cada llista, ja que en l'inicial no hi ha contingut).
            while (node_a_reinserir != null) {                  // Mentre no s'arribi fins al final de cada llista,
                K clau = node_a_reinserir.k;
                // Inserció en la nova taula:
                int pos_on_reinserir = posicio(clau);           // Calculem la posició en la taula de l'element.
                NodeHash<K,V> llista_on_reinserir = nova_taula.get(pos_on_reinserir); // Punter a l'inici de la llista de la nova taula on toca reinserir.

                node_a_reinserir.seg = llista_on_reinserir.seg; // Que el node a reinserir, tingui com a següent al primer node (amb contingut) de la llista
                llista_on_reinserir.seg = node_a_reinserir;     // Que el node de l'inici de la llista sense contingut, tingui a aquest node com a següent.

                node_a_reinserir = node_a_reinserir.seg;        // Un cop reinserit un dels nodes, passem al següent.
            }
        }
        return nova_taula;                                      // I retornem la nova taula (redimensionalitzada) amb la qual treballarem a partir d'ara.
        // Notem que no es varia el nombre d'elements, ja que aquest es manté constant en el redimensionament (només es tornen a posicionar els elements que ja existien prèviament).
    }

    // Metode per inserir un element a la taula. Si existeix un element amb aquesta clau s'actualitza el valor
    public void inserir(K key, V value){
        if (factorCarrega() > 0.75) {
            taula = redimensionament(taula);
        }

        int pos_on_inserir = posicio(key);              // Calculem la posició on inserir l'element.
        if (buscar(key)) {                              // Si aquest element ja ha estat inserit en la taula,

            boolean substituit = false;                 // Booleà que desa si ja ha estat substituit l'element.
            NodeHash<K,V> node_a_substituir = taula.get(pos_on_inserir).seg;
            while (!substituit) {                       // Mentre no s'hagi substituit encara el node,      (Nota: no cal imposar que node_a_subs. != null, ja que sabem que l'element es troba en la taula, pel que la condició de parar vindrà donada pel moment en què s'hagi substituit l'element)
                if (key.equals(node_a_substituir.k)) {  // en el moment en què es troba la clau,
                    node_a_substituir.v = value;        // s'hi substitueix el valor,
                    substituit = true;                  // i per tant amb això se surt del bucle.
                } else {
                    node_a_substituir = node_a_substituir.seg;
                }
            }

        } else {                                        // En cas contrari, és a dir, en cas de que la clau no es trobi en la taula de hash, es fa el següent:

            NodeHash<K,V> llista_on_inserir = taula.get(pos_on_inserir); // Punter al NodeHash en el principi de la llista que pertoca a la posició a inserir
            NodeHash<K,V> node_a_inserir = new NodeHash<K,V>(key, value, llista_on_inserir.seg); // Creem el NodeHash amb tota la informació a inserir (el següent d'aquest node a inserir té com a següent al primer node (amb contingut) de la llista)
            llista_on_inserir.seg = node_a_inserir; // El node de l'inici de la llista sense contingut, passa a tenir a aquest node com a següent.
            num_elems += 1;
        }
    }

    // Metode per a obtenir un array amb tots els elements de K
    public V consultar(K key) throws ElementNoTrobat{
        int pos_on_consultar = posicio(key);
        NodeHash<K,V> node_a_consultar = taula.get(pos_on_consultar).seg; // Es comença la cerca al primer node de la llista on hi ha contingut (el que hi ha després del node buit de la taula).

        while (node_a_consultar != null) { // Mentre el node a consultar tingui contingut (mentre seguim dins la llista)
            if (key.equals(node_a_consultar.k)) { // Si la clau del node és igual a la que es passa en el mètode,
                return node_a_consultar.v;        // es retorna.
            } else {                                // En cas contrari,
                node_a_consultar = node_a_consultar.seg; // s'avança al següent node de la llista.
            }
        }
        throw new ElementNoTrobat();    // Si la llista està buida, o bé no s'ha trobat a l'element, es retorna aquesta excepció.
    }

    // Metode per a esborrar un element de la taula de hash
    public void esborrar(K key) throws ElementNoTrobat{
        int pos_on_esborrar = posicio(key);
        NodeHash<K,V> node_a_esborrar = taula.get(pos_on_esborrar);
        boolean esborrat = false;
        while ((node_a_esborrar.seg != null)&&(!esborrat)) {        // Es fa la comparació amb el següent node.
            if (key.equals(node_a_esborrar.seg.k)) {                // Si la clau del node següent és igual a la que es passa en el mètode,
                node_a_esborrar.seg = node_a_esborrar.seg.seg;      // Es fa que el node següent a l'actual, apunti al següent del següent (omitint així al node a esborrar).
                esborrat = true;                                    // Actualizació del booleà a cert.
                num_elems -= 1;
            } else {
                node_a_esborrar = node_a_esborrar.seg;              // Si no es troba en una certa iteració, es passa al següent node.
            }
        }
        if (!esborrat) {                                            // Si se surt de la llista i no s'ha esborrat l'element,
            throw new ElementNoTrobat();                            // es retorna la següent excepció.
        }
    }

    // Metode per a comprovar si un element esta a la taula de hash
    public boolean buscar(K key){
        int pos_on_buscar = posicio(key);                           // Es va a la llista on es pot trobar a l'element.
        NodeHash<K,V> node_a_buscar = taula.get(pos_on_buscar).seg; // I inicializem un punter que anirà mirant totes les posicions de la taula.
        while (node_a_buscar != null) {                             // Mentre no ens trobem al final de la taula,
            if (key.equals(node_a_buscar.k)) {                      // es mira si l'element es troba en alguna posició.
                return true;
            } else {
                node_a_buscar = node_a_buscar.seg;                  // Si no, es passa al següent node.
            }
        }
        return false;
    }
    // 2a manera:
    // public boolean buscar(K key){
    //     try {
    //         consultar(key);
    //         return true;
    //     } catch (ElementNoTrobat e) {
    //         return false;
    //     }
    // }

    // Metode per a comprovar si la taula te elements
    public boolean esBuida(){
        return num_elems == 0;
    }

    // Metode per a obtenir el nombre d'elements de la llista
    public int numElements(){
        return num_elems;
    }

    // Metode per a obtenir les claus de la taula
    public ILlistaGenerica<K> obtenirClaus(){
        ILlistaGenerica<K> claus = new LlistaOrdenada<>();  // Llista de tipus LlistaOrdenada.
        for (int j = 0; j < mida; j++) {                    // Per totes les posicions de la taula de hash,
            NodeHash<K,V> node_llista = taula.get(j).seg;   // creem un punter que vagi passant per totes les posicions no nules de totes les llistes.
            while (node_llista != null) {                   // Si el node al qual apunta té contingut,
                claus.inserir(node_llista.k);               // es desa la clau del node en qüestió a la llista,
                node_llista = node_llista.seg;              // i es passa al següent node.
            }
        }
        return claus;
    }

    // Metode per a saber el factor de carrega actual de la taula
    public float factorCarrega(){
        return (float) num_elems / mida;
    }

    // Metode per a saber la mida actual de la taula (la mida de la part estatica)
    public int midaTaula(){
        return mida;
    }

    // Classe de l'iterador on s'incorpora: el constructor, que definirà el que és; i els seus 2 mètodes hasNext() i next(), que definirà com s'opera amb ell.
    public class HashMapIterator implements Iterator<V> {
        private LlistaAbstracta<K> claus_ordenades;             // Llista ordenada que desarà les claus (IDs) de manera ordenada!
        private Iterator<K> iterador;                           // I aquest és l'iterador de dita llista.

        public HashMapIterator() {                              // Constructor.

            this.claus_ordenades = new LlistaOrdenada<>();      // S'inicialitza la llista ordenada.

            for (int i = 0; i < mida; i++) {                    // Al llarg de totes les posicions de la taula,
                NodeHash<K,V> elem_llista = taula.get(i).seg;   // s'agafa elem_llista com el primer element (amb contingut) de la llista en dita posició (taula.get(i) dona el node inicial de cada llista, que està buit. Fent .seg s'obté el primer que pot tenir contingut).

                while (elem_llista != null) {                   // Mentre aquest tingui contingut,
                    claus_ordenades.inserir(elem_llista.k);     // s'insereix la seva clau en la llista de claus ordenades,
                    elem_llista = elem_llista.seg;              // i s'avança al següent node de la llista.
                }
            }

            this.iterador = claus_ordenades.iterator();         // Es fa que l'iterador d'aquesta classe sigui l'iterador de la llista ordenada: claus_ordenades.
            // Fent claus_ordenades.iterator(), es retorna un iterador de la llista ordenada, que ja es té implementat!
        }

        // Com que ja es té implementat l'iterador per a llistes ordenades, només cal cridar les funcions ja definides!
        public boolean hasNext() {
            return iterador.hasNext(); // Aquí es crida al mètode hasNext() de l'iterador de la llista ordenada.
        }

        public V next() {
            try {
                return consultar(iterador.next()); // Iterador.next avança a la següent clau de la llista ordenada, i et retorna aquesta mateixa. Si es consulta dita clau, s'obte el valor (la persona) que li pertoca, que és justament el que es vol retornar.
            } catch (ElementNoTrobat e) {
                throw new NoSuchElementException(); // Excepció que mai tindrà lloc, ja que la clau que es consulta forma part de la taula de hash, pel que tindrà un valor (persona) associat.
            }
        }
    }

    // Mètode per a poder iterar pels elements de la taula.
    @Override
    public Iterator<V> iterator() {
        return new HashMapIterator();
    }

}
