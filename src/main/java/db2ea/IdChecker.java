package db2ea;

import java.util.HashSet;
import java.util.Set;

public class IdChecker {
    private Set<String> ids = new HashSet<String>(1024);

    public IdChecker() {
    }

    public boolean isDuplicated(String id) {
        if (ids == null) {
            return false;
        }

        if (ids.contains(id)) {
            return true;
        }

        ids.add(id);
        return false;
    }
}
