package Oblig;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class DobbeltLenketListe<T> implements Liste<T>
{
    private static final class Node<T>   // en indre nodeklasse
    {
        // instansvariabler
        private T verdi;
        private Node<T> forrige, neste;

        private Node(T verdi, Node<T> forrige, Node<T> neste)  // konstruktør
        {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        protected Node(T verdi)  // konstruktør
        {
            this(verdi, null, null);
        }

    } // Node

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;   // antall endringer i listen

    // hjelpemetode
    private Node<T> finnNode(int indeks)
    {
        Node<T> p;

        if(indeks < antall/2){
            p = hode; //Hode som starter på venstre og går mot høyre
           for(int i = 0; i < indeks; i++){ //Kode som går kjører forover
               p = p.neste;
           }
        }else{
            p = hale; //Hale som starter på høyre og går mot venstre.
            for(int i = antall-1; i > indeks; i--){ //Kode som kjører baklengs
                p = p.forrige;
            }
        }
        return p;
    }

    // konstruktør
    public DobbeltLenketListe()
    {
        hode = hale = null;
        antall = 0;
        endringer = 0;
    }

    // konstruktør
    public DobbeltLenketListe(T[] a)
    {
        this();

        if(a == null) {
            throw new NullPointerException("Tabellen er tom!");
        }

        hode = hale = new Node<>(null);

        for(T verdi : a){
            if(verdi != null){
                hale = hale.neste = new Node<>(verdi,hale,null);
                antall++;
            }
        }
        if(antall == 0) hode = hale = null;
        else(hode = hode.neste).forrige = null;
    }

    // subliste
    private static void fratilKontroll(int antall, int fra, int til){
          if(fra < 0) {
              throw new IndexOutOfBoundsException("fra er negativ");
          }

          if(til > antall){
              throw new IndexOutOfBoundsException("til er større en tall");
          }

          if(fra > til){
              throw new IllegalArgumentException("fra er større enn til");
          }

    }


    public Liste<T> subliste(int fra, int til)
    {
        fratilKontroll(antall, fra, til);
        DobbeltLenketListe<T> liste = new DobbeltLenketListe<T>();
        Node<T> p = finnNode(fra);
        for (int i = fra; i < til; i++){
            liste.leggInn(p.verdi);
            p = p.neste;
        }
        return liste;
    }

    @Override
    public int antall()
    {
        return antall;
    }

    @Override
    public boolean tom()
    {
        if(antall == 0) return true;
        else return false;
    }

    @Override
    public boolean leggInn(T verdi)
    {
        if (verdi == null) {
            throw new NullPointerException("Null-verdier er ikke tillatt");
        }

        Node<T> p = new Node<>(verdi, hale, null);
        hale = tom() ? (hode = p) : (hale.neste = p);

        antall++;
        endringer++;

        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi)
    {
        Node<T> p = hode;
        for(int i = 0; i < indeks; i++){
            p = p.neste;
        }

        if(verdi == null) throw new NullPointerException("Null-verdier er ikke tillat");
        indeksKontroll(indeks, true);
        if(tom()){
            hode = hale = new Node<T>(verdi,null, null);
        }
        else if(indeks == 0){
            hode = hode.forrige = new Node<T>(verdi, null, hode);
        }
        else if(indeks == antall){
            hale = hale.neste = new Node<T>(verdi, hale, null);
        }
        else{
            p.forrige = p.forrige.neste = new Node<T>(verdi, p.forrige, p);
        }
        antall++;
        endringer++;

    }

    @Override
    public boolean inneholder(T verdi)
    {
        if(indeksTil(verdi) != -1) return true;
        else return false;
    }

    @Override
    public T hent(int indeks)
    {
        indeksKontroll(indeks,false);
        return finnNode(indeks).verdi;
    }

    @Override
    public int indeksTil(T verdi)
    {
        if(verdi == null) return -1;
        Node<T> p = hode;
        for(int i = 0; i < antall; i++, p = p.neste){
            if( p.verdi.equals(verdi) ) return i;
        }
        return -1;
    }

    @Override
    public T oppdater(int indeks, T nyverdi)
    {
        if (nyverdi == null) {
            throw new NullPointerException("Null-verdier er ikke tillat");
        }
        indeksKontroll(indeks,false);
        Node<T> p = finnNode(indeks);
        T gammelverdi = p.verdi;
        p.verdi = nyverdi;
        endringer++;
        return gammelverdi;

    }

    //oppgave 6
    public T fjernNode(Node<T> p){
        if(p == hode){
            if(antall == 1){
                hode = hale = null;
            }else{
                (hode = hode.neste).forrige = null;
            }
        }else if(p == hale){
            (hale = hale.forrige).neste = null;
        }else{
            (p.neste.forrige = p.neste).forrige = p.forrige;
        }
        antall--;
        endringer++;
        return p.verdi;
    }

    @Override
    public boolean fjern(T verdi)
    {
        if(verdi == null) return false;
        for (Node<T> p = hode; p != null; p = p.neste){
            if(p.verdi.equals(verdi)){
                fjernNode(p);
                return true;
            }
        }
        return false;
    }

    @Override
    public T fjern(int indeks)
    {
        indeksKontroll(indeks, false);

        return fjernNode(finnNode(indeks));
    }

    @Override
    public void nullstill()
    {
        Node<T> p = hode, q;

        while (p != null)
        {
            q = p.neste;
            p.neste = null;
            p.verdi = null;
            p = q;
        }

        hode = hale = null;

        endringer++;    // nullstilling er en endring
        antall = 0;     // antall lik 0 i en tom liste
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();

        s.append('[');

        if (!tom())
        {
            Node<T> p = hode;
            s.append(p.verdi);

            p = p.neste;

            while (p != null)  // tar med resten hvis det er noe mer
            {
                s.append(',').append(' ').append(p.verdi);
                p = p.neste;
            }
        }

        s.append(']');

        return s.toString();
    }

    public String omvendtString()
    {
        StringBuilder s = new StringBuilder();

        s.append('[');

        if (!tom())
        {
            Node<T> p = hale;
            s.append(p.verdi);

            p = p.forrige;

            while (p != null)  // tar med resten hvis det er noe mer
            {
                s.append(',').append(' ').append(p.verdi);
                p = p.forrige;
            }
        }

        s.append(']');

        return s.toString();
    }

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c)
    {
        throw new UnsupportedOperationException("Ikke laget ennå!");
    }

    @Override
    public Iterator<T> iterator()
    {
        return new DobbeltLenketListeIterator();
    }

    public Iterator<T> iterator(int indeks)
    {
        indeksKontroll(indeks, false);
        return new DobbeltLenketListeIterator(indeks);
    }

    private class DobbeltLenketListeIterator implements Iterator<T>
    {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator()
        {
            denne = hode;     // denne starter på den første i listen
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks)
        {
            denne = finnNode(indeks);
            fjernOK = false;
            iteratorendringer = endringer;
        }

        @Override
        public boolean hasNext()
        {
            return denne != null;  // denne koden skal ikke endres!
        }

        @Override
        public T next()
        {
            if (endringer != iteratorendringer)
                throw new ConcurrentModificationException("Listen er endret!");

            if (!hasNext()) throw new
                    NoSuchElementException("Tomt eller ingen verdier igjen!");

            fjernOK = true;                // nå kan remove() kalles

            T denneVerdi = denne.verdi;    // tar vare på verdien i p
            denne = denne.neste;           // flytter p til den neste noden
            fjernOK = true;

            return denneVerdi;             // returnerer verdien
        }

        @Override
        public void remove()
        {
            if(!fjernOK){
                throw new IllegalStateException("Kan ikke fjerne!");
            }
            if(iteratorendringer != endringer){
                throw new ConcurrentModificationException("Listen er endret!");
            }
            if(antall == 1){
                hode = hale = null;
            }
            if(denne == null){
                //oppdatere
            }
            if(denne.forrige == hode){
                //oppdatere
            }
            if(noden denne.forrige){
                //oppdatere pekere i noden på begge sider
            }
            fjernOK = false;
            fjernNode(denne == null ? hale : denne.forrige);
            iteratorendringer++;
        }

    } // DobbeltLenketListeIterator

} // DobbeltLenketListe
