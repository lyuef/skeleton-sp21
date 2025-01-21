package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator<T> comp ;
    public MaxArrayDeque(Comparator<T> c) {
        comp = c;
    }
    public T max() {
        if(isEmpty()) return null;
        T mx = get(0);
        for(int i=1;i<size();i++) {
            if(comp.compare(get(i),mx)>0) mx = get(i);
        }
        return mx ;
    }
    public T max(Comparator<T> c) {
        if(isEmpty()) return null;
        T mx = get(0);
        for(int i=1;i<size();i++) {
            if(c.compare(get(i),mx)>0 ) mx=get(i);
        }
        return mx;
    }
}
