package com.magg.common.model;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
public class BaseFields<T extends BaseFields>
{

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    protected String id;

    @Column(nullable = false, name = "created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreationTimestamp
    protected Instant createdAt;
    @Column(nullable = false, name = "updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @UpdateTimestamp
    protected Instant updatedAt;

    @Version
    protected Long version;

    public BaseFields()
    {
    }

    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public Instant getCreatedAt()
    {
        return createdAt;
    }


    public void setCreatedAt(Instant createdAt)
    {
        this.createdAt = createdAt;
    }


    public Instant getUpdatedAt()
    {
        return updatedAt;
    }


    public void setUpdatedAt(Instant updatedAt)
    {
        this.updatedAt = updatedAt;
    }


    public Long getVersion()
    {
        return version;
    }


    public void setVersion(Long version)
    {
        this.version = version;
    }
}
