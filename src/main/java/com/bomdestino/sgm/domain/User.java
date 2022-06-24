package com.bomdestino.sgm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.bomdestino.sgm.util.Constants.*;

/**
 * Domain class for User entity.
 */
@Data
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = USER)
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 256)
    @Column(name = USERNAME, nullable = false, length = 256)
    private String username;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = NAME, nullable = false, length = 100)
    private String name;

    @JsonIgnore
    @NotBlank
    @Column(name = PASSWORD_HASH, nullable = false)
    private String password;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = PROFILE_ID, nullable = false)
    private Profile profile;

    @Column(name = LAST_PASSWORD_UPDATE)
    private Instant lastPasswordUpdate;

    @NotNull
    @Column(name = LAST_LOGON_ATTEMPT, nullable = false)
    private Instant lastLogonAttemptDate;

    @Column(name = LAST_LOGIN)
    private Instant lastLogin;

    @Builder.Default
    @NotNull
    @Column(name = LOGON_ATTEMPT, nullable = false)
    private Integer logonAttemptCounts = 0;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @NotNull
    @Column(name = LAST_PASSWORD_HASH, nullable = false)
    private List<String> lastPasswordHashes = new ArrayList<>();

    @Builder.Default
    @NotNull
    @Column(name = IS_SUPER_USER, nullable = false)
    private boolean superUser = false;

    @Builder.Default
    @NotNull
    @Column(name = IS_BLOCKED, nullable = false)
    private boolean blocked = false;

}
