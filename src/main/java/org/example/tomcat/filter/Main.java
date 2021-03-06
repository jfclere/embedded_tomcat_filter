/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.tomcat.filter;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.Filter;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ContextHandler;

public class Main {
    private static final Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws LifecycleException, IOException {
        // Maximum logging
        /*
        Enumeration<String> x = LogManager.getLogManager().getLoggerNames();
        while (x.hasMoreElements()) {
            String s = x.nextElement();
            Logger log = LogManager.getLogManager().getLogger(s);
            log.setLevel(Level.FINE);
            for (Handler h : log.getHandlers())
                h.setLevel(Level.FINE);
        }
        */

        // Embedded Tomcat Configuration:

        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080); // HTTP port for Tomcat; make sure to set the same value in pom.xml
            tomcat.getConnector();

            // Servlet Configuration:
            File base = new File(System.getProperty("java.io.tmpdir"));
            Context context = tomcat.addContext("", base.getAbsolutePath());

            // Add Filter
            Class filterClass = MyFilter.class;
            String filterName = filterClass.getName();
            FilterDef def = new FilterDef();
            def.setFilterName(filterName);
            def.setFilter( new MyFilter() );
            context.addFilterDef( def );
            FilterMap map = new FilterMap();
            map.setFilterName( filterName );
            map.addURLPattern( "/*" );
            context.addFilterMap( map );

            // Add servlet
            tomcat.addServlet(context, "MyServlet", new MyServlet());
            context.addServletMappingDecoded("/*", "MyServlet");

            // Start Tomcat
            tomcat.start();

            FilterDef[] filters = context.findFilterDefs();
            for (int i = 0; i < filters.length; i++) {
                System.out.println("Filter: " + filters[i].getFilter());
            }
            ContextService[] services = context.getNamingResources().findServices();
            for (int i = 0; i < services.length; i++) {
                System.out.println("Service: " + services[i]);
                Iterator<String> names = services[i].getHandlers();
                while(names.hasNext()) {
                    String name = names.next();
                    ContextHandler handler = services[i].getHandler(name);
                    System.out.println("Handler: " + handler);
                }
            }

            // Wait until we stop Tomcat
            tomcat.getServer().await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
