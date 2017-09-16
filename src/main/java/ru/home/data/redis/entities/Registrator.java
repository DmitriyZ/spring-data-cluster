package ru.home.data.redis.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Date;

/**
 * Created by Zaets Dmitriy on 23.03.2017.
 */
@Data
@Builder
@RedisHash("registrators")
@NoArgsConstructor
public class Registrator {

    @Id
    @Indexed
    private Long id;
    @Indexed
    private Integer version;
    @Indexed
    private int lastMessageId;
    @Indexed
    private Date lastAuthDate;
    @Indexed
    private boolean allDataSended;
    private byte[] settings;

    public Registrator(Long id, Integer version, int lastMessageId, Date lastAuthDate, boolean allDataSended, byte[] settings) {
        this.id = id;
        this.version = version;
        this.lastMessageId = lastMessageId;
        this.lastAuthDate = lastAuthDate;
        this.allDataSended = allDataSended;
        this.settings = settings;
    }
}
