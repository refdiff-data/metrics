package io.dropwizard.metrics.servlets.experiments;

import static io.dropwizard.metrics.MetricRegistry.name;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ThreadPool;

import io.dropwizard.metrics.CounterMetric;
import io.dropwizard.metrics.Gauge;
import io.dropwizard.metrics.MetricRegistry;
import io.dropwizard.metrics.health.HealthCheckRegistry;
import io.dropwizard.metrics.jetty9.InstrumentedConnectionFactory;
import io.dropwizard.metrics.jetty9.InstrumentedHandler;
import io.dropwizard.metrics.jetty9.InstrumentedQueuedThreadPool;
import io.dropwizard.metrics.servlets.AdminServlet;
import io.dropwizard.metrics.servlets.HealthCheckServlet;
import io.dropwizard.metrics.servlets.MetricsServlet;

public class ExampleServer {
    private static final MetricRegistry REGISTRY = new MetricRegistry();
    private static final CounterMetric COUNTER_1 = REGISTRY.counter(name(ExampleServer.class,
                                                                   "wah",
                                                                   "doody"));
    private static final CounterMetric COUNTER_2 = REGISTRY.counter(name(ExampleServer.class, "woo"));
    static {
        REGISTRY.register(name(ExampleServer.class, "boo"), new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                throw new RuntimeException("asplode!");
            }
        });
    }

    public static void main(String[] args) throws Exception {
        COUNTER_1.inc();
        COUNTER_2.inc();

        final ThreadPool threadPool = new InstrumentedQueuedThreadPool(REGISTRY);
        final Server server = new Server(threadPool);

        final Connector connector = new ServerConnector(server,
                                                        new InstrumentedConnectionFactory(new HttpConnectionFactory(),
                                                                                          REGISTRY.timer("http.connection")));
        server.addConnector(connector);

        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/initial");
        context.setAttribute(MetricsServlet.METRICS_REGISTRY, REGISTRY);
        context.setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY, new HealthCheckRegistry());

        final ServletHolder holder = new ServletHolder(new AdminServlet());
        context.addServlet(holder, "/dingo/*");

        final InstrumentedHandler handler = new InstrumentedHandler(REGISTRY);
        handler.setHandler(context);
        server.setHandler(handler);

        server.start();
        server.join();
    }
}
