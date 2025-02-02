package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {
    private class BSTNode<K extends Comparable<K>,V> {
        public K key;
        public V val;
        public BSTNode<K,V> lson = null;
        public BSTNode<K,V> rson = null;
        BSTNode(K _key,V _val) {
            key = _key;
            val = _val;
        }
        BSTNode() {
            key = null;
            val = null;
        }
    }
    private BSTNode<K,V> root = null;
    private int size = 0;
    @Override
    public void clear() {
        root = null ;
        size = 0;
    }
    @Override
    public boolean containsKey(K key) {
        if(root == null ) return false;
        class helper {
            public boolean cKhelper(K key,BSTNode now) {
                if(now.key.compareTo(key) == 0) return true;
                if(key.compareTo((K) now.key) < 0) return cKhelper(key,now.lson);
                else return cKhelper(key,now.rson);
            }
        }
        helper h = new helper() ;
        return h.cKhelper(key,root);
    }
    @Override
    public V get(K key) {
        if(root == null ) return null;
        class helper {
            public V gethelper(K key,BSTNode now) {
                if(now.key.compareTo(key) == 0 ) return (V)now.val;
                if(key.compareTo((K) now.key) < 0 ) return (V)gethelper(key,now.lson);
                else return (V)gethelper(key,now.rson);
            }
        }
        helper h = new helper() ;
        return h.gethelper(key,root);
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void put(K key, V value) {
        class helper {
            public void puthelper(K key,V value,BSTNode now) {
                if(key.compareTo((K) now.key) == 0) {
                    now.val = value;
                    return ;
                }
                if(key.compareTo((K) now.key)<0) {
                    if(now.lson == null) {
                        now.lson = new BSTNode<K,V>(key,value);
                        size+=1;
                    } else {
                        puthelper(key,value,now.lson);
                    }
                } else {
                    if(now.rson == null) {
                        now.rson = new BSTNode<K,V>(key,value);
                        size += 1;
                    }else {
                        puthelper(key,value,now.rson);
                    }
                }
            }
        }
        if(root == null) {
            root = new BSTNode<>(key,value);
            size += 1;
            return ;
        }
        helper h = new helper();
        h.puthelper(key,value,root);
    }
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("no\n");
    }
    @Override
    public V remove(K key){
        throw  new UnsupportedOperationException("no\n");
    }
    @Override
    public V remove(K key, V value) {
        throw  new UnsupportedOperationException("no\n");
    }
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("no\n");
    }
    public void printInOrder() {
        throw new UnsupportedOperationException("no\n");
    }
}
