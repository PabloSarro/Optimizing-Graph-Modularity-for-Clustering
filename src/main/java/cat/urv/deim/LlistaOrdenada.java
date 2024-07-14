package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;

public class LlistaOrdenada<E extends Comparable<E>> extends LlistaAbstracta<E> {

    public void inserir(E e) {
        /*
        Exemple del que es té:
            >>> node → node.seguent → ...
        Exemple del que es vol:
            >>> node →(1) node_a_inserir →(2) node.seguent → ...
        Sempre que node_a_inserir vagi ABANS de node.seguent i DESPRÉS de node (on els passos (1) i (2) son actualitzacions de variables, que son implementades més a baix).
        A més, es dirà que el nodeA va abans del nodeB si la persona en el nodeA va alfabèticament abans de la persona en el nodeB.
        */
        Node_Llista<E> node = elem_fant;  // Inicialitzem un punter (amb el nom 'node') que anirà apuntant a tots els nodes de la llista. Inicialment, apunta a elem_fant (és a dir, el primer node).

        while (node.seguent != null && e.compareTo(node.seguent.info) > 0) { // Mentres no s'arribi al final de la llista, i el node apuntat per 'node' vagi ABANS del node que es vol inserir,
            node = node.seguent;    // és que encara no s'ha d'inserir, s'haurà de fer més endavant, així que s'avança a la següent posició.
        }
        /*Se sortirà del bucle quan passi una d'aquestes dues coses:
            a) e.compareTo(node.seguent.info) <= 0:
                    S'ha trobat que el node 'node.seguent' va DESPRÉS del que vull inserir.
                    Aleshores, node_a_inserir apuntarà a node.seguent (2) i node apuntarà a node_a_inserir (1)

            b) node.seguent == null:
                    S'ha arribat al final de la llista, i tot node va ABANS del que vull inserir (és a dir, s'ha d'inserir al final de la llista).
                    Aleshores, node_a_inserir apuntarà a node.seguent (2) (que és null!) i node apuntarà a node_a_inserir (1)

            Pel que, en tots dos casos, la implementació és la mateixa.
        */
        Node_Llista<E> node_a_inserir = new Node_Llista<E>(e,node.seguent); // (1): En el cas a), node_a_inserir apuntarà a node.seguent. En el cas b), node_a_inserir apuntarà a null.
        node.seguent = node_a_inserir;                                      // (2): El darrer node que va ABANS de node_a_inserir ('node'), passa a apuntar a 'node_a_inserir'.
        num_elems += 1;
    }

    public void esborrar(E e) throws ElementNoTrobat {
        /*
        Exemple del que es té:
            >>> node → node.seguent → node.seguent.seguent
        Exemple del que es vol (ficant el cas que node.seguent tingui a 'e' com a persona):
            >>> node →(1) node.seguent.seguent → ...
        */
        Node_Llista<E> node = elem_fant;               // Punter que, inicialment, apunta a l'element fantasma.
        boolean esborrat = false;                      // Inicialment, no s'ha esborrat l'element.

        while (node.seguent != null && e.compareTo(node.seguent.info) >= 0) {  // Primer es comprova que existeixi persona següent. De ser així, adicionalment, es mira si la persona que es vol esborrar vagi DESPRÉS (o sigui igual) que la persona següent (si va abans, voldrà dir que ja ens l'hem passat! I aleshores no s'entrarà al bucle).
            if (e.equals(node.seguent.info)) {         // Si la següent persona és precisament la persona "e" a esborrar,
                node.seguent = node.seguent.seguent;   // aleshores la persona anterior a "e" (que es troba en "node") apunta a la següent a "e" (que es troba en "node.seguent.seguent"). (1): node →(apunta a)→ node.seguent.seguent,
                num_elems -= 1;                        // el nombre d'elements es decrementa en 1,
                esborrat = true;                       // i ja s'ha aconseguit esborrar l'element.
            } else {                                   // En canvi, si en el node "node.seguent" no hi és la persona "e",
                node = node.seguent;                   // es prova amb el següent node de la llista (es fa que el punter apunti al següent node de la llista).
            }
        }
        if (esborrat == false) {                       // Si s'ha sortit del bucle i no s'ha esborrat res,
            throw new ElementNoTrobat();               // és que no s'ha trobat a la persona que es vol esborrar, així que es retorna l'excepció indicada.
        }
    }

    public int buscar(E e) throws ElementNoTrobat {
        Node_Llista<E> node = elem_fant.seguent; // Inicialment, el punter apunta al següent node a l'element fantasma.
        int pos = 0;

        while (e.compareTo(node.info) >= 0) {    // Mentres l'element que es vol buscar vagi DESPRÉS (o sigui el mateix) que el node al que apunta "node",
            if (e.equals(node.info)) {           // si aquell node és precisament el que s'ha passat en el mètode,
                return pos;                      // es retorna la posició en què s'ha trobat.
            } else {                             // Si no ho és,
                node = node.seguent;             // es passa al següent node,
                pos += 1;                        // i la posició s'incrementa en un.
            }
        }
        throw new ElementNoTrobat();
    }

    public boolean existeix(E e) {
        /*
        Es pot fer una implenentació anàloga a la del mètode "buscar", però per evitar redundància en el codi, el mètode "existeix" s'implementa de la següent manera:
        */
        try {                               // S'intenta,
            buscar(e);                      // buscar la posició de l'element "e" passat en el mètode.
            return true;                    // I es retorna cert en cas d'haver-se trobat (ja que aleshores existirà una posició en la llista que el conté).
        } catch (ElementNoTrobat exc) {     // Però si el mètode buscar() llença l'excepció ElementNoTrobat,
            return false;                   // és que no s'ha trobat l'element en la llista, i per tant es retorna fals.
        }
    }
}
