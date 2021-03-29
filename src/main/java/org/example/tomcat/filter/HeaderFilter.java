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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;

public class HeaderFilter implements Filter {
 
    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("init Called!!!"); 
        // ...
    }
 
    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) 
      throws IOException, ServletException {

        System.out.println("HeaderFilter: doFilter Called!!!"); 
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader("myHeader", "myHeaderValue");
        HttpServletRequest httpRequest = new HttpServletRequestWrapper((HttpServletRequest)request) {
            @Override
            public String getHeader(String header) {
                System.out.println("HeaderFilter: getHeader Called!!!"); 
                String val = super.getHeader(header);
                if (val.equals("x-webkit-deflate-frame")) {
                    return("permessage-deflate; client_max_window_bits");
                }
                return val;
            }
        };
        chain.doFilter(httpRequest, httpResponse);
    }
 
    @Override
    public void destroy() {
        // ...
    }
}
