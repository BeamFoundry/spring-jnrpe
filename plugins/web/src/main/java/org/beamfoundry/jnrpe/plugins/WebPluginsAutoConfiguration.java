package org.beamfoundry.jnrpe.plugins;

import org.beamfoundry.jnrpe.plugins.web.URLMonitoringPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ConditionalOnProperty(prefix = "jnrpe.server", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebPluginsAutoConfiguration extends WebMvcConfigurerAdapter {
	

	@Bean
	@Autowired
	public URLMonitoringPlugin jNRPEURLMonitor() {
		URLMonitoringPlugin urlMonitor = new URLMonitoringPlugin();
		return urlMonitor;
	}
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(jNRPEURLMonitor());
    }
	
}
