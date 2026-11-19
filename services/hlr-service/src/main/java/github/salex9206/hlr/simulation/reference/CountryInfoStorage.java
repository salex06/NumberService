package github.salex9206.hlr.simulation.reference;

import java.util.Map;

public class CountryInfoStorage {
    public record CountryInfo(
            String prefix,
            String countryShortName,
            String countryName,
            String defaultCity) {
    }
    final static CountryInfo unknown = new CountryInfo(
            "-", "??", "UNKNOWN", "UNKNOWN"
    );

    public static CountryInfo getCountry(String e164) {
        for (var v : data.values()) {
            if (e164.startsWith(v.prefix))
                return v;
        }

        return unknown;
    }

    private static final Map<String, CountryInfo> data = Map.ofEntries(
            Map.entry("+7", new CountryInfo("+7", "RU", "Russia", "Moscow")),
            Map.entry("+1", new CountryInfo("+1", "US", "United States", "San-Francisco")),
            Map.entry("+375", new CountryInfo("+375", "BY", "Belarus", "Minsk")),
            Map.entry("+995", new CountryInfo("+995", "GE", "Georgia", "Tbilisi")),
            Map.entry("+49", new CountryInfo("+49", "DE", "Germany", "Berlin")),
            Map.entry("+33", new CountryInfo("+33", "FR", "France", "Paris")),
            Map.entry("+39", new CountryInfo("+39", "IT", "Italy", "Rome")),
            Map.entry("+34", new CountryInfo("+34", "ES", "Spain", "Madrid")),
            Map.entry("+81", new CountryInfo("+81", "JP", "Japan", "Tokyo")),
            Map.entry("+86", new CountryInfo("+86", "CN", "China", "Beijing")),
            Map.entry("+55", new CountryInfo("+55", "BR", "Brazil", "Brasilia")),
            Map.entry("+91", new CountryInfo("+91", "IN", "India", "New Delhi")),
            Map.entry("+61", new CountryInfo("+61", "AU", "Australia", "Canberra")),
            Map.entry("+82", new CountryInfo("+82", "KR", "South Korea", "Seoul")),
            Map.entry("+52", new CountryInfo("+52", "MX", "Mexico", "Mexico City")),
            Map.entry("+90", new CountryInfo("+90", "TR", "Turkey", "Ankara")),
            Map.entry("+20", new CountryInfo("+20", "EG", "Egypt", "Cairo")),
            Map.entry("+27", new CountryInfo("+27", "ZA", "South Africa", "Pretoria"))
    );
}
