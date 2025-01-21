package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class LinkedListDeque<T> implements Iterable<T>,Deque<T>{
    private static class node<T>{
        node<T> prev = null,next = null;
        T val;
        node(T val) {
            this.val = val;
        }
        node() {
            this.prev = null;
            this.next = null;
        }
        node(node<T>_prev,node<T>_next,T _val) {
            this.val = _val;
            this.prev = _prev;
            this.next = _next;
        }
    }
    private node<T> First = new node<T>();
    private node<T> Last = new node<T>();
    private int size = 0;
    public LinkedListDeque() {
        First.next = Last;
        Last.prev = First;
        First.prev=First;
        Last.next = Last;
    }
    @Override
    public void addFirst(T item){
        size += 1;
        node<T> now = new node<T>(First,First.next,item);
        First.next.prev = now ;
        First.next = now;
    }
    @Override
    public void addLast(T item) {
        size += 1;
        node<T> now = new node<T>(Last.prev,Last,item);
        Last.prev.next = now;
        Last.prev = now;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        node<T> now = First.next;
        while(now!=Last) {
            System.out.printf(now.val + " ");
            now = now.next;
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if(isEmpty()) return null;
        size -= 1;
        node<T>now = First.next;
        now.next.prev = First;
        First.next = now.next;
        return now.val;
    }
    @Override
    public T removeLast() {
        if(isEmpty()) return null;
        size -= 1;
        node<T>now = Last.prev;
        now.prev.next = Last;
        Last.prev = now.prev;
        return now.val;
    }
    @Override
    public T get(int index) {
        node<T>now = First.next;
        for(int i=0;i<index;i++) {
            now = now.next;
        }
        return now.val;
    }
    public T getRecursive(int index) {
        class helper<T>{
            T gethelper(node<T>now,int index) {
                if(index == 0) return now.val;
                return gethelper(now.next,index-1);
            }
        }
        helper<T> h = new helper<T>();
        return h.gethelper(First.next,index);
    }
    @Override
    public Iterator<T> iterator() {
        return new LLDequeIterator();
    }
    private class LLDequeIterator <T> implements Iterator<T>{

        node<T> now = (node<T>) First.next;
        @Override
        public boolean hasNext() {
            return now!=Last;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T val = now.val;
            now = now.next;
            return val;
        }
    }
    public boolean equals(Object o) {
        if(!(o instanceof Deque)) return false;
        if(((Deque<?>)o).size()!=size) return false;
        for(int i=0;i<((Deque<?>) o).size();i++) {
            if(!get(i).equals(((Deque<?>)o).get(i))) return false;
        }
        return true;
    }
}

