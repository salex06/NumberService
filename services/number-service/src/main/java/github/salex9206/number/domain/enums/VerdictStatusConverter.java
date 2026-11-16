package github.salex9206.number.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class VerdictStatusConverter
        implements AttributeConverter<VerdictStatus, String> {
    @Override
    public String convertToDatabaseColumn(VerdictStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public VerdictStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : VerdictStatus.valueOf(dbData);
    }
}
