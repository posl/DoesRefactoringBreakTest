package utils.db;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * This is for hibernate to transform LocalDateTime and java.sql.Timestamp
 */
@Converter(autoApply = true)
public class LocalDateTimeToTimestampConverter implements AttributeConverter<LocalDateTime, java.sql.Timestamp>, Serializable {
    //transform(LocalDateTime→Timestamp)
    @Override
    public java.sql.Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
    	if (localDateTime == null)
            return null;
        return java.sql.Timestamp.valueOf(localDateTime);
    }
    //transform(Timestamp→LocalDateTime)
    @Override
    public LocalDateTime convertToEntityAttribute(java.sql.Timestamp timestamp) {
    	if (timestamp == null)
            return null;
        return timestamp.toLocalDateTime();
    }
}
