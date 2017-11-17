package effective2.Chapter11.Item77.enum_singleton;
// Enum singleton - the preferred approach - Page 311

import java.util.*;

public enum Elvis {
    INSTANCE;
    private String[] favoriteSongs =
        { "Hound Dog", "Heartbreak Hotel" };
    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }
}