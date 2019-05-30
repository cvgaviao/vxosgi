/**
 * ==========================================================================
 * Copyright © 2015-2019 Cristiano Gavião, C8 Technology ME.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Cristiano Gavião (cvgaviao@c8tech.com.br)- initial API and implementation
 * ==========================================================================
 */
package br.com.c8tech.vxosgi.lib.ds.components;

import org.slf4j.Logger;

public abstract class AbstractComponentServiceProvider
        extends AbstractComponentCompendium {

    /**
     * DS needs a default constructor. But the children classes must call the
     * constructor {@link #AbstractComponent(Logger)} and pass the class logger
     * instance.
     */
    public AbstractComponentServiceProvider() {
    }

    /**
     * Constructor that should be called by the children classe's default
     * constructor in order to explicitly pass the {@link Logger} instance
     * created for it.
     * 
     * @param pLogger
     *            The logger instance used by the children class.
     */
    public AbstractComponentServiceProvider(Logger pLogger) {
        super(pLogger);
    }

}
