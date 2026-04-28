package com.example.api.models.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// tbd. The way Vib is constructed in GET /definitions is not as it is constructed in PUT /change
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Definition {
    private Logger logger;
    private Vib vib;
}
