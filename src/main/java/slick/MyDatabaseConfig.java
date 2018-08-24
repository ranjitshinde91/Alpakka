package slick;

import com.typesafe.config.Config;
import slick.basic.DatabaseConfig;

public class MyDatabaseConfig implements DatabaseConfig {

    @Override
    public Object db() {
        return null;
    }

    @Override
    public Config config() {
        return null;
    }

    @Override
    public String profileName() {
        return null;
    }

    @Override
    public String driverName() {
        return null;
    }

    @Override
    public boolean profileIsObject() {
        return false;
    }

    @Override
    public boolean driverIsObject() {
        return true;
    }

    @Override
    public Object profile() {
        return null;
    }

    @Override
    public Object driver() {
        return null;
    }
}
