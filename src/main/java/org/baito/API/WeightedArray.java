package org.baito.API;

import javafx.util.Pair;

import java.util.ArrayList;

public class WeightedArray<T> {
    private ArrayList<Pair<T, Double>> list = new ArrayList<>();
    private double totalWeight = 0;

    public WeightedArray() {};

    public void add(T item, double weight) {
        list.add(new Pair<>(item, weight));
        totalWeight += weight;
    }

    public void clear() {
        list.clear();
    }

    public void remove(T item, double weight) {
        if (list.contains(new Pair<>(item, weight))) {
            list.remove(new Pair<>(item, weight));
            totalWeight -= weight;
        }
    }

    public T get(int index) {
        return list.get(index).getKey();
    }

    public T roll() {
        double rngweight = Math.random() * totalWeight;
        for (Pair<T, Double> i : list) {
            rngweight -= i.getValue();
            if (rngweight <= 0) {
                return i.getKey();
            }
        }
        return list.size() == 0 ? null : list.get(0).getKey();
    }

    public ArrayList<Pair<T, Double>> list() {
        return list;
    }
}
