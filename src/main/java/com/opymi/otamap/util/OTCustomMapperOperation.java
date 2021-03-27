package com.opymi.otamap.util;

/**
 * Interface that defines the custom behavior for the mapping of the {@param <ORIGIN>} to {@param <TARGET>}
 *
 * @author Antonino Verde
 * @since 1.0
 */
public interface OTCustomMapperOperation<ORIGIN, TARGET> {

    void customMap(ORIGIN origin, TARGET target);

}
