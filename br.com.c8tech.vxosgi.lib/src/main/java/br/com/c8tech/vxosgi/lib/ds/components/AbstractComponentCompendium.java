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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.prefs.PreferencesService;
import org.slf4j.Logger;

import br.com.c8tech.vxosgi.lib.ConstantsLib;
import br.com.c8tech.vxosgi.lib.ConstantsLogging;

/**
 * A basic abstract parent class for OSGi Declarative Service component classes
 * declared using the spec's annotations.
 * <p>
 * Also some custom annotations as {@link DsConfigComponent} can be used in
 * order to facilitate the component configuration.
 *
 * Developer can implement the provided abstract methods in order to specialize
 * the component lifecycle, also its default behavior.
 * <p>
 * Unfortunately the specification doesn't allow us to use annotations in the
 * base parent classes, at least the current implementation doesn't recognize
 * those annotated super classes.
 *
 * So every children class of this class <b>must</b> declare it own public
 * methods, annotate them using the DS annotations and then call the provided
 * protected methods of this class inside it.
 *
 * This class provide at least on methods for each lifecycle phase (start,
 * activate, deactivate, modify).
 *
 * @since 0.1.1
 * @author Cristiano Gavião
 *
 */
@ProviderType
public abstract class AbstractComponentCompendium extends AbstractComponent {

    /**
     * Holds an atomic reference of a {@link ConfigurationAdmin} service.
     */
    private AtomicReference<ConfigurationAdmin> configAdminServiceRef;

    /**
     * Holds an atomic reference of a {@link EventAdmin} service.
     */
    private AtomicReference<EventAdmin> eventAdminServiceRef;

    /**
     * Holds an atomic reference of a {@link PreferencesService} service.
     */
    private AtomicReference<PreferencesService> preferencesServiceRef;

    /**
     * DS needs a default constructor. But the children classes must call the
     * constructor {@link #AbstractComponent(Logger)} and pass the class logger
     * instance.
     */
    public AbstractComponentCompendium() {
    }

