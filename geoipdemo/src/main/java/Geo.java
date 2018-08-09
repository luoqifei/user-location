import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class Geo {

    private static final Logger logger = LoggerFactory.getLogger(Geo.class);

    private DatabaseReader reader;

    public Geo(String dbfile) throws Exception {
        if (dbfile == null) {
            throw new Exception("geo database must not be null!");
        }
        try {
            File db = new File(dbfile);
            logger.info(">>> db file path: {}", db.getAbsolutePath());
            reader = new DatabaseReader.Builder(db).withCache(new CHMCache()).build();
        } catch (IOException e) {
            throw new Exception(String.format("geo database [%s] may not exist!", dbfile));
        }
    }
    public String getCityName(String ip){
        if(ip != null){
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                String databaseType = reader.getMetadata().getDatabaseType();
                City city = null;
                if (databaseType.contains("City")) {
                    city = reader.city(ipAddress).getCity();
                } else if (databaseType.contains("Country")) {
                    logger.error("Used a wrong db which is countryDB to get city.");
                    return null;
                }
                if (city != null && city.getName() != null) {
                    return city.getName();
                }
            } catch (IOException | GeoIp2Exception e) {
                logger.warn("Geo ip parsing failed. Reason: {}", e.getMessage());
            }

        }
        return "unKnown city";
    }

    /**
     * key = {de ru pt-BR,ja, en, fr, zh-CN, es}
     * @param ip
     * @return
     */
    public String getCityNameByLanguageKey(String ip,String language){
        if(ip != null){
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                String databaseType = reader.getMetadata().getDatabaseType();
                City city = null;
                if (databaseType.contains("City")) {
                    city = reader.city(ipAddress).getCity();
                } else if (databaseType.contains("Country")) {
                    logger.error("Used a wrong db which is countryDB to get city.");
                    return null;
                }
                if (city != null && city.getName() != null) {
                    return city.getNames().get(language);
                }
            } catch (IOException | GeoIp2Exception e) {
                logger.warn("Geo ip parsing failed. Reason: {}", e.getMessage());
            }

        }
        return "unKnown city";
    }
    public String getCountryIsoCode(String ip) {
        return getCountryIsoCode(ip, null);
    }

    public boolean isFromChina(String ip){
        String code = getCountryIsoCode(ip);
        if(code != null){
            if(code.equals("CN")) {
                logger.info("check ip={}, result is CN",ip);
                return true;
            } else {
                logger.info("check ip={},result is not CN,but {}",ip,code);
                return false;
            }
        }else {//null set it as china
            logger.info("check ip={} , but the code is null,we set it as CN.",ip);
            return true;
        }
    }
    public String getCountryIsoCode(String ip, String defaultValue) {
        if (ip == null) {
            return defaultValue;
        }
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            String databaseType = reader.getMetadata().getDatabaseType();
            Country country = null;
            if (databaseType.contains("City")) {
                CityResponse response = reader.city(ipAddress);
                country = response.getCountry();
            } else if (databaseType.contains("Country")) {
                CountryResponse response = reader.country(ipAddress);
                country = response.getCountry();
            }
            if (country != null && country.getIsoCode() != null) {
                return country.getIsoCode().toUpperCase();
            } else {
                return defaultValue;
            }
        } catch (IOException | GeoIp2Exception e) {
            logger.warn("Geo ip parsing failed. Reason: {}", e.getMessage());
        }
        return defaultValue;
    }

}
