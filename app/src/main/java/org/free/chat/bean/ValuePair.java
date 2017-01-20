package org.free.chat.bean;

/**
 * Created by Administrator on 2017/1/17.
 */
public class ValuePair<T, V> {
    public T key;
    public V value;

    public ValuePair(T key, V value) {
        this.key = key;
        this.value = value;
    }
}