    /**
     * Constructor that should be called by the children classe's default
     * constructor in order to explicitly pass the {@link Logger} instance
     * created for it.
     *
     * @param pLogger
     *            The logger instance used by the children class.
     */
    public AbstractComponentCompendium(Logger pLogger) {
        super(pLogger);
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi
     * Declarative Service.
     *
     * @param pInjectedBundleContext
     * @param pInjectedComponentContext
     */
    public AbstractComponentCompendium(Logger pLogger,
            ComponentContext pInjectedComponentContext) {
        super(pLogger, pInjectedComponentContext);
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi
     * Declarative Service.
     *
     * @param pLogger
     *            The logger instance used by the children class.
     * @param pConfigurationAdmin
     *            The mock of the ConfigurationAdmin service to work with.
     * @param pEventAdmin
     *            The mock of the EventAdmin service to work with.
     * @param pPreferencesService
     *            The mock of the PreferencesService service to work with.
     */
    public AbstractComponentCompendium(Logger pLogger,
            ConfigurationAdmin pConfigurationAdmin) {
        this(pLogger);
        defaultBindConfigurationAdminService(pConfigurationAdmin);
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi
     * Declarative Service.
     *
     * @param pLogger
     *            The logger instance used by the children class.
     * @param pConfigurationAdmin
     *            The mock of the ConfigurationAdmin service to work with.
     * @param pEventAdmin
     *            The mock of the EventAdmin service to work with.
     * @param pPreferencesService
     *            The mock of the PreferencesService service to work with.
     */
    public AbstractComponentCompendium(Logger pLogger,
            ConfigurationAdmin pConfigurationAdmin, EventAdmin pEventAdmin,
            PreferencesService pPreferencesService) {
        this(pLogger);
        defaultBindConfigurationAdminService(pConfigurationAdmin);
        defaultBindEventAdminService(pEventAdmin);
        defaultBindPreferencesService(pPreferencesService);
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi
     * Declarative Service.
     *
     * @param pLogger
     *            The logger instance used by the children class.
     * @param pEventAdmin
     *            The mock of the EventAdmin service to work
     */
    public AbstractComponentCompendium(Logger pLogger, EventAdmin pEventAdmin) {
        this(pLogger);
        defaultBindEventAdminService(pEventAdmin);
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi
     * Declarative Service.
     *
     * @param pLogger
     *            The logger instance used by the children class.
     * @param pPreferencesService
     *            The mock of the PreferencesService service to work with.
     */
    public AbstractComponentCompendium(Logger pLogger,
            PreferencesService pPreferencesService) {
        this(pLogger);
        defaultBindPreferencesService(pPreferencesService);
    }

    /**
     * This method is aimed to be used to bind an instance of the
     * {@link ConfigurationAdmin} interface to the current instance of this
     * component.
     * <p>
     * It must be overridden in child concrete classes and tagged
     * with @Reference annotation in order to allow the
     * bndlib/maven-bundle-plugin to detected it and generate the proper
     * configuration files.
     * <p>
     *
     * <pre>
     * {@literal @}Reference(policy = ReferencePolicy.DYNAMIC,
     *       cardinality = ReferenceCardinality.OPTIONAL)
     * {@literal @}Override
     * protected void bindConfigurationAdminService(final ConfigurationAdmin pConfigAdminService) {
     *    super(pConfigAdminService);
     * }
     *
     * </pre>
     *
     * @see #defaultBindEventAdminService(EventAdmin)
     * @see #unbindEventAdminService(EventAdmin)
     * @param eventAdmin
     *            the injected EventAdmin service instance. It can be null.
     */
    protected void bindConfigurationAdminService(
            final ConfigurationAdmin pConfigAdminService) {
        defaultBindConfigurationAdminService(pConfigAdminService);
    }

    /**
     * This method is aimed to be used to bind an instance of the
     * {@link EventAdmin} interface to the current instance of this component.
     * <p>
     * It must be overridden in child concrete classes and tagged
     * with @Reference annotation in order to allow the
     * bndlib/maven-bundle-plugin to detected it and generate the proper
     * configuration files.
     * <p>
     *
     * <pre>
     * {@literal @}Reference(policy = ReferencePolicy.DYNAMIC,
     *       cardinality = ReferenceCardinality.OPTIONAL)
     * {@literal @}Override
     * protected void bindEventAdminService(final EventAdmin pEventAdmin) {
     *    super(pEventAdmin);
     * }
     *
     * </pre>
     *
     * @see #defaultBindEventAdminService(EventAdmin)
     * @see #unbindEventAdminService(EventAdmin)
     * @param eventAdmin
     *            the injected EventAdmin service instance. It can be null.
     */
    protected void bindEventAdminService(final EventAdmin pEventAdmin) {
        defaultBindEventAdminService(pEventAdmin);
    }

    /**
     * This method is aimed to be used to bind an instance of the
     * {@link PreferencesService} interface to the current instance of this
     * component.
     * <p>
     * It must be implemented in child concrete classes in order to allow the
     * Declarative Service extender to bind an instance of the specified service
     * in this component instance.
     * <p>
     * Inside the overridden method the developer must call the method
     * {@link #defaultBindPreferencesService(PreferencesService)}, since it
     * already take care of the binding process.
     * <p>
     * Also, developers must add the following annotation on top of the concrete
     * method:
     *
     * <pre>
     * {@literal @}Reference(policy = ReferencePolicy.DYNAMIC,
     *       cardinality = ReferenceCardinality.OPTIONAL)
     *
     * </pre>
     *
     * @see #defaultBindPreferencesService(PreferencesService)
     * @param preferencesService
     *            the injected PreferencesService service instance. It can be
     *            null.
     */
    protected void bindPreferencesService(
            final PreferencesService pPreferencesService) {
        defaultBindPreferencesService(pPreferencesService);
    }

    protected final void defaultBindConfigurationAdminService(
            final ConfigurationAdmin pConfigAdminService) {
        getConfigurationAdminServiceRef().set(pConfigAdminService);
        getLogger().trace(ConstantsLogging.MARKER_SERVICE_BINDING,
                "Bound ConfigurationAdmin service for component.{}", "");
    }

    /**
     * This method will log and save the injected instance of {@link EventAdmin}
     * service.
     *
     * @see #bindEventAdminService(EventAdmin)
     * @param pEventAdmin
     *            the injected EventAdmin service instance. It can be null.
     */
    protected final void defaultBindEventAdminService(
            final EventAdmin pEventAdmin) {
        getEventAdminServiceRef().set(pEventAdmin);
        getLogger().trace(ConstantsLogging.MARKER_SERVICE_BINDING,
                "Bound EventAdmin service for component.{}", "");
    }

    /**
     * This method will log and save the injected instance of
     * {@link PreferencesService} service.
     *
     * @see #bindPreferencesService(PreferencesService)
     * @param preferencesService
     *            the injected PreferencesService service instance. It can be
     *            null.
     */
    protected final void defaultBindPreferencesService(
            final PreferencesService preferencesService) {
        getPreferencesServiceRef().set(preferencesService);
        getLogger().trace(ConstantsLogging.MARKER_SERVICE_BINDING,
                "Bound PreferencesService for component.{}", "");
    }

    /**
     * Method called by the DS or other to unbind an instance of
     * {@link EventAdmin} service.
     *
     * @param pEventAdmin
     *            the injected EventAdmin service instance.
     */
    protected final void defaultUnbindConfigurationAdminService(
            final ConfigurationAdmin pConfigAdminService) {
        getConfigurationAdminServiceRef().compareAndSet(pConfigAdminService,
                null);
        getLogger().trace(ConstantsLogging.MARKER_SERVICE_BINDING,
                "Unbound ConfigurationAdmin for component '{}'.", getId());
    }

    /**
     * Method called by the DS or other to unbind an instance of
     * {@link EventAdmin} service.
     *
     * @param pEventAdmin
     *            the injected EventAdmin service instance.
     */
    protected final void defaultUnbindEventAdminService(
            final EventAdmin pEventAdmin) {
        getEventAdminServiceRef().compareAndSet(pEventAdmin, null);
        getLogger().trace(ConstantsLogging.MARKER_SERVICE_BINDING,
                "Unbound EventAdmin for component '{}'.", getId());
    }

    /**
     * Method called by the DS or other to unbind an instance of
     * {@link EventAdmin} service.
     *
     * @param pEventAdmin
     *            the injected EventAdmin service instance.
     */
    protected final void defaultUnbindPreferencesService(
            final PreferencesService pPreferencesService) {
        getPreferencesServiceRef().compareAndSet(pPreferencesService, null);
        getLogger().trace(ConstantsLogging.MARKER_SERVICE_BINDING,
                "Unbound PreferencesService for component '{}'.", getId());
    }

    protected ConfigurationAdmin getConfigurationAdmin() {
        return getConfigurationAdminServiceRef().get();
    }

    /**
     *
     * @return the atomic reference for EventAdmin.
     */
    private AtomicReference<ConfigurationAdmin> getConfigurationAdminServiceRef() {
        if (configAdminServiceRef == null) {
            configAdminServiceRef = new AtomicReference<>();
        }
        return configAdminServiceRef;
    }

    /**
     * A method that returns the {@link EventAdmin} service instance.
     *
     * @return the EventAdmin service instance. It can be null.
     */
    protected final EventAdmin getEventAdminService() {
        return getEventAdminServiceRef().get();
    }

    /**
     *
     * @return the atomic reference for EventAdmin.
     */
    private AtomicReference<EventAdmin> getEventAdminServiceRef() {
        if (eventAdminServiceRef == null) {
            eventAdminServiceRef = new AtomicReference<>();
        }
        return eventAdminServiceRef;
    }

    /**
     * A method that returns the {@link PreferencesService} instance.
     *
     * @return the PreferencesService instance. It can be null.
     */
    protected final PreferencesService getPreferencesService() {
        return getPreferencesServiceRef().get();
    }

    /**
     *
     * @return the atomic reference for PreferencesService.
     */
    private AtomicReference<PreferencesService> getPreferencesServiceRef() {
        if (preferencesServiceRef == null) {
            preferencesServiceRef = new AtomicReference<>();
        }
        return preferencesServiceRef;
    }

    @Override
    protected void initializeComponentProperties(
            ComponentContext pInjectedComponentContext) {
    }

    /**
     * Post an event (asynchronously) using the specified topic.
     *
     * @param pEventTopic
     *            the topic of the event being sent.
     */
    protected final void postEvent(final String pEventTopic) {
        Map<String, Object> properties = new HashMap<>();
        postEvent(pEventTopic, properties);
    }

    /**
     * Post an event (asynchronously) using the specified topic, attaching the
     * specified properties map to it.
     *
     * @param pEventTopic
     *            the topic of the event being sent.
     * @param pPropertiesMap
     *            the properties map to be attached to the event.
     */
    protected final void postEvent(final String pEventTopic,
            final Map<String, ?> pPropertiesMap) {
        Event event = new Event(pEventTopic, pPropertiesMap);
        getEventAdminService().postEvent(event);
    }

    /**
     * Post an event (asynchronously) using the specified topic, attaching the
     * specified context as a property of its properties map.
     *
     * @param pEventTopic
     *            the topic of the event being sent.
     * @param pContext
     *            the context that will be added to event properties.
     */
    protected final void postEvent(final String pEventTopic,
            final String pContext) {
        Map<String, Object> properties = new HashMap<>();

        properties.put(ConstantsLib.EVENTS_PROPERTIES_CONTEXT, pContext);
        postEvent(pEventTopic, properties);
    }

    @Override
    protected void resetComponentProperties() {

    }

    /**
     * Send an event (synchronously) using the specified topic.
     *
     * @param pEventTopic
     *            the topic of the event being sent.
     */
    protected final void sendEvent(final String pEventTopic) {
        Dictionary<String, Object> properties = new Hashtable<>();// NOSONAR
        sendEvent(pEventTopic, properties);
    }

    /**
     * Send an event (synchronously) using the specified topic and attaching the
     * specified properties map to it.
     *
     * @param pEventTopic
     *            the topic of the event being sent.
     * @param pProperties
     *            the properties map to be attached to the event.
     */
    protected final void sendEvent(final String pEventTopic,
            final Dictionary<String, ?> pProperties) {
        Event event = new Event(pEventTopic, pProperties);
        getEventAdminService().sendEvent(event);
    }

    /**
     * Sends an event using the specified topic, attaching the specified context
     * as one property of its properties map.
     *
     * @param pEventTopic
     *            the topic of the event being sent.
     * @param pContext
     *            the context that will be added to event properties.
     */
    protected final void sendEvent(final String pEventTopic,
            final String pContext) {
        Dictionary<String, Object> properties = new Hashtable<>(); // NOSONAR

        properties.put(ConstantsLib.EVENTS_PROPERTIES_CONTEXT, pContext);
        sendEvent(pEventTopic, properties);
    }

    protected void unbindConfigurationAdminService(
            final ConfigurationAdmin pConfigAdminService) {
        defaultUnbindConfigurationAdminService(pConfigAdminService);
    }

    protected void unbindEventAdminService(final EventAdmin pEventAdmin) {
        defaultUnbindEventAdminService(pEventAdmin);
    }

    protected void unbindPreferencesService(
            final PreferencesService pPreferencesService) {
        defaultUnbindPreferencesService(pPreferencesService);
    }

}
