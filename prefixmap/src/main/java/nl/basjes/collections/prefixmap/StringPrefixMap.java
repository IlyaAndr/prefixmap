/*
 * Copyright (C) 2018-2020 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.basjes.collections.prefixmap;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import nl.basjes.collections.PrefixMap;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The StringPrefixMap is an implementation of PrefixMap where the assumption is that the
 * stored prefixes can be any character in a String.
 *
 * @param <V> The type of the value that is to be stored.
 */
@DefaultSerializer(StringPrefixMap.KryoSerializer.class)
public class StringPrefixMap<V extends Serializable> implements PrefixMap<V>, Serializable {
    private final Boolean             caseSensitive;
    private final PrefixTrie<V>       prefixTrie;
    private final TreeMap<String, V>  allPrefixes;

    PrefixTrie<V> createTrie(boolean newCaseSensitive) {
        return new StringPrefixTrie<>(newCaseSensitive);
    }

    public StringPrefixMap(boolean caseSensitive) {
        this.caseSensitive = caseSensitive; // Only needed for serialization.
        prefixTrie = createTrie(caseSensitive);
        allPrefixes = new TreeMap<>();
    }

    public static class KryoSerializer extends Serializer<StringPrefixMap<Serializable>> {

        public void write(Kryo kryo, Output output, StringPrefixMap<Serializable> instance) {
            output.writeBoolean(instance.caseSensitive);
            kryo.writeClassAndObject(output, instance.allPrefixes);
        }

        @SuppressWarnings("unchecked")
        public StringPrefixMap<Serializable> read(Kryo kryo, Input input, Class<? extends StringPrefixMap<Serializable>> type) {
            try {
                boolean caseSensitive = input.readBoolean();
                StringPrefixMap<Serializable> instance = type.getDeclaredConstructor(boolean.class).newInstance(caseSensitive);
                Map<String, Serializable> allPrefixes = (TreeMap<String, Serializable>) kryo.readClassAndObject(input);

                for (Entry<String, Serializable> entry : allPrefixes.entrySet()) {
                    Serializable value = entry.getValue();
                    instance.put(entry.getKey(), value);
                }
                return instance;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new KryoException("Deserialization of StringPrefixMap failed", e);
            }
        }
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return prefixTrie.containsPrefix(prefix);
    }

    @Override
    public V put(String prefix, V value) {
        if (prefix == null) {
            throw new NullPointerException("The prefix may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }

        V previousValue = prefixTrie.add(prefix, value);
        if (previousValue == null) {
            if (prefixTrie.caseSensitive()) {
                allPrefixes.put(prefix, value);
            } else {
                allPrefixes.put(prefix.toLowerCase(), value);
            }
        }
        return previousValue;
    }

    @Override
    public int size() {
        return allPrefixes.size();
    }

    @Override
    public void clear() {
        prefixTrie.clear();
        allPrefixes.clear();
    }

    @Override
    public V remove(String prefix) {
        if (prefix == null) {
            throw new NullPointerException("The prefix may not be null");
        }
        V oldValue = prefixTrie.remove(prefix);
        if (oldValue != null) {
            if (prefixTrie.caseSensitive()) {
                allPrefixes.remove(prefix);
            } else {
                allPrefixes.remove(prefix.toLowerCase());
            }
        }
        return oldValue;
    }

    @Override
    public V get(String prefix) {
        // NOTE: The 'allPrefixes'
        return prefixTrie.get(prefix);
    }

    @Override
    public V getShortestMatch(String input) {
        return prefixTrie.getShortestMatch(input);
    }

    @Override
    public V getLongestMatch(String input) {
        return prefixTrie.getLongestMatch(input);
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        return allPrefixes.entrySet();
    }

    @Override
    public boolean containsKey(Object key) {
        return allPrefixes.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return allPrefixes.containsValue(value);
    }

    @Override
    public Set<String> keySet() {
        return allPrefixes.keySet();
    }

    @Override
    public Collection<V> values() {
        return allPrefixes.values();
    }
}