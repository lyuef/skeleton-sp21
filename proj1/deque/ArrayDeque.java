package deque;

import javax.swing.text.html.HTMLDocument;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Iterable<T> ,Deque<T> {
    private int size = 0,front = 0,back = 1;
    private Object[] a = new Object[8];
    public ArrayDeque() {
    }

    private void resize(int need) {
        Object [] tmp = new Object[need];
        for(int i=0;i<size;i++) {
            tmp[i] = a[(front+i+1)%a.length];
        }
        a = tmp;
        front = need-1;
        back = size;
    }
    @Override
    public void addFirst(T item) {
        if(size == a.length) {
            resize(size*2);
        }
        size+=1;
        a[front] = item;
        front = (front - 1+a.length)%a.length;
    }
    @Override
    public void addLast(T item) {
        if(size == a.length) {
            resize(size*2);
        }
        size += 1;
        a[back] = item;
        back = (back+1)%a.length;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        for(int i=1;i<=size;i++) {
            System.out.print(a[(front+i)%a.length]+" ");
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if(isEmpty()) return null;
        if(size < a.length/4 && size > 32) {
            resize(size);
        }
        front = (front + 1)%a.length;
        size -= 1;
        return (T)a[front];
    }
    @Override
    public T removeLast() {
        if(isEmpty()) return null;
        if(size<a.length/4 && size > 8) {
            resize(size);
        }
        back = (back - 1 + a.length)%a.length;
        size -= 1;
        return (T)a[back];
    }
    @Override
    public T get(int index){
        return (T)a[(front+1+index)%a.length];
    }
    @Override
    public Iterator<T> iterator() {
        return new ADequeIterator();
    }
    private class ADequeIterator <T> implements Iterator<T>{
        int now = (front + 1)%a.length;
        @Override
        public boolean hasNext() {
            return now!=back;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int bef = now;
            now = (now+1)%a.length;
            return (T)a[bef];
        }
    }
    public boolean equals(Object o) {
        if(!(o instanceof Deque)) return false;
        if(((Deque<?>)o).size()!=size) return false;
        for(int i=0;i<size();i++) {
            if( !get(i).equals(((Deque<?>)o).get(i) ) ) return false;
        }
        return true;
    }
}
