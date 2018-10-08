/*
    Huy Minh Le., s315271
    Tam Thu Caroline Vo, s326167
    Tora Sande Tveit, s326145
    Piotr Jan Kusnierz, s326178
    Kevin Nguyen Ngo, s326170
 */


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

        private Node(T verdi, Node<T> forrige, Node<T> neste)  // konstruktor
        {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        protected Node(T verdi)  // konstruktor
        {
            this(verdi, null, null);
        }

    } // Node

    // instansvariabler
    private Node<T> hode;          // peker til den forste i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;   // antall endringer i listen

    // hjelpemetode
    private Node<T> finnNode(int indeks)
    {
        Node<T> p;

        if(indeks < antall/2){
            p = hode; //Hode som starter paa venstre og gaar mot hoyre
           for(int i = 0; i < indeks; i++){ //Kode som gaar kjorer forover
               p = p.neste;
           }
        }else{
            p = hale; //Hale som starter paa hoyre og gaar mot venstre.
            for(int i = antall-1; i > indeks; i--){ //Kode som kjorer baklengs
                p = p.forrige;
            }
        }
        return p;
    }

    // konstruktor
    public DobbeltLenketListe()
    {
        hode = hale = null;
        antall = 0;
        endringer = 0;
    }

    // konstruktor
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
              throw new IndexOutOfBoundsException("til er storre en tall");
          }

          if(fra > til){
              throw new IllegalArgumentException("fra er storre enn til");
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
        indeksKontroll(indeks, true);
        Node<T> p = hode;
        for(int i = 0; i < indeks; i++){
            p = p.neste;
        }

        if(verdi == null) throw new NullPointerException("Null-verdier er ikke tillat");
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

    //Hjelpemetode for oppgave 6
    private void sjekkFjern(Node<T> r){
        if(r == hode){
            if(antall == 1){
                hode = hale = null;
            }
            else{
                hode = hode.neste;
                hode.forrige = null;
            }
        }
        else if (r == hale){
            hale = hale.forrige;
            hale.neste = null;
        }
        else{
            r.forrige.neste = r.neste;
            r.neste.forrige = r.forrige;
        }
        antall--;
        endringer++;
    }

    @Override
    public boolean fjern(T verdi)
    {
        if(verdi == null) return false;
        for (Node<T> p = hode; p != null; p = p.neste){
            if(p.verdi.equals(verdi)){
                sjekkFjern(p);
                return true;
            }
        }
        return false;
    }

    @Override
    public T fjern(int indeks)
    {
        indeksKontroll(indeks, false);

        Node<T> r = finnNode(indeks);
        T temp = r.verdi;
        sjekkFjern(r);
        return temp;
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
        for (int n = liste.antall(); n > 0; n--){
            Iterator<T> iterator = liste.iterator();
            int m = 0; //m er indeksen til verdien som skal bli dyttet til slutten av listen
            T minverdi = iterator.next(); //Verdien som skal bli sammenlignet med resten av listen
            for (int i = 1; i < n; i++){ //For-loopen som sjekker minverdi med resten av verdiene i listen
                T verdi = iterator.next(); //itererer
                if (c.compare(verdi,minverdi) < 0){ //Sammenligner iterator-verdien med minverdi.
                    m = i; minverdi = verdi; //Hvis iterator-verdien er mindre, oppdater m og gi minverdi ny verdi
                }
            }
            liste.leggInn(liste.fjern(m));
        }
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
            denne = hode;     // denne starter paa den forste i listen
            fjernOK = false;  // blir sann naar next() kalles
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

            fjernOK = true;                // naa kan remove() kalles

            T denneVerdi = denne.verdi;    // tar vare paa verdien i p
            denne = denne.neste;           // flytter p til den neste noden
            fjernOK = true;

            return denneVerdi;             // returnerer verdien
        }

        @Override
        public void remove()
        {
            if (!fjernOK){
                throw new IllegalStateException("Kan ikke fjerne en verdi paa!");
            }

            if (iteratorendringer != endringer){
                throw new ConcurrentModificationException("Listen har blitt endret!");
            }
            fjernOK = false;

            if(antall == 1) hode = hale = null;

            else if(denne == null){
                //fjern hale og oppdater
                (hale = hale.forrige).neste = null;

            }
            else if(denne.forrige == hode){
                //Fjern hode og oppdater
                (hode = hode.neste).forrige = null;
            }

            else{
                //Fjern denne.forrige
                //Oppdater pekere paa hver side
                Node<T> q = denne.forrige; //Denne vi skal fjerne
                Node<T> p = q.forrige; //Noden for den vi skal fjerne
                Node<T> r = q.neste; //Noden etter den vi skal fjerne
                p.neste = r;
                r.forrige = p;
            }
            endringer++;
            iteratorendringer++;
            antall--;
        }

    } // DobbeltLenketListeIterator

} // DobbeltLenketListe
