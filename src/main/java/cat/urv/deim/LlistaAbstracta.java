package cat.urv.deim;

import java.util.Iterator;
import java.util.NoSuchElementException;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;

public abstract class LlistaAbstracta<E extends Comparable<E>> implements ILlistaGenerica<E>, Iterable<E>{

    public class Node_Llista<T> {
        public E info;
        public Node_Llista<E> seguent;
        public Node_Llista(E info, Node_Llista<E> seguent) {
            this.info = info;
            this.seguent = seguent;
        }
    }

    public int num_elems;
    public Node_Llista<E> elem_fant;

    public LlistaAbstracta() {
        num_elems = 0;
        elem_fant = new Node_Llista<E>(null, null);
    }

    public abstract void inserir(E e);

    public abstract void esborrar(E e) throws ElementNoTrobat;

    public E consultar(int pos) throws PosicioForaRang {
        Node_Llista<E> node = elem_fant.seguent;            // Punter que comença apuntar al node següent a fantasma (ja que, en l'element fantasma, no hi ha contingut, així que es comença la cerca en el següent node).
        int index = 0;                                      // Enter que indicarà la posició de la llista en que ens trobem a cada instant, i que es compararà amb l'enter 'pos'.

        while (node != null) {                              // Mentres seguim dins la llista,
            if (index == pos) {                             // si l'índex coincideix amb la posició passada en el mètode,
                return node.info;                           // es retorna l'Element 'e' desat en el node de dita posició.
            } else {                                        // En cas contrari, si l'índex no coincideix,
                node = node.seguent;                        // fem que el punter apunti al següent node de la llista,
                index += 1;                                 // i l'enter que indica la posició en què ens trobem, s'incrementa en 1 (ja que saltem a la posició següent).
            }
        }
        throw new PosicioForaRang();                        // Si no es troba a la persona en la llista, es retorna aquesta excepció.
    }

    public abstract int buscar(E e) throws ElementNoTrobat;

    public abstract boolean existeix(E e);

    public boolean esBuida() {
        return (elem_fant.seguent == null);                 // També funcionaria fent num_elems == 0
    }

    public int numElements() {
        return num_elems;
    }

    public class PersonaIterator<T> implements Iterator<E> {
        // Atributs
        private Node_Llista<E> actual;                      // Punter que indica a quin node ens trobem en cada moment.

        // Constructor
        public PersonaIterator() {
            actual = elem_fant;                             // Inicialment, s'apunta a l'element fantasma, ja que amb el mètodes següents es retornarà la informació que es trobi després d'aquest node.
        }

        // Mètodes
        public boolean hasNext() {
            return actual.seguent != null;                  // Comprova si existeix un node després de l'actual.
        }

        public E next() {
            if (!hasNext()) {                               // Si no hi ha següent
                throw new NoSuchElementException();
            }
                                                            // Però si n'hi ha,
            actual = actual.seguent;                        // S'avança al següent node.
            return actual.info;                             // I es retorna la info d'aquest.
        }
    }

    public PersonaIterator<E> iterator() {
        return new PersonaIterator<E>();
    }
}
