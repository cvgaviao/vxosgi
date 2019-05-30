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

import static org.osgi.service.component.ComponentConstants.COMPONENT_ID;
import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;

import java.util.Objects;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.c8tech.vxosgi.lib.ConstantsLogging;
import br.com.c8tech.vxosgi.lib.cm.ConfigurationUtil;
import br.com.c8tech.vxosgi.lib.ds.ComponentWorkflowException;

/**
 * A basic abstract parent class for OSGi Declarative Service component classes
 * based on spec annotations.
 * <p>
 * It basically has some abstract methods which are the default one used by DS.
 * <p>
 * Unfortunately the specification do no allow us to put annotations on base
 * parent classes. It <b>must</b> be done on every children class. The methods
 * on this class are aimed to help with this challenge.
 * 
 * @since 0.1.1
 * @author Cristiano Gavião
 *
 */
@ProviderType
public abstract class AbstractComponent {

    private static final String MSG = " of component instance id-{} from '{}'.";

    private static final String MSG_END_ACTIVATION = "Activated component instance id-{} from '{}'.";
    private static final String MSG_END_CONFIGURATION = "Ended configuration"
            + MSG;
    private static final String MSG_END_DEACTIVATION = "Deactivated component instance id-{} from '{}'.";
    private static final String MSG_END_MODIFICATION = "Ended the configuration modification"
            + MSG;
    private static final String MSG_INI_ACTIVATION = "Initiated activation"
            + MSG;
    private static final String MSG_INI_CONFIGURATION = "Initiated configuration"
            + MSG;
    private static final String MSG_INI_DEACTIVATION = "Initiated deactivation of component instance id-{} with reason '{}'"
            + MSG;
    private static final String MSG_INI_MODIFICATION = "Started the configuration modification"
            + MSG;

    /**
     * the injected component context object related to this component instance.
     */
    private ComponentContext componentContext;

    /**
     * The component ID is defined by DS bundle.
     */
    private Long componentId;

    /**
     * The component name is defined by the component's developer.
     */
    private String componentName;

    private Logger logger;

    /**
     * DS needs a default constructor. But the children classes must call the
     * constructor {@link #AbstractComponent(Logger)} and pass the class logger
     * instance.
     */
    public AbstractComponent() {
    }

