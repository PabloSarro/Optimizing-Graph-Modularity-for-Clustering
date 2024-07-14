package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;

public class LlistaNoOrdenada<E extends Comparable<E>> extends LlistaAbstracta<E>{

    public void inserir(E e) {
        /*
        Exemple del que es té:
            >>> elem_fant → elem_fant.seguent → ...
        Exemple del que es vol:
            >>> elem_fant →(2) Nou_Node →(1) elem_fant.seguent → ...
        */
        Node_Llista<E> Nou_Node = new Node_Llista<E>(e, elem_fant.seguent); // (1): Nou_Node apunta al següent del fantasma.
        elem_fant.seguent = Nou_Node;                                       // (2): I ara elem_fant passa a apuntar al nou node: Nou_Node.
        num_elems += 1;                                                     // El nombre d'elements de la taula s'incrementa en 1.
    }

    public void esborrar(E e) throws ElementNoTrobat {
        /*
        Exemple del que es té:
            >>> node → node.seguent → node.seguent.seguent → ...
        Exemple del que es vol (ficant el cas que node.seguent tingui a 'e' com a persona):
            >>> node →(1) node.seguent.seguent → ...
        A més, es dirà que el nodeA va abans del nodeB si la persona en el nodeA va alfabèticament abans de la persona en el nodeB.
        */
        Node_Llista<E> node = elem_fant;                     // Punter que, inicialment, apunta a l'element fantasma.
        boolean esborrat = false;                            // Booleà que indica si ja s'ha esborrat l'element desitjat.

        while (node.seguent != null && esborrat == false) {  // Mentre no s'hagi arribat al final de la llista, i encara no s'hagi esborrat l'element,
            if (e.equals(node.seguent.info)) {               // si es troba a la persona 'e' en el node 'node.seguent',
                node.seguent = node.seguent.seguent;         // aleshores (1): node →(apunta a)→ node.seguent.seguent,
                num_elems -= 1;                              // el nombre d'elements decrementa en 1,
                esborrat = true;                             // i ja s'ha esborrat l'element, així que en la següent iteració se sortirà del bucle.
            } else {                                         // En canvi, si en el node 'node.seguent' no hi és la persona 'e',
                node = node.seguent;                         // es prova amb el següent node de la llista (es fa que el punter apunti al següent node de la llista).
            }
        }
        if (esborrat == false) {                             // Si s'ha sortit del bucle i no s'ha esborrat res,
            throw new ElementNoTrobat();                     // és que no s'ha trobat a la persona que es vol esborrar, així que es retorna l'excepció indicada.
        }
    }

    public int buscar(E e) throws ElementNoTrobat {         // El funcionament d'aquest mètode és pràcticament anàlog a l'anterior.
        Node_Llista<E> node = elem_fant.seguent;            // El primer node al que apuntarà el punter "node" serà el següent element a fantasma, ja que en elem_fant no hi pot haver l'element "e".
        int index = 0;

        while (node != null) {
            if (e.equals(node.info)) {                      // La comparació en aquest cas és amb el contingut de cada node, i l'Element 'e' passat en el mètode.
                return index;
            } else {
                node = node.seguent;
                index += 1;
            }
        }
        throw new ElementNoTrobat();
    }


    public boolean existeix(E e) {
        Node_Llista<E> node = elem_fant.seguent;            // Funcionament idèntic.

        while (node != null) {
            if (e.equals(node.info)) {
                return true;                                // Es retorna cert si es troba a l'Element 'e' en algun lloc de la llista.
            } else {
                node = node.seguent;
            }
        }
        return false;                                       // I si no s'ha trobat, es retorna fals.
    }
}
