package com.gjasinski.awssarjava.yaml.policies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KMSDecryptPolicy {
    public String KeyId;

}