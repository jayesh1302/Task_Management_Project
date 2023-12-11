package shared;
import java.util.prefs.Preferences;

public class JwtStorage {

    private static final String JWT_PREF_KEY = "jwtToken";

    public static void saveJwtToken(String jwtToken) {
        Preferences preferences = Preferences.userNodeForPackage(JwtStorage.class);
        preferences.put(JWT_PREF_KEY, jwtToken);
    }

    public static String getJwtToken() {
        Preferences preferences = Preferences.userNodeForPackage(JwtStorage.class);
        return preferences.get(JWT_PREF_KEY, null);
    }

    public static void clearJwtToken() {
        Preferences preferences = Preferences.userNodeForPackage(JwtStorage.class);
        preferences.remove(JWT_PREF_KEY);
    }
}