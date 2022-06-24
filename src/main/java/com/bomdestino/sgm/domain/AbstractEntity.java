package com.bomdestino.sgm.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

import static com.bomdestino.sgm.util.Constants.*;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy
    @NotBlank
    @Column(name = CREATED_BY, nullable = false, updatable = false)
    private String createdBy;

    @Builder.Default
    @CreatedDate
    @NotNull
    @Column(name = CREATED_DATE, nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @NotBlank
    @Column(name = LAST_MODIFIED_BY, nullable = false, length = 256)
    private String lastModifiedBy;

    @Builder.Default
    @LastModifiedDate
    @NotNull
    @Column(name = LAST_MODIFIED_DATE, nullable = false)
    private Instant lastModifiedDate = Instant.now();

    @Builder.Default
    @NotNull
    @Column(name = ACTIVATED, nullable = false)
    private boolean activated = false;

}
