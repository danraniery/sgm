package com.bomdestino.sgm.domain;

import com.bomdestino.sgm.config.security.auth.SGMRole;
import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

import static com.bomdestino.sgm.util.Constants.*;

/**
 * Domain class for Profile entity.
 */
@Data
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = PROFILE)
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Profile extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = NAME, nullable = false, length = 100)
    private String name;

    @Size(max = 256)
    @Column(name = DESCRIPTION, length = 256)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = ROLES, nullable = false)
    private Set<SGMRole> roles;

    @Builder.Default
    @NotNull
    @Column(name = IS_ONLY_READ, nullable = false)
    private boolean onlyRead = false;

}
