package org.beamfoundry.jnrpe.plugins;

import org.beamfoundry.jnrpe.plugins.commands.CTestPlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "jnrpe.server", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JNRPEPluginsAutoConfiguration {

	@Bean
	public CTestPlugin jNRPETestPlugin() {
		return new CTestPlugin();
	}
}
