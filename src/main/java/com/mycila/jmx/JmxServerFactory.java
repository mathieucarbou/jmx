/**
 * Copyright (C) 2010 Mathieu Carbou <mathieu.carbou@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycila.jmx;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class JmxServerFactory {

    /**
     * Locate the default MBean server: first one in registered MBean servers,
     * or platform MBean server
     *
     * @return the located MBean server
     * @throws JmxServerException If not found
     */
    public MBeanServer locateDefault() throws JmxServerException {
        return locateByAgent(null);
    }

    /**
     * Locate an existing MBean server from its agent name
     *
     * @param agent MBean serevr name
     * @return The located MBean server
     * @throws JmxServerException If not found
     */
    public MBeanServer locateByAgent(String agent) throws JmxServerException {
        // null means any registered server, but "" specifically means the platform server
        if (!"".equals(agent)) {
            List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(agent);
            if (servers != null && !servers.isEmpty()) return servers.get(0);
        }
        if (agent == null || agent.length() == 0)
            try {
                return ManagementFactory.getPlatformMBeanServer();
            }
            catch (SecurityException ex) {
                throw new JmxServerException("No specific MBeanServer found, and not allowed to obtain the Java platform MBeanServer", ex);
            }
        throw new JmxServerException("Unable to locate an MBeanServer instance" + (agent != null ? " with agent id [" + agent + "]" : ""));
    }

    /**
     * Locate an existing MBean server from its domain name
     *
     * @param domain domain name. I.e. 'jboss'
     * @return The located MBean server
     * @throws JmxServerException If not found
     */
    public MBeanServer locateByDomain(String domain) throws JmxServerException {
        List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
        if (servers != null && !servers.isEmpty())
            for (MBeanServer server : servers)
                if (domain.equalsIgnoreCase(server.getDefaultDomain()))
                    return server;
        throw new JmxServerException("Unable to locate an MBeanServer instance" + (domain != null ? " with domain [" + domain + "]" : ""));
    }
}
