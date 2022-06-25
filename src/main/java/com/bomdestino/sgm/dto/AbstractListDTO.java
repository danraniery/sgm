package com.bomdestino.sgm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * A general DTO object representing a simple entity for list components, with its id and name.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractListDTO implements Serializable {

    private Long id;
    private String name;
    private boolean activated;

}
