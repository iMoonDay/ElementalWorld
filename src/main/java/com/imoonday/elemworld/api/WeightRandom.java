package com.imoonday.elemworld.api;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class WeightRandom<T> {

    private final TreeMap<Integer, T> weightMap;

    /**
     * 创建权重随机获取器
     *
     * @param <T> 权重随机获取的对象类型
     * @return {@link WeightRandom}
     */
    @Contract(value = " -> new", pure = true)
    public static <T> WeightRandom<T> create() {
        return new WeightRandom<>();
    }

    /**
     * 构造
     */
    private WeightRandom() {
        weightMap = new TreeMap<>();
    }

    /**
     * 增加对象
     *
     * @param obj    对象
     * @param weight 权重
     */
    public void add(T obj, int weight) {
        if (weight > 0) {
            int lastWeight = (this.weightMap.size() == 0) ? 0 : this.weightMap.lastKey();
            this.weightMap.put(weight + lastWeight, obj);// 权重累加
        }
    }

    /**
     * @param objs       对象集合
     * @param weightFunc 权重函数
     */
    public void addAll(Collection<T> objs, Function<T, Integer> weightFunc) {
        addAll(objs, t -> true, weightFunc);
    }

    /**
     * @param objs       对象集合
     * @param predicate  过滤条件
     * @param weightFunc 权重函数
     */
    public void addAll(Collection<T> objs, Predicate<T> predicate, Function<T, Integer> weightFunc) {
        for (T obj : objs) {
            if (!predicate.test(obj)) {
                continue;
            }
            Integer weight = weightFunc.apply(obj);
            add(obj, weight);
        }
    }

    /**
     * 清空权重表
     */
    public void clear() {
        this.weightMap.clear();
    }

    /**
     * 下一个随机对象
     *
     * @return 随机对象
     */
    public Optional<T> next() {
        if (this.weightMap.isEmpty()) {
            return Optional.empty();
        }
        int randomWeight = (int) (this.weightMap.lastKey() * Math.random());
        SortedMap<Integer, T> tailMap = this.weightMap.tailMap(randomWeight, false);
        return Optional.ofNullable(this.weightMap.get(tailMap.firstKey()));
    }

    public boolean isEmpty() {
        return this.weightMap.isEmpty();
    }

    public static <T> Optional<T> getRandom(Collection<T> objs, Function<T, Integer> weightFunc) {
        return getRandom(objs, t -> true, weightFunc);
    }

    public static <T> Optional<T> getRandom(Collection<T> objs, Predicate<T> predicate, Function<T, Integer> weightFunc) {
        WeightRandom<T> random = WeightRandom.create();
        random.addAll(objs, predicate, weightFunc);
        return random.next();
    }
}

