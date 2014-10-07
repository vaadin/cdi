package com.vaadin.cdi.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.ClientMethodInvocation;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Extension;
import com.vaadin.server.Resource;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

public class MyComponent extends MyBean implements Component {

    @Override
    public void addAttachListener(AttachListener listener) {
    }

    @Override
    public void removeAttachListener(AttachListener listener) {
    }

    @Override
    public void addDetachListener(DetachListener listener) {
    }

    @Override
    public void removeDetachListener(DetachListener listener) {
    }

    @Override
    public List<ClientMethodInvocation> retrievePendingRpcCalls() {
        return null;
    }

    @Override
    public boolean isConnectorEnabled() {
        return false;
    }

    @Override
    public Class<? extends SharedState> getStateType() {
        return null;
    }

    @Override
    public void requestRepaint() {

    }

    @Override
    public void markAsDirty() {
    }

    @Override
    public void requestRepaintAll() {
    }

    @Override
    public void markAsDirtyRecursive() {
    }

    @Override
    public boolean isAttached() {
        return false;
    }

    @Override
    public void detach() {
    }

    @Override
    public Collection<Extension> getExtensions() {
        return null;
    }

    @Override
    public void removeExtension(Extension extension) {
    }

    @Override
    public void beforeClientResponse(boolean initial) {
    }

    @Override
    public JSONObject encodeState() throws JSONException {
        return null;
    }

    @Override
    public boolean handleConnectorRequest(VaadinRequest request,
            VaadinResponse response, String path) throws IOException {
        return false;
    }

    @Override
    public ServerRpcManager<?> getRpcManager(String rpcInterfaceName) {
        return null;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
    }

    @Override
    public String getConnectorId() {
        return null;
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public Unit getWidthUnits() {
        return null;
    }

    @Override
    public Unit getHeightUnits() {
        return null;
    }

    @Override
    public void setHeight(String height) {
    }

    @Override
    public void setWidth(float width, Unit unit) {
    }

    @Override
    public void setHeight(float height, Unit unit) {
    }

    @Override
    public void setWidth(String width) {
    }

    @Override
    public void setSizeFull() {
    }

    @Override
    public void setSizeUndefined() {
    }

    @Override
    public void setWidthUndefined() {
    }

    @Override
    public void setHeightUndefined() {
    }

    @Override
    public String getStyleName() {
        return null;
    }

    @Override
    public void setStyleName(String style) {
    }

    @Override
    public void addStyleName(String style) {
    }

    @Override
    public void removeStyleName(String style) {
    }

    @Override
    public String getPrimaryStyleName() {
        return null;
    }

    @Override
    public void setPrimaryStyleName(String style) {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
    }

    @Override
    public void setParent(HasComponents parent) {
    }

    @Override
    public HasComponents getParent() {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public void setCaption(String caption) {
    }

    @Override
    public Resource getIcon() {
        return null;
    }

    @Override
    public void setIcon(Resource icon) {
    }

    @Override
    public UI getUI() {
        return null;
    }

    @Override
    public void attach() {
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void setId(String id) {
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void addListener(Listener listener) {
    }

    @Override
    public void removeListener(Listener listener) {
    }

}
