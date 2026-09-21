package practica1.CircularQ;

import java.util.Iterator;
import util.Queue;

public class CircularQueue<E> implements Queue<E> {

    private final E[] queue;
    private final int N;
    protected int g, numElements;

    public CircularQueue(int N) {
        this.N = N;
        queue = (E[]) (new Object[N]);
        this.g = 0;
        this.numElements=0;
    }

    @Override
    public int size() {
        return numElements;
    }

    @Override
    public int free() {
        return N-numElements;
    }

    @Override
    public boolean empty() {
        return numElements==0;
    }

    @Override
    public boolean full() {
        return numElements==N;
    }

    @Override
    public E peekFirst() {
        if(empty()){
        return null;
        }else{
        
        return queue[g];
        }
    }

    @Override
    public E get() {
        if (empty()) {
            throw new IllegalStateException("La cua està buida");
        }
        E temp = queue[g];   
        queue[g] = null;     
        g = (g + 1) % N;    
        numElements--;    
        return temp;
    }

    @Override
    public void put(E e) {
        if (full()) {
            throw new IllegalStateException("La cua està plena!");
        }
        int p = (g + numElements) % N; 
        queue[p] = e;
        numElements++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < numElements; i++) {
            int idx = (g + i) % N; // Recorrem des de 'g' de forma circular
            sb.append(queue[idx]);
            if (i < numElements - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        //Completar...

        @Override
        public boolean hasNext() {
            throw new RuntimeException("//Completar...");
        }

        @Override
        public E next() {
            throw new RuntimeException("//Completar...");
        }

        @Override
        public void remove() {
            throw new RuntimeException("//Completar...");
        }

    }
}
