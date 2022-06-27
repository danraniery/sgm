package com.bomdestino.sgm.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.bomdestino.sgm.util.Constants.*;

/**
 * Domain class for Service entity.
 */
@Data
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = SERVICE)
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SGMService extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = NAME, nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(min = 2, max = 256)
    @Column(name = PATH, nullable = false, length = 256, unique = true)
    private String path;

    @NotNull
    @Column(name = IS_LOCAL_PATH, nullable = false)
    private boolean localPath;

    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Area> areas = new HashSet<>();

}
