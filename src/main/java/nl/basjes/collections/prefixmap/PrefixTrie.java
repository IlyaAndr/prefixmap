/*
 * Copyright (C) 2018-2019 Niels Basjes
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

import java.io.Serializable;

interface PrefixTrie<V extends Serializable> extends Serializable {
    V add(String prefix, V value);

    boolean containsPrefix(String prefix);

    V getShortestMatch(String input);

    V getLongestMatch(String input);

    default V remove(String prefix) {
        throw new UnsupportedOperationException("The 'remove(String prefix)' method has not been implemented in " +
            this.getClass().getCanonicalName());
    }

    void clear();
}
