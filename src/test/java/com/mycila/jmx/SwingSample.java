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

import com.mycila.jmx.export.JmxExporter;
import com.mycila.jmx.export.MycilaJmxExporter;
import com.mycila.jmx.export.PublicMetadataAssembler;
import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.http.XSLTProcessor;

import javax.management.ObjectName;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
final class SwingSample {
    public static void main(String[] args) throws Exception {
        final HttpAdaptor httpAdaptor = new HttpAdaptor(5486, "localhost");
        httpAdaptor.setProcessor(new XSLTProcessor());
        ManagementFactory.getPlatformMBeanServer().registerMBean(httpAdaptor, ObjectName.getInstance("mx4j:type=HttpAdaptor"));
        httpAdaptor.start();

        final JFrame frame = new JFrame("Export test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                httpAdaptor.stop();
            }
        });

        final JButton button = new JButton("Export all Swing elements");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                button.setText("Registering...");

                MycilaJmxExporter exporter = new MycilaJmxExporter();
                exporter.setEnsureUnique(true);
                exporter.setMetadataAssembler(new PublicMetadataAssembler());

                export(frame, exporter);
                button.setText("Done !");
                System.out.println("http://localhost:" + httpAdaptor.getPort() + "/");
            }
        });

        Container c = frame.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(button, BorderLayout.CENTER);

        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
    }

    private static final Set<Component> exported = new HashSet<Component>();

    private static void export(Component c, JmxExporter exporter) {
        if (!exported.contains(c)) {
            exported.add(c);
            System.out.println("Exporting " + c);
            exporter.register(c);
            if (c instanceof Container)
                for (Component component : ((Container) c).getComponents())
                    export(component, exporter);
        }
    }
}
