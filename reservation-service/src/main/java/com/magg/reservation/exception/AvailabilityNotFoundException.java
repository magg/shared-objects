/*
 * Copyright (c) 2020 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.magg.reservation.exception;

import com.magg.common.exception.EntityNotFoundException;

/**
 * Class Description goes here.
 * Created by miguelgonzalez on 22/07/20
 */
public class AvailabilityNotFoundException extends EntityNotFoundException
{
    /**
     * Constructor.
     *
     * @param objectId Error objectId.
     */
    public AvailabilityNotFoundException(String objectId) {
        super(objectId);
    }
}
