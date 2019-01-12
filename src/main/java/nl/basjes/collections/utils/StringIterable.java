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

package nl.basjes.collections.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringIterable implements Iterable<Character> {
    private String string;

    public StringIterable(String newString) {
        this.string = newString;
    }

    @Override
    public Iterator<Character> iterator() {
        return new StringIterator();
    }

    public class StringIterator implements Iterator<Character> {
        int offset = 0;
        @Override
        public boolean hasNext() {
            return offset < string.length();
        }

        @Override
        public Character next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Too many");
            }
            return string.charAt(offset++);
        }
    }

}