    /**
     * Constructor that should be called by the children classe's default
     * constructor in order to explicitly pass the {@link Logger} instance
     * created for it.
     * 
     * @param pLogger
     *            The logger instance used by the children class.
     */
    public AbstractComponent(Logger pLogger) {
        logger = pLogger;
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi
     * Declarative Service.
     *
     * @param pInjectedBundleContext
     * @param pInjectedComponentContext
     */
    public AbstractComponent(Logger pLogger,
            ComponentContext pInjectedComponentContext) {
        logger = pLogger;
        componentContext = pInjectedComponentContext;
    }

    /**
     * This method may be overridden by concrete child classes and annotated
     * with {@link Activate @Activate} annotation in order to ensure that it
     * will be called by OSGi Declarative Service extender when modifying the
     * component instance. Recent versions will call this method even without
     * the annotation, though.
     * <p>
     * 
     * An default behavior for the component instance modification is already
     * set by the use of the method
     * {@link #defaultComponentActivationWorkflow(ComponentContext)}, but a
     * custom behavior is allowed by simply overriding the method
     * {@link #afterActivationWorkflow()}.<br>
     *
     * But the developer can choose a different activation workflow method.
     *
     * @param pInjectedComponentContext
     *            the injected component context object related to this
     *            component instance.
     * @throws ComponentWorkflowException
     *             an exception that will make DS fail the activation.
     */
    public void activate(final ComponentContext pInjectedComponentContext)
            throws ComponentWorkflowException {
        defaultComponentActivationWorkflow(pInjectedComponentContext);
    }

    /**
     * This is custom activate method called by one of those provided activation
     * workflow methods.
     * 
     * It must be overridden by concrete classes where all custom initialization
     * behavior must be coordinated.
     * <p>
     * The activation process is initiated by the OSGi Declarative Service
     * through a method annotated by {@literal @Activate}. <br>
     * It is a developer's job then to call the method
     * {@link #defaultComponentActivationWorkflow(ComponentContext, Object)} in
     * order to proper initialize the default behavior of this component.
     * <p>
     * This method will be called after the default initialization steps have
     * occurred and before it have ended.
     *
     * @see #afterActivationWorkflow(ComponentContext)
     * @see #defaultComponentActivationWorkflow(ComponentContext)
     *
     * @throws ComponentWorkflowException
     *             an exception that will make DS fail the activation.
     */
    protected void afterActivationWorkflow() throws ComponentWorkflowException {
    }

    /**
     * This empty method is aimed to be optionally overridden by concrete
     * subclasses in order to extend the default deactivate workflow.
     * <p>
     * The deactivate process is initiated by the OSGi Declarative Service
     * through a method annotated by {@literal @Deactivate}. <br>
     * It is a developer's job then to call the method
     * {@link #defaultComponentDeactivationWorkflow(ComponentContext)} in order
     * to proper deactivate this component.
     * <p>
     * This method will be called before the default deactivate steps have
     * occurred.
     * 
     * @param pDeactivationReason
     *
     * @see #deactivate(ComponentContext)
     * @see #defaultComponentDeactivationWorkflow(ComponentContext)
     *
     */
    protected void beforeDeactivationWorkflow(Integer pDeactivationReason)
            throws ComponentWorkflowException {
    }

    /**
     *
     * This method may be overridden by concrete child classes and annotated
     * with {@link Deactivate @Deactivate} annotation in order to ensure that it
     * will be called by OSGi Declarative Service extender when modifying the
     * component instance. Recent versions will call this method even without
     * the annotation, though.
     * <p>
     * 
     * An default behavior for the component instance modification is already
     * set by the use of the method
     * {@link #defaultComponentDeactivationWorkflow(ComponentContext)}, but a
     * custom behavior is allowed by simply overriding the method
     * {@link #beforeDeactivationWorkflow(Integer)}.<br>
     *
     * But the developer can choose a different activation workflow method.
     *
     * @param pInjectedComponentContext
     *            the injected component context object related to this
     *            component instance.
     * @throws ComponentWorkflowException
     *             an exception that will make DS fail the activation.
     */
    public void deactivate(final Integer pDeactivationReason)
            throws ComponentWorkflowException {
        defaultComponentDeactivationWorkflow(pDeactivationReason);
    }

    /**
     *
     * @see #activate(ComponentContext)
     * @see #initializeComponentProperties(ComponentContext)
     * @see #afterActivationWorkflow()
     * @param pInjectedComponentContext
     *            the injected component context object related to this
     *            component instance.
     * @param pProperties
     *            the injected properties of this component. This allows the
     *            injection of Component Property Types.
     * @throws ComponentWorkflowException
     *             an exception that will make DS fail the activation.
     */
    protected final void defaultComponentActivationWorkflow(
            final ComponentContext pInjectedComponentContext)
            throws ComponentWorkflowException {

        getLogger().trace(ConstantsLogging.MARKER_COMPONENT_LIFECYCLE,
                MSG_INI_ACTIVATION, componentId, getNameShort());

        initializeMandatoryComponentProperties(pInjectedComponentContext);

        getLogger().trace(ConstantsLogging.MARKER_COMPONENT_LIFECYCLE,
                MSG_INI_CONFIGURATION, componentId, getNameShort());

        initializeComponentProperties(pInjectedComponentContext);

        getLogger().trace(ConstantsLogging.MARKER_COMPONENT_LIFECYCLE,
                MSG_END_CONFIGURATION, componentId, getNameShort());

        // calls custom children optional activation method
        afterActivationWorkflow();

        getLogger().info(ConstantsLogging.MARKER_COMPONENT_LIFECYCLE,
                MSG_END_ACTIVATION, getId(), getNameShort());
    }

    /**
     * @param pDeactivationReason
     * @see #deactivate(ComponentContext)
     * @see #beforeDeactivate(Integer)
     * @param pInjectedComponentContext
     * @throws ComponentWorkflowException
     */
    protected final void defaultComponentDeactivationWorkflow(
            Integer pDeactivationReason) throws ComponentWorkflowException {

        getLogger().debug(ConstantsLogging.MARKER_COMPONENT_LIFECYCLE,
                MSG_INI_DEACTIVATION, getId(), pDeactivationReason);
        beforeDeactivationWorkflow(pDeactivationReason);

        resetMandatoryComponentProperties();
    }

    /**
     *
     * @see #modified(ComponentContext)
     * @see #afterModificationWorkflow(ComponentContext)
     * @param pInjectedComponentContext
     *            the injected component context object related to this
     *            component instance.
     * @throws ComponentWorkflowException
     */
    protected final void defaultComponentModificationWorkflow(
            final ComponentContext pInjectedComponentContext)
            throws ComponentWorkflowException {

        initializeMandatoryComponentProperties(pInjectedComponentContext);
        getLogger().debug(ConstantsLogging.MARKER_COMPONENT_LIFECYCLE,
                MSG_INI_MODIFICATION, getId(), getNameShort());

        initializeComponentProperties(pInjectedComponentContext);

        // calls custom children optional activation method
        afterModificationWorkflow(pInjectedComponentContext);

        getLogger().trace(ConstantsLogging.MARKER_COMPONENT_LIFECYCLE,
                MSG_END_MODIFICATION, getId(), getNameShort());
    }

    /**
     * This method returns the associated BundleContext.
     *
     * @return the bundle context associated to this component.
     */
    protected final BundleContext getBundleContext() {
        if (componentContext != null
                && componentContext.getBundleContext() != null) {
            return componentContext.getBundleContext();
        } else {
            Bundle bundle = FrameworkUtil.getBundle(this.getClass());
            return bundle.getBundleContext();
        }
    }

    /**
     * This method returns the associated ComponentContext.
     *
     * @return the ComponentContext.
     */
    protected final ComponentContext getComponentContext() {
        return componentContext;
    }

    public final ConfigurationUtil getContextProperties() {
        return ConfigurationUtil.instance(getComponentContext().getProperties());
    }

    /**
     * The ID of the component instance.
     *
     * @return the id generated for this component.
     */
    public final Long getId() {
        return componentId != null ? componentId : 0;
    }

    /**
     * The location of the bundle that holds this component instance.
     *
     * @return the location for the component's bundle.
     */
    protected final String getLocation() {
        return getBundleContext().getBundle().getLocation();
    }

    protected final Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(getClass());
        }
        return logger;
    }

    /**
     * The name of the this component instance.
     * <p>
     * This value is set after the component be activated by DS.
     *
     * @return the component's name.
     */
    public final String getName() {
        return componentName != null ? componentName
                : getClass().getSimpleName();
    }

    /**
     * The latest segment of name of the this component instance.
     * <p>
     * This value is set after the component be activated by DS.
     *
     * @return the component's name.
     */
    public final String getNameShort() {
        return componentName != null
                ? componentName.substring(componentName.lastIndexOf('.') + 1)
                : getClass().getSimpleName();
    }

    /**
     * The version of the bundle that contains this component.
     *
     * @return the version of bundle that contains this component.
     */
    public final String getVersion() {
        return getBundleContext() != null
                ? getBundleContext().getBundle().getVersion().toString()
                : "not activated";
    }

    /**
     * Do the initialization of some injected properties.
     *
     * @param pInjectedComponentContext
     *            the injected component context object related to this
     *            component instance.
     * @param pProperties
     *            the injected properties of this component. This allows the
     *            injection of Component Property Types.
     */
    protected abstract void initializeComponentProperties(
            final ComponentContext pInjectedComponentContext);

    /**
     * This method is used to initialize the basic attributes of this component
     * with values coming from properties injected by OSGi DS.
     * <p>
     * It is part of the default activation workflow defined by the method
     * {@link #defaultComponentActivationWorkflow(ComponentContext, Object)}.
     *
     * @see #afterActivationWorkflow()
     * @see #defaultComponentActivationWorkflow(ComponentContext)
     * @see #initializeComponentProperties(ComponentContext)
     *
     * @param pInjectedComponentContext
     *            the injected component context object related to this
     *            component instance.
     * @param pProperties
     *            the injected properties of this component. This allows the
     *            injection of Component Property Types.
     */
    protected final void initializeMandatoryComponentProperties(
            final ComponentContext pInjectedComponentContext) {

        // save bundleContext reference...
        this.componentContext = pInjectedComponentContext;

        // set common component attributes
        componentId = (Long) componentContext.getProperties().get(COMPONENT_ID);
        componentName = (String) componentContext.getProperties()
                .get(COMPONENT_NAME);

    }

    /**
     * This method may be overridden by concrete child classes and annotated
     * with {@link Modified @Modified} annotation in order to ensure that it
     * will be called by OSGi Declarative Service extender when modifying the
     * component instance. Recent versions will call this method even without
     * the annotation, though.
     * <p>
     * 
     * An default behavior for the component instance modification is already
     * set by the use of the method
     * {@link #defaultComponentModificationWorkflow(ComponentContext)}, but a
     * custom behavior is allowed by simply overriding the method
     * {@link #afterModificationWorkflow(ComponentContext)}.<br>
     *
     * But the developer can choose a different activation workflow method.
     *
     * @param pInjectedComponentContext
     *            the injected component context object related to this
     *            component instance.
     * @throws ComponentWorkflowException
     *             an exception that will make DS fail the activation.
     */
    public void modified(final ComponentContext pInjectedComponentContext)
            throws ComponentWorkflowException {
        defaultComponentModificationWorkflow(pInjectedComponentContext);
    }

    /**
     * 
     * @param pInjectedComponentContext
     * @throws ComponentWorkflowException
     */
    protected void afterModificationWorkflow(
            final ComponentContext pInjectedComponentContext)
            throws ComponentWorkflowException {
    }

    /**
     * This method is part of the default deactivate workflow and must be used
     * in order to extend the resetting of attributes of children component
     * implementations.
     */
    protected abstract void resetComponentProperties();

    /**
     * This method is used to reset the basic attributes of this component.
     * <p>
     * It is part of the default deactivate workflow defined by the method
     * {@link #defaultComponentDeactivationWorkflow(ComponentContext)}.
     *
     * @see #deactivate(ComponentContext, Object)
     * @see #defaultComponentDeactivationWorkflow(ComponentContext)
     * @see #resetComponentProperties()
     */
    protected final void resetMandatoryComponentProperties() {
        resetComponentProperties();

        getLogger().info(ConstantsLogging.MARKER_COMPONENT_LIFECYCLE,
                MSG_END_DEACTIVATION, getId(), getNameShort());

        componentName = null;
        componentId = null;
        componentContext = null;

    }

    @Override
    public String toString() {

        return getClass().getName() + "[id=" + Objects.toString(componentId)
                + ", name=" + Objects.toString(getName()) + ", version="
                + Objects.toString(getVersion()) + "]";
    }

}
