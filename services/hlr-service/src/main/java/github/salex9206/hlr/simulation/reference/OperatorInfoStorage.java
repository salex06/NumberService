package github.salex9206.hlr.simulation.reference;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class OperatorInfoStorage {
    public record OperatorInfo(
            String name,
            String mnc,
            String mcc,
            String country,
            String domain
    ) {
    }

    public static OperatorInfo randomOperator(String countryCode) {
        var operators = data.getOrDefault(countryCode, data.get("RU"));

        int index = ThreadLocalRandom.current().nextInt(operators.size());
        return operators.get(index);
    }

    private static final Map<String, List<OperatorInfo>> data = Map.ofEntries(
            Map.entry("RU", List.of(
                    new OperatorInfo("MTS", "01", "250", "RU", "mts.ru"),
                    new OperatorInfo("MegaFon", "02", "250", "RU", "megafon.ru"),
                    new OperatorInfo("Beeline", "99", "250", "RU", "beeline.ru"),
                    new OperatorInfo("T2", "20", "250", "RU", "t2.ru"),
                    new OperatorInfo("Yota", "11", "250", "RU", "yota.ru")
            )),
            Map.entry("US", List.of(
                    new OperatorInfo("Verizon", "010", "310", "US", "verizon.com"),
                    new OperatorInfo("AT&T", "016", "310", "US", "att.com"),
                    new OperatorInfo("T-Mobile", "120", "310", "US", "t-mobile.com")
            )),
            Map.entry("BY", List.of(
                    new OperatorInfo("A1", "01", "257", "BY", "a1.by"),
                    new OperatorInfo("MTS", "02", "257", "BY", "mts.by"),
                    new OperatorInfo("life:)", "04", "257", "BY", "life.com.by")
            )),
            Map.entry("GE", List.of(
                    new OperatorInfo("Geocell", "01", "282", "GE", "geocell.ge"),
                    new OperatorInfo("Magti", "02", "282", "GE", "magticom.ge"),
                    new OperatorInfo("Cellfie", "04", "282", "GE", "cellfie.ge")
            )),
            Map.entry("DE", List.of(
                    new OperatorInfo("Telekom", "01", "262", "DE", "telekom.de"),
                    new OperatorInfo("Vodafone", "02", "262", "DE", "vodafone.de"),
                    new OperatorInfo("O2", "03", "262", "DE", "o2online.de")
            )),
            Map.entry("FR", List.of(
                    new OperatorInfo("Orange", "01", "208", "FR", "orange.fr"),
                    new OperatorInfo("SFR", "09", "208", "FR", "sfr.fr"),
                    new OperatorInfo("Bouygues", "21", "208", "FR", "bouyguestelecom.fr")
            )),
            Map.entry("IT", List.of(
                    new OperatorInfo("TIM", "01", "222", "IT", "tim.it"),
                    new OperatorInfo("Vodafone", "10", "222", "IT", "vodafone.it"),
                    new OperatorInfo("Wind Tre", "88", "222", "IT", "windtre.it")
            )),
            Map.entry("ES", List.of(
                    new OperatorInfo("Vodafone", "01", "214", "ES", "vodafone.es"),
                    new OperatorInfo("Orange", "03", "214", "ES", "orange.es"),
                    new OperatorInfo("Movistar", "07", "214", "ES", "movistar.es")
            )),
            Map.entry("JP", List.of(
                    new OperatorInfo("NTT docomo", "10", "440", "JP", "docomo.ne.jp"),
                    new OperatorInfo("au (KDDI)", "51", "440", "JP", "au.com"),
                    new OperatorInfo("SoftBank", "20", "440", "JP", "softbank.jp")
            )),
            Map.entry("CN", List.of(
                    new OperatorInfo("China Mobile", "00", "460", "CN", "10086.cn"),
                    new OperatorInfo("China Unicom", "01", "460", "CN", "10010.com"),
                    new OperatorInfo("China Telecom", "03", "460", "CN", "189.cn")
            )),
            Map.entry("BR", List.of(
                    new OperatorInfo("Vivo", "06", "724", "BR", "vivo.com.br"),
                    new OperatorInfo("Claro", "05", "724", "BR", "claro.com.br"),
                    new OperatorInfo("TIM", "02", "724", "BR", "tim.com.br")
            )),
            Map.entry("IN", List.of(
                    new OperatorInfo("Reliance Jio", "840", "405", "IN", "jio.com"),
                    new OperatorInfo("Airtel", "10", "404", "IN", "airtel.in"),
                    new OperatorInfo("Vi", "20", "404", "IN", "myvi.in")
            )),
            Map.entry("AU", List.of(
                    new OperatorInfo("Telstra", "01", "505", "AU", "telstra.com.au"),
                    new OperatorInfo("Optus", "02", "505", "AU", "optus.com.au"),
                    new OperatorInfo("Vodafone", "03", "505", "AU", "vodafone.com.au")
            )),
            Map.entry("KR", List.of(
                    new OperatorInfo("SK Telecom", "05", "450", "KR", "sktelecom.com"),
                    new OperatorInfo("KT", "08", "450", "KR", "kt.com"),
                    new OperatorInfo("LG U+", "06", "450", "KR", "uplus.co.kr")
            )),
            Map.entry("MX", List.of(
                    new OperatorInfo("Telcel", "020", "334", "MX", "telcel.com"),
                    new OperatorInfo("AT&T", "070", "334", "MX", "att.com.mx"),
                    new OperatorInfo("Movistar", "030", "334", "MX", "movistar.com.mx")
            )),
            Map.entry("TR", List.of(
                    new OperatorInfo("Turkcell", "01", "286", "TR", "turkcell.com.tr"),
                    new OperatorInfo("Vodafone", "02", "286", "TR", "vodafone.com.tr"),
                    new OperatorInfo("Türk Telekom", "03", "286", "TR", "turktelekom.com.tr")
            )),
            Map.entry("EG", List.of(
                    new OperatorInfo("Orange", "01", "602", "EG", "orange.eg"),
                    new OperatorInfo("Vodafone", "02", "602", "EG", "vodafone.com.eg"),
                    new OperatorInfo("Etisalat", "03", "602", "EG", "etisalat.eg")
            )),
            Map.entry("ZA", List.of(
                    new OperatorInfo("Vodacom", "01", "655", "ZA", "vodacom.co.za"),
                    new OperatorInfo("MTN", "10", "655", "ZA", "mtn.co.za"),
                    new OperatorInfo("Telkom", "02", "655", "ZA", "telkom.co.za")
            ))
    );
}
