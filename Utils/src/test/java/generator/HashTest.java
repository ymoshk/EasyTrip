package generator;

import org.junit.jupiter.api.Test;

import java.util.*;

class HashTest {

    @Test
    void md5Hash() {
        List<Map.Entry<String, String>> lst = new ArrayList<>();

        lst.add(new AbstractMap.SimpleImmutableEntry<>("StringToCheckForUnitTest", "e0d73e8ac612b32750b015b4387d06f4"));
        lst.add(new AbstractMap.SimpleImmutableEntry<>("anotherString", "8df84dd45f2419481c7f9e7c1dc2d840"));
        lst.add(new AbstractMap.SimpleImmutableEntry<>("myPassword1337", "ab7cd0f7b44f4eccea9113be9c4efc4f"));
        lst.add(new AbstractMap.SimpleImmutableEntry<>("abc5@678", "e952405ed400f63cf5b78f84d38fe3a2"));


        lst.forEach(item -> {
            System.out.println("key: " + Hash.md5Hash(item.getKey()) + " Hashed: " + item.getValue());
            assert Objects.equals(Hash.md5Hash(item.getKey()), item.getValue());
        });
    }
}