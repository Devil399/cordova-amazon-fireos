/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */
package org.apache.cordova;

import java.util.List;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;


/**
 * This class represents a service entry object.
 */
public class PluginEntry implements Comparable<PluginEntry> {

    /**
     * The name of the service that this plugin implements
     */
    public String service = "";

    /**
     * The plugin class name that implements the service.
     */
    public String pluginClass = "";

    /**
     * The plugin object.
     * Plugin objects are only created when they are called from JavaScript.  (see PluginManager.exec)
     * The exception is if the onload flag is set, then they are created when PluginManager is initialized.
     */
    public CordovaPlugin plugin = null;

    /**
     * Flag that indicates the plugin object should be created when PluginManager is initialized.
     */
    public boolean onload = false;

	/**
	 * The numerical priority used to determine which plugin takes precendence
	 * in a conflict scenario with consumable webview callbacks. Note that this
	 * is a float value, therefore decimal and negative values are supported.
	 * The priority field represents order, and for this reason a lower value
	 * will take precedence over a higher value.
	 */
	public float priority = 0;
    private List<String> urlFilters;

    /**
     * @param service               The name of the service
     * @param plugin                The plugin associated with this entry
     */
    public PluginEntry(String service, CordovaPlugin plugin) {
        this(service, plugin.getClass().getName(), true, null);
        this.plugin = plugin;
    }

    /**
     * @param service               The name of the service
     * @param pluginClass           The plugin class name
     * @param onload                Create plugin object when HTML page is loaded
     */
    public PluginEntry(String service, String pluginClass, boolean onload, float priority) {
        this(service, pluginClass, onload, null);
        this.priority = priority;
    }
    
    public PluginEntry(String service, String pluginClass, boolean onload) {
        this(service, pluginClass, onload, null);
    }
    

    public PluginEntry(String service, String pluginClass, boolean onload, List<String> urlFilters) {
        this.service = service;
        this.pluginClass = pluginClass;
        this.onload = onload;
        this.urlFilters = urlFilters;
        this.priority = 0;
    }

    public List<String> getUrlFilters() {
        return urlFilters;
    }

    /**
     * Create plugin object.
     * If plugin is already created, then just return it.
     *
     * @return                      The plugin object
     */
    public CordovaPlugin createPlugin(CordovaWebView webView, CordovaInterface ctx) {
        if (this.plugin != null) {
            return this.plugin;
        }
        try {
            Class<?> c = getClassByName(this.pluginClass);
            if (isCordovaPlugin(c)) {
                this.plugin = (CordovaPlugin) c.newInstance();
                this.plugin.privateInitialize(ctx, webView, webView.getPreferences());
                return plugin;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error adding plugin " + this.pluginClass + ".");
        }
        return null;
    }

    /**
     * Get the class.
     *
     * @param clazz
     * @return a reference to the named class
     * @throws ClassNotFoundException
     */
    private Class<?> getClassByName(final String clazz) throws ClassNotFoundException {
        Class<?> c = null;
        if ((clazz != null) && !("".equals(clazz))) {
            c = Class.forName(clazz);
        }
        return c;
    }

    /**
     * Returns whether the given class extends CordovaPlugin.
     */
    private boolean isCordovaPlugin(Class<?> c) {
        if (c != null) {
            return CordovaPlugin.class.isAssignableFrom(c);
        }
        return false;
    }

	/**
	 * Make PluginEntry comparable to allow for sorting by priority.
	 */
	@Override
	public int compareTo(PluginEntry another) {
		return Float.compare(this.priority, another.priority);
	}
}
